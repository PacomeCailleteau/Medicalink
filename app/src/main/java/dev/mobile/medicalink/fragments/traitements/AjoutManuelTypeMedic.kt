package dev.mobile.medicalink.fragments.traitements

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.os.Build
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R


class AjoutManuelTypeMedic : Fragment() {

    private lateinit var retour: ImageView
    private lateinit var suivant : Button


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_type_medic, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        val traitement = arguments?.getSerializable("traitement") as Traitement
        var isAddingTraitement  = arguments?.getString("isAddingTraitement")
        var schema_prise1  = arguments?.getString("schema_prise1")
        var dureePriseDbt = arguments?.getString("dureePriseDbt")
        var dureePriseFin = arguments?.getString("dureePriseFin")

        var listeTypeMedic : MutableList<String> =
            mutableListOf("Comprimé","Gellule","Sachet","Sirop","Pipette","Seringue","Bonbon")

        var selected = traitement.typeComprime

        val recyclerViewTypeMedic = view.findViewById<RecyclerView>(R.id.recyclerViewTypeMedic)
        recyclerViewTypeMedic.layoutManager = LinearLayoutManager(context)


        var AjoutManuelTypeMedicAdapter = AjoutManuelTypeMedicAdapterR(listeTypeMedic,selected)
        recyclerViewTypeMedic.adapter = AjoutManuelTypeMedicAdapter

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacement = 20
        recyclerViewTypeMedic.addItemDecoration(SpacingRecyclerView(espacement))

        suivant.setOnClickListener {
            val bundle = Bundle()
            Log.d("LLLL",AjoutManuelTypeMedicAdapter.selected)
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,null,AjoutManuelTypeMedicAdapter.selected,25,false,null,traitement.prises,traitement.totalQuantite,traitement.UUID,traitement.UUIDUSER,traitement.dateDbtTraitement))
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
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



        retour.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,traitement.dosageNb,traitement.dosageUnite,null,AjoutManuelTypeMedicAdapter.selected,25,false,null,traitement.prises,traitement.totalQuantite,traitement.UUID,traitement.UUIDUSER,traitement.dateDbtTraitement))
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelSearchFragment()
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
                var isAddingTraitement  = arguments?.getString("isAddingTraitement")
                val schema_prise1 = arguments?.getString("schema_prise1")
                val dureePriseDbt = arguments?.getString("dureePriseDbt")
                val dureePriseFin = arguments?.getString("dureePriseFin")
                var listeTypeMedic : MutableList<String> =
                    mutableListOf("Comprimé","Gellule","Sachet","Sirop","Pipette","Seringue","Bonbon")

                var selected = traitement.typeComprime
                var AjoutManuelTypeMedicAdapter = AjoutManuelTypeMedicAdapterR(listeTypeMedic,selected)

                val bundle = Bundle()
                bundle.putSerializable("traitement", Traitement(traitement.nomTraitement, traitement.dosageNb, traitement.dosageUnite, null, AjoutManuelTypeMedicAdapter.selected, 25, false, null, traitement.prises,traitement.totalQuantite,traitement.UUID,traitement.UUIDUSER,traitement.dateDbtTraitement))
                bundle.putString("isAddingTraitement", "$isAddingTraitement")
                bundle.putString("schema_prise1", "$schema_prise1")
                bundle.putString("dureePriseDbt", "$dureePriseDbt")
                bundle.putString("dureePriseFin", "$dureePriseFin")

                val destinationFragment = AjoutManuelSearchFragment()
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