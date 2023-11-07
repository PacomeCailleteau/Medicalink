package dev.mobile.medicalink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.textfield.TextInputEditText


class AjoutManuelSearchFragment : Fragment() {

    private lateinit var addManuallySearchBar: TextInputEditText
    private lateinit var addManuallyButton: Button

    private lateinit var addManuallyButtonLauncher: ActivityResultLauncher<Intent>

    private lateinit var retour: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_search, container, false)

        var nom_traitement = arguments?.getString("nom_traitement")
        var schema_prise1 = arguments?.getString("schema_prise1")


        addManuallySearchBar = view.findViewById(R.id.add_manually_search_bar)
        addManuallyButton = view.findViewById(R.id.add_manually_button)

        addManuallySearchBar.setText(nom_traitement)
        addManuallyButtonLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Gérez l'activité de résultat ici
            }
        }




        addManuallyButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("nom_traitement", "${addManuallySearchBar.text}")
            bundle.putString("schema_prise1", "$schema_prise1")
            val destinationFragment = AjoutManuelSchemaPriseFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        retour = view.findViewById(R.id.retour_schema_prise2)

        //On retourne au fragment précédent
        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, AddTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

}