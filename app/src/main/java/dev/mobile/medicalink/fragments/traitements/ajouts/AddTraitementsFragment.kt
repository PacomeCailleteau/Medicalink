package dev.mobile.medicalink.fragments.traitements.ajouts

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.MainTraitementsFragment
import dev.mobile.medicalink.utils.GoTo
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddTraitementsFragment : Fragment() {
    private lateinit var photoButton: LinearLayout
    private lateinit var loadButton: LinearLayout
    private lateinit var manualImportButton: LinearLayout

    private lateinit var annuler: ImageView

    private var currentPhotoPath: Uri? = null

    private lateinit var photoLauncher: ActivityResultLauncher<Uri>
    private lateinit var loadLauncher: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_traitements, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.VISIBLE
        }

        photoButton = view.findViewById(R.id.cardphoto)
        loadButton = view.findViewById(R.id.cardload)
        manualImportButton = view.findViewById(R.id.cardaddmanually)

        manualImportButton = view.findViewById(R.id.cardaddmanually)
        annuler = view.findViewById(R.id.annulerAddTraitement)

        //Gestion de la redirection après un clic sur l'un des boutons

        //Redirection vers le module de capture photo
        photoLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { _ -> //was succcess
                // Utilise le chemin de l'image capturée (currentPhotoPath)
                Log.d("photoPath", currentPhotoPath.toString())
                // Le chemin n'étant jamais null, on utilise la fonction testRealImage qui nous renvoie un boolean (vrai si la photo n'est pas null)
                val trueImage = testRealImage(currentPhotoPath!!)
                if (trueImage) {
                    //On appelle le parent pour changer de fragment
                    val bundle = Bundle()
                    bundle.putString("uri", currentPhotoPath.toString())
                    bundle.putString("type", "photo")
                    val destinationFragment = PreviewFragment()
                    destinationFragment.arguments = bundle
                    GoTo.fragment(destinationFragment, parentFragmentManager)
                } else {
                    GoTo.fragment(AddTraitementsFragment(), parentFragmentManager)
                }
            }

        //Redirection vers le module d'import depuis la galerie du téléphone
        loadLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val bundle = Bundle()
                bundle.putString("uri", uri.toString())
                bundle.putString("type", "charger")
                val destinationFragment = PreviewFragment()
                destinationFragment.arguments = bundle
                GoTo.fragment(destinationFragment, parentFragmentManager)
            } else {
                GoTo.fragment(AddTraitementsFragment(), parentFragmentManager)
            }
        }

        //Gestion du clic sur le bouton "prendre en photo l'ordonnance"
        photoButton.setOnClickListener {
            val uri: Uri = createImageFile()
            currentPhotoPath = uri
            photoLauncher.launch(uri)
        }

        //Gestion du clic sur le bouton "charger depuis la gallerie"
        loadButton.setOnClickListener {
            loadLauncher.launch("image/*")
        }

        //Gestion du clic sur le bouton "import manuel d'un traitement"
        manualImportButton.setOnClickListener {
            viewModel.setIsAddingTraitement(true)
            GoTo.fragment(AjoutManuelSearchFragment(), parentFragmentManager)
        }

        //Retour à la page précédente (MainTraitementsFragment)
        annuler.setOnClickListener {
            GoTo.fragment(MainTraitementsFragment(), parentFragmentManager)
        }
        return view
    }

    /**
     * Fonction utilisé pour générer le fichier de la photo prise par l'utilisateur
     * puis pour la pour stocker dans le cache
     * @return Uri
     */
    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): Uri {

        val context = view?.context
            ?: return Uri.EMPTY  // Si le contexte est nul, renvoie une valeur par défaut

        val cacheDir = context.cacheDir

        val image = File.createTempFile(
            "JPEG_" + SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + "_", /* prefix */
            ".jpg", /* suffix */
            cacheDir      /* directory */
        ).apply {
            createNewFile()
        }

        return FileProvider.getUriForFile(
            view?.context!!.applicationContext,
            "${view?.context?.applicationContext?.packageName}.fileprovider",
            image
        )
    }

    /**
     * Fonction pour tester si la photo a bien été prise et pour éviter les bugs en cas de photo "null"
     * @param uri Uri
     * @return Boolean
     */
    private fun testRealImage(uri: Uri): Boolean {
        val bit = BitmapFactory.decodeStream(context?.contentResolver?.openInputStream(uri))
        return bit != null
    }

}
