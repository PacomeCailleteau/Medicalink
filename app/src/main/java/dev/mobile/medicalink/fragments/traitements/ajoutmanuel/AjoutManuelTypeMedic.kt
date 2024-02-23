package dev.mobile.medicalink.fragments.traitements.ajoutmanuel

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement


class AjoutManuelTypeMedic : Fragment() {

    private lateinit var retour: ImageView
    private lateinit var suivant: Button


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_ajout_manuel_type_medic, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        val traitement = arguments?.getSerializable("traitement") as Traitement
        val isAddingTraitement = arguments?.getString("isAddingTraitement")
        val schema_prise1 = arguments?.getString("schema_prise1")
        val dureePriseDbt = arguments?.getString("dureePriseDbt")
        val dureePriseFin = arguments?.getString("dureePriseFin")


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

        val selected = traitement.typeComprime

        val recyclerViewTypeMedic = view.findViewById<RecyclerView>(R.id.recyclerViewTypeMedic)
        recyclerViewTypeMedic.layoutManager = LinearLayoutManager(context)


        val AjoutManuelTypeMedicAdapter = AjoutManuelTypeMedicAdapterR(listeTypeMedic, selected)
        recyclerViewTypeMedic.adapter = AjoutManuelTypeMedicAdapter

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacement = 20
        recyclerViewTypeMedic.addItemDecoration(SpacingRecyclerView(espacement))


        suivant.setOnClickListener {
            val bundle = Bundle()
            Log.d("LLLL", AjoutManuelTypeMedicAdapter.selected)

            traitement.dateFinTraitement = null
            traitement.typeComprime = AjoutManuelTypeMedicAdapter.selected
            traitement.expire = false
            traitement.effetsSecondaires = null
            bundle.putSerializable(
                "traitement",
                traitement
            )
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
            traitement.dateFinTraitement = null
            traitement.expire = false
            traitement.effetsSecondaires = null

            if (isAddingTraitement == "false") {
                val bundle = Bundle()
                bundle.putSerializable(
                    "traitement",
                    traitement
                )
                bundle.putString("isAddingTraitement", "$isAddingTraitement")
                bundle.putString("schema_prise1", "$schema_prise1")
                bundle.putString("dureePriseDbt", "$dureePriseDbt")
                bundle.putString("dureePriseFin", "$dureePriseFin")
                val destinationFragment = AjoutManuelRecapitulatif()
                destinationFragment.arguments = bundle
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
                return@setOnClickListener
            }
            traitement.typeComprime = AjoutManuelTypeMedicAdapter.selected

            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                traitement
            )
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
                val isAddingTraitement = arguments?.getString("isAddingTraitement")
                val schema_prise1 = arguments?.getString("schema_prise1")
                val dureePriseDbt = arguments?.getString("dureePriseDbt")
                val dureePriseFin = arguments?.getString("dureePriseFin")

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

                val selected = traitement.typeComprime
                val AjoutManuelTypeMedicAdapter =
                    AjoutManuelTypeMedicAdapterR(listeTypeMedic, selected)

                traitement.dateFinTraitement = null
                traitement.expire = false
                traitement.effetsSecondaires = null
                traitement.typeComprime = AjoutManuelTypeMedicAdapter.selected

                if (isAddingTraitement == "false") {
                    val bundle = Bundle()
                    bundle.putSerializable(
                        "traitement",
                        traitement
                    )
                    bundle.putString("isAddingTraitement", "$isAddingTraitement")
                    bundle.putString("schema_prise1", "$schema_prise1")
                    bundle.putString("dureePriseDbt", "$dureePriseDbt")
                    bundle.putString("dureePriseFin", "$dureePriseFin")
                    val destinationFragment = AjoutManuelRecapitulatif()
                    destinationFragment.arguments = bundle
                    val fragTransaction = parentFragmentManager.beginTransaction()
                    fragTransaction.replace(R.id.FL, destinationFragment)
                    fragTransaction.addToBackStack(null)
                    fragTransaction.commit()
                    return
                }

                val bundle = Bundle()
                bundle.putSerializable(
                    "traitement",
                    traitement
                )
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
