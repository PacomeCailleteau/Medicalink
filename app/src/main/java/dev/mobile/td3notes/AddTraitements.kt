package dev.mobile.td3notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts

class AddTraitements : AppCompatActivity() {
    private lateinit var photoButton: Button
    private lateinit var loadButton: Button
    private lateinit var manualImportButton: Button

    private lateinit var photoLauncher: ActivityResultLauncher<Intent>
    private lateinit var loadLauncher: ActivityResultLauncher<Intent>
    private lateinit var manualImportLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_traitements)


        photoButton = findViewById(R.id.photoButton)
        loadButton = findViewById(R.id.loadButton)
        manualImportButton = findViewById(R.id.manualImportButton)

        photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Gérez l'activité de résultat ici
            }
        }

        photoButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            photoLauncher.launch(intent)
        }




    }
}