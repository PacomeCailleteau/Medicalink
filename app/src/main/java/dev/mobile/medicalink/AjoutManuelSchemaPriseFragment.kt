package dev.mobile.medicalink

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
import android.widget.Button
import android.widget.ImageView


class AjoutManuelSchemaPriseFragment : Fragment() {

    private lateinit var quotidiennementButton: Button
    private lateinit var intervalleRegulierButton: Button
    private lateinit var auBesoinButton: Button

    private lateinit var suivant : Button
    private lateinit var retour: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_schema_prise, container, false)


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



        quotidiennementButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
        }

        intervalleRegulierButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
        }

        auBesoinButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
        }

        suivant.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, AjoutManuelSchemaPrise2Fragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }



        //On retourne au fragment précédent
        retour.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, AjoutManuelSearchFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }


}