package dev.mobile.medicalink.fragments.traitements.ajoutmanuel

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Traitement


class AjoutManuelSchemaPriseFragment : Fragment() {

    private lateinit var quotidiennementButton: Button
    private lateinit var intervalleRegulierButton: Button
    private lateinit var auBesoinButton: Button

    private lateinit var suivant: Button
    private lateinit var retour: ImageView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_ajout_manuel_schema_prise, container, false)
        val traitement = arguments?.getSerializable("traitement") as Traitement
        val isAddingTraitement = arguments?.getString("isAddingTraitement")
        var schema_prise1 = arguments?.getString("schema_prise1")
        val dureePriseDbt = arguments?.getString("dureePriseDbt")
        val dureePriseFin = arguments?.getString("dureePriseFin")

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        quotidiennementButton = view.findViewById(R.id.debutAjd)
        intervalleRegulierButton = view.findViewById(R.id.intervalle_regulier_button)
        auBesoinButton = view.findViewById(R.id.au_besoin_button)

        suivant = view.findViewById(R.id.suivant1)
        retour = view.findViewById(R.id.retour_schema_prise2)


        val textePrincipal = resources.getString(R.string.quotidiennement)
        val sousTexte = resources.getString(R.string.meme_heure_meme_quanti)
        val spannableTextePrincipal = SpannableString(textePrincipal)
        val spannableSousTexte = SpannableString(sousTexte)
        spannableSousTexte.setSpan(
            ForegroundColorSpan(android.graphics.Color.parseColor("#6B70A0")),
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
            ForegroundColorSpan(android.graphics.Color.parseColor("#6B70A0")),
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
            ForegroundColorSpan(android.graphics.Color.parseColor("#6B70A0")),
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
            if (schema_prise1 != "Quotidiennement") {
                traitement.prises = null
            }
            schema_prise1 = "Quotidiennement"
        }

        intervalleRegulierButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            if (schema_prise1 != "Intervalle") {
                traitement.prises = null
            }
            schema_prise1 = "Intervalle"

        }

        auBesoinButton.setOnClickListener {
            quotidiennementButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            intervalleRegulierButton.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
            auBesoinButton.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
            if (schema_prise1 != "auBesoin") {
                traitement.prises = null
            }
            schema_prise1 = "auBesoin"

        }

        suivant.setOnClickListener {
            var dosageUnite = ""
            if (schema_prise1 != null) {
                when (schema_prise1) {
                    "Quotidiennement" -> {
                        dosageUnite = resources.getString(R.string.quoti)
                    }

                    "Intervalle" -> {
                        dosageUnite = resources.getString(R.string.semaines)
                    }

                    "auBesoin" -> {
                        dosageUnite = "auBesoin"
                    }
                }
            }
            //Gestion de la redirection en fonction du bouton sélectionné

            val bundle = Bundle()
            var destinationFragment = Fragment()
            when (schema_prise1) {
                "Quotidiennement" -> {
                    destinationFragment = AjoutManuelSchemaPrise2Fragment()
                    bundle.putSerializable(
                        "traitement",
                        Traitement(
                            traitement.CodeCIS,
                            traitement.nomTraitement,
                            traitement.dosageNb,
                            dosageUnite,
                            null,
                            traitement.typeComprime,
                            traitement.comprimesRestants,
                            false,
                            null,
                            traitement.prises,
                            traitement.totalQuantite,
                            traitement.UUID,
                            traitement.UUIDUSER,
                            traitement.dateDbtTraitement
                        )
                    )
                    bundle.putString("isAddingTraitement", "$isAddingTraitement")
                    bundle.putString("schema_prise1", "$schema_prise1")
                    bundle.putString("provenance", "quotidiennement")
                    bundle.putString("dureePriseDbt", "$dureePriseDbt")
                    bundle.putString("dureePriseFin", "$dureePriseFin")
                }

                "Intervalle" -> {
                    destinationFragment = AjoutManuelIntervalleRegulier()
                    bundle.putSerializable(
                        "traitement",
                        Traitement(
                            traitement.CodeCIS,
                            traitement.nomTraitement,
                            traitement.dosageNb,
                            dosageUnite,
                            null,
                            traitement.typeComprime,
                            traitement.comprimesRestants,
                            false,
                            null,
                            traitement.prises,
                            traitement.totalQuantite,
                            traitement.UUID,
                            traitement.UUIDUSER,
                            traitement.dateDbtTraitement
                        )
                    )
                    bundle.putString("isAddingTraitement", "$isAddingTraitement")
                    bundle.putString("schema_prise1", "$schema_prise1")
                    bundle.putString("provenance", "intervalleRegulier")
                    bundle.putString("dureePriseDbt", "$dureePriseDbt")
                    bundle.putString("dureePriseFin", "$dureePriseFin")
                }

                "auBesoin" -> {
                    destinationFragment = AjoutManuelDateSchemaPrise()
                    bundle.putSerializable(
                        "traitement",
                        Traitement(
                            traitement.CodeCIS,
                            traitement.nomTraitement,
                            traitement.dosageNb,
                            "auBesoin",
                            null,
                            traitement.typeComprime,
                            traitement.comprimesRestants,
                            false,
                            null,
                            traitement.prises,
                            traitement.totalQuantite,
                            traitement.UUID,
                            traitement.UUIDUSER,
                            traitement.dateDbtTraitement
                        )
                    )
                    bundle.putString("isAddingTraitement", "$isAddingTraitement")
                    bundle.putString("schema_prise1", "$schema_prise1")
                    bundle.putString("provenance", "auBesoin")
                    bundle.putString("dureePriseDbt", "$dureePriseDbt")
                    bundle.putString("dureePriseFin", "$dureePriseFin")
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
            var dosageUnite = ""
            if (schema_prise1 != null) {
                when (schema_prise1) {
                    "Quotidiennement" -> {
                        dosageUnite = resources.getString(R.string.jour)
                    }

                    "Intervalle" -> {
                        dosageUnite = resources.getString(R.string.semaines)
                    }

                    "auBesoin" -> {
                        dosageUnite = "auBesoin"
                    }
                }
            }

            if (isAddingTraitement == "false") {
                val bundle = Bundle()
                bundle.putSerializable(
                    "traitement",
                    Traitement(
                        traitement.CodeCIS,
                        traitement.nomTraitement,
                        traitement.dosageNb,
                        traitement.dosageUnite,
                        null,
                        traitement.typeComprime,
                        traitement.comprimesRestants,
                        false,
                        null,
                        traitement.prises,
                        traitement.totalQuantite,
                        traitement.UUID,
                        traitement.UUIDUSER,
                        traitement.dateDbtTraitement
                    )
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

            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                Traitement(
                    traitement.CodeCIS,
                    traitement.nomTraitement,
                    traitement.dosageNb,
                    dosageUnite,
                    null,
                    traitement.typeComprime,
                    traitement.comprimesRestants,
                    false,
                    null,
                    traitement.prises,
                    traitement.totalQuantite,
                    traitement.UUID,
                    traitement.UUIDUSER,
                    traitement.dateDbtTraitement
                )
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelTypeMedic()
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

                if (isAddingTraitement == "false") {
                    val bundle = Bundle()
                    bundle.putSerializable(
                        "traitement",
                        Traitement(
                            traitement.CodeCIS,
                            traitement.nomTraitement,
                            traitement.dosageNb,
                            traitement.dosageUnite,
                            null,
                            AjoutManuelTypeMedicAdapter.selected,
                            traitement.comprimesRestants,
                            false,
                            null,
                            traitement.prises,
                            traitement.totalQuantite,
                            traitement.UUID,
                            traitement.UUIDUSER,
                            traitement.dateDbtTraitement
                        )
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
                    Traitement(
                        traitement.CodeCIS,
                        traitement.nomTraitement,
                        traitement.dosageNb,
                        traitement.dosageUnite,
                        null,
                        AjoutManuelTypeMedicAdapter.selected,
                        traitement.comprimesRestants,
                        false,
                        null,
                        traitement.prises,
                        traitement.totalQuantite,
                        traitement.UUID,
                        traitement.UUIDUSER,
                        traitement.dateDbtTraitement
                    )
                )
                bundle.putString("isAddingTraitement", "$isAddingTraitement")
                bundle.putString("schema_prise1", "$schema_prise1")
                bundle.putString("dureePriseDbt", "$dureePriseDbt")
                bundle.putString("dureePriseFin", "$dureePriseFin")

                val destinationFragment = AjoutManuelTypeMedic()
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
