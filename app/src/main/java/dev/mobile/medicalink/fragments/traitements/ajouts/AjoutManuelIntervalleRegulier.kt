package dev.mobile.medicalink.fragments.traitements.ajouts

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Traitement


class AjoutManuelIntervalleRegulier : Fragment() {

    private lateinit var inputIntervalle: TextInputEditText
    private lateinit var retour: ImageView
    private lateinit var suivant: Button


    @RequiresApi(Build.VERSION_CODES.O)
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

        inputIntervalle.setText(resources.getString(R.string.toutes_2_semaines))
        inputIntervalle.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_CLASS_TEXT
        inputIntervalle.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            source?.let {
                if (it.contains("\n")) {
                    // Empeche le collage de texte
                    return@InputFilter ""
                }
            }
            null
        })

        inputIntervalle.setOnClickListener {
            showIntervalleRegulierDialog(viewModel, view.context)
        }


        suivant.setOnClickListener {
            val destinationFragment = AjoutManuelSchemaPrise2Fragment()
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        retour.setOnClickListener {
            val destinationFragment = AjoutManuelSchemaPriseFragment()
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
        return view
    }

    /**
     * Fonction gérant la création et l'affichage de la dialog view s'affichant lors de la sélection
     * de l'intervalle
     */
    @RequiresApi(Build.VERSION_CODES.O)
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
        secondNumberPicker.value = when (viewModel.dosageUnite.value) {
            resources.getString(R.string.jours) -> 0
            resources.getString(R.string.semaines) -> 1
            resources.getString(R.string.mois) -> 2
            else -> 0
        }

        // Mise à jour des valeurs du premier NumberPicker en fonction de la sélection du deuxième
        updateFirstNumberPickerValues(
            firstNumberPicker,
            secondNumberPicker.value,
            viewModel.dosageNb.value ?: 1
        )

        secondNumberPicker.setOnValueChangedListener { _, _, newVal ->
            updateFirstNumberPickerValues(firstNumberPicker, newVal, firstNumberPicker.value)
        }

        annulerButton.setOnClickListener {
            intervalleRegulierDialog.dismiss()
        }

        okButton.setOnClickListener {
            viewModel.setDosageNb(firstNumberPicker.value)
            viewModel.setDosageUnite(
                when (secondNumberPicker.value) {
                    0 -> resources.getString(R.string.jours)
                    1 -> resources.getString(R.string.semaines)
                    2 -> resources.getString(R.string.mois)
                    else -> resources.getString(R.string.jour)
            })
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

            else -> {
                firstNumberPicker.minValue = 2
                firstNumberPicker.maxValue = 99
            }
        }

        firstNumberPicker.value =
            currentDosage.coerceIn(firstNumberPicker.minValue, firstNumberPicker.maxValue)
    }


    override fun onResume() {
        super.onResume()

        val callback = object : OnBackPressedCallback(true) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun handleOnBackPressed() {
                val destinationFragment = AjoutManuelSchemaPriseFragment()
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}
