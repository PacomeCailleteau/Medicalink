package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.CisBdpmRepository
import dev.mobile.medicalink.db.local.repository.CisCompoBdpmRepository

class InfoMedicamentFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_descriptif_medoc, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val cisCompoDatabaseInterface = CisCompoBdpmRepository(db.cisCompoBdpmDao())
        val cisBdpmDatabaseInterface = CisBdpmRepository(db.cisBdpmDao())

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
        val textSubstanceActiveDosageView = view.findViewById<TextView>(R.id.textSubstanceActiveInfoMedoc)
        val formePharmaceutiqueView = view.findViewById<TextView>(R.id.formePharmaceutique)
        val textFormePharmaceutiqueView = view.findViewById<TextView>(R.id.textFormePharmaceutique)
        val voiesAdministrationView = view.findViewById<TextView>(R.id.voieAdmission)
        val textVoiesAdministrationView = view.findViewById<TextView>(R.id.textVoieAdmission)
        val etatCommercialisationView = view.findViewById<TextView>(R.id.etatDeCommercialisation)
        val textEtatCommercialisationView = view.findViewById<TextView>(R.id.textEtatDeCommercialisation)
        val dateAmmView = view.findViewById<TextView>(R.id.dateAMM)
        val textDateAmmView = view.findViewById<TextView>(R.id.textDateAMM)
        val titulaireView = view.findViewById<TextView>(R.id.laboratoire)
        val textTitulaireView = view.findViewById<TextView>(R.id.textLaboratoire)
        val surveillanceRenforceeView = view.findViewById<TextView>(R.id.surveillanceRenforcee)
        val textSurveillanceRenforceeView = view.findViewById<TextView>(R.id.textSurveillanceRenforcee)
        val contresIndications = view.findViewById<TextView>(R.id.contreIndications)
        val textcontresIndications = view.findViewById<TextView>(R.id.textContreIndications)
        val allergies = view.findViewById<TextView>(R.id.allergies)
        val textAllergies = view.findViewById<TextView>(R.id.textAllergies)


        Thread{
            val cisBdpm = cisBdpmDatabaseInterface.getOneCisBdpmById(
                (codeCis?:return@Thread).toInt()
            ).first()
            val cisCompoBdpm = cisCompoDatabaseInterface.getOneCisCompoBdpmById(
                codeCis.toInt()
            ).first()
            //informations du medicament : code CIS, denomination, forme pharmaceutique, voies administration, etat commercialisation, date AMM, titulaire, surveillance renforcée

            //TODO effet secondaire et contre-indications quand on aura

            nomMedocView.text = buildString {
                append(cisBdpm.denomination.trim())
            }
            codeCisView.text = buildString {
                append("Code CIS : ")
                append(cisBdpm.CodeCIS.toString().trim())
            }
            //info Compo : denomination, dosage
            textSubstanceActiveDosageView.text = "Substance active :"
            substanceActiveDosageView.text = buildString {
                append(cisCompoBdpm.denomination.trim())
                append(", ")
                append(cisCompoBdpm.dosage.trim())
            }
            textFormePharmaceutiqueView.text = "Forme pharmaceutique :"
            formePharmaceutiqueView.text = buildString {
                append(cisBdpm.formePharmaceutique.trim())
            }
            textVoiesAdministrationView.text = "Voie d'administration :"
            voiesAdministrationView.text = buildString {
                append(cisBdpm.voiesAdministration.trim())
            }
            textEtatCommercialisationView.text = "État de commercialisation :"
            etatCommercialisationView.text = buildString {
                append(cisBdpm.etatCommercialisation.trim())
            }
            textDateAmmView.text = "Date d'autorisation de mise sur le marché :"
            dateAmmView.text = buildString {
                append(cisBdpm.dateAMM.trim())
            }
            textTitulaireView.text = "Laboratoire :"
            titulaireView.text = buildString {
                append(cisBdpm.titulaire.trim())
            }
            textSurveillanceRenforceeView.text = "Surveillance renforcée :"
            surveillanceRenforceeView.text = buildString {
                append(cisBdpm.surveillanceRenforcee.trim())
            }

            textcontresIndications.text = "Contre-indications :"
            var html =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(cisBdpm.contreIndications, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(cisBdpm.contreIndications)
            }
            contresIndications.text = html

            textAllergies.text = "Allergies :"
            html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(cisBdpm.allergies, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(cisBdpm.allergies)
            }
            allergies.text = html


        }.start()
        return view
    }
}
