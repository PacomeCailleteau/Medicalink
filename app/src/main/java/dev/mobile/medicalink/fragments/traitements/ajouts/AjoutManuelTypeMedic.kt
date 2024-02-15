package dev.mobile.medicalink.fragments.traitements.ajouts

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement
import dev.mobile.medicalink.fragments.traitements.adapter.AjoutManuelTypeMedicAdapterR


class AjoutManuelTypeMedic : Fragment() {

    private lateinit var retour: ImageView
    private lateinit var suivant: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_type_medic, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        val listeTypeMedic: MutableList<String> =
            mutableListOf(
                resources.getString(R.string.comprime),
                resources.getString(R.string.gellule),
                resources.getString(R.string.sachet),
                resources.getString(R.string.sirop),
                resources.getString(R.string.pipette),
                resources.getString(R.string.seringue),
                resources.getString(R.string.bonbon),
            )

        var selected = viewModel.typeComprime.value.toString()
        Log.d("selected", selected)
        if (selected == "") {
            viewModel.setTypeComprime(resources.getString(R.string.comprime))
            selected = resources.getString(R.string.comprime)
        }

        val recyclerViewTypeMedic = view.findViewById<RecyclerView>(R.id.recyclerViewTypeMedic)
        recyclerViewTypeMedic.layoutManager = LinearLayoutManager(context)


        val AjoutManuelTypeMedicAdapter = AjoutManuelTypeMedicAdapterR(listeTypeMedic, selected, viewModel)
        recyclerViewTypeMedic.adapter = AjoutManuelTypeMedicAdapter



        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacement = 20
        recyclerViewTypeMedic.addItemDecoration(SpacingRecyclerView(espacement))

        suivant.setOnClickListener {
            Log.d("selected", AjoutManuelTypeMedicAdapter.selected)
            viewModel.setTypeComprime(AjoutManuelTypeMedicAdapter.selected)
            val destinationFragment = AjoutManuelSchemaPriseFragment()
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        retour.setOnClickListener {
            viewModel.setTypeComprime(AjoutManuelTypeMedicAdapter.selected)
            val destinationFragment = AjoutManuelSearchFragment()
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
                val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)
                val listeTypeMedic: MutableList<String> =
                    mutableListOf(
                        resources.getString(R.string.comprime),
                        resources.getString(R.string.gellule),
                        resources.getString(R.string.sachet),
                        resources.getString(R.string.sirop),
                        resources.getString(R.string.pipette),
                        resources.getString(R.string.seringue),
                        resources.getString(R.string.bonbon),
                    )

                var selected = viewModel.typeComprime.value.toString()
                if (selected == "") {
                    viewModel.setTypeComprime(resources.getString(R.string.comprime))
                    selected = resources.getString(R.string.comprime)
                }
                val ajoutManuelTypeMedicAdapter =
                    AjoutManuelTypeMedicAdapterR(listeTypeMedic, selected, viewModel)

                viewModel.setTypeComprime(ajoutManuelTypeMedicAdapter.selected)
                val destinationFragment = AjoutManuelSearchFragment()
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


}
