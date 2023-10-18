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
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    private lateinit var buttonTakePicture: Button
    private lateinit var buttonChooseFromGallery: Button
    private lateinit var imagePreview: ImageView
    private lateinit var validateButton: Button

    private lateinit var takePictureLauncher: ActivityResultLauncher<Context>
    private lateinit var chooseFromGalleryLauncher: ActivityResultLauncher<String>
    private lateinit var validateLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonTakePicture = findViewById(R.id.button_take_picture)
        buttonChooseFromGallery = findViewById(R.id.button_choose_from_gallery)
        validateButton = findViewById(R.id.validate_button)
        imagePreview = findViewById(R.id.image_preview)

        takePictureLauncher = registerForActivityResult(TakePictureContract()) { uri ->
            if (uri != null) {
                displayImage(uri)
            }else{
                validateButton.visibility = Button.GONE
            }
        }



        chooseFromGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                displayImage(uri)
            }else{
                validateButton.visibility = Button.GONE
            }
        }

        validateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Gérez l'activité de résultat ici
            }
        }

        buttonTakePicture.setOnClickListener {
            takePictureLauncher.launch(this)
        }

        buttonChooseFromGallery.setOnClickListener {
            chooseFromGalleryLauncher.launch("image/*")
        }

        validateButton.setOnClickListener {
            val intent = Intent(this, MainTraitementsActivity::class.java)
            validateLauncher.launch(intent)
        }



    }

    private fun displayImage(uri: Uri) {
        imagePreview.setImageURI(uri)
        imagePreview.visibility = ImageView.VISIBLE
        validateButton.visibility = Button.VISIBLE
    }
}