package dev.mobile.medicalink.fragments.traitements.ajouts

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
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
import dev.mobile.medicalink.R
import dev.mobile.medicalink.utils.GoTo


class AjoutManuelSchemaPriseFragment : Fragment() {

    private lateinit var quotidiennementButton: Button
    private lateinit var intervalleRegulierButton: Button
    private lateinit var auBesoinButton: Button

    private lateinit var suivant: Button
    private lateinit var retour: ImageView

    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_schema_prise, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        quotidiennementButton = view.findViewById(R.id.debutAjd)
        intervalleRegulierButton = view.findViewById(R.id.intervalle_regulier_button)
        auBesoinButton = view.findViewById(R.id.au_besoin_button)

        suivant = view.findViewById(R.id.suivant1)
        retour = view.findViewById(R.id.retour_schema_prise2)

        val couleurSousTexte = resources.getColor(R.color.scheduleBlue)

        val textePrincipal = resources.getString(R.string.quotidiennement)
        val sousTexte = resources.getString(R.string.meme_heure_meme_quanti)
        val spannableTextePrincipal = SpannableString(textePrincipal)
        val spannableSousTexte = SpannableString(sousTexte)
        spannableSousTexte.setSpan(
            ForegroundColorSpan(couleurSousTexte),
            0,
            sousTexte.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableSousTexte.setSpan(
            RelativeSizeSpan(0.8f),
            0,
            sousTexte.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        ) // Ajustez la taille (0.8f) selon vos besoins
        val texteComplet = SpannableStringBuilder()
        texteComplet.append(spannableTextePrincipal)
        texteComplet.append("\n") // Pour ajouter le sous-texte en dessous
        texteComplet.append(spannableSousTexte)
        quotidiennementButton.text = texteComplet

        val textePrincipal2 = resources.getString(R.string.dialog_intervalle_regulier)
        val sousTexte2 = resources.getString(R.string.tous_les_3_jours_toutes_les_2_semaines)
        val spannableTextePrincipal2 = SpannableString(textePrincipal2)
        val spannableSousTexte2 = SpannableString(sousTexte2)
        spannableSousTexte2.setSpan(
            ForegroundColorSpan(couleurSousTexte),
            0,
            sousTexte2.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableSousTexte2.setSpan(
            RelativeSizeSpan(0.8f),
            0,
            sousTexte2.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        ) // Ajustez la taille (0.8f) selon vos besoins
        val texteComplet2 = SpannableStringBuilder()
        texteComplet2.append(spannableTextePrincipal2)
        texteComplet2.append("\n") // Pour ajouter le sous-texte en dessous
        texteComplet2.append(spannableSousTexte2)
        intervalleRegulierButton.text = texteComplet2

        val textePrincipal3 = resources.getString(R.string.au_besoin)
        val sousTexte3 = resources.getString(R.string.pas_de_prise_regu)
        val spannableTextePrincipal3 = SpannableString(textePrincipal3)
        val spannableSousTexte3 = SpannableString(sousTexte3)
        spannableSousTexte3.setSpan(
            ForegroundColorSpan(couleurSousTexte),
            0,
            sousTexte3.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableSousTexte3.setSpan(
            RelativeSizeSpan(0.8f),
            0,
            sousTexte3.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        ) // Ajustez la taille (0.8f) selon vos besoins
        val texteComplet3 = SpannableStringBuilder()
        texteComplet3.append(spannableTextePrincipal3)
        texteComplet3.append("\n") // Pour ajouter le sous-texte en dessous
        texteComplet3.append(spannableSousTexte3)
        auBesoinButton.text = texteComplet3


        when (viewModel.schema_prise1.value) {
            "Quotidiennement" -> {
                quotidiennementButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
                intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
                auBesoinButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            }

            "Intervalle" -> {
                quotidiennementButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
                intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
                auBesoinButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            }

            "auBesoin" -> {
                quotidiennementButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
                intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
                auBesoinButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            }

            "" -> {
                viewModel.setSchemaPrise1("Quotidiennement")
                quotidiennementButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
                intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
                auBesoinButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            }
        }



        quotidiennementButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            if (viewModel.schema_prise1.value != "Quotidiennement") {
                viewModel.setPrises(mutableListOf())
            }
            viewModel.setSchemaPrise1("Quotidiennement")
        }

        intervalleRegulierButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            if (viewModel.schema_prise1.value != "Intervalle") {
                viewModel.setPrises(mutableListOf())
            }
            viewModel.setSchemaPrise1("Intervalle")

        }

        auBesoinButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            if (viewModel.schema_prise1.value != "auBesoin") {
                viewModel.setPrises(mutableListOf())
            }
            viewModel.setSchemaPrise1("auBesoin")

        }

        Log.d("cacapipi", viewModel.frequencePrise.value.toString())

        suivant.setOnClickListener {
            //Gestion de la redirection en fonction du bouton sélectionné

            var destinationFragment = Fragment()
            when (viewModel.schema_prise1.value) {
                "Quotidiennement" -> {
                    destinationFragment = AjoutManuelSchemaPrise2Fragment()
                    viewModel.setFrequencePrise("quotidiennement")
                    viewModel.setProvenance("quotidiennement")
                }

                "Intervalle" -> {
                    destinationFragment = AjoutManuelIntervalleRegulier()
                    viewModel.setProvenance("intervalleRegulier")
                }

                "auBesoin" -> {
                    destinationFragment = AjoutManuelDateSchemaPrise()
                    viewModel.setFrequencePrise("auBesoin")
                    viewModel.setProvenance("auBesoin")
                }
            }
            GoTo.fragment(destinationFragment, parentFragmentManager)
        }


        //On retourne au fragment précédent
        retour.setOnClickListener {
            GoTo.fragment(AjoutManuelTypeMedic(), parentFragmentManager)
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        val callback = object : OnBackPressedCallback(true) {
            
            override fun handleOnBackPressed() {
                GoTo.fragment(AjoutManuelTypeMedic(), parentFragmentManager)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


}
