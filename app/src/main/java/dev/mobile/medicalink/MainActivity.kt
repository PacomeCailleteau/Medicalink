package dev.mobile.medicalink

import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.MainFragment
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
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
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity() {
    private lateinit var imageConnexion: ImageView
    private lateinit var textBienvenue: TextView
    private lateinit var buttonConnexion: Button
    private lateinit var buttonChangerUtilisateur: Button
    private lateinit var boutonAjouterProfil: Button

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var userId: String? = null
        val intent = intent
        if (intent.hasExtra("userId")) {
            // Récupérer la valeur associée à la clé "userId"
            userId = intent.getStringExtra("userId")
        }


        mBackgroundThread = HandlerThread("BackgroundThread")
        mBackgroundThread.start()
        mHandle = Handler(mBackgroundThread.looper)

        mHandle.removeCallbacks(modelRunnable)
        mHandle.post(modelRunnable)

        try {
            loadDictionaryFile(this.assets)
            Log.v(TAG, "Dictionary loaded.")
        } catch (ex: IOException) {
            Log.e(TAG, ex.message!!)
        }

        try {
            loadIdToLabelFile(this.assets)
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
        val feature: Feature = featureConverter.convert("")
        Log.v(TAG, "Set inputs...")
        val inputIds = feature.inputIds
        val inputMask = feature.inputMask
        val segmentIds = feature.segmentIds
        val startLogits = FloatArray(MAX_SEQ_LEN)
        val endLogits = FloatArray(MAX_SEQ_LEN)

        // Show token and tokenoToOrigMap
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

        val startLogitsTensor = outputTensor[0].toTensor() // Tensor([1, 20, 3], dtype=torch.float32)
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

        //On ne le fait qu'une seule fois dans toute l'application
        creerCanalNotification()

        //NotificationService.sendNotification(this, "Youpi", "Ça rime avec Tchoupi", 5000)

        //masquer la barre de titre
        supportActionBar?.hide()

        imageConnexion = findViewById(R.id.image_connexion)
        textBienvenue = findViewById(R.id.text_bienvenue)
        buttonConnexion = findViewById(R.id.button_connexion)
        buttonChangerUtilisateur = findViewById(R.id.button_changer_utilisateur)

        //Connection à la base de données

        val db = AppDatabase.getInstance(this)
        val userDatabaseInterface = UserRepository(db.userDao())

        val queue = LinkedBlockingQueue<String?>()

        Thread {
            //TODO : enlever Pierre Denis et Jacques pour la version finale
            //On créer un User connecté pour tester
            val user = User(
                "111111",
                "Professionnel",
                "BOUTET",
                "Paul",
                "01/01/2000",
                "pierre.denis@gmail",
                "123456",
                true
            )
            val user2 = User(
                "111112",
                "Utilisateur",
                "DUTRONC",
                "Jacques",
                "05/06/2003",
                "jacques.dutronc@gmail",
                "654321",
                false
            )
            userDatabaseInterface.insertUser(user)
            userDatabaseInterface.insertUser(user2)


            val res = userDatabaseInterface.getUsersConnected()
            queue.add(res.first().prenom)

        }.start()

        var prenom = queue.take()
        if (prenom != null) {
            //Changement du texte
            val txtBienvenue = resources.getString(R.string.bienvenue) + " " + prenom + " !"
            textBienvenue.text = txtBienvenue

            //Changement du texte du bouton de "Creer mon profil" à "Me connecter"
            buttonConnexion.text = resources.getString(R.string.me_connecter)

            //On met le bouton "Changer d'utilisateur" visible
            buttonChangerUtilisateur.visibility = View.VISIBLE

            //On met les bons listeners
            buttonConnexion.setOnClickListener {
                showPasswordDialog()
                authenticateWithBiometric()
            }
            buttonChangerUtilisateur.setOnClickListener {
                showIntervalleRegulierDialog(this)
                /*
                val intent = Intent(this, ChangerUtilisateur::class.java)
                startActivity(intent)
                */

            }
        } else {
            //Changement du texte
            textBienvenue.text = resources.getString(R.string.bienvenue_sur_medicalink)

            //Changement du texte du bouton de "Me connecter" à "Creer mon profil"
            buttonConnexion.text = resources.getString(R.string.creer_mon_profil)

            //On met le bouton "Changer d'utilisateur" gone
            buttonChangerUtilisateur.visibility = View.GONE

            //On met le bon listener
            buttonConnexion.setOnClickListener {
                val intent = Intent(this, CreerProfilActivity::class.java)
                startActivity(intent)
            }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Ne rien faire ici pour désactiver le bouton de retour arrière
                }
            }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun authenticateWithBiometric() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> showBiometricPrompt()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // L'appareil ne prend pas en charge la biométrie
                // Gérez le cas où la biométrie n'est pas disponible
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // La biométrie n'est pas disponible pour le moment
                // Gérez le cas où la biométrie n'est pas disponible
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Aucune empreinte n'a été enregistrée sur l'appareil
                // Gérez le cas où aucune empreinte n'est enregistrée
            }
        }
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.authentification_biometrique))
            .setSubtitle(resources.getString(R.string.utilisez_empreinte_digitale))
            .setNegativeButtonText(resources.getString(R.string.utilisez_mdp))
            .setConfirmationRequired(false)
            .build()

        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Gérer les erreurs d'authentification ici
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        // L'utilisateur a annulé l'authentification, ajoutez votre logique ici
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // L'authentification a réussi, ouvrez votre intent ici
                    val intent = Intent(this@MainActivity, MainFragment::class.java)
                    startActivity(intent)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // L'authentification a échoué, demandez à l'utilisateur de réessayer
                }
            })

        // Afficher la boîte de dialogue de la biométrie
        biometricPrompt.authenticate(promptInfo)
    }

    private fun showPasswordDialog() {
        val dialogBuilder = AlertDialog.Builder(this,R.style.RoundedDialog)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_password, null)
        dialogBuilder.setView(dialogView)

        val editTextPassword = dialogView.findViewById<EditText>(R.id.editTextPassword)
        val buttonValidate = dialogView.findViewById<Button>(R.id.buttonValidate)
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val textMotDePasseIncorrect =
            dialogView.findViewById<TextView>(R.id.textMotDePasseIncorrect)

        val alertDialog = dialogBuilder.create()

        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ne rien faire avant la modification du texte
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ne rien faire lorsqu'il y a un changement dans le texte
            }

            override fun afterTextChanged(s: Editable?) {
                val isValidLength = s?.length == 6
                buttonValidate.isEnabled = isValidLength
                if (isValidLength) {
                    buttonValidate.alpha = 1F
                } else {
                    buttonValidate.alpha = 0.3F
                }
                if (s?.length ?: 0 > 6) {
                    // Si la longueur est supérieure à 6, tronquer le texte
                    editTextPassword.setText(s?.subSequence(0, 6))
                    editTextPassword.setSelection(6)
                }
            }
        })

        buttonValidate.setOnClickListener {
            // Gérer la validation du mot de passe ici
            val password = editTextPassword.text.toString()
            if (isValidPassword(password)) {
                // Le mot de passe est valide, effectuez votre action
                val intent = Intent(this@MainActivity, MainFragment::class.java)
                startActivity(intent)
                alertDialog.dismiss()
            } else {
                textMotDePasseIncorrect.visibility = View.VISIBLE
                editTextPassword.text = null
                // Le mot de passe n'est pas valide, gérer en conséquence
                // Vous pouvez afficher un message d'erreur, etc.
            }
        }

        buttonCancel.setOnClickListener {
            // L'utilisateur a annulé, ajoutez votre logique ici
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun isValidPassword(password: String): Boolean {
        // Ajoutez votre logique de validation du mot de passe ici

        val db = AppDatabase.getInstance(this)
        val userDatabaseInterface = UserRepository(db.userDao())
        val queue = LinkedBlockingQueue<Boolean>()
        Thread {
            queue.add(userDatabaseInterface.isValidPassword(password).first)
        }.start()
        return queue.take()
    }


    private fun creerCanalNotification() {
        // Créez le canal de notification (pour les API > Oreo donc > 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "medicalinkNotificationChannel"
            val channelName = "canal de notification"
            val channelDescription = "canal de notification pour les notifications de l'application"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            // Enregistrez le canal auprès du gestionnaire de notifications
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showIntervalleRegulierDialog(context: Context) {
        val dialog = Dialog(context, R.style.RoundedDialog)
        val dialogView =
            LayoutInflater.from(dialog.context).inflate(R.layout.activity_changer_utilisateur, null)
        dialog.setContentView(dialogView)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewChangerUtilisateur)
        recyclerView.layoutManager = LinearLayoutManager(context)

        boutonAjouterProfil = dialog.findViewById(R.id.boutonAjouterProfilChangerUtilisateur)

        val db = AppDatabase.getInstance(context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())


        val queue = LinkedBlockingQueue<List<User>>()

        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread {
            val listeUserBDD = userDatabaseInterface.getAllUsers()

            queue.add(listeUserBDD)
        }.start()

        val mesUsers = queue.take()


        val adapter = ChangerUtilisateurAdapterR(mesUsers) { clickedUser ->

            var queue = LinkedBlockingQueue<String>()
            Thread {
                userDatabaseInterface.setConnected(
                    userDatabaseInterface.getOneUserById(clickedUser.uuid).first()
                )
                queue.add(clickedUser.prenom)
            }.start()
            val prenom = queue.take()
            Log.d("test", prenom.toString())
            val txtBienvenue = resources.getString(R.string.bienvenue) + " " + prenom + " !"
            textBienvenue.text = txtBienvenue

            dialog.dismiss()
        }

        recyclerView.adapter = adapter

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        boutonAjouterProfil.setOnClickListener {
            val intent = Intent(context, CreerProfilActivity::class.java)
            startActivity(intent)
            dialog.show()
        }

        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.9).toInt()
        dialog.window?.setLayout(width, height)
        dialog.show()

    }

    private val modelRunnable = Runnable {
        loadModel()
    }

    @WorkerThread
    private fun loadModel() {
        if (mModule == null) {
            val moduleFileAbsoluteFilePath: String? = assetFilePath(this, "model.pt")
            mModule = Module.load(moduleFileAbsoluteFilePath)
        }
    }

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



