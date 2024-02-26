package dev.mobile.medicalink.fragments.traitements

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.CisSideInfos
import dev.mobile.medicalink.db.local.repository.CisBdpmRepository
import dev.mobile.medicalink.db.local.repository.CisCompoBdpmRepository
import dev.mobile.medicalink.db.local.repository.CisSideInfosRepository

class InfoMedicamentFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_descriptif_medoc, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val cisCompoDatabaseInterface = CisCompoBdpmRepository(db.cisCompoBdpmDao())
        val cisBdpmDatabaseInterface = CisBdpmRepository(db.cisBdpmDao())
        val cisSideInfosDatabaseInterface = CisSideInfosRepository(db.cisSideInfosDao())

        val codeCis = arguments?.getString("codeCIS")

        val retour = view.findViewById<ImageView>(R.id.retour_descriptif_medoc)

        retour.setOnClickListener {
            val destinationFragment = ListeTraitementsFragment()
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        //informations du medicament : code CIS, denomination, forme pharmaceutique, voies administration, etat commercialisation, date AMM, titulaire, surveillance renforcée
        val nomMedocView = view.findViewById<TextView>(R.id.nomCompletMedoc)
        val codeCisView = view.findViewById<TextView>(R.id.cis)
        val substanceActiveDosageView = view.findViewById<TextView>(R.id.substanceActiveInfoMedoc)
        val textSubstanceActiveDosageView =
            view.findViewById<TextView>(R.id.textSubstanceActiveInfoMedoc)
        val formePharmaceutiqueView = view.findViewById<TextView>(R.id.formePharmaceutique)
        val textFormePharmaceutiqueView = view.findViewById<TextView>(R.id.textFormePharmaceutique)
        val voiesAdministrationView = view.findViewById<TextView>(R.id.voieAdmission)
        val textVoiesAdministrationView = view.findViewById<TextView>(R.id.textVoieAdmission)
        val etatCommercialisationView = view.findViewById<TextView>(R.id.etatDeCommercialisation)
        val textEtatCommercialisationView =
            view.findViewById<TextView>(R.id.textEtatDeCommercialisation)
        val dateAmmView = view.findViewById<TextView>(R.id.dateAMM)
        val textDateAmmView = view.findViewById<TextView>(R.id.textDateAMM)
        val titulaireView = view.findViewById<TextView>(R.id.laboratoire)
        val textTitulaireView = view.findViewById<TextView>(R.id.textLaboratoire)
        val surveillanceRenforceeView = view.findViewById<TextView>(R.id.surveillanceRenforcee)
        val textSurveillanceRenforceeView =
            view.findViewById<TextView>(R.id.textSurveillanceRenforcee)
        val contresIndications = view.findViewById<TextView>(R.id.contreIndications)
        val textcontresIndications = view.findViewById<TextView>(R.id.textContreIndications)
        val allergies = view.findViewById<TextView>(R.id.allergies)
        val textAllergies = view.findViewById<TextView>(R.id.textAllergies)
        val plusDeDetails = view.findViewById<TextView>(R.id.plusDetailMedoc)

        Thread {
            val cisBdpm = cisBdpmDatabaseInterface.getOneCisBdpmById(
                (codeCis ?: return@Thread).toInt()
            ).first()
            val cisCompoBdpm = cisCompoDatabaseInterface.getOneCisCompoBdpmById(
                codeCis.toInt()
            ).first()
            //informations du medicament : code CIS, denomination, forme pharmaceutique, voies administration, etat commercialisation, date AMM, titulaire, surveillance renforcée
            val cisSideInfos = cisSideInfosDatabaseInterface.getOneCisSideInfosById(
                codeCis.toInt()
            ).getOrNull(0)

            //Visionez les stings dans strings.xml translation editor

            activity?.runOnUiThread {
                nomMedocView.text = cisBdpm.denomination.trim()

                codeCisView.text = getString(R.string.code_cis, cisBdpm.CodeCIS.toString().trim())

                textSubstanceActiveDosageView.text = getString(R.string.label_substance_active)
                substanceActiveDosageView.text = getString(
                    R.string.substance_active_dosage,
                    cisCompoBdpm.denomination.trim(),
                    cisCompoBdpm.dosage.trim()
                )

                textFormePharmaceutiqueView.text = getString(R.string.label_forme_pharmaceutique)
                formePharmaceutiqueView.text = cisBdpm.formePharmaceutique.trim()

                textVoiesAdministrationView.text = getString(R.string.label_voie_administration)
                voiesAdministrationView.text = cisBdpm.voiesAdministration.trim()

                textEtatCommercialisationView.text =
                    getString(R.string.label_etat_commercialisation)
                etatCommercialisationView.text = cisBdpm.etatCommercialisation.trim()

                textDateAmmView.text = getString(R.string.label_date_amm)
                dateAmmView.text = cisBdpm.dateAMM.trim()

                textTitulaireView.text = getString(R.string.label_laboratoire)
                titulaireView.text = cisBdpm.titulaire.trim()

                textSurveillanceRenforceeView.text =
                    getString(R.string.label_surveillance_renforcee)
                surveillanceRenforceeView.text = cisBdpm.surveillanceRenforcee.trim()

                cisInfo(
                    cisSideInfos,
                    contresIndications,
                    textcontresIndications,
                    allergies,
                    textAllergies
                )

                val texteComplet = plusDeDetails.text
                val spannableString = SpannableString(texteComplet)

                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        val url = "https://base-donnees-publique.medicaments.gouv.fr/extrait.php?specid=${cisBdpm.CodeCIS}"
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }

                val indiceMotIci = texteComplet.indexOf("ici")

                spannableString.setSpan(
                    clickableSpan,
                    indiceMotIci,
                    indiceMotIci + "ici".length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                plusDeDetails.text = spannableString

                plusDeDetails.movementMethod = LinkMovementMethod.getInstance()

            }


        }.start()
        return view
    }

    private fun cisInfo(
        cisSideInfos: CisSideInfos?,
        contresIndications: TextView,
        textcontresIndications: TextView,
        allergies: TextView,
        textAllergies: TextView
    ) {
        if (cisSideInfos == null) {
            hideViews(textcontresIndications, contresIndications, textAllergies, allergies)
            return
        }

        if (cisSideInfos.contreIndications.isNotEmpty()) {
            showTextWithHtml(
                cisSideInfos.contreIndications,
                textcontresIndications,
                contresIndications,
                R.string.contre_indications
            )
        } else {
            hideViews(textcontresIndications, contresIndications)
        }

        if (cisSideInfos.allergies.isNotEmpty()) {
            showTextWithHtml(cisSideInfos.allergies, textAllergies, allergies, R.string.allergies)
        } else {
            hideViews(textAllergies, allergies)
        }
    }

    private fun hideViews(vararg views: View) {
        views.forEach { it.visibility = View.GONE }
    }

    @Suppress("DEPRECATION")
    private fun showTextWithHtml(
        htmlText: String,
        textView: TextView,
        targetView: TextView,
        stringResource: Int
    ) {
        textView.text = targetView.context.getString(stringResource)
        val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(htmlText)
        }
        targetView.text = html
    }


}
