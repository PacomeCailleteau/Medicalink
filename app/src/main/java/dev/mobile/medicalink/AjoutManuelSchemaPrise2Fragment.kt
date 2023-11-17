package dev.mobile.medicalink

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class AjoutManuelSchemaPrise2Fragment : Fragment() {

    private lateinit var addNouvellePrise: Button
    private lateinit var retour: ImageView
    private lateinit var suivant : Button

    private var numeroPrise : Int = 1
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_schema_prise2, container, false)

        addNouvellePrise = view.findViewById(R.id.btn_add_nouvelle_prise)
        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)


        val traitement = arguments?.getSerializable("traitement") as Traitement
        var schema_prise1  = arguments?.getString("schema_prise1")
        var provenance  = arguments?.getString("provenance")
        var dureePriseDbt = arguments?.getString("dureePriseDbt")
        var dureePriseFin = arguments?.getString("dureePriseFin")

        var listePrise : MutableList<Prise>? = traitement.prises
        if (listePrise == null){
            listePrise= mutableListOf<Prise>(Prise(numeroPrise,"17h00",1,traitement.typeComprime))
        }else{
            for (prise in listePrise){
                prise.dosageUnite=traitement.dosageUnite
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAjoutPrise)
        recyclerView.layoutManager = LinearLayoutManager(context)

        var ajoutManuelAdapter = AjoutManuelAdapterR(listePrise)
        recyclerView.adapter = ajoutManuelAdapter

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 20
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        addNouvellePrise.setOnClickListener {
            numeroPrise=listePrise.size+1
            var nouvellePrise = Prise(numeroPrise,"17h00",1,traitement.typeComprime)
            listePrise.add(nouvellePrise)
            ajoutManuelAdapter.notifyDataSetChanged()
        }



        suivant.setOnClickListener {
            var dosageNB = 0
            if (listePrise!=null){
                for (prise in listePrise){
                    dosageNB+=prise.quantite
                }
            }
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement, dosageNB,traitement.dosageUnite,null,traitement.typeComprime,25,false,null,listePrise))
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            var destinationFragment = AjoutManuelDateSchemaPrise()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }


        retour.setOnClickListener {
            var dosageNB = 0
            if (listePrise!=null){
                for (prise in listePrise){
                    dosageNB+=prise.quantite
                }
            }
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,dosageNB,traitement.dosageUnite,null,traitement.typeComprime,25,false,null,listePrise))
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            var destinationFragment = Fragment()
            when (provenance){
                "quotidiennement" -> {
                    destinationFragment = AjoutManuelSchemaPriseFragment()

                }
                "intervalleRegulier" -> {
                    destinationFragment = AjoutManuelIntervalleRegulier()
                }
            }
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }




        return view
    }
}