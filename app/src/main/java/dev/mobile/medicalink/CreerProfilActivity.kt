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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * Classe qui permet de créer un profil
 */
class CreerProfilActivity : AppCompatActivity() {

    private lateinit var textMedicalink: TextView
    private lateinit var imageProfil: ImageView
    private lateinit var textVotreProfil: TextView
    private lateinit var textStatut: TextView
    private lateinit var radioButtonUtilisateur: RadioButton
    private lateinit var radioButtonProfessionnel: RadioButton
    private lateinit var textInformationsPersonnelles: TextView
    private lateinit var inputNom: TextInputEditText
    private lateinit var inputPrenom: TextInputEditText
    private lateinit var inputDateDeNaissance: TextInputEditText
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputMotDePasse: TextInputEditText
    private lateinit var checkboxRgpd: CheckBox
    private lateinit var buttonCreerProfil: Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creer_profile)

        //masquer la barre de titre
        supportActionBar?.hide()

        // Récupération des éléments de la vue
        textMedicalink = findViewById(R.id.text_medicalink)
        imageProfil = findViewById(R.id.image_profil)
        textVotreProfil = findViewById(R.id.text_votre_profil)
        textStatut = findViewById(R.id.text_statut)
        radioButtonUtilisateur = findViewById(R.id.radio_button_utilisateur)
        radioButtonProfessionnel = findViewById(R.id.radio_button_professionnel)
        textInformationsPersonnelles = findViewById(R.id.text_informations_personnelles)
        inputNom = findViewById(R.id.input_nom)
        inputPrenom = findViewById(R.id.input_prenom)
        inputDateDeNaissance = findViewById(R.id.input_date_de_debut)
        inputEmail = findViewById(R.id.input_email)
        inputMotDePasse = findViewById(R.id.input_password)
        checkboxRgpd = findViewById(R.id.checkbox_rgpd)
        buttonCreerProfil = findViewById(R.id.button_creer_profil)

        val checkBoxText = checkboxRgpd.text.toString()

        val startIndex = checkBoxText.indexOf(resources.getString(R.string.rgpd))

        val spannableString = SpannableString(checkBoxText)

        // Création du lien vers le site de la CNIL
        val clickableSpanRGPD = object : ClickableSpan() {
            override fun onClick(view: View) {
                val url = "https://www.cnil.fr/fr/reglement-europeen-protection-donnees"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }

        // Ajout du lien vers le site de la CNIL
        spannableString.setSpan(
            clickableSpanRGPD,
            startIndex,
            startIndex + 4,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        checkboxRgpd.text = spannableString
        checkboxRgpd.movementMethod = LinkMovementMethod.getInstance()

        // Création du regex
        val regex = Regex(
            pattern = "^[a-zA-ZéèàêîôûäëïöüçÉÈÀÊÎÔÛÄËÏÖÜÇ-]*$",
            options = setOf(RegexOption.IGNORE_CASE)
        )

        // Création du filtre
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            val input = source.subSequence(start, end).toString()
            val currentText =
                dest.subSequence(0, dstart).toString() + dest.subSequence(dend, dest.length)
            val newText = currentText.substring(0, dstart) + input + currentText.substring(dstart)

            if (regex.matches(newText)) {
                null // Caractères autorisés
            } else {
                dest.subSequence(dstart, dend)
            }
        }

        //On ajoute le filtre aux champs de texte nom et prénom
        inputNom.filters = arrayOf(
            filter,
            InputFilter.AllCaps(), // Mettre en majuscule
            InputFilter { source, _, _, _, _, _ -> //was start, end, dest, dstart, dend
                source?.let {
                    if (it.contains("\n")) {
                        // Bloquer le collage de texte
                        return@InputFilter ""
                    }
                }
                null
            })

        inputPrenom.filters = arrayOf(
            filter,
            InputFilter { source, _, _, _, _, _ -> //was start, end, dest, dstart, dend
                source?.let {
                    if (it.contains("\n")) {
                        // Bloquer le collage de texte
                        return@InputFilter ""
                    }
                }
                null
            })

        // Permet de cacher le clavier et de supprimer le focus lorsqu'on clique en dehors des champs de texte
        val rootLayout = findViewById<View>(R.id.constraint_layout_creer_profil)
        rootLayout.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                clearFocusAndHideKeyboard(v)
            }
            return@setOnTouchListener false
        }

        inputDateDeNaissance.setOnClickListener {
            showDatePickerDialog()
        }

        // Was buttonView, isChecked
        checkboxRgpd.setOnCheckedChangeListener { _, _ ->
            updateButtonState()
        }

        radioButtonUtilisateur.setOnCheckedChangeListener { _, _ ->
            updateButtonState()
        }

        radioButtonProfessionnel.setOnCheckedChangeListener { _, _ ->
            updateButtonState()
        }

        inputDateDeNaissance.filters =
            arrayOf(InputFilter { source, _, _, _, _, _ -> // was start, end, dest, dstart, dend
                source?.let {
                    if (it.contains("\n")) {
                        // Bloquer le collage de texte
                        return@InputFilter ""
                    }
                }
                null
            })
        inputEmail.filters = arrayOf(InputFilter { source, _, _, _, _, _ -> // was start, end, dest, dstart, dend
            source?.let {
                if (it.contains("\n")) {
                    // Bloquer le collage de texte
                    return@InputFilter ""
                }
            }
            null
        })

        inputMotDePasse.filters = arrayOf(InputFilter { source, _, _, _, _, _ -> // was start, end, dest, dstart, dend
            source?.let {
                if (it.contains("\n")) {
                    // Bloquer le collage de texte
                    return@InputFilter ""
                }
            }
            null
        })

        // Ajout des textWatcher aux champs de texte
        inputNom.addTextChangedListener(textWatcher)
        inputPrenom.addTextChangedListener(textWatcher)
        inputDateDeNaissance.addTextChangedListener(textWatcher)
        inputEmail.addTextChangedListener(textWatcher)
        inputMotDePasse.addTextChangedListener(textWatcher)

        inputMotDePasse.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ne rien faire avant la modification du texte
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ne rien faire lorsqu'il y a un changement dans le texte
            }

            override fun afterTextChanged(s: Editable?) {
                if ((s?.length ?: 0) > 6) {
                    // Si la longueur est supérieure à 6, tronquer le texte
                    inputMotDePasse.setText(s?.subSequence(0, 6))
                    inputMotDePasse.setSelection(6)
                }
            }
        })

        // Ajout du listener sur le bouton créer profil
        buttonCreerProfil.setOnClickListener {
            // Création de la connexion à la base de données
            val db = AppDatabase.getInstance(this)
            val userDatabaseInterface = UserRepository(db.userDao())
            // Création de la variable de sortie de la fonction d'insertion
            var res: Pair<Boolean, String>?
            // Récupération des données du formulaire et de l'uuid aléatoire
            val uuid = UUID.randomUUID().toString()
            val statut =
                if (radioButtonUtilisateur.isChecked) resources.getString(R.string.Utilisateur) else resources.getString(
                    R.string.professionnel
                )
            val nom = inputNom.text.toString()
            val prenom = inputPrenom.text.toString()
            val dateNaissance = inputDateDeNaissance.text.toString()
            val email = inputEmail.text.toString()
            val password = inputMotDePasse.text.toString()
            val isConnected = true
            // Création de l'utilisateur
            val user = User(
                uuid,
                statut,
                nom,
                prenom,
                dateNaissance,
                email,
                password,
                isConnected
            )
            // Insertion de l'utilisateur dans la base de données dans un Thread parce que un appel à la base de données est asynchrone
            Thread {
                res = userDatabaseInterface.insertUser(user)
                userDatabaseInterface.setConnected(user)
                if (res!!.first) {
                    Log.d("CREER_PROFIL", res!!.second)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d("CREER_PROFIL", res!!.second)
                }
            }.start()
        }

    }

    /**
     * Permet de vérifier si les champs sont remplis
     */
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Ne rien faire avant la modification du texte
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Ne rien faire lorsqu'il y a un changement dans le texte
        }

        override fun afterTextChanged(editable: Editable?) {
            // Vérifie si les champs sont remplis
            updateButtonState()
            validateEmail(inputEmail.text.toString())
        }
    }

    /**
     * Permet de vérifier si l'email est valide
     */
    private fun validateEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        val isValidEmail = pattern.matcher(email).matches()

        if (!isValidEmail) {
            inputEmail.error = resources.getString(R.string.invalid_email)
        } else {
            inputEmail.error = null
        }
        return isValidEmail
    }

    /**
     * Permet de mettre le bouton créer profil activé ou désactivé si les champs sont remplis ou non
     */
    private fun updateButtonState() {
        val isCheckboxChecked = checkboxRgpd.isChecked
        val isRadioButtonSelected =
            radioButtonUtilisateur.isChecked || radioButtonProfessionnel.isChecked

        val isEmailValid = validateEmail(inputEmail.text.toString())
        val isPasswordValid = isValidPassword(inputMotDePasse.text.toString())

        val allFieldsFilled = inputNom.text!!.isNotBlank() &&
                inputPrenom.text!!.isNotBlank() &&
                inputDateDeNaissance.text!!.isNotBlank() &&
                inputEmail.text!!.isNotBlank() &&
                inputMotDePasse.text!!.isNotBlank()

        if (isCheckboxChecked && isRadioButtonSelected && allFieldsFilled && isEmailValid && isPasswordValid) {
            buttonCreerProfil.isEnabled = true
            buttonCreerProfil.alpha = 1.0f
        } else {
            buttonCreerProfil.isEnabled = false
            buttonCreerProfil.alpha = 0.3.toFloat()
        }
    }

    /**
     * Permet de vérifier si le mot de passe est valide
     */
    private fun isValidPassword(password: String): Boolean {
        return password.length == 6
    }

    /**
     * Permet de supprimer le focus et de cacher le clavier
     */
    fun clearFocusAndHideKeyboard(view: View) {
        // Parcours tous les champs de texte, efface le focus
        val editTextList = listOf(
            inputNom,
            inputPrenom,
            inputDateDeNaissance,
            inputEmail,
            inputMotDePasse
        )
        for (editText in editTextList) {
            editText.clearFocus()
        }

        // Cache le clavier
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Permet d'afficher le calendrier pour choisir la date de naissance
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar[Calendar.YEAR]
        val currentMonth = calendar[Calendar.MONTH]
        val currentDay = calendar[Calendar.DAY_OF_MONTH]

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                if (selectedYear > currentYear ||
                    (selectedYear == currentYear && selectedMonth > currentMonth) ||
                    (selectedYear == currentYear && selectedMonth == currentMonth && selectedDay > currentDay)
                ) {
                    Toast.makeText(
                        this,
                        getString(R.string.date_invalide),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val formattedDate = formatDate(selectedDay, selectedMonth, selectedYear)
                    inputDateDeNaissance.setText(formattedDate)
                }
            }, currentYear, currentMonth, currentDay)

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    /**
     * Permet de formater la date
     */
    private fun formatDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = day
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH)
        return dateFormat.format(calendar.time)
    }
}
