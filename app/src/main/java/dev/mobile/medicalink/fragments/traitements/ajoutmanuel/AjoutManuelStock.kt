package dev.mobile.medicalink.fragments.traitements.ajoutmanuel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AjoutManuelStock : Fragment() {
    private lateinit var retour: ImageView
    private lateinit var suivant: Button
    private lateinit var layoutStock: ConstraintLayout
    private lateinit var inputStockActuel: EditText
    private lateinit var inputRappelJour: EditText
    private lateinit var inputRappelHeure: EditText

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchStock: Switch

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_stock, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        val rootLayout = view.findViewById<View>(R.id.constraintStock)
        rootLayout.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                clearFocusAndHideKeyboard(v)
            }
            return@setOnTouchListener false
        }


        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)
        layoutStock = view.findViewById(R.id.constraintLayout3)
        inputStockActuel = view.findViewById(R.id.stockActuelInput)
        inputRappelJour = view.findViewById(R.id.jourRappelInput)
        inputRappelHeure = view.findViewById(R.id.heureRappelInput)
        switchStock = view.findViewById(R.id.switchStock)

        val traitement = arguments?.getSerializable("traitement") as Traitement
        val isAddingTraitement = arguments?.getString("isAddingTraitement")
        val schema_prise1 = arguments?.getString("schema_prise1")
        val provenance = arguments?.getString("provenance")
        val dureePriseDbt = arguments?.getString("dureePriseDbt")
        val dureePriseFin = arguments?.getString("dureePriseFin")

        inputStockActuel.setText(traitement.comprimesRestants.toString())

        switchStock.isChecked = true

        if (switchStock.isChecked) {
            // Switch est activé (état "on")
            switchStock.thumbTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.bleuSwitch)
            switchStock.trackTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
            layoutStock.visibility = View.VISIBLE
        } else {
            // Switch est désactivé (état "off")
            switchStock.thumbTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
            switchStock.trackTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
            layoutStock.visibility = View.GONE
        }

        switchStock.setOnCheckedChangeListener { buttonView, isChecked ->
            updateSwitchAppearance(isChecked, layoutStock)
            updateButtonState()
        }

        updateSwitchAppearance(switchStock.isChecked, layoutStock)

        inputRappelJour.setOnClickListener {
            showJourStockDialog(traitement, view.context)
        }

        inputRappelHeure.setOnClickListener {
            showTimePickerDialog(view.context, inputRappelHeure)
        }

        inputStockActuel.addTextChangedListener(textWatcher)

        updateButtonState()
        //TODO("Faire la vérif sur tous les boutons suivant du processus de création de traitement")
        traitement.comprimesRestants = inputStockActuel.text.toString().toInt()

        traitement.expire = false
        traitement.effetsSecondaires = null

        suivant.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.CodeCIS,
                    traitement.nomTraitement,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    traitement.dateFinTraitement,
                    traitement.typeComprime,
                    inputStockActuel.text.toString().toInt(),
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
            val destinationFragment = AjoutManuelRecapitulatif()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }



        retour.setOnClickListener {

            if (isAddingTraitement == "false") {
                traitement.dateFinTraitement = null
                val bundle = Bundle()
                bundle.putSerializable(
                    "traitement",
                    traitement
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
            traitement.comprimesRestants = inputStockActuel.text.toString().toInt()
            bundle.putSerializable(
                "traitement",
                traitement
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelDateSchemaPrise()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
        return view
    }

    /**
     * Fonction gérant l'apparence du switch
     */
    private fun updateSwitchAppearance(isChecked: Boolean, layoutStock: View) {
        val thumbColor = ContextCompat.getColorStateList(
            requireContext(),
            if (isChecked) R.color.bleuSwitch else R.color.grisSwitch
        )
        val trackColor = ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)

        val thumbStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(thumbColor!!.defaultColor, trackColor!!.defaultColor)
        )

        switchStock.thumbTintList = thumbStateList
        switchStock.trackTintList = trackColor

        switchStock.invalidate()

        layoutStock.visibility = if (isChecked) View.VISIBLE else View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun showJourStockDialog(traitement: Traitement, context: Context) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_jours_stock, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val intervalleRegulierDialog = builder.create()

        val firstNumberPicker = dialogView.findViewById<NumberPicker>(R.id.firstNumberPicker)
        val annulerButton = dialogView.findViewById<Button>(R.id.annulerButton)
        val okButton = dialogView.findViewById<Button>(R.id.prendreButton)
        val textJour = dialogView.findViewById<TextView>(R.id.textJour)
        var uniteJour = "jours"

        firstNumberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        firstNumberPicker.minValue = 1
        firstNumberPicker.maxValue = 30
        firstNumberPicker.value = inputRappelJour.text.split(" ")[0].toInt()
        firstNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == 1) {
                textJour.text = resources.getString(R.string.jour_min)
                uniteJour = resources.getString(R.string.jour_min)
            } else {
                textJour.text = resources.getString(R.string.jours_min)
                uniteJour = resources.getString(R.string.jours_min)
            }
        }
        annulerButton.setOnClickListener {
            intervalleRegulierDialog.dismiss()
        }

        okButton.setOnClickListener {
            inputRappelJour.setText("${firstNumberPicker.value} ${uniteJour}")

            intervalleRegulierDialog.dismiss()
        }

        intervalleRegulierDialog.show()
    }

    private fun showTimePickerDialog(
        context: Context,
        heurePriseInput: EditText,
    ) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val formattedTime = formatTime(selectedHour, selectedMinute)
                heurePriseInput.setText(formattedTime)
            },
            currentHour,
            currentMinute,
            true
        )

        timePickerDialog.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat =
            SimpleDateFormat("HH:mm", Locale.FRENCH) // Modifiez le format selon vos besoins
        return timeFormat.format(calendar.time)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Ne rien faire avant la modification du texte
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Ne rien faire avant la fin de la modification du texte car pas une recherche instantanée
        }

        override fun afterTextChanged(editable: Editable?) {
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        val isSwitchChecked = switchStock.isChecked
        val allFieldsFilled = inputStockActuel.text!!.isNotBlank()

        if (isSwitchChecked && !allFieldsFilled) {
            suivant.isEnabled = false
            suivant.alpha = 0.3f
        } else {
            suivant.isEnabled = true
            suivant.alpha = 1.0F
        }
    }

    fun clearFocusAndHideKeyboard(view: View) {
        // Parcours tous les champs de texte, efface le focus
        val editTextList = listOf(
            inputStockActuel,
            inputRappelJour,
            inputRappelHeure
        )
        for (editText in editTextList) {
            editText.clearFocus()
        }

        // Cache le clavier
        val imm =
            requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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

                traitement.dateFinTraitement = null
                traitement.expire = false
                traitement.effetsSecondaires = null

                if (isAddingTraitement == "false") {
                    val bundle = Bundle()
                    bundle.putSerializable(
                        "traitement",
                        traitement
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
                    traitement
                )
                bundle.putString("isAddingTraitement", "$isAddingTraitement")
                bundle.putString("schema_prise1", "$schema_prise1")
                bundle.putString("provenance", "$provenance")
                bundle.putString("dureePriseDbt", "$dureePriseDbt")
                bundle.putString("dureePriseFin", "$dureePriseFin")

                val destinationFragment = AjoutManuelDateSchemaPrise()
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
