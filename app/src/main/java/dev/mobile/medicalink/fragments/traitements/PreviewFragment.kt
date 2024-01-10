package dev.mobile.medicalink.fragments.traitements

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dev.mobile.medicalink.R
import dev.mobile.medicalink.utils.ModelOCR
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date


class PreviewFragment : Fragment() {
    private lateinit var buttonTakePicture: Button
    private lateinit var buttonChooseFromGallery: Button
    private lateinit var imagePreview: ImageView
    private lateinit var validateButton: Button

    private var currentPhotoPath: Uri? = null

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var chooseFromGalleryLauncher: ActivityResultLauncher<String>

    private lateinit var retour: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_preview, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        retour = view.findViewById(R.id.annulerPreview)

        buttonTakePicture = view.findViewById(R.id.button_take_picture)
        buttonChooseFromGallery = view.findViewById(R.id.button_choose_from_gallery)
        imagePreview = view.findViewById(R.id.image_preview)
        validateButton = view.findViewById(R.id.validate_button)

        val data = arguments?.getString("uri")

        val uri: Uri = data!!.toUri()
        displayImage(uri)

        //Appel de la fonction pour extraire le texte
        processImageAndExtractText(uri)

        when (arguments?.getString("type")) {
            "photo" -> {
                buttonChooseFromGallery.visibility = Button.GONE
                takePictureLauncher =
                    registerForActivityResult(ActivityResultContracts.TakePicture()) {
                        if (currentPhotoPath != null) {
                            displayImage(currentPhotoPath!!)
                        } else {
                            validateButton.visibility = Button.GONE
                        }
                    }

                buttonTakePicture.setOnClickListener {
                    val uriImageAppareilPhoto: Uri = createImageFile()
                    currentPhotoPath = uriImageAppareilPhoto
                    takePictureLauncher.launch(uriImageAppareilPhoto)
                }
            }

            "charger" -> {
                buttonTakePicture.visibility = Button.GONE
                chooseFromGalleryLauncher =
                    registerForActivityResult(ActivityResultContracts.GetContent()) { uriImageDepuisGalerie ->
                        if (uriImageDepuisGalerie != null) {
                            displayImage(uriImageDepuisGalerie)
                        } else {
                            validateButton.visibility = Button.GONE
                        }
                    }

                buttonChooseFromGallery.setOnClickListener {
                    chooseFromGalleryLauncher.launch("image/*")
                }
            }
        }

        //=================================================================================================================================//
        //               On met le listener sur le bouton valider dans la fonction processImageAndExtractText                              //
        //=================================================================================================================================//

        //Retour à la page précédente (AddTraitementsFragment)
        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, AddTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

    private fun displayImage(uri: Uri) {
        imagePreview.setImageURI(uri)
        imagePreview.visibility = ImageView.VISIBLE
        validateButton.visibility = Button.VISIBLE
    }

    private fun createImageFile(): Uri {
        val provider = "${view?.context?.applicationContext?.packageName}.fileprovider"
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"

        val context = view?.context
            ?: return Uri.EMPTY  // Si le contexte est nul, renvoyez une valeur par défaut

        val cacheDir = context.cacheDir

        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            cacheDir      /* directory */
        ).apply {
            createNewFile()
        }

        return FileProvider.getUriForFile(view?.context!!.applicationContext, provider, image)
    }


    //=========================================================================================//

    private fun extractTextFromImage(bitmap: Bitmap, onTextExtracted: (String) -> Unit) {
        val textRecognizer: TextRecognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val image = InputImage.fromBitmap(bitmap, 0)

        textRecognizer.process(image)
            .addOnSuccessListener { texts ->
                val extractedText = processTextRecognitionResult(texts)
                onTextExtracted(extractedText)
            }
            .addOnFailureListener { e ->
                // Gérer les erreurs ici
            }
    }

    private fun processTextRecognitionResult(texts: Text): String {
        val result = StringBuilder()
        for (block in texts.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    result.append(element.text).append(" ")
                }
                result.append("\n")
            }
        }
        return result.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processImageAndExtractText(uri: Uri): Boolean {
        // Convertir l'URI de l'image en Bitmap
        val inputStream: InputStream? = context?.contentResolver?.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        return if (bitmap == null) {
            false
        } else {
            extractTextFromImage(bitmap) {
                val text = it
                validateButton.setOnClickListener {
                    val destination = LoaderFragment()
                    //On ajoute le texte à l'argument
                    val bundle = Bundle()
                    bundle.putString("texte", text)
                    destination.arguments = bundle
                    //On appelle le parent pour changer de fragment
                    val fragTransaction = parentFragmentManager.beginTransaction()
                    fragTransaction.replace(R.id.FL, destination)
                    fragTransaction.addToBackStack(null)
                    fragTransaction.commit()
                }
            }
            true
        }
    }


}


