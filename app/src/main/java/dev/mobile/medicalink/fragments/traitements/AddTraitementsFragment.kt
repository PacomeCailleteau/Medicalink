package dev.mobile.medicalink.fragments.traitements

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import dev.mobile.medicalink.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class AddTraitementsFragment : Fragment() {
    private lateinit var photoButton: LinearLayout
    private lateinit var loadButton: LinearLayout
    private lateinit var manualImportButton: LinearLayout

    private lateinit var annuler : ImageView

    private var currentPhotoPath: Uri? = null

    private lateinit var photoLauncher: ActivityResultLauncher<Uri>
    private lateinit var loadLauncher: ActivityResultLauncher<String>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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

        photoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { _ -> //was succcess
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
                }
            else {
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, AddTraitementsFragment())
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }


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
                }
            else{
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, AddTraitementsFragment())
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        photoButton.setOnClickListener {
            val uri: Uri = createImageFile()
            currentPhotoPath = uri
            photoLauncher.launch(uri)
        }

        // Bouton charger
        loadButton.setOnClickListener {
            loadLauncher.launch("image/*")
        }

        manualImportButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement("",1,"Jour",null,"Comprimé",25,false,null,null,null))
            bundle.putString("schema_prise1", "Quotidiennement")
            val destinationFragment = AjoutManuelSearchFragment()
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


    private fun createImageFile(): Uri {
        val provider = "${view?.context?.applicationContext?.packageName}.fileprovider"
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

    private fun testRealImage(uri: Uri): Boolean {
        val bit = BitmapFactory.decodeStream(context?.contentResolver?.openInputStream(uri))
        return bit != null
    }

}
