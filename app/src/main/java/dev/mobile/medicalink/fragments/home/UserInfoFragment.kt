package dev.mobile.medicalink.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.utils.GoTo


class UserInfoFragment : Fragment() {
    private lateinit var retour: ImageView
    private lateinit var nom: TextView
    private lateinit var prenom: TextView
    private lateinit var dateDeNaissance: TextView
    private lateinit var email: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var user: User
    private lateinit var modifPass: Button
    private lateinit var modifEmail: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_info, container, false)

        // On récupère les élements de la vue
        retour = view.findViewById(R.id.userInfoRetour)
        nom = view.findViewById(R.id.userInfoNom)
        prenom = view.findViewById(R.id.userInfoPrenom)
        dateDeNaissance = view.findViewById(R.id.userInfoDateNaissance)
        email = view.findViewById(R.id.userInfoEmail)
        progressBar = view.findViewById(R.id.userInfoProgressBar)
        modifPass = view.findViewById(R.id.boutonModifPassword)
        modifEmail = view.findViewById(R.id.boutonModifEmail)

        retour.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        getUserInfos()

        modifPass.setOnClickListener {
            val fragment = ModiferInfoFragment()
            val buddle = Bundle()
            buddle.putString("type", "password")
            fragment.arguments = buddle
            GoTo.fragment(fragment, parentFragmentManager)
        }

        modifEmail.setOnClickListener {
            val fragment = ModiferInfoFragment()
            val buddle = Bundle()
            buddle.putString("type", "email")
            fragment.arguments = buddle
            GoTo.fragment(fragment, parentFragmentManager)
        }

        return view
    }

    /**
     * Fonction pour récupérer les informations de l'utilisateur et les afficher
     */
    private fun getUserInfos() {
        Thread {
            try {
                val db = AppDatabase.getInstance(requireContext())
                val userInterface = UserRepository(db.userDao())
                user = userInterface.getUsersConnected()[0]
                activity?.runOnUiThread {
                    nom.text = getString(R.string.nom_s, user.nom)
                    prenom.text = getString(R.string.prenom_s, user.prenom)
                    dateDeNaissance.text =
                        getString(R.string.date_de_naissance_s, user.dateDeNaissance)
                    email.text = getString(R.string.email_s, user.email)
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                // On toast un message d'erreur
                Toast.makeText(
                    requireContext(),
                    "Erreur lors de la récupération des informations de l'utilisateur",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("UserInfoFragment", "Error get user info : ", e)
            }
        }.start()
    }


}