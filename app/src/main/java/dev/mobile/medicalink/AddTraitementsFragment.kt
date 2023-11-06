package dev.mobile.medicalink

import android.app.Activity
import android.content.Intent
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
    private lateinit var addManuallyLauncher: ActivityResultLauncher<Intent>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_traitements, container, false)

        // Get the intent that started this activity TODO : Pacôme has to check if it's the right way to do it
        val intent = activity?.intent!!

        photoButton = view.findViewById(R.id.cardphoto)
        loadButton = view.findViewById(R.id.cardload)
        manualImportButton = view.findViewById(R.id.cardaddmanually)

        manualImportButton = view.findViewById(R.id.cardaddmanually)
        annuler = view.findViewById<ImageView>(R.id.annulerAddTraitement)

        photoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            // Utilise le chemin de l'image capturée (currentPhotoPath)
            if (currentPhotoPath != null) {
                // L'image a été capturée avec succès, tu peux utiliser currentPhotoPath ici
                // Ensuite, lance une autre activité pour afficher l'image ou effectuer d'autres actions

            } else {
                null
            }
        }

        loadLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
            }else{
                null
            }
        }

        addManuallyLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Gérez l'activité de résultat ici
            }
        }




        photoButton.setOnClickListener {

        }

        loadButton.setOnClickListener {
            loadLauncher.launch("image/*")
        }

        //TODO: Ajouter un traitement manuellement en fragment
        manualImportButton.setOnClickListener {

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

}