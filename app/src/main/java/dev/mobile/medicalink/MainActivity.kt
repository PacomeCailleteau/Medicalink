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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.MainFragment
import dev.mobile.medicalink.utils.NotificationService
import dev.mobile.medicalink.fragments.traitements.AjoutManuelTypeMedic
import dev.mobile.medicalink.fragments.traitements.AjoutManuelTypeMedicAdapterR
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity() {
    private lateinit var imageConnexion: ImageView
    private lateinit var textBienvenue: TextView
    private lateinit var buttonConnexion: Button
    private lateinit var buttonChangerUtilisateur: Button
    private lateinit var boutonAjouterProfil : Button

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

        NotificationService.sendNotification(this, "Youpi", "Ça rime avec Tchoupi", 5000)

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
                val intent = Intent(this, MainFragment::class.java)
                startActivity(intent)
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



