package dev.mobile.medicalink.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.PriseValideeRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.home.adapter.HomeAdapterR
import dev.mobile.medicalink.fragments.traitements.enums.EnumFrequence
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement
import dev.mobile.medicalink.fragments.traitements.enums.EnumTypeMedic
import dev.mobile.medicalink.utils.GoTo
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.LinkedBlockingQueue

/**
 * Le fragment de la page d'accueil
 */
class HomeFragment : Fragment() {
    private lateinit var homeAdapter: HomeAdapterR

    private lateinit var calendrierMoisTextView: TextView

    private lateinit var jourAvantButton: Button
    private lateinit var jourJButton: Button
    private lateinit var jPlus1Button: Button
    private lateinit var jPlus2Button: Button
    private lateinit var jPlus3Button: Button
    private lateinit var jPlus4Button: Button
    private lateinit var jPlus5Button: Button

    private lateinit var jourAvantLettre: TextView
    private lateinit var jourJLettre: TextView
    private lateinit var jPlus1Lettre: TextView
    private lateinit var jPlus2Lettre: TextView
    private lateinit var jPlus3Lettre: TextView
    private lateinit var jPlus4Lettre: TextView
    private lateinit var jPlus5Lettre: TextView

    private lateinit var revenirDateCourante: ImageView
    private lateinit var nextMonth: ImageView
    private lateinit var previousMonth: ImageView


    private lateinit var jourAvant: LocalDate
    private lateinit var jourJ: LocalDate
    private lateinit var jPlus1: LocalDate
    private lateinit var jPlus2: LocalDate
    private lateinit var jPlus3: LocalDate
    private lateinit var jPlus4: LocalDate
    private lateinit var jPlus5: LocalDate

    private lateinit var listeMois: Map<String, String>
    private lateinit var listeJour: Map<String, String>

    private lateinit var listePriseValidee: MutableList<Pair<LocalDate, String>>

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Création des listes de mois et de jours
        listeMois = mapOf(
            Pair("JANUARY", resources.getString(R.string.janvier)),
            Pair("FEBRUARY", resources.getString(R.string.fevrier)),
            Pair("MARCH", resources.getString(R.string.mars)),
            Pair("APRIL", resources.getString(R.string.avril)),
            Pair("MAY", resources.getString(R.string.mai)),
            Pair("JUNE", resources.getString(R.string.juin)),
            Pair("JULY", resources.getString(R.string.juillet)),
            Pair("AUGUST", resources.getString(R.string.aout)),
            Pair("SEPTEMBER", resources.getString(R.string.septembre)),
            Pair("OCTOBER", resources.getString(R.string.octobre)),
            Pair("NOVEMBER", resources.getString(R.string.novembre)),
            Pair("DECEMBER", resources.getString(R.string.decembre)),
        )

        listeJour = mapOf(
            Pair("MONDAY", resources.getString(R.string.lundi)),
            Pair("TUESDAY", resources.getString(R.string.mardi)),
            Pair("WEDNESDAY", resources.getString(R.string.mercredi)),
            Pair("THURSDAY", resources.getString(R.string.jeudi)),
            Pair("FRIDAY", resources.getString(R.string.vendredi)),
            Pair("SATURDAY", resources.getString(R.string.samedi)),
            Pair("SUNDAY", resources.getString(R.string.dimanche)),
        )

        // Récupération des éléments de la vue
        calendrierMoisTextView = view.findViewById(R.id.calendrierMois)
        jourAvantButton = view.findViewById(R.id.jourAvant)
        jourJButton = view.findViewById(R.id.jourJ)
        jPlus1Button = view.findViewById(R.id.jPlus1)
        jPlus2Button = view.findViewById(R.id.jPlus2)
        jPlus3Button = view.findViewById(R.id.jPlus3)
        jPlus4Button = view.findViewById(R.id.jPlus4)
        jPlus5Button = view.findViewById(R.id.jPlus5)

        jourAvantLettre = view.findViewById(R.id.lettreJourAvant)
        jourJLettre = view.findViewById(R.id.lettreJourJ)
        jPlus1Lettre = view.findViewById(R.id.lettreJourJplus1)
        jPlus2Lettre = view.findViewById(R.id.lettreJourJplus2)
        jPlus3Lettre = view.findViewById(R.id.lettreJourJplus3)
        jPlus4Lettre = view.findViewById(R.id.lettreJourJplus4)
        jPlus5Lettre = view.findViewById(R.id.lettreJourJplus5)

        //Création des boutons de navigation des mois du calendrier et bouton permettant de revenir au jour d'aujourd'hui
        revenirDateCourante = view.findViewById(R.id.revenirDateCourante)
        nextMonth = view.findViewById(R.id.nextMonth)
        previousMonth = view.findViewById(R.id.previousMonth)

        jourAvant = LocalDate.now().minusDays(1)
        jourJ = LocalDate.now()
        jPlus1 = LocalDate.now().plusDays(1)
        jPlus2 = LocalDate.now().plusDays(2)
        jPlus3 = LocalDate.now().plusDays(3)
        jPlus4 = LocalDate.now().plusDays(4)
        jPlus5 = LocalDate.now().plusDays(5)

        val paramBtn: ImageView = view.findViewById(R.id.btnParam)
        val traitementsTries = mutableListOf<Pair<Prise, Traitement>>()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHome)
        recyclerView.layoutManager = LinearLayoutManager(context)
        homeAdapter = HomeAdapterR(traitementsTries, mutableListOf(), LocalDate.now(), recyclerView)
        recyclerView.adapter = homeAdapter
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))
        updateListePrise(LocalDate.now(), view.context.applicationContext)

        //Gestion du calendrier
        revenirDateCourante.visibility = View.GONE
        updateCalendrier()

        // Les listeners sur les boutons du calendrier
        jourAvantButton.setOnClickListener {
            updateCalendrier(jourAvant, view.context.applicationContext)
        }
        jourJButton.setOnClickListener {
            updateCalendrier(jourJ, view.context.applicationContext)
        }
        jPlus1Button.setOnClickListener {
            updateCalendrier(jPlus1, view.context.applicationContext)
        }
        jPlus2Button.setOnClickListener {
            updateCalendrier(jPlus2, view.context.applicationContext)
        }
        jPlus3Button.setOnClickListener {
            updateCalendrier(jPlus3, view.context.applicationContext)
        }
        jPlus4Button.setOnClickListener {
            updateCalendrier(jPlus4, view.context.applicationContext)
        }
        jPlus5Button.setOnClickListener {
            updateCalendrier(jPlus5, view.context.applicationContext)
        }
        revenirDateCourante.setOnClickListener {
            updateCalendrier(LocalDate.now(), view.context.applicationContext)
        }
        nextMonth.setOnClickListener {
            updateCalendrier(jourJ.plusMonths(1), view.context.applicationContext)
        }
        previousMonth.setOnClickListener {
            updateCalendrier(jourJ.minusMonths(1), view.context.applicationContext)
        }

        // Le listener sur le bouton paramètre
        paramBtn.setOnClickListener {
            //Navigation vers le fragment parametre
            GoTo.fragment(ParametreFragment(), parentFragmentManager)
        }

        return view
    }

    /**
     * Fonction permettant de mettre à jour le calendrier
     * @param dateClique la date sur laquelle l'utilisateur a cliqué
     * @param context le contexte de l'application
     */
    @SuppressLint("SetTextI18n")
    fun updateCalendrier(dateClique: LocalDate, context: Context) {
        if (dateClique != LocalDate.now()) {
            revenirDateCourante.visibility = View.VISIBLE
        } else {
            revenirDateCourante.visibility = View.GONE
        }
        jourAvant = dateClique.minusDays(1)
        jourJ = dateClique
        jPlus1 = dateClique.plusDays(1)
        jPlus2 = dateClique.plusDays(2)
        jPlus3 = dateClique.plusDays(3)
        jPlus4 = dateClique.plusDays(4)
        jPlus5 = dateClique.plusDays(5)

        updateCalendrier()
        updateListePrise(dateClique, context)
    }

    /**
     * Fonction permettant de mettre à jour la liste des prises à afficher
     * @param dateActuelle la date actuelle
     * @param context le contexte de l'application
     */

    private fun updateListePrise(dateActuelle: LocalDate, context: Context) {
        val db = AppDatabase.getInstance(context)
        val userDatabaseInterface = UserRepository(db.userDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())
        val priseValideeDatabaseInterface = PriseValideeRepository(db.priseValideeDao())

        val listeTraitementPrise: MutableList<Pair<Prise, Traitement>> =
            findListeTraitementPrise(userDatabaseInterface, medocDatabaseInterface)

        val listePriseAffiche: MutableList<Pair<Prise, Traitement>> = mutableListOf()

        for (element in listeTraitementPrise) {
            if (toAdd(element, dateActuelle)) {
                listePriseAffiche.add(element)
            }
        }

        val traitementsTries =
            listePriseAffiche.sortedBy { it.first.heurePrise.uppercase() }.toMutableList()
        if (traitementsTries.isNotEmpty()) {
            traitementsTries.add(
                0,
                Pair(
                    Prise(
                        "123456",
                        "17:00",
                        7,
                        EnumTypeMedic.COMPRIME
                    ),
                    Traitement(
                        "x",
                        "0",
                        1,
                        EnumFrequence.AUBESOIN,
                        null,
                        EnumTypeMedic.COMPRIME,
                        25,
                        false,
                        null,
                        null,
                        null,
                        null,
                        null,
                        LocalDate.now()
                    )
                )
            )
        }
        val queue2 = LinkedBlockingQueue<MutableList<Pair<LocalDate, String>>>()
        Thread {

            val resultatListePriseValidee: MutableList<Pair<LocalDate, String>> = mutableListOf()

            val listePriseValideeDB = priseValideeDatabaseInterface.getAllPriseValidee()

            for (priseValidee in listePriseValideeDB) {

                val uuidTraitement = priseValidee.uuidPrise

                var dateReformate = LocalDate.now()

                if (priseValidee.date != "null") {
                    val formatter = DateTimeFormatter.ofPattern(datePattern)
                    val date = priseValidee.date

                    //convert String to LocalDate
                    dateReformate = LocalDate.parse(date, formatter)
                }

                resultatListePriseValidee.add(Pair(dateReformate, uuidTraitement))
            }
            queue2.add(resultatListePriseValidee)

        }.start()
        listePriseValidee = queue2.take()

        homeAdapter.updateData(traitementsTries, listePriseValidee, dateActuelle)
        homeAdapter.notifyDataSetChanged()
    }

    /**
     * Fonction permettant de savoir si une prise doit être ajoutée à la liste des prises à afficher
     * @param element la prise à vérifier
     * @param dateActuelle la date actuelle
     * @return true si la prise doit être ajoutée, false sinon
     */
    private fun toAdd(element: Pair<Prise, Traitement>, dateActuelle: LocalDate): Boolean {
        var toAdd = false
        if ((!element.second.expire) && (dateActuelle >= element.second.dateDbtTraitement)) {
            //Si le traitement n'est pas expiré et que la date actuelle est supérieure à la date de début de traitement
            if ((element.second.dateFinTraitement != null) && (dateActuelle > element.second.dateFinTraitement!!)) {
                //Si la date actuelle est supérieure à la date de fin de traitement, on passe au traitement suivant
                return false
            }

            when (element.second.frequencePrise) {
                EnumFrequence.AUBESOIN -> {
                    toAdd = false
                }

                EnumFrequence.QUOTIDIEN -> {
                    toAdd = true
                }

                else -> {
                    val jourEntreDeuxDates =
                        ChronoUnit.DAYS.between(element.second.dateDbtTraitement, dateActuelle)
                    val tousLesXJours: Long
                    when (element.second.frequencePrise) {
                        EnumFrequence.JOUR -> {
                            tousLesXJours = element.second.dosageNb.toLong()
                            toAdd = jourEntreDeuxDates % tousLesXJours == 0L
                        }

                        EnumFrequence.SEMAINE -> {
                            tousLesXJours = element.second.dosageNb.toLong() * 7L
                            toAdd = jourEntreDeuxDates % tousLesXJours == 0L
                        }

                        EnumFrequence.MOIS -> {
                            val moisEntreDeuxDates = Period.between(
                                element.second.dateDbtTraitement,
                                dateActuelle
                            ).months
                            toAdd = (moisEntreDeuxDates % element.second.dosageNb == 0) &&
                                    (dateActuelle.dayOfMonth == element.second.dateDbtTraitement.dayOfMonth)
                        }

                        else -> toAdd = false
                    }
                }
            }
        }
        return toAdd
    }

    private fun findListeTraitementPrise(
        userDatabaseInterface: UserRepository,
        medocDatabaseInterface: MedocRepository
    ): MutableList<Pair<Prise, Traitement>> {
        val queue = LinkedBlockingQueue<MutableList<Pair<Prise, Traitement>>>()
        Thread {
            val listeTraitement: MutableList<Pair<Prise, Traitement>> = mutableListOf()

            //Récupération des traitements de l'utilisateur connecté
            val listeMedoc = medocDatabaseInterface.getAllMedocByUserId(
                userDatabaseInterface.getUsersConnected().first().uuid
            )

            for (medoc in listeMedoc) {
                val traitement = medoc.toTraitement()

                //Vérification de la date de fin de traitement
                if ((!medoc.expire) && (traitement.dateFinTraitement != null) && LocalDate.now() > traitement.dateFinTraitement!!) {
                    //Si la date de fin de traitement est dépassée, on met le traitement en expiré
                    medoc.expire = true
                    traitement.expire = true
                    medocDatabaseInterface.updateMedoc(medoc)
                }

                if (traitement.prises?.size != 0 && traitement.prises != null) {
                    //Si le traitement a des prises, on les ajoute à la liste des prises à afficher
                    for (prise in traitement.prises!!) {
                        listeTraitement.add(Pair(prise, traitement))
                    }
                }
            }
            queue.add(listeTraitement)
        }.start()
        return queue.take()
    }

    @SuppressLint("SetTextI18n")
    private fun updateCalendrier() {
        calendrierMoisTextView.text = "${listeMois[jourJ.month.toString()]} ${jourJ.year}"
        jourAvantButton.text = jourAvant.dayOfMonth.toString()
        jourJButton.text = jourJ.dayOfMonth.toString()
        jPlus1Button.text = jPlus1.dayOfMonth.toString()
        jPlus2Button.text = jPlus2.dayOfMonth.toString()
        jPlus3Button.text = jPlus3.dayOfMonth.toString()
        jPlus4Button.text = jPlus4.dayOfMonth.toString()
        jPlus5Button.text = jPlus5.dayOfMonth.toString()

        jourAvantLettre.text = "${listeJour[jourAvant.dayOfWeek.toString()]}"
        jourJLettre.text = "${listeJour[jourJ.dayOfWeek.toString()]}"
        jPlus1Lettre.text = "${listeJour[jPlus1.dayOfWeek.toString()]}"
        jPlus2Lettre.text = "${listeJour[jPlus2.dayOfWeek.toString()]}"
        jPlus3Lettre.text = "${listeJour[jPlus3.dayOfWeek.toString()]}"
        jPlus4Lettre.text = "${listeJour[jPlus4.dayOfWeek.toString()]}"
        jPlus5Lettre.text = "${listeJour[jPlus5.dayOfWeek.toString()]}"
    }


    companion object {
        private const val datePattern = "yyyy-MM-dd"
    }
}
