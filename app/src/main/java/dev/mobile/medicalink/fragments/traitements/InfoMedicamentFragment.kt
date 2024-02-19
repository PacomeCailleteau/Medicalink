package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.os.Bundle
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
        val formePharmaceutiqueView = view.findViewById<TextView>(R.id.formePharmaceutique)
        val voiesAdministrationView = view.findViewById<TextView>(R.id.voieAdmission)
        val etatCommercialisationView = view.findViewById<TextView>(R.id.etatDeCommercialisation)
        val dateAmmView = view.findViewById<TextView>(R.id.dateAMM)
        val titulaireView = view.findViewById<TextView>(R.id.laboratoire)
        val surveillanceRenforceeView = view.findViewById<TextView>(R.id.surveillanceRenforcee)

        //info Compo : denomination, dosage
        val substanceActiveDosageView = view.findViewById<TextView>(R.id.substanceActiveInfoMedoc)


        Thread {
            val cisBdpm = cisBdpmDatabaseInterface.getOneCisBdpmById(
                (codeCis ?: return@Thread).toInt()
            ).first()
            val cisCompoBdpm = cisCompoDatabaseInterface.getOneCisCompoBdpmById(
                codeCis.toInt()
            ).first()
            //informations du medicament : code CIS, denomination, forme pharmaceutique, voies administration, etat commercialisation, date AMM, titulaire, surveillance renforcée

            //TODO effet secondaire et contre-indications quand on aura

            nomMedocView.text = buildString {
                append("Dénomination : \n")
                append(cisBdpm.denomination.trim())
            }
            codeCisView.text = buildString {
                append("Code CIS : ")
                append(cisBdpm.CodeCIS.toString().trim())
            }
            formePharmaceutiqueView.text = buildString {
                append("Forme pharmaceutique : \n")
                append(cisBdpm.formePharmaceutique.trim())
            }
            voiesAdministrationView.text = buildString {
                append("Voie d'administration : \n")
                append(cisBdpm.voiesAdministration.trim())
            }
            etatCommercialisationView.text = buildString {
                append("État de commercialisation : \n")
                append(cisBdpm.etatCommercialisation.trim())
            }
            dateAmmView.text = buildString {
                append("Date d'autorisation de mise sur le marché : \n")
                append(cisBdpm.dateAMM.trim())
            }
            titulaireView.text = buildString {
                append("Laboratoire : \n")
                append(cisBdpm.titulaire.trim())
            }
            surveillanceRenforceeView.text = buildString {
                append("Surveillance renforcée : \n")
                append(cisBdpm.surveillanceRenforcee.trim())
            }

            //info Compo : denomination, dosage
            substanceActiveDosageView.text = buildString {
                append("Substance active : \n")
                append(cisCompoBdpm.denomination.trim())
                append(", ")
                append(cisCompoBdpm.dosage.trim())
            }

        }.start()
        return view
    }
}
