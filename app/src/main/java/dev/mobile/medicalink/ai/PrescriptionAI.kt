package fr.medicapp.medicapp.ai

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.annotation.WorkerThread
import dev.mobile.medicalink.fragments.traitements.Traitement
import dev.mobile.medicalink.fragments.traitements.enums.EnumFrequence
import dev.mobile.medicalink.fragments.traitements.enums.EnumTypeMedic
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
import java.time.LocalDate

/**
 * Classe PrescriptionAI pour l'analyse des prescriptions médicales.
 *
 * @property context Le contexte de l'application.
 */
class PrescriptionAI(
    val context: Context
) {
    // singleton pour pouvoir charger le modèle de manière préventive
    // lancer avec: PrescriptionAI.getInstance(this)
    /**
     * Module PyTorch utilisé pour l'analyse.
     */
    private var mModule: Module? = null

    /**
     * Thread en arrière-plan pour les tâches lourdes.
     */
    private var mBackgroundThread: HandlerThread

    /**
     * Gestionnaire pour poster des tâches à exécuter sur le thread en arrière-plan.
     */
    private var mHandle: Handler

    /**
     * Tag utilisé pour les logs.
     */
    private val TAG = "CamemBERT"

    /**
     * Chemin vers le dossier contenant les fichiers du modèle PyTorch.
     */
    private val FOLDER_PATH = "ai/"

    /**
     * Chemin vers le fichier du modèle PyTorch.
     */
    private val MODEL_PATH = "model.pt"

    /**
     * Chemin vers le fichier du dictionnaire.
     */
    private val DIC_PATH = "vocab.txt"

    /**
     * Chemin vers le fichier des labels.
     */
    private val LABEL_PATH = "id_to_label.txt"

    /**
     * Dictionnaire utilisé pour le tokenization.
     */
    private val dic: HashMap<String, Int> = HashMap()

    /**
     * Labels utilisés pour la prédiction.
     */
    private val labels: HashMap<Int, String> = HashMap()

    /**
     * Convertisseur de features utilisé pour le modèle.
     */
    private lateinit var featureConverter: FeatureConverter

    /**
     * Longueur maximale de la requête.
     */
    private val MAX_QUERY_LEN = 512

    /**
     * Longueur maximale de la séquence.
     */
    private val MAX_SEQ_LEN = 512

    /**
     * Indique si les tokens doivent être convertis en minuscules.
     */
    private val DO_LOWER_CASE = false

    /**
     * Initialise le thread en arrière-plan et charge le modèle PyTorch.
     */
    init {
        // Crée un nouveau thread en arrière-plan.
        mBackgroundThread = HandlerThread("BackgroundThread")

        // Démarre le thread en arrière-plan.
        mBackgroundThread.start()

        // Crée un nouveau gestionnaire pour poster des tâches à exécuter sur le thread en arrière-plan.
        mHandle = Handler(mBackgroundThread.looper)

        // Poste une tâche pour charger le modèle PyTorch sur le thread en arrière-plan.
        mHandle.post {
            loadModel()
        }
    }

    /**
     * Analyse une image de prescription et génère des prédictions.
     *
     * @param imageUri L'URI de l'image à analyser.
     * @return Liste de 'Traitement'
     */
    fun analyse(
        imageUri: Uri
    ): List<Traitement> {
        // Attend que le module PyTorch soit chargé.
        while (mModule == null) {
            Thread.sleep(100)
        }

        // Reconnaît le texte dans l'image spécifiée par l'URI.
        val visionText = recognizeText(imageUri)
        val result: List<Traitement>


        // Exécute le modèle PyTorch sur le texte reconnu et génère des prédictions.
        val sentenceTokenized = runModel(visionText)
        result = postAnalyse(sentenceTokenized)
        // Appelle le callback avec les prédictions générées.
        return result
    }

    /**
     * Analyse les résultats de l'OCR et créé des traitements
     *
     * @param prediction resultat de l'OCR avec les tokens
     * @return liste de 'Traitement'
     */
    private fun postAnalyse(prediction: List<Pair<String, String>>): List<Traitement> {
        val treatment = mutableListOf(
            Traitement(
                "",
                "",
                0,
                EnumFrequence.AUBESOIN,
                null,
                comprimesRestants = null,
                effetsSecondaires = null,
                totalQuantite = null,
                uuid = null,
                uuidUser = null,
                dateDbtTraitement = LocalDate.now()
            )
        )
        var last = 0
        prediction.forEach { (word, label) ->
            when {
                // B -> début mot
                // I -> intermédiaire du mot
                // B-Doliprane I-500mg
                label.startsWith("B-") -> {
                    // query est la barre de recherche pour associer ce qui est trouver au nom de médicament via un algo
                    // donc cette condition -> si on trouve un nom de medic (drug) et que la barre de recherche est déjà pleine (indiquant qu'il sert déja à quelque chose) -> renvoyer les results + recréer un traitement
                    if (label.removePrefix("B-") == "Drug" && treatment[last].nomTraitement != "") {

                        treatment.add(
                            Traitement(
                                "",
                                "",
                                0,
                                EnumFrequence.AUBESOIN,
                                null,
                                comprimesRestants = null,
                                effetsSecondaires = null,
                                totalQuantite = null,
                                uuid = null,
                                uuidUser = null,
                                dateDbtTraitement = LocalDate.now()
                            )
                        )
                        last = treatment.size - 1

                    }
                    when (label.removePrefix("B-")) {
                        "Drug" -> treatment[last].nomTraitement = word
                        "DrugQuantity" -> treatment[last].dosageNb = word.toInt()
                        "DrugForm" -> treatment[last].typeComprime =
                            EnumTypeMedic.valueOf(word.uppercase())

                        "DrugFrequency" -> treatment[last].suggFrequence = word
                        "DrugDuration" -> treatment[last].suggDuree = word
                    }
                }

                label.startsWith("I-") -> {
                    when (label.removePrefix("I-")) {
                        "Drug" -> treatment[last].nomTraitement += " $word"
                        "DrugQuantity" -> treatment[last].dosageNb = word.toInt()
                        "DrugForm" -> treatment[last].typeComprime =
                            EnumTypeMedic.valueOf(word.uppercase())

                        "DrugFrequency" -> treatment[last].suggFrequence = " $word"
                        "DrugDuration" -> treatment[last].suggDuree += " $word"
                    }
                }
            }
        }
        //lancer pour chaque élément le traitement du traitement
        val aSupprimer = mutableListOf<Int>()
        treatment.forEach {
            try {
                it.paufine(context)
            } catch (e: Exception) {
                aSupprimer.add(treatment.indexOf(it))
            }
        }
        aSupprimer.forEach {
            treatment.removeAt(it)
        }
        return treatment
    }

    /**
     * Analyse une image de prescription et génère des prédictions.
     *
     * @param imageUri L'URI de l'image à analyser.
     */
    @WorkerThread
    private fun recognizeText(imageUri: Uri): String {
        // Crée une image à partir de l'URI spécifiée.
        // Retourne le texte reconnu.
        return imageUri.toString()
    }

    /**
     * Charge le modèle PyTorch à partir des assets de l'application.
     */
    @WorkerThread
    private fun loadModel() {
        // Vérifie si le module PyTorch n'a pas déjà été chargé.
        if (mModule == null) {
            try {
                // Charge le fichier de dictionnaire à partir des assets de l'application
                loadDictionaryFile(context.assets)

                // Log pour indiquer que le dictionnaire a été chargé
                Log.v(TAG, "Dictionary loaded.")
            } catch (ex: IOException) {
                // Log pour indiquer une erreur lors du chargement du dictionnaire
                Log.e(TAG, ex.message!!)
            }

            try {
                // Charge le fichier d'identification des labels à partir des assets de l'application
                loadIdToLabelFile(context.assets)

                // Log pour indiquer que les labels ont été chargés
                Log.v(TAG, "Id to label loaded.")
            } catch (ex: IOException) {
                // Log pour indiquer une erreur lors du chargement des labels
                Log.e(TAG, ex.message!!)
            }

            // Récupère le chemin absolu du fichier modèle à partir des assets de l'application
            val moduleFileAbsoluteFilePath: String? = assetFilePath(context.assets)

            // Charge le modèle PyTorch
            mModule = Module.load(moduleFileAbsoluteFilePath)

            // Log pour indiquer que le modèle a été chargé
            Log.v(TAG, "Model loaded.")
        }
    }

    /**
     * Exécute le modèle PyTorch sur le texte reconnu.
     *
     * @param visionText Le texte reconnu à analyser.
     * @return Une liste de paires de tokens et de labels prédits.
     */
    @WorkerThread
    fun runModel(visionText: String): MutableList<Pair<String, String>> {
        // Crée une liste mutable pour stocker les paires de tokens et de labels prédits.
        val sentenceTokenized: MutableList<Pair<String, String>> = mutableListOf()

        // Vérifie si le module PyTorch a été chargé.
        if (mModule != null) {
            // Initialise le convertisseur de features avec le dictionnaire, les paramètres de tokenization et les longueurs maximales.
            featureConverter = FeatureConverter(
                dic,
                DO_LOWER_CASE,
                MAX_QUERY_LEN,
                MAX_SEQ_LEN
            )

            // Convertit le texte reconnu en features.
            val feature: Feature = featureConverter.convert(visionText)
            val inputIds = feature.inputIds
            val inputMask = feature.inputMask
            val startLogits = FloatArray(MAX_SEQ_LEN)
            val endLogits = FloatArray(MAX_SEQ_LEN)

            // Crée une map mutable pour stocker les logits de début et de fin.
            val output: MutableMap<Int, Any> = HashMap()
            output[0] = startLogits
            output[1] = endLogits

            // Convertit les IDs d'entrée et le masque d'entrée en tensors.
            val inputIdsTensor = Tensor.fromBlob(
                inputIds,
                longArrayOf(1, MAX_SEQ_LEN.toLong())
            )
            val inputMaskTensor = Tensor.fromBlob(
                inputMask,
                longArrayOf(1, MAX_SEQ_LEN.toLong())
            )

            // Convertit les tensors en valeurs IValue.
            val inputPredictions = IValue.from(
                inputIdsTensor
            )
            val inputMaskPredictions = IValue.from(
                inputMaskTensor
            )

            // Aligne les IDs de mots avec les labels.
            val labelIds = FeatureConverter.alignWordIds(feature)

            // Exécute le modèle PyTorch avec les prédictions d'entrée et de masque.
            val outputTensor = mModule!!.forward(
                inputPredictions,
                inputMaskPredictions
            ).toTuple()

            // Récupère le tensor des logits de début.
            val startLogitsTensor = outputTensor[0].toTensor()

            // Convertit le tensor des logits de début en tableau.
            val startLogitsArray = startLogitsTensor.dataAsFloatArray

            // Convertit le tableau des logits de début en liste de listes.
            val startLogitsList = List(startLogitsTensor.shape()[1].toInt()) { row ->
                List(startLogitsTensor.shape()[2].toInt()) { col ->
                    startLogitsArray[row * startLogitsTensor.shape()[2].toInt() + col]
                }
            }

            // Filtre la liste des logits de début pour ne garder que ceux qui correspondent à des labels connus.
            val startCleanLogitsList = startLogitsList.filterIndexed { index, _ ->
                labelIds[index] != -100
            }

            // Convertit la liste des logits de début en liste de prédictions.
            val startPredictionsList = startCleanLogitsList.map { row ->
                row.indexOf(row.maxOrNull())
            }

            // Convertit la liste de prédictions en liste de labels.
            val predictionsLabelList: List<String> = startPredictionsList.map { index ->
                labels[index] ?: ""
            }

            // Parcourt la liste des labels prédits.
            for (i in predictionsLabelList.indices) {
                // Vérifie si le label prédit n'est pas "O".
                if (predictionsLabelList[i] != "O") {
                    // Ajoute la paire de token et de label prédit à la liste des paires tokenisées.
                    sentenceTokenized.add(Pair(feature.origTokens[i], predictionsLabelList[i]))
                }
            }
        }

        // Retourne la liste des paires tokenisées.
        return sentenceTokenized
    }

    /**
     * Récupère le chemin absolu du fichier modèle à partir des assets de l'application.
     *
     * @param assetManager Le gestionnaire d'assets de l'application.
     * @return Le chemin absolu du fichier modèle, ou null si une erreur se produit.
     */
    private fun assetFilePath(assetManager: AssetManager): String? {
        // Ouvre le fichier du modèle à partir des assets de l'application.
        assetManager.open(FOLDER_PATH + MODEL_PATH).use { ins ->
            // Crée un fichier dans le cache de l'application avec le même nom que le fichier du modèle.
            val file = File(context.cacheDir, MODEL_PATH)

            // Ouvre un flux de sortie de fichier pour écrire dans le fichier créé.
            FileOutputStream(file).use { out ->
                // Crée un tampon pour stocker les données lues à partir du fichier du modèle.
                val buffer = ByteArray(1024)
                var read: Int

                // Lit les données du fichier du modèle dans le tampon jusqu'à ce qu'il n'y ait plus de données à lire.
                while (ins.read(buffer).also { read = it } != -1) {
                    // Écrit les données du tampon dans le fichier de sortie.
                    out.write(buffer, 0, read)
                }

                // Force toutes les données tamponnées à être écrites dans le fichier.
                out.flush()
            }

            // Retourne le chemin absolu du fichier créé.
            return file.absolutePath
        }
    }

    /**
     * Charge le fichier de dictionnaire à partir des assets de l'application.
     *
     * @param assetManager Le gestionnaire d'assets de l'application.
     */
    private fun loadDictionaryFile(assetManager: AssetManager) {
        // Ouvre le fichier de dictionnaire à partir des assets de l'application.
        assetManager.open(FOLDER_PATH + DIC_PATH).use { ins ->
            // Crée un BufferedReader pour lire le fichier de dictionnaire.
            BufferedReader(InputStreamReader(ins)).use { reader ->
                // Initialise un index à 0 pour associer chaque mot du dictionnaire à un index.
                var index = 0

                // Parcourt le fichier de dictionnaire tant qu'il reste des lignes à lire.
                while (reader.ready()) {
                    // Lit la ligne suivante du fichier de dictionnaire.
                    val key = reader.readLine()

                    // Ajoute le mot lu et son index associé au dictionnaire.
                    dic.put(key, index++)
                }
            }
        }
    }

    /**
     * Charge le fichier d'identification des labels à partir des assets de l'application.
     *
     * @param assetManager Le gestionnaire d'assets de l'application.
     */
    private fun loadIdToLabelFile(assetManager: AssetManager) {
        // Ouvre le fichier des labels à partir des assets de l'application.
        assetManager.open(FOLDER_PATH + LABEL_PATH).use { ins ->
            // Crée un BufferedReader pour lire le fichier des labels.
            BufferedReader(InputStreamReader(ins)).use { reader ->
                // Initialise un index à 0 pour associer chaque label à un index.
                var index = 0

                // Parcourt le fichier des labels tant qu'il reste des lignes à lire.
                while (reader.ready()) {
                    // Lit la ligne suivante du fichier des labels.
                    val key = reader.readLine()

                    // Ajoute le label lu et son index associé à la map des labels.
                    labels.put(index++, key)
                }
            }
        }
    }

    /**
     * Objet compagnon pour la classe PrescriptionAI.
     * Fournit une instance singleton de PrescriptionAI.
     */
    companion object {
        /**
         * Instance singleton de PrescriptionAI.
         */
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: PrescriptionAI? = null

        /**
         * Récupère l'instance singleton de PrescriptionAI.
         *
         * @param context Le contexte de l'application.
         * @return L'instance singleton de PrescriptionAI.
         */
        fun getInstance(context: Context): PrescriptionAI {
            return INSTANCE ?: synchronized(this) {
                val instance = PrescriptionAI(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
