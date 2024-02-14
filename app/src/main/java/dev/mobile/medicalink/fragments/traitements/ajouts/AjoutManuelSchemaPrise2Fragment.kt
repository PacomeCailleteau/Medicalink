package dev.mobile.medicalink.fragments.traitements.ajouts

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.adapter.AjoutManuelAdapterR
import java.util.UUID


class AjoutManuelSchemaPrise2Fragment : Fragment() {

    private lateinit var addNouvellePrise: Button
    private lateinit var retour: ImageView
    private lateinit var suivant: Button


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_schema_prise2, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        addNouvellePrise = view.findViewById(R.id.btn_add_nouvelle_prise)
        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        var listePrise: MutableList<Prise>? = viewModel.prises.value
        if (listePrise == null) {
            listePrise =
                mutableListOf(
                    Prise(
                        UUID.randomUUID().toString(),
                        resources.getString(R.string._17_00),
                        1,
                        viewModel.typeComprime.value?.toString() ?: ""
                    )
                )
        } else {
            for (prise in listePrise) {
                prise.typeComprime = viewModel.typeComprime.value?.toString() ?: ""
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAjoutPrise)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val ajoutManuelAdapter = AjoutManuelAdapterR(listePrise)
        recyclerView.adapter = ajoutManuelAdapter

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 20
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        //Gestion du bouton pour ajouter une nouvelle prise
        addNouvellePrise.setOnClickListener {
            val nouvellePrise = Prise(
                UUID.randomUUID().toString(),
                resources.getString(R.string._17_00),
                1,
                viewModel.typeComprime.value.toString()
            )
            listePrise.add(nouvellePrise)
            ajoutManuelAdapter.notifyItemInserted(listePrise.size - 1)
            recyclerView.scrollToPosition(listePrise.size - 1)
        }

        suivant.setOnClickListener {
            //s'il y a des conflits d'heures de prises, on ne peut pas passer à l'étape suivante et on affiche un message d'erreur sous forme de Toast
            if (conflitsHeuresPrises(listePrise)) {
                mettreAJourCouleurs(listePrise, recyclerView)
                Toast.makeText(
                    requireContext(),
                    "Il y a des conflits d'heures de prises, veuillez les modifier",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            //S'il n'y a plus de prise, on ne peut pas passer à l'étape suivante et on affiche un message d'erreur sous forme de Toast
            if (listePrise.size == 0) {
                Toast.makeText(
                    requireContext(),
                    "Veuillez ajouter au moins une prise",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val totalQuantite: Int = listePrise.sumOf { it.quantite }
            viewModel.setPrises(listePrise)
            viewModel.setTotalQuantite(totalQuantite)
            val destinationFragment = AjoutManuelDateSchemaPrise()
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        retour.setOnClickListener {
            val totalQuantite: Int = listePrise.sumOf { it.quantite }
            viewModel.setPrises(listePrise)
            viewModel.setTotalQuantite(totalQuantite)

            var destinationFragment = Fragment()
            when (viewModel.provenance.value) {
                "quotidiennement" -> {
                    destinationFragment = AjoutManuelSchemaPriseFragment()

                }
                "intervalleRegulier" -> {
                    destinationFragment = AjoutManuelIntervalleRegulier()
                }
                null -> {
                    destinationFragment = AjoutManuelSchemaPriseFragment()
                }
            }
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }


        return view
    }

    /**
     * Méthode qui vérifie s'il y a des conflits d'heures de prises
     * @param listePrise la liste des prises
     * @return true s'il y a des conflits d'heures de prises, false sinon
     */
    private fun conflitsHeuresPrises(listePrise: MutableList<Prise>): Boolean {
        for (prisePrincipale in 0 until listePrise.size) {
            for (priseCompare in prisePrincipale + 1 until listePrise.size) {
                if (listePrise[prisePrincipale].heurePrise == listePrise[priseCompare].heurePrise) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Méthode qui gère le bouton de retour arrière de l'appareil
     */
    override fun onResume() {
        super.onResume()

        // Attacher le gestionnaire du bouton de retour arrière de l'appareil
        val callback = object : OnBackPressedCallback(true) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun handleOnBackPressed() {
                // Code à exécuter lorsque le bouton de retour arrière est pressé
                val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)
                var listePrise: MutableList<Prise>? = viewModel.prises.value

                if (listePrise == null) {
                    listePrise = mutableListOf<Prise>(
                        Prise(
                            UUID.randomUUID().toString(),
                            resources.getString(R.string._17_00),
                            1,
                            viewModel.typeComprime.value?.toString() ?: ""
                        )
                    )
                } else {
                    for (prise in listePrise) {
                        prise.typeComprime = viewModel.typeComprime.value?.toString() ?: ""
                    }
                }

                val totalQuantite: Int = listePrise.sumOf { it.quantite }
                viewModel.setPrises(listePrise)
                viewModel.setTotalQuantite(totalQuantite)

                var destinationFragment: Fragment = AjoutManuelSchemaPriseFragment()

                when (viewModel.provenance.value) {
                    "quotidiennement" -> {
                        destinationFragment = AjoutManuelSchemaPriseFragment()
                    }

                    "intervalleRegulier" -> {
                        destinationFragment = AjoutManuelIntervalleRegulier()
                    }
                }
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /**
     * Méthode qui récupère tous les index des prises qui ont des conflits d'heures de prises puis qui appellent la méthode changerCouleur
     * @param listePrise la liste des prises
     * @param recyclerView le recyclerView contenant les prises
     */
    private fun mettreAJourCouleurs(listePrise: MutableList<Prise>, recyclerView: RecyclerView) {
        val indexAMettreEnRouge = mutableSetOf<Int>()
        for (prisePrincipale in 0 until listePrise.size) {
            for (priseCompare in prisePrincipale + 1 until listePrise.size) {
                if (listePrise[prisePrincipale].heurePrise == listePrise[priseCompare].heurePrise) {
                    indexAMettreEnRouge.add(prisePrincipale)
                    indexAMettreEnRouge.add(priseCompare)
                }
            }
        }
        changerCouleur(indexAMettreEnRouge, recyclerView)
    }

    /**
     * Méthode qui change la couleur des heures de prises en rouge si elles ont des conflits d'heures de prises et en noir sinon
     * @param indexAMettreEnRouge la liste des index des prises qui ont des conflits d'heures de prises
     * @param recyclerView le recyclerView contenant les prises
     */
    private fun changerCouleur(indexAMettreEnRouge: MutableSet<Int>, recyclerView: RecyclerView) {
        for (index in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(index)
            val heurePriseInput = child.findViewById<TextInputEditText>(R.id.heurePriseInput)
            // En rouge si conflit d'heures de prises, en noir sinon
            if (indexAMettreEnRouge.contains(index)) {
                heurePriseInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            } else {
                heurePriseInput.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            }
        }
    }


}




