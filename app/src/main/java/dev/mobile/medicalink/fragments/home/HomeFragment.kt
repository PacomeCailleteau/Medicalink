package dev.mobile.medicalink.fragments.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.ListeTraitementAdapterR
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.concurrent.LinkedBlockingQueue


class HomeFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())

        //Get elements from view
        val paramBtn: ImageView = view.findViewById(R.id.btnParam)
        Log.d("test", "ici")
        val queue = LinkedBlockingQueue<MutableList<Pair<Prise, Traitement>>>()

        Thread {

            val listeTraitement: MutableList<Pair<Prise, Traitement>> = mutableListOf()

            val listeMedoc = medocDatabaseInterface.getAllMedocByUserId(
                userDatabaseInterface.getUsersConnected(true).first().uuid
            )

            for (medoc in listeMedoc) {

                var listeEffetsSec: MutableList<String>? = null
                if (medoc.effetsSecondaires != null) {
                    listeEffetsSec = medoc.effetsSecondaires.split(";").toMutableList()
                }


                val listePrise = mutableListOf<Prise>()

                if (medoc.prises != null) {
                    for (prise in medoc.prises.split("/")) {
                        val traitementPrise: MutableList<String> = prise.split(";").toMutableList()
                        val maPrise = Prise(
                            traitementPrise[0].toInt(),
                            traitementPrise[1],
                            traitementPrise[2].toInt(),
                            traitementPrise[3]
                        )
                        listePrise.add(maPrise)
                    }
                }

                var newTraitementFinDeTraitement: LocalDate? = null

                if (medoc.dateFinTraitement != "null") {
                    Log.d("test", medoc.dateFinTraitement.toString())
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val date = medoc.dateFinTraitement

                    //convert String to LocalDate

                    //convert String to LocalDate
                    newTraitementFinDeTraitement = LocalDate.parse(date, formatter)
                }

                var newTraitementDbtDeTraitement: LocalDate? = null

                if (medoc.dateDbtTraitement != "null") {
                    Log.d("test", medoc.dateDbtTraitement.toString())
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val date = medoc.dateDbtTraitement

                    //convert String to LocalDate

                    //convert String to LocalDate
                    newTraitementDbtDeTraitement = LocalDate.parse(date, formatter)
                }

                val traitement = Traitement(
                    medoc.nom,
                    medoc.dosageNB?.toInt(),
                    medoc.dosageUnite,
                    newTraitementFinDeTraitement,
                    medoc.typeComprime,
                    medoc.comprimesRestants,
                    medoc.expire,
                    listeEffetsSec,
                    listePrise,
                    medoc.totalQuantite,
                    medoc.uuid,
                    medoc.uuidUser,
                    newTraitementDbtDeTraitement
                )
                if (traitement.prises?.size != 0 && traitement.prises != null) {
                    for (prise in traitement.prises!!) {
                        listeTraitement.add(Pair(prise, traitement))
                    }
                }
            }

            Log.d("test", listeTraitement.toString())

            queue.add(listeTraitement)

        }.start()
        var listeTraitementPrise = queue.take()
        Log.d("test", listeTraitementPrise.toString())
        var listePriseAffiche : MutableList<Pair<Prise,Traitement>> = mutableListOf()
        var doIaddIt : Boolean = false
        for (element in listeTraitementPrise){
            if ((!element.second.expire)){
                when (element.second.dosageUnite) {
                    "auBesoin" -> {
                        doIaddIt=false
                    }
                    "quotidiennement" -> {
                        doIaddIt=true
                    }
                    else -> {
                        val jourEntreDeuxDates = Duration.between(element.second.dateDbtTraitement,LocalDate.now()).toDays().toInt()
                        var tousLesXJours = 0
                        when (element.second.dosageUnite){
                            "Jours" -> {
                                tousLesXJours=element.second.dosageNb
                                if (jourEntreDeuxDates%tousLesXJours == 0){
                                    doIaddIt=true
                                }
                            }
                            "Semaines" -> {
                                tousLesXJours=element.second.dosageNb*7
                                if (jourEntreDeuxDates%tousLesXJours == 0){
                                    doIaddIt=true
                                }
                            }
                            "Mois" -> {
                                var moisEntreDeuxDates = Period.between(element.second.dateDbtTraitement,LocalDate.now()).months
                                if (moisEntreDeuxDates%element.second.dosageNb == 0){
                                    doIaddIt=true
                                }
                            }
                            else -> doIaddIt=false
                        }
                    }
                }
            }
            if (doIaddIt){
                listePriseAffiche.add(element)
            }
        }
        Log.d("listePrise à afficher",listePriseAffiche.toString())
        Log.d("text",listePriseAffiche.first().first.quantite.toString())
        Log.d("test",listePriseAffiche.first().first.dosageUnite)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHome)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HomeAdapterR(listePriseAffiche)
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))



        //Set click listener
        paramBtn.setOnClickListener {
            //Navigate to parametre fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, ParametreFragement())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

}


















