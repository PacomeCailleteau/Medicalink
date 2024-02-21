package dev.mobile.medicalink.fragments.traitements.ajouts

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.mobile.medicalink.R
import dev.mobile.medicalink.utils.GoTo
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_stock, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        val rootLayout = view.findViewById<View>(R.id.constraintStock)
        rootLayout.setOnTouchListener { v, event ->
            clearFocusAndHideKeyboard(v)
            return@setOnTouchListener false
        }


        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)
        layoutStock = view.findViewById(R.id.constraintLayout3)
        inputStockActuel = view.findViewById(R.id.stockActuelInput)
        inputRappelJour = view.findViewById(R.id.jourRappelInput)
        inputRappelHeure = view.findViewById(R.id.heureRappelInput)
        switchStock = view.findViewById(R.id.switchStock)

        inputStockActuel.setText(viewModel.comprimesRestants.value.toString())

        switchStock.isChecked = false

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

        switchStock.setOnCheckedChangeListener { _, isChecked ->
            updateSwitchAppearance(isChecked, layoutStock)
            updateButtonState()
        }

        updateSwitchAppearance(switchStock.isChecked, layoutStock)

        inputRappelJour.setOnClickListener {
            showJourStockDialog(view.context)
        }

        inputRappelHeure.setOnClickListener {
            showTimePickerDialog(view.context, inputRappelHeure)
        }

        inputStockActuel.addTextChangedListener(textWatcher)

        updateButtonState()
        suivant.setOnClickListener {
            viewModel.setComprimesRestants(inputStockActuel.text.toString().toInt())
            GoTo.fragment(AjoutManuelRecapitulatif(), parentFragmentManager)
        }

        retour.setOnClickListener {
            viewModel.setComprimesRestants(inputStockActuel.text.toString().toInt())
            GoTo.fragment(AjoutManuelDateSchemaPrise(), parentFragmentManager)
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
    private fun showJourStockDialog(context: Context) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_jours_stock, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val intervalleRegulierDialog = builder.create()

        val firstNumberPicker = dialogView.findViewById<NumberPicker>(R.id.firstNumberPicker)
        val annulerButton = dialogView.findViewById<Button>(R.id.sauterButton)
        val okButton = dialogView.findViewById<Button>(R.id.prendreButton)
        val textJour = dialogView.findViewById<TextView>(R.id.textJour)
        var uniteJour = "jours"

        firstNumberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        firstNumberPicker.minValue = 1
        firstNumberPicker.maxValue = 30
        firstNumberPicker.value = inputRappelJour.text.split(" ")[0].toInt()
        firstNumberPicker.setOnValueChangedListener { _, _, newVal ->
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
            inputRappelJour.setText("${firstNumberPicker.value} $uniteJour")
            intervalleRegulierDialog.dismiss()
        }

        intervalleRegulierDialog.show()
    }

    private fun showTimePickerDialog(
        context: Context,
        heurePriseInput: EditText,
    ) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar[Calendar.HOUR_OF_DAY]
        val currentMinute = calendar[Calendar.MINUTE]

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
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        val timeFormat =
            SimpleDateFormat("HH:mm", Locale.FRENCH) // Modifiez le format selon vos besoins
        return timeFormat.format(calendar.time)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Nothing to do before the text changes
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Nothing to do while the text is changing
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
}
