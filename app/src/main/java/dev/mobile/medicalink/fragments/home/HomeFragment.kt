package dev.mobile.medicalink.fragments.home

import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.LinkedBlockingQueue


class HomeFragment : Fragment() {

    private lateinit var calendrierMoisTextView: TextView

    private lateinit var jourAvantButton: Button
    private lateinit var jourJButton: Button
    private lateinit var jPlus1Button: Button
    private lateinit var jPlus2Button: Button
    private lateinit var jPlus3Button: Button
    private lateinit var jPlus4Button: Button
    private lateinit var jPlus5Button: Button
    private lateinit var revenirDateCourante: ImageView

    private lateinit var jourAvant: LocalDate
    private lateinit var jourJ: LocalDate
    private lateinit var jPlus1: LocalDate
    private lateinit var jPlus2: LocalDate
    private lateinit var jPlus3: LocalDate
    private lateinit var jPlus4: LocalDate
    private lateinit var jPlus5: LocalDate




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

        calendrierMoisTextView = view.findViewById(R.id.calendrierMois)

        jourAvantButton = view.findViewById(R.id.jourAvant)
        jourJButton = view.findViewById(R.id.jourJ)
        jPlus1Button = view.findViewById(R.id.jPlus1)
        jPlus2Button = view.findViewById(R.id.jPlus2)
        jPlus3Button = view.findViewById(R.id.jPlus3)
        jPlus4Button = view.findViewById(R.id.jPlus4)
        jPlus5Button = view.findViewById(R.id.jPlus5)
        revenirDateCourante = view.findViewById(R.id.revenirDateCourante)


        jourAvant = LocalDate.now().minusDays(1)
        jourJ = LocalDate.now()
        jPlus1 = LocalDate.now().plusDays(1)
        jPlus2 = LocalDate.now().plusDays(2)
        jPlus3 = LocalDate.now().plusDays(3)
        jPlus4 = LocalDate.now().plusDays(4)
        jPlus5 = LocalDate.now().plusDays(5)

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
                if ((!medoc.expire) && (newTraitementFinDeTraitement != null)) {
                    if (LocalDate.now() > newTraitementFinDeTraitement) {
                        medoc.expire = true
                        medocDatabaseInterface.updateMedoc(medoc)
                    }
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
        var doIaddIt: Boolean
        var listePriseAffiche: MutableList<Pair<Prise, Traitement>> = mutableListOf()
        var dateActuelle = LocalDate.now()
        Log.d(
            "Date Actuelle Système",
            "${dateActuelle.dayOfMonth} ${dateActuelle.month} ${dateActuelle.year}"
        )
        for (element in listeTraitementPrise) {
            doIaddIt = false
            if ((!element.second.expire) && (dateActuelle >= element.second.dateDbtTraitement!!)) {
                Log.d("unite", element.second.dosageUnite)
                when (element.second.dosageUnite) {
                    "auBesoin" -> {
                        doIaddIt = false
                    }

                    "quotidiennement" -> {
                        doIaddIt = true
                    }

                    else -> {
                        val jourEntreDeuxDates =
                            ChronoUnit.DAYS.between(element.second.dateDbtTraitement, dateActuelle)
                        var tousLesXJours: Long = 0L
                        when (element.second.dosageUnite) {
                            "Jours" -> {
                                tousLesXJours = element.second.dosageNb.toLong()
                                doIaddIt = jourEntreDeuxDates % tousLesXJours == 0L
                            }

                            "Semaines" -> {
                                tousLesXJours = element.second.dosageNb.toLong() * 7L
                                Log.d("s", tousLesXJours.toString())
                                Log.d("s1", jourEntreDeuxDates.toString())
                                Log.d("s2", (jourEntreDeuxDates % tousLesXJours).toString())
                                doIaddIt = jourEntreDeuxDates % tousLesXJours == 0L
                                Log.d("doIaddIt", doIaddIt.toString())
                            }

                            "Mois" -> {
                                var moisEntreDeuxDates = Period.between(
                                    element.second.dateDbtTraitement,
                                    dateActuelle
                                ).months
                                Log.d("m", element.second.dosageNb.toString())
                                Log.d("m1", moisEntreDeuxDates.toString())
                                Log.d("m2",(moisEntreDeuxDates % element.second.dosageNb).toString())
                                if (moisEntreDeuxDates == 0) {
                                    doIaddIt = element.second.dateDbtTraitement == dateActuelle
                                } else {
                                    doIaddIt = moisEntreDeuxDates % element.second.dosageNb == 0
                                }
                            }

                            else -> doIaddIt = false
                        }
                    }
                }
            }
            if (doIaddIt) {
                listePriseAffiche.add(element)
            }
        }
        val traitementsTries = listePriseAffiche.sortedBy {it.first.heurePrise.uppercase()}.toMutableList()


        Log.d("listePrise à afficher", traitementsTries.toString())
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHome)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HomeAdapterR(traitementsTries)
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))




        //Gestion du calendrier
        calendrierMoisTextView.text = jourJ.month.toString()
        jourAvantButton.text = jourAvant.dayOfMonth.toString()
        jourJButton.text = jourJ.dayOfMonth.toString()
        jPlus1Button.text = jPlus1.dayOfMonth.toString()
        jPlus2Button.text = jPlus2.dayOfMonth.toString()
        jPlus3Button.text = jPlus3.dayOfMonth.toString()
        jPlus4Button.text = jPlus4.dayOfMonth.toString()
        jPlus5Button.text = jPlus5.dayOfMonth.toString()

        jourAvantButton.setOnClickListener {
            updateCalendrier(jourAvant)
        }
        jourJButton.setOnClickListener {
            updateCalendrier(jourJ)
        }
        jPlus1Button.setOnClickListener {
            updateCalendrier(jPlus1)
        }
        jPlus2Button.setOnClickListener {
            updateCalendrier(jPlus2)
        }
        jPlus3Button.setOnClickListener {
            updateCalendrier(jPlus3)
        }
        jPlus4Button.setOnClickListener {
            updateCalendrier(jPlus4)
        }
        jPlus5Button.setOnClickListener {
            updateCalendrier(jPlus5)
        }
        revenirDateCourante.setOnClickListener {
            updateCalendrier(LocalDate.now())
        }






        //Set click listener
        paramBtn.setOnClickListener {
            //Navigate to parametre fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, ParametreFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateCalendrier(dateClique : LocalDate){
        if (dateClique!= LocalDate.now()){
            revenirDateCourante.visibility=View.VISIBLE
        }else{
            revenirDateCourante.visibility=View.GONE
        }
        jourAvant = dateClique.minusDays(1)
        jourJ = dateClique
        jPlus1 = dateClique.plusDays(1)
        jPlus2 = dateClique.plusDays(2)
        jPlus3 = dateClique.plusDays(3)
        jPlus4 = dateClique.plusDays(4)
        jPlus5 = dateClique.plusDays(5)

        calendrierMoisTextView.text = dateClique.month.toString()
        jourAvantButton.text = jourAvant.dayOfMonth.toString()
        jourJButton.text = jourJ.dayOfMonth.toString()
        jPlus1Button.text = jPlus1.dayOfMonth.toString()
        jPlus2Button.text = jPlus2.dayOfMonth.toString()
        jPlus3Button.text = jPlus3.dayOfMonth.toString()
        jPlus4Button.text = jPlus4.dayOfMonth.toString()
        jPlus5Button.text = jPlus5.dayOfMonth.toString()
    }

}


















