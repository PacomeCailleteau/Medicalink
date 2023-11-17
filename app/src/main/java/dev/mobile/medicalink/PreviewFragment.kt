package dev.mobile.medicalink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File
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

        val uri : Uri = data!!.toUri()
        displayImage(uri)
        Log.d("test", uri.toString())


        when (arguments?.getString("type")) {
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
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, ListeTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

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
        val provider: String = "${view?.context?.applicationContext?.packageName}.fileprovider"
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"

        val context = view?.context ?: return Uri.EMPTY  // Si le contexte est nul, renvoyez une valeur par défaut

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
}