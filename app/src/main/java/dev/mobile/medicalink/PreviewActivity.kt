package dev.mobile.medicalink

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class PreviewActivity : AppCompatActivity() {
    private lateinit var buttonTakePicture: Button
    private lateinit var buttonChooseFromGallery: Button
    private lateinit var imagePreview: ImageView
    private lateinit var validateButton: Button

    private var currentPhotoPath: Uri? = null

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var chooseFromGalleryLauncher: ActivityResultLauncher<String>
    private lateinit var validateLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        buttonTakePicture = findViewById(R.id.button_take_picture)
        buttonChooseFromGallery = findViewById(R.id.button_choose_from_gallery)
        imagePreview = findViewById(R.id.image_preview)
        validateButton = findViewById(R.id.validate_button)

        when (intent.getStringExtra("type")) {
            "photo" -> {
                buttonChooseFromGallery.visibility = Button.GONE
                takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                    if (currentPhotoPath != null) {
                        displayImage(currentPhotoPath!!)
                    }else{
                        validateButton.visibility = Button.GONE
                    }
                }

                buttonTakePicture.setOnClickListener {
                    val uri: Uri = createImageFile()
                    currentPhotoPath = uri
                    Log.d("MedicalinkBug", uri.toString())
                    takePictureLauncher.launch(uri)
                }
            }
            "charger" -> {
                buttonTakePicture.visibility = Button.GONE
                chooseFromGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    if (uri != null) {
                        displayImage(uri)
                    }else{
                        validateButton.visibility = Button.GONE
                    }
                }

                buttonChooseFromGallery.setOnClickListener {
                    chooseFromGalleryLauncher.launch("image/*")
                }
            }
        }

        validateButton.setOnClickListener {
            val intent = Intent(this, ListeTraitements::class.java)
            startActivity(intent)
        }

        val uri : Uri = intent.getStringExtra("uri")!!.toUri()
        displayImage(uri)





    }
    private fun displayImage(uri: Uri) {
        imagePreview.setImageURI(uri)
        imagePreview.visibility = ImageView.VISIBLE
        validateButton.visibility = Button.VISIBLE
    }

    private fun createImageFile(): Uri {
        val provider: String = "${applicationContext.packageName}.fileprovider"
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"

        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            cacheDir      /* directory */
        ).apply {
            createNewFile()
        }

        return FileProvider.getUriForFile(applicationContext, provider, image)
    }
}