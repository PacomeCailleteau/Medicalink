package dev.mobile.medicalink.fragments.traitements

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R

class AjoutManuelStock : Fragment() {
    private lateinit var retour: ImageView
    private lateinit var suivant: Button
    private lateinit var layoutStock: ConstraintLayout
    private lateinit var inputStockActuel: EditText
    private lateinit var inputRappelJour: EditText
    private lateinit var inputRappelHeure: EditText
    private lateinit var switchStock: Switch

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_stock, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        val rootLayout = view.findViewById<View>(R.id.constraintStock)
        rootLayout.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                clearFocusAndHideKeyboard(v)
            }
            return@setOnTouchListener false
        }


        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)
        layoutStock = view.findViewById(R.id.constraintLayout3)
        inputStockActuel = view.findViewById(R.id.stockActuelInput)
        inputRappelJour = view.findViewById(R.id.jourRappelInput)
        inputRappelHeure = view.findViewById(R.id.heureRappelInput)
        switchStock = view.findViewById(R.id.switchStock)

        val traitement = arguments?.getSerializable("traitement") as Traitement
        var isAddingTraitement = arguments?.getString("isAddingTraitement")
        var schema_prise1 = arguments?.getString("schema_prise1")
        var provenance = arguments?.getString("provenance")
        var dureePriseDbt = arguments?.getString("dureePriseDbt")
        var dureePriseFin = arguments?.getString("dureePriseFin")

        if (switchStock.isChecked) {
            // Switch est activé (état "on")
            switchStock.thumbTintList = ContextCompat.getColorStateList(requireContext(), R.color.bleuSwitch)
            switchStock.trackTintList = ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
            layoutStock.visibility = View.VISIBLE
        } else {
            // Switch est désactivé (état "off")
            switchStock.thumbTintList = ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
            switchStock.trackTintList = ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)
            layoutStock.visibility = View.GONE
        }

        switchStock.setOnCheckedChangeListener { buttonView, isChecked ->
            updateSwitchAppearance(isChecked, layoutStock)
        }

        updateSwitchAppearance(switchStock.isChecked, layoutStock)

        inputRappelJour.setOnClickListener {
            showJourStockDialog(traitement, view.context)
        }

        //TODO("Faire le hour picker")

        suivant.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    traitement.dateFinTraitement,
                    traitement.typeComprime,
                    25,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            var destinationFragment = AjoutManuelRecapitulatif()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }



        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    traitement.dateFinTraitement,
                    traitement.typeComprime,
                    25,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelDateSchemaPrise()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
        return view
    }

    private fun updateSwitchAppearance(isChecked: Boolean, layoutStock: View) {
        val thumbColor = ContextCompat.getColorStateList(requireContext(), if (isChecked) R.color.bleuSwitch else R.color.grisSwitch)
        val trackColor = ContextCompat.getColorStateList(requireContext(), R.color.grisSwitch)

        val thumbStateList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
            intArrayOf(thumbColor!!.defaultColor, trackColor!!.defaultColor)
        )

        switchStock.thumbTintList = thumbStateList
        switchStock.trackTintList = trackColor

        // Assurez-vous que le changement de couleur est pris en compte
        switchStock.invalidate()

        // Mettez à jour la visibilité du layout
        layoutStock.visibility = if (isChecked) View.VISIBLE else View.GONE
    }

    private fun showJourStockDialog(traitement: Traitement, context: Context) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_jours_stock, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val intervalleRegulierDialog = builder.create()

        val firstNumberPicker = dialogView.findViewById<NumberPicker>(R.id.firstNumberPicker)
        val annulerButton = dialogView.findViewById<Button>(R.id.annulerButton)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)
        val textJour = dialogView.findViewById<TextView>(R.id.textJour)
        var uniteJour = "jour"

        firstNumberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        firstNumberPicker.minValue = 1
        firstNumberPicker.maxValue = 30
        firstNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == 1) {
                textJour.text = "jour"
                uniteJour = "jour"
            } else {
                textJour.text = "jours"
                uniteJour = "jours"
            }
        }
        annulerButton.setOnClickListener {
            intervalleRegulierDialog.dismiss()
        }

        okButton.setOnClickListener {
            if (firstNumberPicker.value == 1){

            }
            // Mettre à jour l'interface utilisateur
            // Vous devez définir la logique appropriée pour mettre à jour votre interface utilisateur
            // Par exemple, si vous avez un TextView nommé inputIntervalle, vous pouvez faire quelque chose comme :
            inputRappelJour.setText("${firstNumberPicker.value} ${uniteJour}")

            intervalleRegulierDialog.dismiss()
        }

        intervalleRegulierDialog.show()
    }

    fun clearFocusAndHideKeyboard(view: View) {
        // Parcours tous les champs de texte, efface le focus
        val editTextList = listOf(
            inputStockActuel,
            inputRappelJour,
            inputRappelHeure
        ) // Ajoute tous tes champs ici
        for (editText in editTextList) {
            editText.clearFocus()
        }

        // Cache le clavier
        val imm = requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}