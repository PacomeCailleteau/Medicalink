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