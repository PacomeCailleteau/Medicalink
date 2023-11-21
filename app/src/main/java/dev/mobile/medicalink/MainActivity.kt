package dev.mobile.medicalink

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
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
import dev.mobile.medicalink.utils.NotificationService

class MainActivity : AppCompatActivity() {
    private lateinit var imageConnexion: ImageView
    private lateinit var textBienvenue: TextView
    private lateinit var buttonConnexion: Button
    private lateinit var buttonChangerUtilisateur: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Dans la méthode `onCreate` de votre MainActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channelName = "Nom du canal"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.cloche)
                .setContentTitle("Notification immédiate")
                .setContentText("Cette notification est envoyée immédiatement.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(1, notificationBuilder.build())

            // Dans la méthode `onCreate` de votre MainActivity
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val notificationIntent = Intent(this, NotificationService::class.java)
            //val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            // Calculer le temps actuel + 1 minutes
            val currentTimeMillis = System.currentTimeMillis()
            val oneMinutesMillis = 6000
            val futureTimeMillis = currentTimeMillis + oneMinutesMillis

            //alarmManager.set(AlarmManager.RTC_WAKEUP, futureTimeMillis, pendingIntent)

        }


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

}