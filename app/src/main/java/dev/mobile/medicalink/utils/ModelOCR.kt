/*package dev.mobile.medicalink.utils

import android.content.Context
import android.content.res.AssetManager
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.annotation.WorkerThread
import fr.medicapp.medicapp.tokenization.Feature
import fr.medicapp.medicapp.tokenization.FeatureConverter
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * Classe permettant de gérer l'OCR
 */
class ModelOCR(private val context: Context) {

    private var mModule: Module? = null
    private lateinit var mBackgroundThread: HandlerThread
    private lateinit var mHandle: Handler

    private val TAG = "CamemBERT"
    private val DIC_PATH = "vocab.txt"
    private val LABEL_PATH = "id_to_label.txt"
    private val dic: HashMap<String, Int> = HashMap()
    private val labels: HashMap<Int, String> = HashMap()
    private lateinit var featureConverter: FeatureConverter
    private val MAX_ANS_LEN = 32
    private val MAX_QUERY_LEN = 64
    private val MAX_SEQ_LEN = 512
    private val DO_LOWER_CASE = false
    private val PREDICT_ANS_NUM = 5
    private val NUM_LITE_THREADS = 4

    /**
     * Fonction permettant d'analyser un texte
     * @param texteAAnalyse le texte à analyser
     * @return une liste de String contenant les mots du texte
     */
    fun analyze(texteAAnalyse: String): List<String?> {


        mBackgroundThread = HandlerThread("BackgroundThread")
        mBackgroundThread.start()
        mHandle = Handler(mBackgroundThread.looper)
        mHandle.removeCallbacks(modelRunnable)
        mHandle.post(modelRunnable)

        try {
            loadDictionaryFile(context.assets)
            Log.v(TAG, "Dictionary loaded.")
        } catch (ex: IOException) {
            Log.e(TAG, ex.message!!)
        }

        try {
            loadIdToLabelFile(context.assets)
            Log.v(TAG, "Id to label loaded.")
        } catch (ex: IOException) {
            Log.e(TAG, ex.message!!)
        }

        featureConverter = FeatureConverter(
            dic,
            DO_LOWER_CASE,
            MAX_QUERY_LEN,
            MAX_SEQ_LEN
        )

        Log.v(TAG, "Convert Feature...")
        val feature: Feature = featureConverter.convert(texteAAnalyse)
        Log.v(TAG, "Set inputs...")
        val inputIds = feature.inputIds
        val inputMask = feature.inputMask
        val segmentIds = feature.segmentIds
        val startLogits = FloatArray(MAX_SEQ_LEN)
        val endLogits = FloatArray(MAX_SEQ_LEN)

        // Montre les tokens
        feature.origTokens.forEachIndexed { index, s ->
            Log.v(TAG, "origTokens[$index] = $s")
        }

        /*inputIds.forEachIndexed { index, i ->
            Log.v(TAG, "inputIds[$index] = $i")
        }*/

        val output: MutableMap<Int, Any> = HashMap()
        output[0] = startLogits
        output[1] = endLogits

        val inputIdsTensor = Tensor.fromBlob(
            inputIds,
            longArrayOf(1, MAX_SEQ_LEN.toLong())
        )
        val inputMaskTensor = Tensor.fromBlob(
            inputMask,
            longArrayOf(1, MAX_SEQ_LEN.toLong())
        )

        Log.v(TAG, "inputIdsTensor: $inputIdsTensor")
        Log.v(TAG, "inputIdsTensor.size: ${inputIdsTensor.shape()}")
        Log.v(TAG, "inputIdsTensor.dataAsFloatArray: ${inputIdsTensor.dataAsIntArray.toList()}")
        Log.v(TAG, "inputMaskTensor: $inputMaskTensor")
        Log.v(TAG, "inputMaskTensor.size: ${inputMaskTensor.shape()}")
        Log.v(TAG, "inputMaskTensor.dataAsFloatArray: ${inputMaskTensor.dataAsIntArray.toList()}")

        val inputPredictions = IValue.from(
            inputIdsTensor
        )
        val inputMaskPredictions = IValue.from(
            inputMaskTensor
        )

        val labelIds = FeatureConverter.align_word_ids(feature)

        Log.v(TAG, "Model loading...")
        while (mModule == null) {
            Log.v(TAG, "Model not loaded yet...")
        }
        Log.v(TAG, "Model loaded.")

        Log.v(TAG, "Predicting...")

        val outputTensor = mModule!!.forward(
            inputPredictions,
            inputMaskPredictions
        ).toTuple()

        /*
            logits = model(input_id, mask)
    logits_clean = logits[0][label_ids != -100]

    predictions = logits_clean.argmax(dim=1).tolist()
    prediction_label = [ids_to_labels[i] for i in predictions]
    print(sentence)
    print(prediction_label)
         */

        Log.v(TAG, "Predictions done.")

        Log.v(TAG, "inputIdsTensor: ${inputIds.size}")
        Log.v(TAG, "inputMaskTensor: ${inputMask.size}")
        Log.v(TAG, "labelIds: ${labelIds.size}")

        val startLogitsTensor =
            outputTensor[0].toTensor() // Tensor([1, 20, 3], dtype=torch.float32)
        Log.v(TAG, "startLogitsTensor: $startLogitsTensor")
        Log.v(TAG, "startLogitsTensor.size: ${startLogitsTensor.shape()}")
        val startLogitsArray = startLogitsTensor.dataAsFloatArray
        Log.v(TAG, "startLogitsArray: $startLogitsArray")
        Log.v(TAG, "startLogitsArray.size: ${startLogitsArray.size}")
        val startLogitsList = List(startLogitsTensor.shape()[1].toInt()) { row ->
            List(startLogitsTensor.shape()[2].toInt()) { col ->
                startLogitsArray[row * startLogitsTensor.shape()[2].toInt() + col]
            }
        }
        Log.v(TAG, "startLogitsList: $startLogitsList")
        Log.v(TAG, "startLogitsList.size: ${startLogitsList.size}")
        val startCleanLogitsList = startLogitsList.filterIndexed { index, _ ->
            Log.v(TAG, "index: $index, labelIds[index]: ${labelIds[index]}")
            labelIds[index] != -100
        }
        Log.v(TAG, "startCleanLogitsList: $startCleanLogitsList")
        Log.v(TAG, "startCleanLogitsList.size: ${startCleanLogitsList.size}")
        val startPredictionsList = startCleanLogitsList.map { row ->
            row.indexOf(row.maxOrNull())
        }
        Log.v(TAG, "startPredictionsList: $startPredictionsList")
        Log.v(TAG, "startPredictionsList.size: ${startPredictionsList.size}")

        val predictionsLabelList = startPredictionsList.map { index ->
            labels[index]
        }
        Log.v(TAG, "predictionsLabelList: $predictionsLabelList")
        return predictionsLabelList
    }

    /**
     * Fonction permettant de charger le modèle
     */
    private val modelRunnable = Runnable {
        loadModel()
    }

    /**
     * Fonction permettant de charger le modèle
     */
    @WorkerThread
    private fun loadModel() {
        if (mModule == null) {
            val moduleFileAbsoluteFilePath: String? = assetFilePath(context, "model.pt")
            mModule = Module.load(moduleFileAbsoluteFilePath)
        }
    }

    /**
     * Fonction permettant de charger un asset
     * @param context le contexte
     * @param assetName le nom de l'asset
     * @return le chemin de l'asset
     */
    private fun assetFilePath(context: Context, assetName: String): String? {
        val file = File(context.filesDir, assetName)

        try {
            context.assets.open(assetName).use { `is` ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    while (true) {
                        val length = `is`.read(buffer)
                        if (length <= 0) {
                            break
                        }
                        os.write(buffer, 0, length)
                    }
                    os.flush()
                    os.close()
                }
                return file.absolutePath
            }
        } catch (e: IOException) {
            Log.e("pytorchandroid", "Error process asset $assetName to file path")
        }
        return null
    }

    /**
     * Fonction permettant de charger un dictionnaire
     * @param assetManager le AssetManager
     */
    fun loadDictionaryFile(assetManager: AssetManager) {
        assetManager.open(DIC_PATH).use { ins ->
            BufferedReader(InputStreamReader(ins)).use { reader ->
                var index = 0
                while (reader.ready()) {
                    val key = reader.readLine()
                    dic.put(key, index++)
                }
            }
        }
    }

    /**
     * Fonction permettant de charger un fichier de labels
     * @param assetManager le AssetManager
     */
    fun loadIdToLabelFile(assetManager: AssetManager) {
        assetManager.open(LABEL_PATH).use { ins ->
            BufferedReader(InputStreamReader(ins)).use { reader ->
                var index = 0
                while (reader.ready()) {
                    val key = reader.readLine()
                    labels.put(index++, key)
                }
            }
        }
    }
}
*/