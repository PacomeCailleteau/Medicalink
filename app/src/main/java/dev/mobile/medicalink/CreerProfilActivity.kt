package dev.mobile.medicalink

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreerProfilActivity : AppCompatActivity() {

    private lateinit var textMedicalink: TextView
    private lateinit var imageProfil: ImageView
    private lateinit var textVotreProfil: TextView
    private lateinit var textStatut: TextView
    private lateinit var radioButtonUtilisateur: RadioButton
    private lateinit var radioButtonProfessionnel: RadioButton
    private lateinit var text_informations_personnelles: TextView
    private lateinit var inputNom: TextInputEditText
    private lateinit var inputPrenom: TextInputEditText
    private lateinit var inputDateDeNaissance: TextInputEditText
    private lateinit var inputEmail: TextInputEditText
    private lateinit var checkboxRgpd: CheckBox
    private lateinit var buttonCreerProfil: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creer_profile)

        textMedicalink = findViewById(R.id.text_medicalink)
        imageProfil = findViewById(R.id.image_profil)
        textVotreProfil =findViewById(R.id.text_votre_profil)
        textStatut = findViewById(R.id.text_statut)
        radioButtonUtilisateur = findViewById(R.id.radio_button_utilisateur)
        radioButtonProfessionnel = findViewById(R.id.radio_button_professionnel)
        text_informations_personnelles = findViewById(R.id.text_informations_personnelles)
        inputNom = findViewById(R.id.input_nom)
        inputPrenom = findViewById(R.id.input_prenom)
        inputDateDeNaissance = findViewById(R.id.input_date_de_naissance)
        inputEmail = findViewById(R.id.input_email)
        checkboxRgpd = findViewById(R.id.checkbox_rgpd)
        buttonCreerProfil = findViewById(R.id.button_creer_profil)

        val rootLayout = findViewById<View>(R.id.constraint_layout_creer_profil) // Remplace "constraintLayout" par l'ID de ta disposition racine
        rootLayout.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // L'utilisateur a cliqué sur la page
                clearFocusAndHideKeyboard(v)
            }
            return@setOnTouchListener false
        }

        inputDateDeNaissance.setOnClickListener {
            showDatePickerDialog()
        }

        val editTextList = listOf(inputNom, inputPrenom, inputDateDeNaissance, inputEmail)
        for (editText in editTextList) {
            editText.addTextChangedListener(textWatcher)
        }

        checkboxRgpd.setOnCheckedChangeListener { buttonView, isChecked ->
            updateButtonState(editTextList, checkboxRgpd, buttonCreerProfil)
        }

        buttonCreerProfil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Ne fait rien
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Ne fait rien
        }

        override fun afterTextChanged(editable: Editable?) {
            // Vérifie si tous les champs obligatoires sont remplis
            val isFieldsNotEmpty =
                !inputNom.text.isNullOrBlank() &&
                        !inputPrenom.text.isNullOrBlank() &&
                        !inputDateDeNaissance.text.isNullOrBlank() &&
                        !inputEmail.text.isNullOrBlank() &&
                        checkboxRgpd.isChecked

            // Active ou désactive le bouton "Créer mon profil" en fonction de la validité des champs
            buttonCreerProfil.alpha = if (isFieldsNotEmpty) 1.toFloat() else 0.3.toFloat()
            buttonCreerProfil.isEnabled = isFieldsNotEmpty

            // Vérifie si au moins un champ est vide, puis réactive le bouton
            if (inputNom.text.isNullOrBlank() || inputPrenom.text.isNullOrBlank() || inputDateDeNaissance.text.isNullOrBlank() || inputEmail.text.isNullOrBlank() || !checkboxRgpd.isChecked) {
                buttonCreerProfil.isEnabled = false
                buttonCreerProfil.alpha = 0.3.toFloat()
            }
        }
    }

    private fun updateButtonState(editTextList: List<EditText>, checkboxRgpd: CheckBox, buttonCreerProfil: Button) {
        val allFieldsFilled = editTextList.all { it.text.isNotBlank() }
        val isCheckboxChecked = checkboxRgpd.isChecked
        buttonCreerProfil.isEnabled = allFieldsFilled && isCheckboxChecked
        buttonCreerProfil.isVisible = allFieldsFilled && isCheckboxChecked
        if (allFieldsFilled && isCheckboxChecked) {
            buttonCreerProfil.alpha = 1.0.toFloat()
        }else{
            buttonCreerProfil.alpha = 0.3.toFloat()
        }
    }

    private fun clearFocusAndHideKeyboard(view: View) {
        // Parcours tous les champs de texte, efface le focus
        val editTextList = listOf(inputNom, inputPrenom, inputDateDeNaissance, inputEmail) // Ajoute tous tes champs ici
        for (editText in editTextList) {
            editText.clearFocus()
        }

        // Cache le clavier
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                view, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = formatDate(selectedDay, selectedMonth, selectedYear)
            inputDateDeNaissance.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun formatDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH)
        return dateFormat.format(calendar.time)
    }
}