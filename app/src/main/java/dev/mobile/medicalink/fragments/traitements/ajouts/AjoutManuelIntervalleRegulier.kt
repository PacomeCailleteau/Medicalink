package dev.mobile.medicalink.fragments.traitements.ajouts

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.EnumFrequence
import dev.mobile.medicalink.fragments.traitements.EnumFrequence.Companion.getStringFromEnum
import dev.mobile.medicalink.utils.GoTo


class AjoutManuelIntervalleRegulier : Fragment() {

    private lateinit var inputIntervalle: TextView
    private lateinit var retour: ImageView
    private lateinit var suivant: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.fragment_ajout_manuel_intervalle_regulier, container, false)

        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        inputIntervalle = view.findViewById(R.id.inputIntervalle)
        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        var dosage = viewModel.dosageNb.value ?: 2
        if (dosage == 0) {
            dosage = 2
            viewModel.setDosageNb(2)
        }

        var freq = viewModel.frequencePrise.value ?: EnumFrequence.JOUR
        if (freq == EnumFrequence.QUOTIDIEN || freq == EnumFrequence.AUBESOIN) {
            freq = EnumFrequence.JOUR
            viewModel.setFrequencePrise(EnumFrequence.JOUR)
        }

        updateTextViewIntervalleRegulier(dosage, getStringFromEnum(freq, requireContext()))

        inputIntervalle.setOnClickListener {
            showIntervalleRegulierDialog(viewModel, view.context)
        }

        suivant.setOnClickListener {
            GoTo.fragment(AjoutManuelSchemaPrise2Fragment(), parentFragmentManager)
        }

        retour.setOnClickListener {
            GoTo.fragment(AjoutManuelSchemaPriseFragment(), parentFragmentManager)
        }
        return view
    }

    /**
     * Fonction gérant la création et l'affichage de la dialog view s'affichant lors de la sélection
     * de l'intervalle
     */

    private fun showIntervalleRegulierDialog(viewModel: AjoutSharedViewModel, context: Context) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_intervalle_regulier, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val intervalleRegulierDialog = builder.create()

        val firstNumberPicker = dialogView.findViewById<NumberPicker>(R.id.firstNumberPicker)
        val secondNumberPicker = dialogView.findViewById<NumberPicker>(R.id.secondNumberPicker)
        val annulerButton = dialogView.findViewById<Button>(R.id.sauterButton)
        val okButton = dialogView.findViewById<Button>(R.id.prendreButton)

        firstNumberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        secondNumberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        // Configuration du deuxième NumberPicker (jours, semaines, mois)
        secondNumberPicker.displayedValues = arrayOf(
            resources.getString(R.string.jours),
            resources.getString(R.string.semaines),
            resources.getString(R.string.mois)
        )
        secondNumberPicker.minValue = 0
        secondNumberPicker.maxValue = 2
        secondNumberPicker.value = when (viewModel.frequencePrise.value) {
            EnumFrequence.JOUR -> 0
            EnumFrequence.SEMAINE -> 1
            EnumFrequence.MOIS -> 2
            else -> 0
        }

        // Mise à jour des valeurs du premier NumberPicker en fonction de la sélection du deuxième
        updateFirstNumberPickerValues(
            firstNumberPicker,
            secondNumberPicker.value,
            viewModel.dosageNb.value ?: 2
        )

        secondNumberPicker.setOnValueChangedListener { _, _, newVal ->
            updateFirstNumberPickerValues(firstNumberPicker, newVal, firstNumberPicker.value)
        }

        annulerButton.setOnClickListener {
            intervalleRegulierDialog.dismiss()
        }

        okButton.setOnClickListener {
            viewModel.setDosageNb(firstNumberPicker.value)
            val freq = when (secondNumberPicker.value) {
                0 -> EnumFrequence.JOUR
                1 -> EnumFrequence.SEMAINE
                2 -> EnumFrequence.MOIS
                else -> EnumFrequence.JOUR
            }
            viewModel.setFrequencePrise(freq)
            updateTextViewIntervalleRegulier(
                firstNumberPicker.value, getStringFromEnum(
                    freq,
                    requireContext()
                )
            )
            intervalleRegulierDialog.dismiss()
        }
        intervalleRegulierDialog.show()
    }

    private fun updateFirstNumberPickerValues(
        firstNumberPicker: NumberPicker,
        selectedValue: Int,
        currentDosage: Int
    ) {
        when (selectedValue) {
            0 -> {
                firstNumberPicker.minValue = 2
                firstNumberPicker.maxValue = 99
            }

            1 -> {
                firstNumberPicker.minValue = 1
                firstNumberPicker.maxValue = 52
            }

            2 -> {
                firstNumberPicker.minValue = 1
                firstNumberPicker.maxValue = 12
            }
        }

        firstNumberPicker.value =
            currentDosage.coerceIn(firstNumberPicker.minValue, firstNumberPicker.maxValue)
    }

    private fun updateTextViewIntervalleRegulier(
        dosage: Int,
        frequence: String
    ) {
        var s = ""
        s += when (frequence) {
            resources.getString(R.string.semaines) -> resources.getString(R.string.toutes_les)
            else -> resources.getString(R.string.tous_les)
        }
        s += " $dosage "
        s += frequence
        inputIntervalle.text = s
    }

    override fun onResume() {
        super.onResume()

        val callback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                GoTo.fragment(AjoutManuelSchemaPriseFragment(), parentFragmentManager)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}
