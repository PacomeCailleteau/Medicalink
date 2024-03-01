package dev.mobile.medicalink.fragments.traitements

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.CisBdpm
import dev.mobile.medicalink.db.local.entity.CisSubstance
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.repository.CisBdpmRepository
import dev.mobile.medicalink.db.local.repository.CisSubstanceRepository
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.fragments.traitements.enums.EnumFrequence.Companion.getStringFromEnum
import java.util.concurrent.LinkedBlockingQueue

class InfoMedocFragment : Fragment() {
    private lateinit var txtPlusInfo: TextView
    private lateinit var imageLienExterne: ImageView


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_infos_medoc, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val medocDatabaseInterface = MedocRepository(db.medocDao())
        val substancecDatabaseInterface = CisSubstanceRepository(db.cisSubstanceDao())
        val bdpmDatabaseInterface = CisBdpmRepository(db.cisBdpmDao())

        val data = arguments?.getSerializable("medoc") as Traitement

        val titre = view.findViewById<TextView>(R.id.TitleInfoMedoc)
        val dosage = view.findViewById<TextView>(R.id.dosageInfoMedoc)
        val dosageUnite = view.findViewById<TextView>(R.id.dosageUniteInfoMedoc)
        val typeComprime = view.findViewById<TextView>(R.id.typeComprimeInfoMedoc)
        val restants = view.findViewById<TextView>(R.id.comprimeRestantInfoMedoc)
        val quantite = view.findViewById<TextView>(R.id.quantiteInfoMedoc)
        val debTraitement = view.findViewById<TextView>(R.id.debutTraitementInfoMedoc)
        val finTraitement = view.findViewById<TextView>(R.id.dateFinTraitementInfoMedoc)
        val expire = view.findViewById<TextView>(R.id.expireInfoMedoc)
        val effetsSec = view.findViewById<TextView>(R.id.effetsSecondairesInfoMedoc)
        val prises = view.findViewById<TextView>(R.id.prisesInfoMedoc)
        val denomPrincipe = view.findViewById<TextView>(R.id.denominationPrincipeInfoMedoc)
        val doseSubstance = view.findViewById<TextView>(R.id.dosageSubstanceInfoMedoc)
        val formePharma = view.findViewById<TextView>(R.id.formePharmaceutiqueInfoMedoc)
        val voieAdministration = view.findViewById<TextView>(R.id.voieAdministrationInfoMedoc)
        val codeCIS = data.codeCIS

        txtPlusInfo = view.findViewById(R.id.voirPlusInfo)
        imageLienExterne = view.findViewById(R.id.imageLienExterne)

        ajoutLienCliquable(codeCIS)

        lateinit var monMedoc: Medoc
        lateinit var maSubstance: CisSubstance
        lateinit var monBdpm: CisBdpm

        val queue = LinkedBlockingQueue<Boolean>()
        Thread {
            try {
                monMedoc = medocDatabaseInterface.getOneMedocByCIS(codeCIS)!!
                maSubstance = substancecDatabaseInterface.getOneCisSubstanceById(codeCIS)!!
                monBdpm = bdpmDatabaseInterface.getOneCisBdpmById(codeCIS)[0]
            } catch (e: Exception) {
                throw e
            }
            queue.add(true)
        }.start()
        queue.take()

        titre.text = monMedoc.nom
        dosage.text = getString(R.string.dosageDetailMedoc, monMedoc.dosageNB.toString())
        dosageUnite.text = getString(
            R.string.dosageSubstanceDetailMedoc,
            getStringFromEnum(monMedoc.frequencePrise, view.context)
        )
        typeComprime.text = getString(R.string.typeComprimeDetailMedoc, monMedoc.typeComprime)
        restants.text =
            getString(R.string.typeComprimeDetailMedoc, monMedoc.comprimesRestants.toString())
        quantite.text = getString(R.string.quantiteDetailMedoc, monMedoc.totalQuantite.toString())
        debTraitement.text =
            getString(R.string.debutTraitementDetailMedoc, monMedoc.dateDbtTraitement)
        finTraitement.text =
            getString(R.string.finTraitementDetailMedoc, monMedoc.dateFinTraitement)
        expire.text = getString(R.string.expireDetailMedoc, monMedoc.expire.toString())
        effetsSec.text =
            getString(R.string.effetsSecondairesDetailMedoc, monMedoc.effetsSecondaires)
        prises.text = getString(R.string.prisesDetailMedoc, monMedoc.prises)
        restants.text =
            getString(R.string.restantsDetailMedoc, monMedoc.comprimesRestants.toString())

        denomPrincipe.text =
            getString(R.string.denominationPrincipeDetailMedoc, maSubstance.denominationSubstance)
        doseSubstance.text =
            getString(R.string.dosageSubstanceDetailMedoc, maSubstance.dosageSubstance)

        formePharma.text =
            getString(R.string.formePharmaceutiqueDetailMedoc, monBdpm.formePharmaceutique)
        voieAdministration.text =
            getString(R.string.voieAdministrationDetailMedoc, monBdpm.voiesAdministration)

        return view
    }

    /**
     * Ajoute un lien cliquable sur le texte "Voir plus d'informations" et sur l'image
     * @param codeCIS : le code CIS du médicament
     */
    private fun ajoutLienCliquable(codeCIS: String) {
        // Quand on clique sur le lien, ça doit nous envoyer vers la page internet du médicament
        val url =
            "https://base-donnees-publique.medicaments.gouv.fr/affichageDoc.php?typedoc=R&specid=$codeCIS"
        txtPlusInfo.setOnClickListener {
            lancerIntent(url)
        }
        imageLienExterne.setOnClickListener {
            lancerIntent(url)
        }
    }

    /**
     * Lance l'intent pour ouvrir le lien
     * @param url : le lien à ouvrir
     */
    private fun lancerIntent(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }


}