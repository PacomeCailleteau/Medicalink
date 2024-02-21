package dev.mobile.medicalink.fragments.traitements.ajouts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.EnumFrequence
import dev.mobile.medicalink.fragments.traitements.ListeTraitementsFragment
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.adapter.RecapAdapterR
import dev.mobile.medicalink.utils.GoTo


class AjoutManuelRecapitulatif : Fragment() {

    private lateinit var retour: ImageView
    private lateinit var suivant: Button

    private lateinit var nomMedoc: TextView
    private lateinit var textUnite: TextView
    private lateinit var textStock: TextView
    private lateinit var dateFindeTraitement: TextView
    private lateinit var sousNomPeriodicite: TextView

    private lateinit var nomLayout: ConstraintLayout
    private lateinit var caracteristiqueLayout: ConstraintLayout
    private lateinit var periodiciteLayout: ConstraintLayout
    private lateinit var priseLayout: ConstraintLayout
    private lateinit var reapprovisionnementLayout: ConstraintLayout


    @SuppressLint("SetTextI18n")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_ajout_manuel_recapitulatif, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        nomMedoc = view.findViewById(R.id.nomMedoc)
        textUnite = view.findViewById(R.id.textUnite)
        textStock = view.findViewById(R.id.textStock)
        dateFindeTraitement = view.findViewById(R.id.dateFinTraitementText)
        sousNomPeriodicite = view.findViewById(R.id.sousNomPeriodicite)

        nomLayout = view.findViewById(R.id.nomLayout)
        caracteristiqueLayout = view.findViewById(R.id.caracteristiqueLayout)
        periodiciteLayout = view.findViewById(R.id.periodiciteLayout)
        priseLayout = view.findViewById(R.id.priseLayout)
        reapprovisionnementLayout = view.findViewById(R.id.reapprovionnementLayout)

        var schemaPriseFormatee = ""
        when (viewModel.schemaPrise1.value) {
            "Quotidiennement" -> {
                schemaPriseFormatee = getString(R.string.quotidiennement)
            }

            "Intervalle" -> {
                schemaPriseFormatee =
                    if (viewModel.frequencePrise.value == EnumFrequence.SEMAINE) {
                        getString(R.string.toutes_les) +
                                " ${viewModel.dosageNb.value} ${viewModel.frequencePrise.value}"
                    } else {
                        getString(R.string.tous_les) +
                                " ${viewModel.dosageNb.value} ${viewModel.frequencePrise.value}"
                    }
            }

            "auBesoin" -> {
                schemaPriseFormatee = getString(R.string.au_besoin)
            }
        }

        nomMedoc.text = viewModel.nomTraitement.value
        textUnite.text = viewModel.typeComprime.value
        textStock.text = "${viewModel.comprimesRestants.value} ${viewModel.typeComprime.value}"
        if (viewModel.comprimesRestants.value!! > 1) {
            textStock.text = "${textStock.text}s"
        }
        val dft = viewModel.dateFinTraitement.value
        if (dft == null) {
            dateFindeTraitement.text = resources.getString(R.string.sans_fin)
        } else {
            dateFindeTraitement.text =
                "${dft.dayOfMonth}/${dft.monthValue}/${dft.year}"
        }

        sousNomPeriodicite.text = schemaPriseFormatee

        if (schemaPriseFormatee != "Au besoin") {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_recap)
            recyclerView.layoutManager = LinearLayoutManager(context)
            var liste: MutableList<Prise>
            liste = mutableListOf()
            if (viewModel.prises.value != null) {
                liste = viewModel.prises.value!!.toMutableList()
            }
            recyclerView.adapter = RecapAdapterR(liste)
            // Gestion de l'espacement entre les éléments du RecyclerView
            val espacementEnDp = 5
            recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))
        } else {
            priseLayout.visibility = View.GONE
        }

        suivant.setOnClickListener {
            GoTo.fragment(ListeTraitementsFragment(), parentFragmentManager)
        }

        retour.setOnClickListener {
            GoTo.fragment(AjoutManuelStock(), parentFragmentManager)
        }
        nomLayout.setOnClickListener {
            GoTo.fragment(AjoutManuelSearchFragment(), parentFragmentManager)
        }
        caracteristiqueLayout.setOnClickListener {
            GoTo.fragment(AjoutManuelTypeMedic(), parentFragmentManager)
        }

        periodiciteLayout.setOnClickListener {
            GoTo.fragment(AjoutManuelSchemaPriseFragment(), parentFragmentManager)
        }

        priseLayout.setOnClickListener {
            GoTo.fragment(AjoutManuelSchemaPrise2Fragment(), parentFragmentManager)
        }

        reapprovisionnementLayout.setOnClickListener {
            GoTo.fragment(AjoutManuelStock(), parentFragmentManager)
        }

        return view
    }
}
