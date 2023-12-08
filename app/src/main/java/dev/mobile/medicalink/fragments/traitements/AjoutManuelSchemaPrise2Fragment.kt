package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.os.Bundle
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


class AjoutManuelSchemaPrise2Fragment : Fragment() {

    private lateinit var addNouvellePrise: Button
    private lateinit var retour: ImageView
    private lateinit var suivant: Button

    private var numeroPrise: Int = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_schema_prise2, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        addNouvellePrise = view.findViewById(R.id.btn_add_nouvelle_prise)
        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)


        val traitement = arguments?.getSerializable("traitement") as Traitement
        var isAddingTraitement = arguments?.getString("isAddingTraitement")
        var schema_prise1 = arguments?.getString("schema_prise1")
        var provenance = arguments?.getString("provenance")
        var dureePriseDbt = arguments?.getString("dureePriseDbt")
        var dureePriseFin = arguments?.getString("dureePriseFin")

        var listePrise: MutableList<Prise>? = traitement.prises
        if (listePrise == null) {
            listePrise =
                mutableListOf<Prise>(Prise(numeroPrise, "17:00", 1, traitement.typeComprime))
        } else {
            for (prise in listePrise) {
                prise.dosageUnite = traitement.typeComprime
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAjoutPrise)
        recyclerView.layoutManager = LinearLayoutManager(context)

        var ajoutManuelAdapter = AjoutManuelAdapterR(listePrise)
        recyclerView.adapter = ajoutManuelAdapter

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 20
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        //TODO("Pour Nicolas : Changer le listener car ça fonctionne mais que lorsque
        //      les valeurs des heures sont visibles à l'écran")

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = recyclerView.adapter?.itemCount ?: 0

                if (totalItemCount == 0) {
                    suivant.isEnabled = false
                    suivant.alpha = 0.3F
                } else {
                    suivant.isEnabled = true
                    suivant.alpha = 1F
                }

                mettreAJourCouleurs(ajoutManuelAdapter, recyclerView)
            }
        })


        addNouvellePrise.setOnClickListener {
            numeroPrise = listePrise.size + 1
            var nouvellePrise = Prise(listePrise.size + 1, "17:00", 1, traitement.typeComprime)
            listePrise.add(nouvellePrise)
            ajoutManuelAdapter.notifyItemInserted(listePrise.size - 1)
        }



        suivant.setOnClickListener {
            var totalQuantite = 0
            if (listePrise != null) {
                for (prise in listePrise) {
                    totalQuantite += prise.quantite
                }
            }
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    null,
                    traitement.typeComprime,
                    25,
                    false,
                    null,
                    listePrise,
                    totalQuantite,
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
            var destinationFragment = AjoutManuelDateSchemaPrise()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }


        retour.setOnClickListener {
            var totalQuantite = 0
            if (listePrise != null) {
                for (prise in listePrise) {
                    totalQuantite += prise.quantite
                }
            }
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.nomTraitement,
                    traitement.dosageNb,
                    traitement.dosageUnite,
                    null,
                    traitement.typeComprime,
                    25,
                    false,
                    null,
                    listePrise,
                    totalQuantite,
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
            var destinationFragment = Fragment()
            when (provenance) {
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

    override fun onResume() {
        super.onResume()

        // Attacher le gestionnaire du bouton de retour arrière de l'appareil
        val callback = object : OnBackPressedCallback(true) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun handleOnBackPressed() {
                // Code à exécuter lorsque le bouton de retour arrière est pressé
                val traitement = arguments?.getSerializable("traitement") as Traitement
                var isAddingTraitement = arguments?.getString("isAddingTraitement")
                val schema_prise1 = arguments?.getString("schema_prise1")
                val provenance = arguments?.getString("provenance")
                val dureePriseDbt = arguments?.getString("dureePriseDbt")
                val dureePriseFin = arguments?.getString("dureePriseFin")
                var listePrise: MutableList<Prise>? = traitement.prises

                if (listePrise == null) {
                    listePrise = mutableListOf<Prise>(Prise(1, "17:00", 1, traitement.typeComprime))
                } else {
                    for (prise in listePrise) {
                        prise.dosageUnite = traitement.typeComprime
                    }
                }

                val bundle = Bundle()
                bundle.putSerializable(
                    "traitement",
                    Traitement(
                        traitement.nomTraitement,
                        traitement.dosageNb,
                        traitement.dosageUnite,
                        null,
                        traitement.typeComprime,
                        25,
                        false,
                        null,
                        listePrise,
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
                var destinationFragment: Fragment = AjoutManuelSchemaPriseFragment()

                when (provenance) {
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
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    fun mettreAJourCouleurs(ajoutManuelAdapter: AjoutManuelAdapterR, recyclerView: RecyclerView) {
        val maListePrise = ajoutManuelAdapter.getItems()

        if (maListePrise != null) {
            for (prisePrincipale in 0 until maListePrise.size) {
                for (priseCompare in 0 until maListePrise.size) {
                    if (prisePrincipale !== priseCompare) {
                        val viewHolderPrincipale =
                            recyclerView.findViewHolderForAdapterPosition(prisePrincipale)
                        val viewHolderCompare =
                            recyclerView.findViewHolderForAdapterPosition(priseCompare)
                        if (viewHolderPrincipale is AjoutManuelAdapterR.AjoutManuelViewHolder &&
                            viewHolderCompare is AjoutManuelAdapterR.AjoutManuelViewHolder
                        ) {
                            if (maListePrise[prisePrincipale].heurePrise == maListePrise[priseCompare].heurePrise) {
                                // false veut dire qu'on met la couleur du texte en rouge
                                ajoutManuelAdapter.mettreAJourCouleurTexte(
                                    viewHolderPrincipale.heurePriseInput,
                                    false
                                )
                                ajoutManuelAdapter.mettreAJourCouleurTexte(
                                    viewHolderCompare.heurePriseInput,
                                    false
                                )
                                suivant.isEnabled = false
                                suivant.alpha = 0.3F
                            } else {
                                // true veut dire qu'on met la couleur du texte en noire
                                ajoutManuelAdapter.mettreAJourCouleurTexte(
                                    viewHolderPrincipale.heurePriseInput,
                                    true
                                )
                                ajoutManuelAdapter.mettreAJourCouleurTexte(
                                    viewHolderCompare.heurePriseInput,
                                    true
                                )

                                suivant.isEnabled = true
                                suivant.alpha = 1F
                            }
                        }
                    }
                }
            }
        }
    }


}