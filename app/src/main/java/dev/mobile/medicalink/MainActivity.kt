package dev.mobile.medicalink

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import dev.mobile.medicalink.fragments.MainFragment
import dev.mobile.medicalink.fragments.home.ChangerUtilisateur
import dev.mobile.medicalink.utils.NotificationService

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

        buttonConnexion.setOnClickListener {
            val intent = Intent(this, MainFragment::class.java)
            startActivity(intent)
        }

        buttonChangerUtilisateur.setOnClickListener {
            val intent = Intent(this, CreerProfilActivity::class.java)
            startActivity(intent)
        }
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



