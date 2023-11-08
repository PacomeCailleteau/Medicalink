package dev.mobile.medicalink

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class AjoutManuelSchemaPrise2Fragment : Fragment() {

    private lateinit var addNouvellePrise: Button
    private lateinit var retour: ImageView

    private var numeroPrise : Int = 1
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_schema_prise2, container, false)

        addNouvellePrise = view.findViewById(R.id.btn_add_nouvelle_prise)
        retour = view.findViewById(R.id.retour_schema_prise2)



        var listePrise = mutableListOf<Prise>(Prise(numeroPrise,"17h00",1,"Comprimé"))

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAjoutPrise)
        recyclerView.layoutManager = LinearLayoutManager(context)

        var ajoutManuelAdapter = AjoutManuelAdapterR(listePrise)
        recyclerView.adapter = ajoutManuelAdapter

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 5
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        addNouvellePrise.setOnClickListener {
            numeroPrise=listePrise.size+1
            var nouvellePrise = Prise(numeroPrise,"17h00",1,"Comprimé")
            listePrise.add(nouvellePrise)
            Log.d("test lilian",listePrise.toString())
            Log.d("test lilian",numeroPrise.toString())
            ajoutManuelAdapter.notifyDataSetChanged()


        }

        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, AjoutManuelSchemaPriseFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }




        return view
    }

}