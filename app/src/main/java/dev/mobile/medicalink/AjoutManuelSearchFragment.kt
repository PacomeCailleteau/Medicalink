package dev.mobile.medicalink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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


        addManuallySearchBar = view.findViewById(R.id.add_manually_search_bar)
        addManuallyButton = view.findViewById(R.id.add_manually_button)

        addManuallyButtonLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Gérez l'activité de résultat ici
            }
        }




        addManuallyButton.setOnClickListener {
            val intent = Intent(this, AjoutManuelSchemaPrise::class.java)
            addManuallyButtonLauncher.launch(intent)
        }

        retour = view.findViewById(R.id.retour_schema_prise)

        //On retourne au fragment précédent TODO : choisir le fragment précédent (je sais pas c quoi)
        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, MainTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

}