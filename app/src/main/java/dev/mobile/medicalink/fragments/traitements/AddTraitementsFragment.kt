package dev.mobile.medicalink.fragments.traitements

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.ajoutmanuel.AjoutManuelSearchFragment
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class AddTraitementsFragment : Fragment() {
    private lateinit var photoButton: LinearLayout
    private lateinit var loadButton: LinearLayout
    private lateinit var manualImportButton: LinearLayout

    private lateinit var annuler: ImageView

    private var currentPhotoPath: Uri? = null

    private lateinit var photoLauncher: ActivityResultLauncher<Uri>
    private lateinit var loadLauncher: ActivityResultLauncher<String>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_traitements, container, false)

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
                    val fragTransaction = parentFragmentManager.beginTransaction()
                    fragTransaction.replace(R.id.FL, destinationFragment)
                    fragTransaction.addToBackStack(null)
                    fragTransaction.commit()
                } else {
                    val fragTransaction = parentFragmentManager.beginTransaction()
                    fragTransaction.replace(R.id.FL, AddTraitementsFragment())
                    fragTransaction.addToBackStack(null)
                    fragTransaction.commit()
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
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            } else {
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, AddTraitementsFragment())
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
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
            val bundle = Bundle()
            /*
            On créer un traitement vide avec des valeurs par défaut que l'on va passer à la
            prochaine vue de création d'un traitement manuellement, et qui va passer
            de vue en vue lors de sa création, en se remplissant au fur et à mesure pour finir complété.

             */
            bundle.putSerializable(
                "traitement",
                Traitement(
                    null,
                    "",
                    2,
                    resources.getString(R.string.semaines),
                    null,
                    resources.getString(R.string.comprime),
                    25,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    LocalDate.now()
                )
            )
            bundle.putString("isAddingTraitement", "true")
            bundle.putString("schema_prise1", "Quotidiennement")
            val destinationFragment = AjoutManuelSearchFragment()
            destinationFragment.arguments = bundle
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        //Retour à la page précédente (MainTraitementsFragment)
        annuler.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, MainTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }


        return view
    }

    /**
     * Fonction utilisé pour générer le fichier de la photo prise par l'utilisateur
     * puis pour la pour stocker dans le cache
     */
    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): Uri {
        val provider = "${view?.context?.applicationContext?.packageName}.fileprovider"
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"

        val context = view?.context
            ?: return Uri.EMPTY  // Si le contexte est nul, renvoie une valeur par défaut

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

    /**
     * Fonction pour tester si la photo a bien été prise et pour éviter les bugs en cas de photo "null"
     */
    private fun testRealImage(uri: Uri): Boolean {
        val bit = BitmapFactory.decodeStream(context?.contentResolver?.openInputStream(uri))
        return bit != null
    }

}
