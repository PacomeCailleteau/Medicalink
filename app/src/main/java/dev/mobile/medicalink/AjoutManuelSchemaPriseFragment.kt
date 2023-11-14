package dev.mobile.medicalink

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi


class AjoutManuelSchemaPriseFragment : Fragment() {

    private lateinit var quotidiennementButton: Button
    private lateinit var intervalleRegulierButton: Button
    private lateinit var auBesoinButton: Button

    private lateinit var suivant : Button
    private lateinit var retour: ImageView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_schema_prise, container, false)
        val traitement = arguments?.getSerializable("traitement") as Traitement
        var schema_prise1  = arguments?.getString("schema_prise1")

        quotidiennementButton = view.findViewById(R.id.quotidiennement_button)
        intervalleRegulierButton = view.findViewById(R.id.intervalle_regulier_button)
        auBesoinButton = view.findViewById(R.id.au_besoin_button)

        suivant = view.findViewById(R.id.suivant1)
        retour = view.findViewById(R.id.retour_schema_prise2)


        val textePrincipal = "Quotidiennement"
        val sousTexte = "Même heure, même quantité"
        val spannableTextePrincipal = SpannableString(textePrincipal)
        val spannableSousTexte = SpannableString(sousTexte)
        spannableSousTexte.setSpan(ForegroundColorSpan(android.graphics.Color.parseColor("#6B70A0")), 0, sousTexte.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableSousTexte.setSpan(RelativeSizeSpan(0.8f), 0, sousTexte.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Ajustez la taille (0.8f) selon vos besoins
        val texteComplet = SpannableStringBuilder()
        texteComplet.append(spannableTextePrincipal)
        texteComplet.append("\n") // Pour ajouter le sous-texte en dessous
        texteComplet.append(spannableSousTexte)
        quotidiennementButton.text = texteComplet

        val textePrincipal2 = "Intervalle régulier"
        val sousTexte2 = "Ex : Tous les 3 jours, toutes les 2 semaines"
        val spannableTextePrincipal2 = SpannableString(textePrincipal2)
        val spannableSousTexte2 = SpannableString(sousTexte2)
        spannableSousTexte2.setSpan(ForegroundColorSpan(android.graphics.Color.parseColor("#6B70A0")), 0, sousTexte2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableSousTexte2.setSpan(RelativeSizeSpan(0.8f), 0, sousTexte2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Ajustez la taille (0.8f) selon vos besoins
        val texteComplet2 = SpannableStringBuilder()
        texteComplet2.append(spannableTextePrincipal2)
        texteComplet2.append("\n") // Pour ajouter le sous-texte en dessous
        texteComplet2.append(spannableSousTexte2)
        intervalleRegulierButton.text = texteComplet2

        val textePrincipal3 ="Au besoin"
        val sousTexte3 = "Pas de prise régulière"
        val spannableTextePrincipal3 = SpannableString(textePrincipal3)
        val spannableSousTexte3 = SpannableString(sousTexte3)
        spannableSousTexte3.setSpan(ForegroundColorSpan(android.graphics.Color.parseColor("#6B70A0")), 0, sousTexte3.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableSousTexte3.setSpan(RelativeSizeSpan(0.8f), 0, sousTexte3.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Ajustez la taille (0.8f) selon vos besoins
        val texteComplet3 = SpannableStringBuilder()
        texteComplet3.append(spannableTextePrincipal3)
        texteComplet3.append("\n") // Pour ajouter le sous-texte en dessous
        texteComplet3.append(spannableSousTexte3)
        auBesoinButton.text = texteComplet3


        when (schema_prise1) {
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
        }



        quotidiennementButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            schema_prise1 = "Quotidiennement"
        }

        intervalleRegulierButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            schema_prise1 = "Intervalle"

        }

        auBesoinButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            schema_prise1 = "auBesoin"

        }

        suivant.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,0,"Comprimé",null,25,false,null,traitement.prises))
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "quotidiennement")
            var destinationFragment = Fragment()
            when (schema_prise1){
                "Quotidiennement" -> {
                    destinationFragment = AjoutManuelSchemaPrise2Fragment()

                }
                "Intervalle" -> {
                    destinationFragment = AjoutManuelIntervalleRegulier()

                }
                "auBesoin" -> {
                    destinationFragment = AjoutManuelSchemaPrise2Fragment()

                }
            }
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }



        //On retourne au fragment précédent
        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable("traitement", Traitement(traitement.nomTraitement,0,"Comprimé",null,25,false,null,traitement.prises))
            bundle.putString("schema_prise1", "$schema_prise1")
            val destinationFragment = AjoutManuelSearchFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }


}