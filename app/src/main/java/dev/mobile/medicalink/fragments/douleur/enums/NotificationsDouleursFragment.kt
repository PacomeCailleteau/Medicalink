package dev.mobile.medicalink.fragments.douleur.enums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R

class NotificationsDouleursFragment : Fragment() {
    private lateinit var intervalle : EditText
    private lateinit var firstNumberPicker : EditText
    private lateinit var secondNumberPicker : EditText
    private lateinit var buttonOk : AppCompatButton
    private lateinit var buttonCancel : AppCompatButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_douleurs, container, false)

        buttonOk = view.findViewById(R.id.prendreButton)
        buttonCancel = view.findViewById(R.id.sauterButton)
        intervalle = view.findViewById(R.id.firstTime)
        firstNumberPicker = view.findViewById(R.id.firstNumberPicker)
        secondNumberPicker = view.findViewById(R.id.secondNumberPicker)

        buttonOk.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        buttonCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }


}