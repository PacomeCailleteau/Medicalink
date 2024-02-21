package dev.mobile.medicalink.fragments.traitements.ajouts

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.utils.GoTo
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class AjoutManuelDateSchemaPrise : Fragment() {

    private lateinit var finSF: Button
    private lateinit var finDate: Button
    private lateinit var inputDateDeDebut: TextInputEditText
    private lateinit var inputDateDeFin: TextInputEditText


    private lateinit var retour: ImageView
    private lateinit var suivant: Button


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.fragment_ajout_manuel_date_schema_prise, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        finSF = view.findViewById(R.id.finSF)
        finDate = view.findViewById(R.id.finDate)
        inputDateDeDebut = view.findViewById(R.id.input_date_de_debut)
        inputDateDeFin = view.findViewById(R.id.input_date_de_fin)
        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        inputDateDeDebut.inputType =
            InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_CLASS_TEXT

        if (viewModel.dateDbtTraitement.value == null) {
            viewModel.setDateDbtTraitement(LocalDate.now())
        }
        val dateDbt = viewModel.dateDbtTraitement.value
        inputDateDeDebut.setText("${dateDbt?.dayOfMonth}/${dateDbt?.monthValue}/${dateDbt?.year}")

        inputDateDeDebut.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source?.let {
                if (it.contains("\n")) {
                    // Bloquer le collage de texte
                    return@InputFilter ""
                }
            }
            null
        })

        if (viewModel.dateFinTraitement.value == null) {
            finSF.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            finDate.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
        } else {
            finSF.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            finDate.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
        }

        inputDateDeFin.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_CLASS_TEXT
        inputDateDeFin.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source?.let {
                if (it.contains("\n")) {
                    // Bloquer le collage de texte
                    return@InputFilter ""
                }
            }
            null
        })

        finSF.setOnClickListener {
            finSF.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            finDate.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            inputDateDeFin.visibility = View.GONE
            inputDateDeFin.text = null
            viewModel.setDateFinTraitement(null)
        }

        finDate.setOnClickListener {
            inputDateDeFin.visibility = View.VISIBLE
            finSF.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            finDate.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            showDatePicker(inputDateDeFin, viewModel)
        }

        inputDateDeDebut.setOnClickListener {
            showDatePicker(inputDateDeDebut, viewModel)
        }

        inputDateDeFin.setOnClickListener {
            showDatePicker(inputDateDeFin, viewModel)
        }

        suivant.setOnClickListener {
            GoTo.fragment(AjoutManuelStock(), parentFragmentManager)
        }

        retour.setOnClickListener {
            var destinationFragment = Fragment()
            when (viewModel.provenance.value) {
                "quotidiennement" -> {
                    destinationFragment = AjoutManuelSchemaPrise2Fragment()
                }

                "intervalleRegulier" -> {
                    destinationFragment = AjoutManuelSchemaPrise2Fragment()
                }

                "auBesoin" -> {
                    destinationFragment = AjoutManuelSchemaPriseFragment()
                }
            }
            GoTo.fragment(destinationFragment, parentFragmentManager)
        }
        return view
    }

    /**
     * Fonction gérant le date Picker pour sélectionner les dates de début et de fin du traitement
     * @param element L'élément TextInputEditText à modifier
     * @param viewModel Le ViewModel partagé entre les fragments
     */
    private fun showDatePicker(element: TextInputEditText, viewModel: AjoutSharedViewModel) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar[Calendar.YEAR]
        val currentMonth = calendar[Calendar.MONTH]
        val currentDay = calendar[Calendar.DAY_OF_MONTH]

        val minDate = calendar.timeInMillis

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = formatDate(day, month, year)
                element.setText(selectedDate)

                if (element == inputDateDeDebut) {
                    viewModel.setDateDbtTraitement(LocalDate.of(year, month + 1, day))
                } else if (element == inputDateDeFin) {
                    viewModel.setDateFinTraitement(LocalDate.of(year, month + 1, day))
                }
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.show()
    }

    /**
     * Fonction pour formater la date
     * @param day Jour
     * @param month Mois
     * @param year Année
     * @return La date formatée
     */
    private fun formatDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = day
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
