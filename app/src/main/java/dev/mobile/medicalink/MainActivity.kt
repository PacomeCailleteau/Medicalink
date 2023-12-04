package dev.mobile.medicalink

import android.app.AlertDialog
import android.app.Dialog
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.MainFragment
import dev.mobile.medicalink.fragments.home.HomeFragment
import dev.mobile.medicalink.utils.NotificationService
import dev.mobile.medicalink.fragments.traitements.AjoutManuelTypeMedic
import dev.mobile.medicalink.fragments.traitements.AjoutManuelTypeMedicAdapterR
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity() {
    private lateinit var imageConnexion: ImageView
    private lateinit var textBienvenue: TextView
    private lateinit var buttonConnexion: Button
    private lateinit var buttonChangerUtilisateur: Button
    private lateinit var boutonAjouterProfil : Button
    private val BIOMETRIC_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var userId : String? = null
        val intent = intent
        if (intent.hasExtra("userId")) {
            // Récupérer la valeur associée à la clé "userId"
            userId = intent.getStringExtra("userId")
        }

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
            val user = User("111111","Professionnel","BOUTET","Paul","01/01/2000","pierre.denis@gmail","123456",true)
            val user2 = User("111112","Utilisateur","DUTRONC","Jacques","05/06/2003","jacques.dutronc@gmail","654321",false)
            userDatabaseInterface.insertUser(user)
            userDatabaseInterface.insertUser(user2)


            val res = userDatabaseInterface.getUsersConnected()
            queue.add(res.first().prenom)

        }.start()

        var prenom=queue.take()
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
        }
        else {
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
            .setTitle("Authentification biométrique")
            .setSubtitle("Utilisez votre empreinte digitale pour vous authentifier")
            .setNegativeButtonText("Utiliser le mot de passe")
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
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_password, null)
        dialogBuilder.setView(dialogView)

        val editTextPassword = dialogView.findViewById<EditText>(R.id.editTextPassword)
        val buttonValidate = dialogView.findViewById<Button>(R.id.buttonValidate)
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val textMotDePasseIncorrect = dialogView.findViewById<TextView>(R.id.textMotDePasseIncorrect)

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
                }else{
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
        if (password == "111111") {
            return true
        }else{
            return false
        }
         // Modifiez en fonction de votre logique de validation
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
        val dialog = Dialog(context,R.style.RoundedDialog)
        val dialogView = LayoutInflater.from(dialog.context).inflate(R.layout.activity_changer_utilisateur, null)
        dialog.setContentView(dialogView)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewChangerUtilisateur)
        recyclerView.layoutManager = LinearLayoutManager(context)

        boutonAjouterProfil = dialog.findViewById(R.id.boutonAjouterProfilChangerUtilisateur)

        val db = AppDatabase.getInstance(context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())


        val queue = LinkedBlockingQueue<List<User>>()

        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread{
            val listeUserBDD = userDatabaseInterface.getAllUsers()

            queue.add(listeUserBDD)
        }.start()

        val mesUsers = queue.take()


        val adapter = ChangerUtilisateurAdapterR(mesUsers) { clickedUser ->

            var queue = LinkedBlockingQueue<String>()
            Thread{
                userDatabaseInterface.setConnected(userDatabaseInterface.getOneUserById(clickedUser.uuid).first())
                queue.add(clickedUser.prenom)
            }.start()
            val prenom=queue.take()
            Log.d("test",prenom.toString())
            val txtBienvenue = resources.getString(R.string.bienvenue) + " " + prenom + " !"
            textBienvenue.text = txtBienvenue

            dialog.dismiss()
        }

        recyclerView.adapter=adapter

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        boutonAjouterProfil.setOnClickListener {
            val intent = Intent(context, CreerProfilActivity::class.java)
            startActivity(intent)
            dialog.show()
        }

        val width = (resources.displayMetrics.widthPixels*0.9).toInt()
        val height = (resources.displayMetrics.heightPixels*0.9).toInt()
        dialog.window?.setLayout(width, height)
        dialog.show()

    }





}



