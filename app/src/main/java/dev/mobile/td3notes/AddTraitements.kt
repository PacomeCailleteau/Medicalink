package dev.mobile.td3notes

import android.annotation.SuppressLint
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
    private lateinit var annuler : ImageView

    private lateinit var photoLauncher: ActivityResultLauncher<Context>
    private lateinit var loadLauncher: ActivityResultLauncher<String>
    private lateinit var manualImportLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_traitements)


        photoButton = findViewById(R.id.photoButton)
        loadButton = findViewById(R.id.loadButton)
        manualImportButton = findViewById(R.id.manualImportButton)
        annuler = findViewById<ImageView>(R.id.annulerAddTraitement)

        photoLauncher = registerForActivityResult(TakePictureContract()) { uri ->
            if (uri != null) {
                startActivity(Intent(this, PreviewActivity::class.java)
                    .putExtra("uri", uri.toString())
                    .putExtra("type", "photo"))
            }else{
                null
            }
        }

        loadLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                startActivity(Intent(this, PreviewActivity::class.java)
                    .putExtra("uri", uri.toString())
                    .putExtra("type", "charger"))
            }else{
                null
            }
        }

        photoButton.setOnClickListener {
            photoLauncher.launch(this)
        }

        loadButton.setOnClickListener {
            loadLauncher.launch("image/*")
        }

        annuler.setOnClickListener {
            finish()
        }




    }
}