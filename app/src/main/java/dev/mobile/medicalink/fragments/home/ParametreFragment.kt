package dev.mobile.medicalink.fragments.home

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.MainActivity
import dev.mobile.medicalink.R


class ParametreFragment : Fragment() {

    private lateinit var btnDeconnexion: LinearLayout
    private lateinit var btnDarkMode: LinearLayout
    private lateinit var switchDarkMode: Switch

    private var isDarkMode: Boolean =
        AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_parametre_fragement, container, false)


        //Get elements from view
        btnDeconnexion = view.findViewById(R.id.cardDeconnexion)
        btnDarkMode = view.findViewById(R.id.cardDarkMode)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)

        btnDeconnexion.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        Log.d("test", isDarkMode.toString())
        switchDarkMode.isChecked = isDarkMode


        if (switchDarkMode.isChecked) {
            // Switch est activé (état "on")
            switchDarkMode.thumbTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.bleuSwitch)
            switchDarkMode.trackTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
        } else {
            // Switch est désactivé (état "off")
            switchDarkMode.thumbTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
            switchDarkMode.trackTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
        }

        switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Définir le mode sombre à partir du fragment
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                // Redémarrer l'activité pour appliquer le changement
            } else {
                Log.d("Ici", "tr")
                // Définir le mode sombre à partir du fragment
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            updateSwitchAppearance(isChecked)
            refreshFragment()
        }

        return view
    }

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

    fun refreshFragment() {
        val transaction = requireFragmentManager().beginTransaction()
        transaction.detach(this)
        transaction.attach(this)
        transaction.commit()
    }
}
