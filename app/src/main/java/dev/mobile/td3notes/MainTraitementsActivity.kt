package dev.mobile.td3notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dev.mobile.td3notes.R

class MainTraitementsActivity : AppCompatActivity() {
    private lateinit var addTraitementButton: LinearLayout
    private lateinit var traitementsButton: LinearLayout
    private lateinit var journalButton: LinearLayout

    private lateinit var addLauncher: ActivityResultLauncher<Intent>
    private lateinit var traitementLauncher: ActivityResultLauncher<Intent>
    private lateinit var journalLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_traitements) // Assurez-vous d'ajuster le layout à votre fichier XML

        // Placez ici le code pour gérer la vue de l'activité "MainTraitementsActivity"

        // Exemple : Vous pouvez trouver des éléments de vue par leur ID et ajouter des gestionnaires d'événements
        // val someButton = findViewById<Button>(R.id.some_button_id)
        // someButton.setOnClickListener {
        //     // Votre code ici
        // }
        addTraitementButton = findViewById(R.id.cardaddtraitements)
        traitementsButton = findViewById(R.id.cardtraitements)
        journalButton = findViewById(R.id.cardjournal)


        addLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Gérez l'activité de résultat ici
            }
        }

        traitementLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Gérez l'activité de résultat ici
            }
        }

        journalLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Gérez l'activité de résultat ici
            }
        }




        addTraitementButton.setOnClickListener {
            val intent = Intent(this, AddTraitements::class.java)
            addLauncher.launch(intent)
        }

        traitementsButton.setOnClickListener {
            val intent = Intent(this, ListeTraitements::class.java)
            traitementLauncher.launch(intent)
        }

        journalButton.setOnClickListener {
            val intent = Intent(this, ListeEffetsSecondaires::class.java)
            journalLauncher.launch(intent)
        }


    }
}