package dev.mobile.medicalink

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.MainFragment

class MainActivity : AppCompatActivity() {
    private lateinit var imageConnexion: ImageView
    private lateinit var textBienvenue: TextView
    private lateinit var buttonConnexion: Button
    private lateinit var buttonChangerUtilisateur: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        creerCanalNotification()


        //OMG ÇA MARCHE ET C'EST TROP COOL
        val notificationIntent = Intent(this, NotificationService::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        // Définissez le délai en millisecondes (par exemple, 10 secondes)
        val delayMillis = 10000

        // Configurez l'alarme avec le délai
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + delayMillis,
            pendingIntent
        )
        //FIN DU OMG ÇA MARCHE ET C'EST TROP COOL
*/

        //masquer la barre de titre
        supportActionBar?.hide()

        imageConnexion = findViewById(R.id.image_connexion)
        textBienvenue = findViewById(R.id.text_bienvenue)
        buttonConnexion = findViewById(R.id.button_connexion)
        buttonChangerUtilisateur = findViewById(R.id.button_changer_utilisateur)

        //Connection à la base de données
        val db = AppDatabase.getInstance(this)
        val userDatabaseInterface = UserRepository(db.userDao())
        Thread {
            //TODO : enlever Pierre Denis pour la version finale
            //On créer un User connecté pour tester
            val user = User("111111","Professionnel","BOUTET","Paul","01/01/2000","pierre.denis@gmail","123456",true)
            userDatabaseInterface.insertUser(user)
            val res = userDatabaseInterface.getUsersConnected()
            if (res.size == 1) {
                //Changement du texte
                val txtBienvenue = resources.getString(R.string.bienvenue) + " " + res[0].prenom + " !"
                textBienvenue.text = txtBienvenue

                //Changement du texte du bouton de "Creer mon profil" à "Me connecter"
                buttonConnexion.text = resources.getString(R.string.me_connecter)

                //On met le bouton "Changer d'utilisateur" visible
                buttonChangerUtilisateur.visibility = View.VISIBLE

                //On met les bons listeners
                buttonConnexion.setOnClickListener {
                    val intent = Intent(this, CreerProfilActivity::class.java)
                    startActivity(intent)
                }
                buttonChangerUtilisateur.setOnClickListener {
                    //TODO("Faire le changement d'utilisateur")
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
        }.start()

    }

    private fun creerCanalNotification() {
        // Créez le canal de notification (pour les API > Oreo donc > 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "soleil123"
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

}



