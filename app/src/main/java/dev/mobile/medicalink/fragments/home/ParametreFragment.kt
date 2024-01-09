package dev.mobile.medicalink.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.MainActivity
import dev.mobile.medicalink.R


class ParametreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_parametre_fragement, container, false)

        //Get elements from view
        val btnDeconnexion: LinearLayout = view.findViewById(R.id.cardDeconnexion)

        btnDeconnexion.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
