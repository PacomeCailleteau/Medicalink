package dev.mobile.medicalink.fragments.home

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.MainActivity
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.utils.GoTo
import java.util.concurrent.LinkedBlockingQueue

/**
 *
 * le fragment de la page paramètre
 */
class ParametreFragment : Fragment() {

    private lateinit var btnDeconnexion: LinearLayout
    private lateinit var btnDarkMode: LinearLayout
    private lateinit var switchDarkMode: SwitchCompat
    private lateinit var supprimerCompte: LinearLayout
    private lateinit var infoCompte: LinearLayout
    private lateinit var retour: ImageView

    private var isDarkMode: Boolean =
        AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parametre, container, false)

        //Récupérer les elements de la view
        btnDeconnexion = view.findViewById(R.id.cardDeconnexion)
        btnDarkMode = view.findViewById(R.id.cardDarkMode)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        supprimerCompte = view.findViewById(R.id.deleteAccount)
        infoCompte = view.findViewById(R.id.cardInfo)
        retour = view.findViewById(R.id.userInfoRetour)

        retour.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        infoCompte.setOnClickListener {
            GoTo.fragment(UserInfoFragment(), parentFragmentManager)
        }

        btnDeconnexion.setOnClickListener {
            //Si on clique sur le bouton de déconnexion, on déconnecte l'utilisateur et on le redirige vers la page de connexion
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        switchDarkMode.isChecked = isDarkMode

        // Le switch pour le mode sombre
        if (switchDarkMode.isChecked) {
            // Si le switch est activé (état "on"), on passe l'application en mode sombre
            switchDarkMode.thumbTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.bleuSwitch)
            switchDarkMode.trackTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
        } else {
            // Sinon, on passe l'application en mode clair
            switchDarkMode.thumbTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
            switchDarkMode.trackTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked -> // was buttonView
            if (isChecked) {
                // Définir le mode sombre à partir du fragment
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Définir le mode sombre à partir du fragment
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            // Mettre à jour l'apparence du switch
            updateSwitchAppearance(isChecked)
            refreshFragment()
        }

        //Si on clique sur le bouton de suppression de compte, on supprime le compte de l'utilisateur et on le redirige vers la page de connexion
        supprimerCompte.setOnClickListener {
            val db = AppDatabase.getInstance(requireContext())
            val userDatabaseInterface = UserRepository(db.userDao())

            val queue = LinkedBlockingQueue<String>()

            Thread {
                val res = userDatabaseInterface.getUsersConnected()
                val userToDelete = userDatabaseInterface.getOneUserById(res.first().uuid).first()

                if (userDatabaseInterface.getAllUsers().size == 1) {
                    userDatabaseInterface.deleteUser(userToDelete)
                } else {
                    userDatabaseInterface.deleteUser(userToDelete)
                    userDatabaseInterface.setConnected(userDatabaseInterface.getAllUsers().first())
                }
                queue.add("True")
            }.start()

            // Pour attendre la fin du thread
            queue.take()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    /**
     * Mettre à jour l'apparence du switch du mode sombre
     * @param isChecked
     */
    private fun updateSwitchAppearance(isChecked: Boolean) {
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

        switchDarkMode.thumbTintList = thumbStateList
        switchDarkMode.trackTintList = trackColor

        // Assurez-vous que le changement de couleur est pris en compte
        switchDarkMode.invalidate()
    }

    /**
     * Rafraîchir le fragment
     */
    fun refreshFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.detach(this)
        transaction.attach(this)
        transaction.commit()
    }
}
