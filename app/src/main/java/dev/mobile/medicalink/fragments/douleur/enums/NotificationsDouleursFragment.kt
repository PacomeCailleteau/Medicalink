package dev.mobile.medicalink.fragments.douleur.enums

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Prise
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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


        buttonCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /*intervalle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    val value = s.toString().split(":")[1]

                    if (value == null || value.toIntOrNull() !in 0..59) {

                        // Affichez un Toast pour expliquer l'erreur
                        Toast.makeText(requireContext(), R.string.pb_digit, Toast.LENGTH_SHORT)
                            .show()
                        // La valeur n'est pas valide, videz le champ
                        intervalle.clearFocus()
                        intervalle.text = null
                    }
                }
            }
        })

        firstNumberPicker.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    val value = s.toString().split(":")[1]

                    if (value == null || value.toIntOrNull() !in 0..59) {

                        // Affichez un Toast pour expliquer l'erreur
                        Toast.makeText(requireContext(), R.string.pb_digit, Toast.LENGTH_SHORT)
                            .show()
                        // La valeur n'est pas valide, videz le champ
                        intervalle.clearFocus()
                        intervalle.text = null
                    }
                }
            }
        })

        secondNumberPicker.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    val value = s.toString().split(":")[1]

                    if (value == null || value.toIntOrNull() !in 0..59) {

                        // Affichez un Toast pour expliquer l'erreur
                        Toast.makeText(requireContext(), R.string.pb_digit, Toast.LENGTH_SHORT)
                            .show()
                        // La valeur n'est pas valide, videz le champ
                        intervalle.clearFocus()
                        intervalle.text = null
                    }
                }
            }
        })*/


        buttonOk.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}