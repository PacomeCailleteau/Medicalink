package dev.mobile.medicalink.fragments.traitements

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Contact
import dev.mobile.medicalink.db.local.entity.EffetSecondaire
import dev.mobile.medicalink.db.local.repository.CisBdpmRepository
import dev.mobile.medicalink.db.local.repository.CisCompoBdpmRepository

class InfoEffetSecondaireFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info_effet_secondaire, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val cisCompoDatabaseInterface = CisCompoBdpmRepository(db.cisCompoBdpmDao())
        val cisBdpmDatabaseInterface = CisBdpmRepository(db.cisBdpmDao())

        val effetSecondaire = requireArguments().getSerializable("effetSecondaire") as EffetSecondaire

        val retour = view.findViewById<ImageView>(R.id.retour_info_effet_secondaire)

        retour.setOnClickListener {
            val destinationFragment = ListeEffetsSecondairesFragment()
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        val nomEffetSecondaire = view.findViewById<TextView>(R.id.nomInfoEffetSecondaire)
        val messageEffetSecondaire = view.findViewById<TextView>(R.id.messageInfoEffetSecondaire)
        val previewPhoto = view.findViewById<ImageView>(R.id.previewPhotoInfoEffetSecondaire)
        val dateEffetSecondaire = view.findViewById<TextView>(R.id.dateInfoEffetSecondaire)
        val supprimerEffetSecondaire = view.findViewById<AppCompatButton>(R.id.boutonSupprimerEffetSecondaire)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        supprimerEffetSecondaire.setOnClickListener {
            Thread {
                db.effetSecondaireDao().delete(effetSecondaire)
                val destinationFragment = ListeEffetsSecondairesFragment()
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }.start()
        }

        Thread {
            activity?.runOnUiThread {
                nomEffetSecondaire.text = effetSecondaire.titre
                messageEffetSecondaire.text = effetSecondaire.message

                val bitmap = BitmapFactory.decodeFile(context?.filesDir?.path + "/" + effetSecondaire.uuidEffetSecondaire + ".png")
                if (bitmap == null) {
                    previewPhoto.visibility = View.GONE
                } else {
                    val path = BitmapDrawable(context?.resources, bitmap)
                    previewPhoto.setImageDrawable(path)
                }
                dateEffetSecondaire.text = effetSecondaire.date
            }
        }.start()

        return view
    }
}
