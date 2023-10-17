package dev.mobile.td3notes

import android.content.Context
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonTakePicture = findViewById(R.id.button_take_picture)
        buttonChooseFromGallery = findViewById(R.id.button_choose_from_gallery)
        validateButton = findViewById(R.id.validate_button)
        imagePreview = findViewById(R.id.image_preview)


        takePictureLauncher = registerForActivityResult(TakePictureContract()) { uri ->
            if (uri != null) {
                imagePreview.setImageURI(uri)
                imagePreview.visibility = ImageView.VISIBLE
                validateButton.visibility = Button.VISIBLE
            }else{
                validateButton.visibility = Button.INVISIBLE
            }
        }

        chooseFromGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imagePreview.setImageURI(uri)
                imagePreview.visibility = ImageView.VISIBLE
                validateButton.visibility = Button.VISIBLE
            }else{
                validateButton.visibility = Button.INVISIBLE
            }
        }

        buttonTakePicture.setOnClickListener {
            takePictureLauncher.launch(this)
        }

        buttonChooseFromGallery.setOnClickListener {
            chooseFromGalleryLauncher.launch("image/*")
        }

    }
}