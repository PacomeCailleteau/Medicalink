package dev.mobile.medicalink.fragments.traitements


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.douleur.AffichageGraphFragment
import dev.mobile.medicalink.fragments.traitements.ajouts.AddTraitementsFragment
import dev.mobile.medicalink.utils.GoTo

class MainTraitementsFragment : Fragment() {
    private lateinit var addTraitementButton: LinearLayout
    private lateinit var traitementsButton: LinearLayout
    private lateinit var journalButton: LinearLayout
    private lateinit var graphDouleurButton: LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_traitements, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.VISIBLE
        }

        addTraitementButton = view.findViewById(R.id.cardaddtraitements)
        traitementsButton = view.findViewById(R.id.cardtraitements)
        journalButton = view.findViewById(R.id.cardjournal)
        graphDouleurButton = view.findViewById(R.id.cardDouleur)


        //Si on clique sur le bouton "Ajouter un traitement" alors on change le fragment actuel (MainTraitementsFragment) par le fragment AddTraitementsFragment
        addTraitementButton.setOnClickListener {
            GoTo.fragment(AddTraitementsFragment(), parentFragmentManager)
        }

        //Si on clique sur le bouton "Traitements" alors on change le fragment actuel (MainTraitementsFragment) par le fragment ListeTraitementsFragment
        traitementsButton.setOnClickListener {
            GoTo.fragment(ListeTraitementsFragment(), parentFragmentManager)
        }

        journalButton.setOnClickListener {
            GoTo.fragment(ListeEffetsSecondairesFragment(), parentFragmentManager)
        }

        graphDouleurButton.setOnClickListener {
            GoTo.fragment(AffichageGraphFragment(), parentFragmentManager)
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Ne fait rien ici pour désactiver le bouton de retour arrière
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



        return view
    }
}
