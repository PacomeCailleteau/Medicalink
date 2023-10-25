package dev.mobile.medicalink

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.textfield.TextInputEditText

class AjoutManuelSearch : AppCompatActivity() {

    private lateinit var addManuallySearchBar: TextInputEditText
    private lateinit var addManuallyButton: Button

    private lateinit var addManuallyButtonLauncher: ActivityResultLauncher<Intent>

    private lateinit var retour: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajout_manuel_search)

        addManuallySearchBar = findViewById(R.id.add_manually_search_bar)
        addManuallyButton = findViewById(R.id.add_manually_button)

        addManuallyButtonLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Gérez l'activité de résultat ici
            }
        }




        addManuallyButton.setOnClickListener {
            val intent = Intent(this, AjoutManuelSchemaPrise::class.java)
            addManuallyButtonLauncher.launch(intent)
        }

        retour = findViewById(R.id.retour_schema_prise)

        retour.setOnClickListener {
            finish()
        }





    }


}