package dev.mobile.medicalink.fragments.traitements

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.os.Build
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R


class AjoutManuelIntervalleRegulier : Fragment() {

    private lateinit var inputIntervalle: TextInputEditText
    private lateinit var retour: ImageView
    private lateinit var suivant : Button


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_intervalle_regulier, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        inputIntervalle = view.findViewById(R.id.inputIntervalle)
        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        val traitement = arguments?.getSerializable("traitement") as Traitement
        var schema_prise1  = arguments?.getString("schema_prise1")
        var dureePriseDbt = arguments?.getString("dureePriseDbt")
        var dureePriseFin = arguments?.getString("dureePriseFin")

        // Dans votre fragment ou activité
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_intervalle_regulier, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val intervalleRegulierDialog = builder.create()

        // Obtenez les références des Spinners
        val firstSpinner = dialogView.findViewById<Spinner>(R.id.firstSpinner)
        val secondSpinner = dialogView.findViewById<Spinner>(R.id.secondSpinner)
        val thirdSpinner = dialogView.findViewById<Spinner>(R.id.thirdSpinner)

        // Remplissez les Spinners avec les données appropriées
        val firstAdapter = ArrayAdapter.createFromResource(view.context, R.array.entiers_de_2_a_99, android.R.layout.simple_spinner_item)
        val secondAdapter = ArrayAdapter.createFromResource(view.context, R.array.jours_semaines_mois, android.R.layout.simple_spinner_item)
        val thirdAdapter = ArrayAdapter.createFromResource(view.context, R.array.entiers_de_1_a_52, android.R.layout.simple_spinner_item)

        firstSpinner.adapter = firstAdapter
        secondSpinner.adapter = secondAdapter
        thirdSpinner.adapter = thirdAdapter

        // Configurez les écouteurs de clic pour les boutons Annuler et OK
        val annulerButton = dialogView.findViewById<Button>(R.id.annulerButton)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)

        annulerButton.setOnClickListener {
            intervalleRegulierDialog.dismiss()
        }

        okButton.setOnClickListener {
            // Obtenez les valeurs sélectionnées des Spinners
            val selectedFirstValue = firstSpinner.selectedItem.toString().toInt()
            val selectedSecondValue = secondSpinner.selectedItem.toString()
            val selectedThirdValue = thirdSpinner.selectedItem.toString().toInt()

            // Faites quelque chose avec les valeurs sélectionnées
            // ...

            intervalleRegulierDialog.dismiss()
        }

        intervalleRegulierDialog.show()


        suivant.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,null,traitement.typeComprime,25,false,null,traitement.prises))
            bundle.putString("provenance", "intervalleRegulier")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")

            val destinationFragment = AjoutManuelSchemaPrise2Fragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }



        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,null,traitement.typeComprime,25,false,null,traitement.prises))
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelSchemaPriseFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
        return view
    }

    override fun onResume() {
        super.onResume()

        // Attacher le gestionnaire du bouton de retour arrière de l'appareil
        val callback = object : OnBackPressedCallback(true) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun handleOnBackPressed() {
                // Code à exécuter lorsque le bouton de retour arrière est pressé
                val traitement = arguments?.getSerializable("traitement") as Traitement
                val schema_prise1 = arguments?.getString("schema_prise1")
                val dureePriseDbt = arguments?.getString("dureePriseDbt")
                val dureePriseFin = arguments?.getString("dureePriseFin")

                val bundle = Bundle()
                bundle.putSerializable("traitement", Traitement(traitement.nomTraitement, traitement.dosageNb, traitement.dosageUnite, null, traitement.typeComprime, 25, false, null, traitement.prises))
                bundle.putString("provenance", "intervalleRegulier")
                bundle.putString("schema_prise1", "$schema_prise1")
                bundle.putString("dureePriseDbt", "$dureePriseDbt")
                bundle.putString("dureePriseFin", "$dureePriseFin")

                val destinationFragment = AjoutManuelSchemaPriseFragment()
                destinationFragment.arguments = bundle

                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}