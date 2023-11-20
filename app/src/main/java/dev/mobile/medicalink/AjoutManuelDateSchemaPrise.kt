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
import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


class AjoutManuelDateSchemaPrise : Fragment() {

    private lateinit var debutAjd: Button
    private lateinit var debutDate: Button
    private lateinit var finSF: Button
    private lateinit var finDate: Button

    private lateinit var retour: ImageView
    private lateinit var suivant : Button


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_date_schema_prise, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        debutAjd = view.findViewById(R.id.debutAjd)
        debutDate = view.findViewById(R.id.debutDate)
        finSF = view.findViewById(R.id.finSF)
        finDate = view.findViewById(R.id.finDate)



        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        val traitement = arguments?.getSerializable("traitement") as Traitement
        var schema_prise1  = arguments?.getString("schema_prise1")
        var provenance  = arguments?.getString("provenance")
        var dureePriseDbt = arguments?.getString("dureePriseDbt")
        var dureePriseFin = arguments?.getString("dureePriseFin")

        if (dureePriseDbt==null){
            dureePriseDbt="ajd"
        }
        if (dureePriseFin==null){
            dureePriseFin="date"
        }
        var dateFinDeTraitement : LocalDate? = null
        when (dureePriseDbt) {
            "ajd" -> {
                debutAjd.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
                debutDate.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
                dateFinDeTraitement=LocalDate.now()
            }

            "date" -> {
                debutAjd.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
                debutDate.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            }
        }
        when (dureePriseFin) {
            "sf" -> {
                finSF.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
                finDate.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            }

            "date" -> {
                finSF.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
                finDate.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            }
        }


        debutAjd.setOnClickListener {
            debutAjd.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            debutDate.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            dureePriseDbt = "ajd"
        }

        debutDate.setOnClickListener {
            debutAjd.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            debutDate.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            dureePriseDbt = "date"

        }

        finSF.setOnClickListener {
            finSF.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            finDate.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            dureePriseFin = "sf"
            dateFinDeTraitement=null

        }

        finDate.setOnClickListener {
            finSF.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            finDate.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            dureePriseFin = "date"
            dateFinDeTraitement=LocalDate.now()
        }

        suivant.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("newTraitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,dateFinDeTraitement,traitement.typeComprime,25,false,null,traitement.prises))
            bundle.putString("isAddingTraitement", "true")/*

            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            */

            val destinationFragment = ListeTraitementsFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }



        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,dateFinDeTraitement,traitement.typeComprime,25,false,null,traitement.prises))
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelSchemaPrise2Fragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
        return view
    }
}