package dev.mobile.medicalink

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creer_profile)

        //masquer la barre de titre
        supportActionBar?.hide()

        //TODO : Mettre le RGPD en lien cliquable

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

        // Récupérez le texte actuel de la CheckBox
        val checkBoxText = checkboxRgpd.text.toString()

        // Trouvez la position du mot "RGPD" dans le texte
        val startIndex = checkBoxText.indexOf("RGPD")

        // Créez une SpannableString pour le texte de la CheckBox
        val spannableString = SpannableString(checkBoxText)

        // Ajoutez un ClickableSpan au mot "RGPD"
        val clickableSpanRGPD = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Ouvrez l'URL des termes de la RGPD dans le navigateur
                val url = "https://www.cnil.fr/fr/reglement-europeen-protection-donnees"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }

        // Indiquez la position du mot "RGPD" dans la SpannableString
        spannableString.setSpan(clickableSpanRGPD, startIndex, startIndex + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Appliquez la SpannableString à la CheckBox
        checkboxRgpd.text = spannableString
        checkboxRgpd.movementMethod = LinkMovementMethod.getInstance()

        val regex = Regex(pattern = "^[a-zA-ZéèàêîôûäëïöüçÉÈÀÊÎÔÛÄËÏÖÜÇ-]*$", options = setOf(RegexOption.IGNORE_CASE))

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            val input = source.subSequence(start, end).toString()
            val currentText = dest.subSequence(0, dstart).toString() + dest.subSequence(dend, dest.length)
            val newText = currentText.substring(0, dstart) + input + currentText.substring(dstart)

            if (regex.matches(newText)) {
                null // Caractères autorisés
            } else {
                dest.subSequence(dstart, dend)
            }
        }

        inputNom.filters = arrayOf(filter)

        inputPrenom.filters = arrayOf(filter)

        val rootLayout = findViewById<View>(R.id.constraint_layout_creer_profil)
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

        checkboxRgpd.setOnCheckedChangeListener { buttonView, isChecked ->
            updateButtonState()
        }

        radioButtonUtilisateur.setOnCheckedChangeListener { buttonView, isChecked ->
            updateButtonState()
        }

        radioButtonProfessionnel.setOnCheckedChangeListener { buttonView, isChecked ->
            updateButtonState()
        }

        inputNom.addTextChangedListener(textWatcher)
        inputPrenom.addTextChangedListener(textWatcher)
        inputDateDeNaissance.addTextChangedListener(textWatcher)
        inputEmail.addTextChangedListener(textWatcher)

        buttonCreerProfil.setOnClickListener {
            val db = AppDatabase.getInstance(this)
            val userDatabaseInterface = UserRepository(db.userDao())
            var res : Pair<Boolean, String>? = null
            val uuid = java.util.UUID.randomUUID().toString()
            val statut = if (radioButtonUtilisateur.isChecked) "Utilisateur" else "Professionnel"
            val nom = inputNom.text.toString()
            val prenom = inputPrenom.text.toString()
            val dateNaissance = inputDateDeNaissance.text.toString()
            val email = inputEmail.text.toString()
            val user = User(
                uuid,
                statut,
                nom,
                prenom,
                dateNaissance,
                email
            )
            Log.d("CREER", "Utilisateur : $user")
            Thread {
                res = userDatabaseInterface.insertUser(user)
                if (res!!.first) {
                    Log.d("CREER_PROFIL", res!!.second)
                } else {
                    Log.d("CREER_PROFIL", res!!.second)
                }
            }.start()
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
            updateButtonState()
            validateEmail(inputEmail.text.toString())
        }
    }

    private fun validateEmail(email: String) : Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        val isValidEmail = pattern.matcher(email).matches()

        if (!isValidEmail) {
            inputEmail.error = "Adresse e-mail non valide"
        } else {
            inputEmail.error = null
        }
        return isValidEmail
    }

    private fun updateButtonState() {
        val isCheckboxChecked = checkboxRgpd.isChecked
        val isRadioButtonSelected = radioButtonUtilisateur.isChecked || radioButtonProfessionnel.isChecked

        val isEmailValid = validateEmail(inputEmail.text.toString())

        val allFieldsFilled = inputNom.text!!.isNotBlank() &&
                inputPrenom.text!!.isNotBlank() &&
                inputDateDeNaissance.text!!.isNotBlank() &&
                inputEmail.text!!.isNotBlank()

        if (isCheckboxChecked && isRadioButtonSelected && allFieldsFilled && isEmailValid) {
            buttonCreerProfil.isEnabled = true
            buttonCreerProfil.alpha = 1.0f
        } else {
            buttonCreerProfil.isEnabled = false
            buttonCreerProfil.alpha = 0.3.toFloat()
        }
    }
    fun clearFocusAndHideKeyboard(view: View) {
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
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDay ->
            if (selectedYear > currentYear ||
                (selectedYear == currentYear && selectedMonth > currentMonth) ||
                (selectedYear == currentYear && selectedMonth == currentMonth && selectedDay > currentDay)) {
                // La date sélectionnée est future, ne faites rien ou montrez un message à l'utilisateur
            } else {
                val formattedDate = formatDate(selectedDay, selectedMonth, selectedYear)
                inputDateDeNaissance.setText(formattedDate)
            }
        }, currentYear, currentMonth, currentDay)

        // Configurez les limites du DatePickerDialog pour empêcher la sélection de dates futures
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

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