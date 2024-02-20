package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.CisBdpm
import dev.mobile.medicalink.db.local.entity.CisSubstance
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.repository.CisBdpmRepository
import dev.mobile.medicalink.db.local.repository.CisSubstanceRepository
import dev.mobile.medicalink.db.local.repository.MedocRepository
import java.lang.Exception
import java.util.concurrent.LinkedBlockingQueue

class InfoMedocFragment : Fragment() {

    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_infos_medoc,container,false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val medocDatabaseInterface = MedocRepository(db.medocDao())
        val substancecDatabaseInterface = CisSubstanceRepository(db.cisSubstanceDao())
        val bdpmDatabaseInterface = CisBdpmRepository(db.cisBdpmDao())

        var data = arguments?.getSerializable("medoc") as Traitement

        var titre = view.findViewById<TextView>(R.id.TitleInfoMedoc)
        var dosage = view.findViewById<TextView>(R.id.dosageInfoMedoc)
        var dosageUnite = view.findViewById<TextView>(R.id.dosageUniteInfoMedoc)
        var typeComprime = view.findViewById<TextView>(R.id.typeComprimeInfoMedoc)
        var restants = view.findViewById<TextView>(R.id.comprimeRestantInfoMedoc)
        var quantite = view.findViewById<TextView>(R.id.quantiteInfoMedoc)
        var debTraitement = view.findViewById<TextView>(R.id.debutTraitementInfoMedoc)
        var finTraitement = view.findViewById<TextView>(R.id.dateFinTraitementInfoMedoc)
        var expire = view.findViewById<TextView>(R.id.expireInfoMedoc)
        var effetsSec = view.findViewById<TextView>(R.id.effetsSecondairesInfoMedoc)
        var prises = view.findViewById<TextView>(R.id.prisesInfoMedoc)
        var principeActif = view.findViewById<TextView>(R.id.principeActifnfoMedoc)
        var denomPrincipe = view.findViewById<TextView>(R.id.denominationPrincipeInfoMedoc)
        var doseSubstance = view.findViewById<TextView>(R.id.dosageSubstanceInfoMedoc)
        var formePharma = view.findViewById<TextView>(R.id.formePharmaceutiqueInfoMedoc)
        var voieAdministration = view.findViewById<TextView>(R.id.voieAdministrationInfoMedoc)

        var codeCIS = data.codeCIS

        lateinit var monMedoc : Medoc
        lateinit var maSubstance : CisSubstance
        lateinit var monBdpm : CisBdpm

        val queue = LinkedBlockingQueue<Boolean>()
        Thread {
            try {
                monMedoc = medocDatabaseInterface.getOneMedocByCIS(codeCIS) as Medoc
                maSubstance = substancecDatabaseInterface.getOneCisSubstanceById(codeCIS)!!
                monBdpm = bdpmDatabaseInterface.getOneCisBdpmById(codeCIS)[0]
            } catch (e : Exception) {
                throw e
            }
            queue.add(true)
        }.start()
        queue.take()

        titre.text = monMedoc.nom
        dosage.text = dosage.text.toString() + monMedoc.dosageNB
        dosageUnite.text = dosageUnite.text.toString() + monMedoc.frequencePrise
        typeComprime.text = typeComprime.text.toString() + monMedoc.typeComprime
        restants.text = restants.text.toString() + monMedoc.comprimesRestants
        quantite.text = quantite.text.toString() + monMedoc.totalQuantite
        debTraitement.text = debTraitement.text.toString() + monMedoc.dateDbtTraitement
        finTraitement.text = finTraitement.text.toString() + monMedoc.dateFinTraitement
        expire.text = expire.text.toString() + monMedoc.expire
        effetsSec.text = effetsSec.text.toString() + monMedoc.effetsSecondaires
        prises.text = prises.text.toString() + monMedoc.prises
        restants.text = restants.text.toString() + monMedoc.comprimesRestants

        principeActif.text = principeActif.text.toString() + maSubstance.elementPharmaceutique
        denomPrincipe.text = denomPrincipe.text.toString() + maSubstance.denominationSubstance
        doseSubstance.text = doseSubstance.text.toString() + maSubstance.dosageSubstance

        formePharma.text = formePharma.text.toString() + monBdpm.formePharmaceutique
        voieAdministration.text = voieAdministration.text.toString() + monBdpm.voiesAdministration

        return view
    }
}