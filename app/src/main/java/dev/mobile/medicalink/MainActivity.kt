package dev.mobile.medicalink

import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import java.util.concurrent.LinkedBlockingQueue

/**
 * Classe qui gère l'activité principale de l'application. (Page au démarrage de l'application)
 */
class MainActivity : AppCompatActivity() {
    private lateinit var imageConnexion: ImageView
    private lateinit var textBienvenue: TextView
    private lateinit var buttonConnexion: Button
    private lateinit var buttonChangerUtilisateur: Button
    private lateinit var boutonAjouterProfil: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //On ne le fait qu'une seule fois dans toute l'application
        creerCanalNotification()

        //masquer la barre de titre
        supportActionBar?.hide()

        //On récupère les éléments de la vue
        imageConnexion = findViewById(R.id.image_connexion)
        textBienvenue = findViewById(R.id.text_bienvenue)
        buttonConnexion = findViewById(R.id.button_connexion)
        buttonChangerUtilisateur = findViewById(R.id.button_changer_utilisateur)


        //Connection à la base de données
        val db = AppDatabase.getInstance(this)
        val userDatabaseInterface = UserRepository(db.userDao())

        val queue = LinkedBlockingQueue<String>()

        // Thread pour récupérer le prénom de l'utilisateur connecté pour son affichage
        Thread {
            val res = userDatabaseInterface.getUsersConnected()
            if (res.isNotEmpty()) {
                queue.add(res.first().prenom)
            } else {
                queue.add("")
            }
        }.start()

        val prenom = queue.take()
        if (prenom != "") {
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
            }
        } else {
            //Changement du texte
            textBienvenue.text = resources.getString(R.string.bienvenue_sur_medicalink)

            //Changement du texte du bouton de "Me connecter" à "Creer mon profil"
            buttonConnexion.text = resources.getString(R.string.creer_mon_profil)

            //On met le bouton "Changer d'utilisateur" invisible
            buttonChangerUtilisateur.visibility = View.GONE

            //Si on clqiue sur le bouton "Creer mon profil", on lance l'activité de création de profil
            //Si on clique sur le bouton "Me connecter", on lance l'activité de connexion
            buttonConnexion.setOnClickListener {
                val intent = Intent(this, CreerProfilActivity::class.java)
                startActivity(intent)
            }
        }

        //On désactive le bouton de retour arrière
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Ne rien faire ici pour désactiver le bouton de retour arrière
                }
            }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    /**
     * Authentifie l'utilisateur avec la biométrie.
     */
    private fun authenticateWithBiometric() {
        val biometricManager = BiometricManager.from(this)

        // On vérifie que l'authentification biométrique est disponible sur l'appareil
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            showBiometricPrompt()
        }
    }

    /**
     * Affiche la boîte de dialogue de la biométrie.
     */
    private fun showBiometricPrompt() {
        // Création de la boîte de dialogue de la biométrie
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.authentification_biometrique))
            .setSubtitle(resources.getString(R.string.utilisez_empreinte_digitale))
            .setNegativeButtonText(resources.getString(R.string.utilisez_mdp))
            .setConfirmationRequired(false)
            .build()

        // Création de l'authentification biométrique
        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Erreur d'authentification
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // L'authentification a réussi, accès à la page d'accueil
                    val intent = Intent(this@MainActivity, MainFragment::class.java)
                    startActivity(intent)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // L'authentification a échoué, demande à l'utilisateur de réessayer
                }
            })

        // Affiche la boîte de dialogue de la biométrie
        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Fonction qui affiche la boîte de dialogue du mot de passe.
     */
    private fun showPasswordDialog() {
        // Création de la boîte de dialogue du mot de passe
        val dialogBuilder = AlertDialog.Builder(this, R.style.RoundedDialog)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_password, null)
        dialogBuilder.setView(dialogView)

        // Récupération des éléments de la boîte de dialogue
        val editTextPassword = dialogView.findViewById<EditText>(R.id.editTextPassword)
        val buttonValidate = dialogView.findViewById<Button>(R.id.buttonValidate)
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val textMotDePasseIncorrect =
            dialogView.findViewById<TextView>(R.id.textMotDePasseIncorrect)

        // On affiche la boîte de dialogue
        val alertDialog = dialogBuilder.create()

        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ne rien faire avant la modification du texte
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ne rien faire lorsqu'il y a un changement dans le texte
            }

            /**
             * Fonction qui est appelée après la modification du texte.
             * On limite la longueur du texte à 6 caractères et au caractères numériques.
             * Une fois que tous est valide, on active le bouton de validation.
             * @param s le texte modifié
             */
            override fun afterTextChanged(s: Editable?) {
                val isValidLength = s?.length == 6
                buttonValidate.isEnabled = isValidLength
                if (isValidLength) {
                    buttonValidate.alpha = 1F
                } else {
                    buttonValidate.alpha = 0.3F
                }
                if ((s?.length ?: 0) > 6) {
                    // Si la longueur est supérieure à 6, tronquer le texte
                    editTextPassword.setText(s?.subSequence(0, 6))
                    editTextPassword.setSelection(6)
                }
            }
        })

        // Gestion du clic sur le bouton de validation
        buttonValidate.setOnClickListener {
            val password = editTextPassword.text.toString()
            if (isValidPassword(password)) {
                // Le mot de passe est valide, accès à la page d'accueil
                val intent = Intent(this@MainActivity, MainFragment::class.java)
                startActivity(intent)
                alertDialog.dismiss()
            } else {
                textMotDePasseIncorrect.visibility = View.VISIBLE
                editTextPassword.text = null
                // Le mot de passe n'est pas valide
            }
        }

        buttonCancel.setOnClickListener {
            // L'utilisateur a annulé, fermeture de la boîte de dialogue
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    /**
     * Fonction qui vérifie si le mot de passe est valide
     * @param password le mot de passe à vérifier
     * @return true si le mot de passe est valide, false sinon
     */
    private fun isValidPassword(password: String): Boolean {

        val db = AppDatabase.getInstance(this)
        val userDatabaseInterface = UserRepository(db.userDao())
        val queue = LinkedBlockingQueue<Boolean>()
        Thread {
            queue.add(userDatabaseInterface.isValidPassword(password).first)
        }.start()
        return queue.take()
    }

    /**
     * Fonction qui crée le canal de notification.
     */
    private fun creerCanalNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "medicalinkNotificationChannel"
            val channelName = "canal de notification"
            val channelDescription = "canal de notification pour les notifications de l'application"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            // Enregistrement du canal auprès du gestionnaire de notifications
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Fonction qui affiche la boîte de dialogue de changement d'utilisateur.
     */
    private fun showIntervalleRegulierDialog(context: Context) {
        val dialog = Dialog(context, R.style.RoundedDialog)
        val dialogView =
            LayoutInflater.from(dialog.context).inflate(R.layout.activity_changer_utilisateur, null)
        dialog.setContentView(dialogView)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewChangerUtilisateur)
        recyclerView.layoutManager = LinearLayoutManager(context)

        boutonAjouterProfil = dialog.findViewById(R.id.boutonAjouterProfilChangerUtilisateur)

        // Connection à la base de données
        val db = AppDatabase.getInstance(context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())


        val queue = LinkedBlockingQueue<List<User>>()

        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread {
            val listeUserBDD = userDatabaseInterface.getAllUsers()

            queue.add(listeUserBDD)
        }.start()

        val mesUsers = queue.take()

        // Création de l'adapter pour le RecyclerView
        val adapter = ChangerUtilisateurAdapterR(mesUsers) { clickedUser ->

            val queue = LinkedBlockingQueue<String>()
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

}



