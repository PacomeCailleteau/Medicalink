package dev.mobile.medicalink.fragments.traitements

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.EffetSecondaire
import dev.mobile.medicalink.db.local.repository.EffetSecondaireRepository
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.contacts.ContactsFragment
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue

class AjoutEffetSecondaireFragment: Fragment() {
    private lateinit var annuler: ImageView
    private lateinit var inputNomEffetSecondaire: TextInputEditText
    private lateinit var inputMessageEffetSecondaire: TextInputEditText
    private lateinit var ajouterSupprimerPhoto: ImageView
    private lateinit var previewPhoto: ImageView
    private lateinit var boutonAjouter: AppCompatButton
    private lateinit var chooseFromGalleryLauncher: ActivityResultLauncher<String>



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val view = inflater.inflate(R.layout.fragment_add_effet_secondaire, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())
        val effetSecondaireDatabaseInterface = EffetSecondaireRepository(db.effetSecondaireDao())

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        annuler = view.findViewById(R.id.retour_schema_prise)
        inputNomEffetSecondaire = view.findViewById(R.id.inputNomEffetSecondaire)
        inputMessageEffetSecondaire = view.findViewById(R.id.inputMessageEffetSecondaire)
        ajouterSupprimerPhoto = view.findViewById(R.id.imageGalerie)
        previewPhoto = view.findViewById(R.id.previewPhotoEffetSecondaire)
        boutonAjouter = view.findViewById(R.id.boutonAjouterEffetSecondaire)

        chooseFromGalleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uriImageDepuisGalerie ->
                if (uriImageDepuisGalerie != null) {
                    previewPhoto.setImageURI(uriImageDepuisGalerie)
                    previewPhoto.setPadding(0)

                    ajouterSupprimerPhoto.setImageResource(R.drawable.delete_image)
                    previewPhoto.visibility = View.VISIBLE
                } else {
                    previewPhoto.visibility = View.GONE
                    ajouterSupprimerPhoto.setImageResource(R.drawable.galerie)
                }
            }

        ajouterSupprimerPhoto.setOnClickListener {
            if (ajouterSupprimerPhoto.drawable.constantState == resources.getDrawable(R.drawable.galerie)?.constantState) {
                chooseFromGalleryLauncher.launch("image/*")
            } else {
                ajouterSupprimerPhoto.setImageResource(R.drawable.galerie)
                previewPhoto.visibility = View.GONE
            }
        }

        boutonAjouter.setOnClickListener {
            Thread {
                val uuidEffetSecondaire = UUID.randomUUID().toString()
                saveImageToInternalStorage(
                    view.context,
                    previewPhoto.drawToBitmap(),
                    "${uuidEffetSecondaire}.png")
                val bundle = Bundle()
                effetSecondaireDatabaseInterface.insertEffetSecondaire(EffetSecondaire(
                    userDatabaseInterface.getUsersConnected()[0].uuid,
                    uuidEffetSecondaire,
                    inputNomEffetSecondaire.text.toString(),
                    inputMessageEffetSecondaire.text.toString(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH'h'mm"))
                    ))
                val destinationFragment = ListeEffetsSecondairesFragment()
                destinationFragment.arguments = bundle
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }.start()
        }


        annuler.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, ListeEffetsSecondairesFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }



        return view
    }

    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String {
        // Context.MODE_PRIVATE assure que le fichier est privé à l'application
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            Log.d("filePath", context.getFileStreamPath(fileName).absolutePath)
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return context.getFileStreamPath(fileName).absolutePath
    }
}