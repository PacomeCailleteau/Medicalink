package dev.mobile.medicalink.fragments.traitements.ajoutmanuel

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

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        inputIntervalle = view.findViewById(R.id.inputIntervalle)
        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        val traitement = arguments?.getSerializable("traitement") as Traitement
        val isAddingTraitement = arguments?.getString("isAddingTraitement")
        val schema_prise1 = arguments?.getString("schema_prise1")
        val provenance = arguments?.getString("provenance")
        val dureePriseDbt = arguments?.getString("dureePriseDbt")
        val dureePriseFin = arguments?.getString("dureePriseFin")

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
            showIntervalleRegulierDialog(traitement, view.context)
        }


        suivant.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.CodeCIS,
                    traitement.nomTraitement,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    null,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("provenance", "$provenance")
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

            if (isAddingTraitement == "false") {
                val bundle = Bundle()
                bundle.putSerializable(
                    "traitement",
                    Traitement(
                        traitement.CodeCIS,
                        traitement.nomTraitement,
                        traitement.dosageNb,
                        traitement.dosageUnite,
                        null,
                        traitement.typeComprime,
                        traitement.comprimesRestants,
                        false,
                        null,
                        traitement.prises,
                        traitement.totalQuantite,
                        traitement.UUID,
                        traitement.UUIDUSER,
                        traitement.dateDbtTraitement
                    )
                )
                bundle.putString("isAddingTraitement", "$isAddingTraitement")
                bundle.putString("schema_prise1", "$schema_prise1")
                bundle.putString("dureePriseDbt", "$dureePriseDbt")
                bundle.putString("dureePriseFin", "$dureePriseFin")
                val destinationFragment = AjoutManuelRecapitulatif()
                destinationFragment.arguments = bundle
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
                return@setOnClickListener
            }

            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.CodeCIS,
                    traitement.nomTraitement,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    null,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
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

    /**
     * Fonction gérant la création et l'affichage de la dialog view s'affichant lors de la sélection
     * de l'intervalle
     */
    private fun showIntervalleRegulierDialog(traitement: Traitement, context: Context) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_intervalle_regulier, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val intervalleRegulierDialog = builder.create()

        val firstNumberPicker = dialogView.findViewById<NumberPicker>(R.id.firstNumberPicker)
        val secondNumberPicker = dialogView.findViewById<NumberPicker>(R.id.secondNumberPicker)
        val annulerButton = dialogView.findViewById<Button>(R.id.annulerButton)
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
        secondNumberPicker.value = when (traitement.dosageUnite) {
            resources.getString(R.string.jours) -> 0
            resources.getString(R.string.semaines) -> 1
            resources.getString(R.string.mois) -> 2
            else -> 0
        }

        // Mise à jour des valeurs du premier NumberPicker en fonction de la sélection du deuxième
        updateFirstNumberPickerValues(
            firstNumberPicker,
            secondNumberPicker.value,
            traitement.dosageNb
        )

        secondNumberPicker.setOnValueChangedListener { _, _, newVal ->
            updateFirstNumberPickerValues(firstNumberPicker, newVal, traitement.dosageNb)
        }

        annulerButton.setOnClickListener {
            intervalleRegulierDialog.dismiss()
        }

        okButton.setOnClickListener {
            traitement.dosageNb = firstNumberPicker.value
            traitement.dosageUnite = when (secondNumberPicker.value) {
                0 -> resources.getString(R.string.jours)
                1 -> resources.getString(R.string.semaines)
                2 -> resources.getString(R.string.mois)
                else -> resources.getString(R.string.jour)
            }

            //Gestion des variantes possible du français avec le féminin/masculin et pluriel/singulier
            if (traitement.dosageNb == 1 && traitement.dosageUnite == resources.getString(R.string.semaines)) {
                inputIntervalle.setText("${resources.getString(R.string.toutes_les)} ${traitement.dosageUnite}")
            } else if (traitement.dosageNb > 1 && traitement.dosageUnite == resources.getString(R.string.semaines)) {
                inputIntervalle.setText("${resources.getString(R.string.toutes_les)} ${traitement.dosageNb} ${traitement.dosageUnite}")
            } else if (traitement.dosageNb == 1 && traitement.dosageUnite == resources.getString(R.string.mois)) {
                inputIntervalle.setText("${resources.getString(R.string.tous_les)} ${traitement.dosageUnite}")
            } else if (traitement.dosageNb > 1 && traitement.dosageUnite == resources.getString(R.string.mois)) {
                inputIntervalle.setText("${resources.getString(R.string.tous_les)} ${traitement.dosageNb} ${traitement.dosageUnite}")
            } else if (traitement.dosageUnite == resources.getString(R.string.jours)) {
                inputIntervalle.setText("${resources.getString(R.string.tous_les)} ${traitement.dosageNb} ${traitement.dosageUnite}")
            } else {
                inputIntervalle.setText("${resources.getString(R.string.tous_les)} ${traitement.dosageNb} ${traitement.dosageUnite}")
            }

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
                val traitement = arguments?.getSerializable("traitement") as Traitement
                val isAddingTraitement = arguments?.getString("isAddingTraitement")
                val schema_prise1 = arguments?.getString("schema_prise1")
                val provenance = arguments?.getString("provenance")
                val dureePriseDbt = arguments?.getString("dureePriseDbt")
                val dureePriseFin = arguments?.getString("dureePriseFin")

                if (isAddingTraitement == "false") {
                    val bundle = Bundle()
                    bundle.putSerializable(
                        "traitement",
                        Traitement(
                            traitement.CodeCIS,
                            traitement.nomTraitement,
                            traitement.dosageNb,
                            traitement.dosageUnite,
                            null,
                            traitement.typeComprime,
                            traitement.comprimesRestants,
                            false,
                            null,
                            traitement.prises,
                            traitement.totalQuantite,
                            traitement.UUID,
                            traitement.UUIDUSER,
                            traitement.dateDbtTraitement
                        )
                    )
                    bundle.putString("isAddingTraitement", "$isAddingTraitement")
                    bundle.putString("schema_prise1", "$schema_prise1")
                    bundle.putString("dureePriseDbt", "$dureePriseDbt")
                    bundle.putString("dureePriseFin", "$dureePriseFin")
                    val destinationFragment = AjoutManuelRecapitulatif()
                    destinationFragment.arguments = bundle
                    val fragTransaction = parentFragmentManager.beginTransaction()
                    fragTransaction.replace(R.id.FL, destinationFragment)
                    fragTransaction.addToBackStack(null)
                    fragTransaction.commit()
                    return
                }

                val bundle = Bundle()
                bundle.putSerializable(
                    "traitement",
                    Traitement(
                        traitement.CodeCIS,
                        traitement.nomTraitement,
                        traitement.dosageNb,
                        traitement.dosageUnite,
                        null,
                        traitement.typeComprime,
                        traitement.comprimesRestants,
                        false,
                        null,
                        traitement.prises,
                        traitement.totalQuantite,
                        traitement.UUID,
                        traitement.UUIDUSER,
                        traitement.dateDbtTraitement
                    )
                )
                bundle.putString("isAddingTraitement", "$isAddingTraitement")
                bundle.putString("schema_prise1", "$schema_prise1")
                bundle.putString("provenance", "$provenance")
                bundle.putString("dureePriseDbt", "$dureePriseDbt")
                bundle.putString("dureePriseFin", "$dureePriseFin")

                val destinationFragment = AjoutManuelSchemaPriseFragment()
                destinationFragment.arguments = bundle

                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}
