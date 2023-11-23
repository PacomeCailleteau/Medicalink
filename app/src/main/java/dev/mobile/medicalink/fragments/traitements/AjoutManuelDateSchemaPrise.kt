package dev.mobile.medicalink.fragments.traitements

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.widget.Button
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class AjoutManuelDateSchemaPrise : Fragment() {

    private lateinit var debutAjd: Button
    private lateinit var debutDate: Button
    private lateinit var finSF: Button
    private lateinit var finDate: Button
    private lateinit var inputDateDeDebut: TextInputEditText
    private lateinit var inputDateDeFin: TextInputEditText
    private var dureePriseDbt: String? = null
    private var dureePriseFin: String? = null
    private var dateDeDebut: LocalDate? = null
    private var dateDeFin: LocalDate? = null

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
        dureePriseDbt = arguments?.getString("dureePriseDbt")
        dureePriseFin = arguments?.getString("dureePriseFin")


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
                inputDateDeDebut.visibility = View.GONE
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

        inputDateDeDebut.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_CLASS_TEXT
        inputDateDeDebut.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            source?.let {
                if (it.contains("\n")) {
                    // Bloquer le collage de texte
                    return@InputFilter ""
                }
            }
            null
        })

        inputDateDeFin.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_CLASS_TEXT
        inputDateDeFin.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            source?.let {
                if (it.contains("\n")) {
                    // Bloquer le collage de texte
                    return@InputFilter ""
                }
            }
            null
        })

        debutAjd.setOnClickListener {
            debutAjd.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            debutDate.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            inputDateDeDebut.visibility = View.GONE
            suivant.alpha = 1f
            suivant.isEnabled = true
            inputDateDeDebut.text = null
            dureePriseDbt = "ajd"
        }

        debutDate.setOnClickListener {
            inputDateDeDebut.visibility = View.VISIBLE
            debutAjd.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            debutDate.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            showDatePicker(inputDateDeDebut)
            dureePriseDbt = "date"

        }

        finSF.setOnClickListener {
            finSF.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            finDate.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            inputDateDeFin.visibility = View.GONE
            suivant.alpha = 1f
            suivant.isEnabled = true
            inputDateDeFin.text = null
            dureePriseFin = "sf"
            dateFinDeTraitement=null

        }

        finDate.setOnClickListener {
            inputDateDeFin.visibility = View.VISIBLE
            finSF.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            finDate.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            showDatePicker(inputDateDeFin)
            dureePriseFin = "date"
            dateFinDeTraitement=LocalDate.now()
        }

        inputDateDeDebut.setOnClickListener {
            showDatePicker(inputDateDeDebut)
        }

        inputDateDeFin.setOnClickListener {
            showDatePicker(inputDateDeFin)
        }

        suivant.setOnClickListener {
            val bundle = Bundle()
            //bundle.putSerializable("newTraitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,dateFinDeTraitement,traitement.typeComprime,25,false,null,traitement.prises))
            //bundle.putString("isAddingTraitement", "true")

            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement, traitement.dosageNb,traitement.dosageUnite,dateFinDeTraitement,traitement.typeComprime,25,false,null,traitement.prises,traitement.totalQuantite))
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
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,dateFinDeTraitement,traitement.typeComprime,25,false,null,traitement.prises,traitement.totalQuantite))
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateSuivantButtonStatus() {
        // Vérifier si une date de début est sélectionnée
        if (dureePriseDbt == "date" && dateDeDebut != null) {
            // Si une date de fin est également sélectionnée
            if (dureePriseFin == "date" && dateDeFin != null) {
                // Vérifier si la date de fin est supérieure à la date de début
                if (dateDeFin!!.isAfter(dateDeDebut)) {
                    // Les conditions sont remplies, le bouton peut être activé
                    suivant.isEnabled = true
                    suivant.setBackgroundResource(R.drawable.rounded_darker_blue_button_no_stroke_background)
                } else {
                    // La date de fin n'est pas supérieure à la date de début, désactiver le bouton
                    suivant.isEnabled = false
                    suivant.alpha = 0.3f
                }
            } else {
                // Pas de date de fin sélectionnée, activer le bouton
                suivant.isEnabled = true
                suivant.setBackgroundResource(R.drawable.rounded_darker_blue_button_no_stroke_background)
            }
        } else if (dureePriseFin == "date" && dateDeFin != null) {
            // Pas de date de début sélectionnée, mais une date de fin est sélectionnée
            suivant.isEnabled = true
            suivant.setBackgroundResource(R.drawable.rounded_darker_blue_button_no_stroke_background)
        } else {
            // Aucune des conditions n'est remplie, désactiver le bouton
            suivant.isEnabled = false
            suivant.alpha = 0.3f
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker(element: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Définissez la date minimale à partir de demain
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val minDate = calendar.timeInMillis

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // Mettez à jour la valeur de input_date_de_debut avec la date sélectionnée
                val selectedDate = formatDate(day, month, year)
                element.setText(selectedDate)

                // Mettez à jour la variable de dateDeDebut ou dateDeFin en fonction de l'élément
                if (element == inputDateDeDebut) {
                    dateDeDebut = LocalDate.of(year, month + 1, day)
                } else if (element == inputDateDeFin) {
                    dateDeFin = LocalDate.of(year, month + 1, day)
                }

                // Vérifiez les conditions et mettez à jour le statut du bouton Suivant
                updateSuivantButtonStatus()
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Limitez la sélection aux dates supérieures ou égales à la date de demain
        datePickerDialog.datePicker.minDate = minDate

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