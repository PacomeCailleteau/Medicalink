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
import android.graphics.Bitmap
import android.os.Build
import android.widget.Button
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class AjoutManuelIntervalleRegulier : Fragment() {

    private lateinit var inputIntervalle: TextInputEditText
    private lateinit var retour: ImageView
    private lateinit var suivant : Button


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_intervalle_regulier, container, false)

        inputIntervalle = view.findViewById(R.id.inputIntervalle)
        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        val traitement = arguments?.getSerializable("traitement") as Traitement
        var schema_prise1  = arguments?.getString("schema_prise1")
        var dureePriseDbt = arguments?.getString("dureePriseDbt")
        var dureePriseFin = arguments?.getString("dureePriseFin")



        suivant.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,null,traitement.typeComprime,25,false,null,traitement.prises))
            bundle.putString("provenance", "intervalleRegulier")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")

            val destinationFragment = AjoutManuelSchemaPrise2Fragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }



        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable("addTraitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,null,traitement.typeComprime,25,false,null,traitement.prises))
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelSchemaPriseFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
        return view
    }
}