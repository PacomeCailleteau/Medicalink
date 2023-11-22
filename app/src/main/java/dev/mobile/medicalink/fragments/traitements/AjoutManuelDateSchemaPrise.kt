package dev.mobile.medicalink.fragments.traitements

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.os.Build
import android.widget.Button
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale


class AjoutManuelDateSchemaPrise : Fragment() {

    private lateinit var debutAjd: Button
    private lateinit var debutDate: Button
    private lateinit var finSF: Button
    private lateinit var finDate: Button
    private lateinit var inputDateDeDebut: TextInputEditText
    private lateinit var inputDateDeFin: TextInputEditText

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
        inputDateDeDebut = view.findViewById(R.id.input_date_de_debut)
        inputDateDeFin = view.findViewById(R.id.input_date_de_fin)



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


        debutDate.setOnClickListener {
            showDatePicker()
        }

        finDate.setOnClickListener {
            showDatePicker()
        }

        debutAjd.setOnClickListener {
            debutAjd.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            inputDateDeDebut.visibility = View.GONE
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
            //bundle.putSerializable("newTraitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,dateFinDeTraitement,traitement.typeComprime,25,false,null,traitement.prises))
            //bundle.putString("isAddingTraitement", "true")
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement, traitement.dosageNb,traitement.dosageUnite,dateFinDeTraitement,traitement.typeComprime,25,false,null,traitement.prises))
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")


            val destinationFragment = AjoutManuelStock()
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

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                // Mettez à jour la valeur de input_date_de_debut avec la date sélectionnée
                val selectedDate = formatDate(day, month, year)
                inputDateDeDebut.setText(selectedDate)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Afficher le sélecteur de date
        datePickerDialog.show()
    }

    private fun formatDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}