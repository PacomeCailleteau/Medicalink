package dev.mobile.medicalink.fragments.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.UserRepository


class ModiferInfoFragment : Fragment() {
    private lateinit var retour: ImageView
    private lateinit var titre: TextView
    private lateinit var titre2: TextView
    private lateinit var edit: EditText
    private lateinit var effacerText: ImageView
    private lateinit var valider: Button
    private lateinit var type: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_modifer_info, container, false)
        // On récupère les élements de la vue
        retour = view.findViewById(R.id.retourModifInfo)
        titre = view.findViewById(R.id.titreModifierInfo)
        titre2 = view.findViewById(R.id.titreModifierInfo2)
        edit = view.findViewById(R.id.edit)
        effacerText = view.findViewById(R.id.effacerText)
        valider = view.findViewById(R.id.valider)

        // type est soit "password" soit "email", default est "email"
        type = arguments?.getString("type") ?: "email"
        if (type == "password") {
            passwordPage()
        } else {
            emailPage()
        }

        retour.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        effacerText.setOnClickListener {
            edit.text.clear()
        }

        updateValider()

        valider.setOnClickListener {
            updateInfos()
        }

        return view
    }

    /**
     * On met en place la page pour changer le mot de passe en mettant les string qui vont bien
     */
    private fun passwordPage() {
        titre.text = getString(R.string.changer_mon_s, getString(R.string.mot_de_passe))
        titre2.text = getString(R.string.entrez_votre_nouveau_s, getString(R.string.mot_de_passe))
        // Mot de passe avec chiffre seulement
        edit.inputType = 18

        // On vérifie que le mot de passe a 6 caractères
        edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ne rien faire avant la modification du texte
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ne rien faire lorsqu'il y a un changement dans le texte
            }

            override fun afterTextChanged(s: Editable?) {
                if ((s?.length ?: 0) > 6) {
                    // Si la longueur est supérieure à 6, tronquer le texte
                    edit.setText(s?.subSequence(0, 6))
                    edit.setSelection(6)
                }
                updateValider()
            }
        })
    }

    /**
     * On met en place la page pour changer l'email en mettant les string qui vont bien
     */
    private fun emailPage() {
        titre.text = getString(R.string.changer_mon_s, getString(R.string.email))
        titre2.text = getString(R.string.entrez_votre_nouveau_s, getString(R.string.email))
        // Email
        edit.inputType = 32

        // On vérifie que l'email est valide
        edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ne rien faire avant la modification du texte
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ne rien faire lorsqu'il y a un changement dans le texte
            }

            override fun afterTextChanged(s: Editable?) {
                if (!isEmail()) {
                    edit.error = getString(R.string.invalid_email)
                }
                updateValider()
            }
        })
    }

    /**
     * Vérifie si l'email est valide
     */
    private fun isEmail(): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(edit.text).matches() && type == "email"
    }

    /**
     * Vérifie si le mot de passe est valide
     */
    private fun isPassword(): Boolean {
        return edit.text.length == 6 && type == "password"
    }

    /**
     * Met à jour le bouton valider en fonction de la validité de l'email ou du mot de passe
     */
    private fun updateValider() {
        if (isEmail() || isPassword()) {
            valider.isEnabled = true
            valider.alpha = 1.0f
        } else {
            valider.isEnabled = false
            valider.alpha = 0.3.toFloat()
        }
    }

    /**
     * Met à jour les informations de l'utilisateur et revient à la page précédente
     */
    private fun updateInfos() {
        val db = AppDatabase.getInstance(requireContext())
        val userDatabaseInterface = UserRepository(db.userDao())
        Thread {
            try {
                val currentUser = userDatabaseInterface.getUsersConnected()[0]
                val modifyUser = currentUser.copy(
                    email = if (type == "email") edit.text.toString() else currentUser.email,
                    password = if (type == "password") edit.text.toString() else currentUser.password
                )
                userDatabaseInterface.updateUser(modifyUser)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Erreur lors de la modification des informations",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.start()
        parentFragmentManager.popBackStack()
    }

}









