package dev.mobile.medicalink.fragments.traitements.ajoutmanuel

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class AjoutManuelDateSchemaPrise : Fragment() {

    private lateinit var finSF: Button
    private lateinit var finDate: Button
    private lateinit var inputDateDeDebut: TextInputEditText
    private lateinit var inputDateDeFin: TextInputEditText
    private var dureePriseDbt: String? = null
    private var dureePriseFin: String? = null
    private var dateDeDebut: LocalDate? = null
    private var dateDeFin: LocalDate? = null
    private var sansFinClicked = false


    private lateinit var retour: ImageView
    private lateinit var suivant: Button


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.fragment_ajout_manuel_date_schema_prise, container, false)

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

        //Récupération des informations circulant entre toutes les vues de l'ajout du traitement
        // pour garder les informations entre les vues
        val traitement = arguments?.getSerializable("traitement") as Traitement
        val isAddingTraitement = arguments?.getString("isAddingTraitement")
        val schema_prise1 = arguments?.getString("schema_prise1")
        val provenance = arguments?.getString("provenance")
        dureePriseDbt = arguments?.getString("dureePriseDbt")
        dureePriseFin = arguments?.getString("dureePriseFin")

        //Mise à jour de la valeur par défaut des boutons en fonctions de ce qui a déjà été sélectionné
        //auparavant (valable quand l'utilisateur revient sur cette vue pour modifier ou vérifier
        //ce qu'il a mis)
        if (dureePriseDbt == null) {
            dureePriseDbt = "ajd"
        }
        if (dureePriseFin == null) {
            dureePriseFin = "date"
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

        inputDateDeDebut.inputType =
            InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_CLASS_TEXT
        inputDateDeDebut.setText("${traitement.dateDbtTraitement?.dayOfMonth.toString()}/${traitement.dateDbtTraitement?.monthValue}/${traitement.dateDbtTraitement?.year}")
        dateDeDebut = LocalDate.of(
            traitement.dateDbtTraitement!!.year,
            traitement.dateDbtTraitement!!.monthValue,
            traitement.dateDbtTraitement!!.dayOfMonth
        )
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

        finSF.setOnClickListener {
            sansFinClicked = true
            finSF.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            finDate.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            inputDateDeFin.visibility = View.GONE
            suivant.alpha = 1f
            suivant.isEnabled = true
            inputDateDeFin.text = null
            dateDeFin = null
            dureePriseFin = "sf"

            // Mettez à jour le statut du bouton suivant
            updateSuivantButtonStatus()
        }

        finDate.setOnClickListener {
            sansFinClicked = false
            inputDateDeFin.visibility = View.VISIBLE
            finSF.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            finDate.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            showDatePicker(inputDateDeFin)
            dureePriseFin = "date"

            updateSuivantButtonStatus()
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
            Log.d("test", inputDateDeFin.text.toString().split("/").toString())

            //Récupération et traitement des dates des inputs pour les reformater dans un format exploitable
            //par la suite
            var textFinTraite: LocalDate? = null
            if ((inputDateDeFin.text != null) && (inputDateDeFin.text.toString() != "")) {
                textFinTraite = LocalDate.of(
                    inputDateDeFin.text.toString().split("/")[2].toInt(),
                    inputDateDeFin.text.toString().split("/")[1].toInt(),
                    inputDateDeFin.text.toString().split("/")[0].toInt()
                )
            }

            var textDbtTraite: LocalDate? = null
            if ((inputDateDeDebut.text != null) && (inputDateDeDebut.text.toString() != "")) {
                textDbtTraite = LocalDate.of(
                    inputDateDeDebut.text.toString().split("/")[2].toInt(),
                    inputDateDeDebut.text.toString().split("/")[1].toInt(),
                    inputDateDeDebut.text.toString().split("/")[0].toInt()
                )
            }

            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.CodeCIS,
                    traitement.nomTraitement,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    textFinTraite,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    textDbtTraite
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
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
            var textFinTraite: LocalDate? = null
            if ((inputDateDeFin.text != null) && (inputDateDeFin.text.toString() != "")) {
                textFinTraite = LocalDate.of(
                    inputDateDeFin.text.toString().split("/")[2].toInt(),
                    inputDateDeFin.text.toString().split("/")[1].toInt(),
                    inputDateDeFin.text.toString().split("/")[0].toInt()
                )
            }

            var textDbtTraite: LocalDate? = null
            if ((inputDateDeDebut.text != null) && (inputDateDeDebut.text.toString() != "")) {
                textDbtTraite = LocalDate.of(
                    inputDateDeDebut.text.toString().split("/")[2].toInt(),
                    inputDateDeDebut.text.toString().split("/")[1].toInt(),
                    inputDateDeDebut.text.toString().split("/")[0].toInt()
                )
            }

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
                    textFinTraite,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    textDbtTraite
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")

            var destinationFragment = Fragment()
            when (provenance) {
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
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
        return view
    }

    /**
     * Fonction qui vérifie que tous les champs de la vue ont bien été complété avant de pouvoir cliquer
     * sur le bouton suivant et passer à la vue suivante
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateSuivantButtonStatus() {
        // Vérifier si une date de début n'est pas null
        if (dateDeDebut != null) {
            // Si une date de fin est également sélectionnée
            if (dureePriseFin == "sf" || (dureePriseFin == "date" && dateDeFin != null)) {
                // Vérifier si la date de fin est supérieure à la date de début
                if (dateDeFin == null || dateDeFin!!.isAfter(dateDeDebut)) {
                    // Les conditions sont remplies, le bouton peut être activé
                    suivant.isEnabled = true
                    suivant.alpha = 1f
                    suivant.setBackgroundResource(R.drawable.rounded_darker_blue_button_no_stroke_background)
                } else {
                    // La date de fin n'est pas supérieure à la date de début, désactiver le bouton
                    suivant.isEnabled = false
                    suivant.alpha = 0.3f
                }
            } else {
                // Pas de date de fin sélectionnée, activer le bouton
                suivant.isEnabled = true
                suivant.alpha = 1f
                suivant.setBackgroundResource(R.drawable.rounded_darker_blue_button_no_stroke_background)
            }
        } else {
            // Aucune des conditions n'est remplie, désactiver le bouton
            suivant.isEnabled = false
            suivant.alpha = 0.3f
        }

        if (dateDeDebut != null && dureePriseFin == "sf") {
            suivant.isEnabled = true
            suivant.alpha = 1f
        }

        if (dateDeDebut == null && dateDeFin != null) {
            suivant.isEnabled = false
            suivant.alpha = 0.3f
        }

        if (dateDeDebut != null && dureePriseFin == "date" && dateDeFin == null) {
            suivant.isEnabled = false
            suivant.alpha = 0.3f
        }
    }

    /**
     * Fonction gérant le date Picker pour sélectionner les dates de début et de fin du traitement
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker(element: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val minDate = calendar.timeInMillis

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val selectedDate = formatDate(day, month, year)
                element.setText(selectedDate)

                if (element == inputDateDeDebut) {
                    dateDeDebut = LocalDate.of(year, month + 1, day)
                } else if (element == inputDateDeFin) {
                    dateDeFin = LocalDate.of(year, month + 1, day)
                }

                updateSuivantButtonStatus()
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.datePicker.minDate = minDate

        datePickerDialog.show()

        updateSuivantButtonStatus()
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

                val destinationFragment = AjoutManuelSchemaPrise2Fragment()
                destinationFragment.arguments = bundle

                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
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
