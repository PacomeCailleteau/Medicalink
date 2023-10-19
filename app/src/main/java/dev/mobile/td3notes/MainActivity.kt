package dev.mobile.td3notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {
    private lateinit var image_connexion: ImageView
    private lateinit var textBienvenue: TextView
    private lateinit var buttonConnexion: Button
    private lateinit var buttonChangerUtilisateur: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image_connexion = findViewById(R.id.image_connexion)
        textBienvenue = findViewById(R.id.texte_bienvenue)
        buttonConnexion = findViewById(R.id.button_connexion)
        buttonChangerUtilisateur = findViewById(R.id.button_changer_utilisateur)

        buttonConnexion.setOnClickListener {
            val intent = Intent(this, AddTraitements::class.java)
            startActivity(intent)
        }

        buttonChangerUtilisateur.setOnClickListener {
            val intent = Intent(this, ListeTraitements::class.java)
            startActivity(intent)
        }
    }

}