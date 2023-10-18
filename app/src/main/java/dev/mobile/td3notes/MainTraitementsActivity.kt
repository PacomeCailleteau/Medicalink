import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import dev.mobile.td3notes.R

class MainTraitementsActivity : AppCompatActivity() {
    private lateinit var addTraitementButton: Button
    private lateinit var traitementsButton: Button
    private lateinit var journalButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_traitements) // Assurez-vous d'ajuster le layout à votre fichier XML

        // Placez ici le code pour gérer la vue de l'activité "MainTraitementsActivity"

        // Exemple : Vous pouvez trouver des éléments de vue par leur ID et ajouter des gestionnaires d'événements
        // val someButton = findViewById<Button>(R.id.some_button_id)
        // someButton.setOnClickListener {
        //     // Votre code ici
        // }
        addTraitementButton = findViewById(R.id.addTraitementButton)
        traitementsButton = findViewById(R.id.traitementsButton)
        journalButton = findViewById(R.id.journalButton)
    }
}