package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.ajoutmanuel.AjoutManuelRecapitulatif
import dev.mobile.medicalink.utils.NotificationService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.LinkedBlockingQueue


class ListeTraitementsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_liste_traitements, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())

        val isAddingTraitement = arguments?.getString("isAddingTraitement")
        var uuidUpdateTraitement = arguments?.getString("uuidUpdateTraitement")



        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.VISIBLE
        }


        /* ##############################################################
        #               Partie update et insert du traitement           #
        ################################################################# */
        if (isAddingTraitement == "true" || isAddingTraitement == "false") {
            val newTraitement = arguments?.getSerializable("newTraitement") as Traitement

            val newMedoc: Medoc
            var traitementUUID: String = UUID.randomUUID().toString()
            when (isAddingTraitement) {
                "true" -> {
                    traitementUUID = UUID.randomUUID().toString()
                }

                "false" -> {
                    traitementUUID = newTraitement.UUID.toString()
                }
            }

            var newTraitementEffetsSec: String? = null
            if (newTraitement.effetsSecondaires != null) {
                var chaineDeChar = ""
                for (effet in newTraitement.effetsSecondaires!!) {
                    chaineDeChar += "$effet;"
                }
                chaineDeChar = chaineDeChar.subSequence(0, chaineDeChar.length - 1).toString()
                newTraitementEffetsSec = chaineDeChar
            }

            var newTraitementPrises: String? = null
            Log.d("test", newTraitement.prises.toString())
            if (newTraitement.prises != null) {
                var chaineDeChar = ""
                for (prise in newTraitement.prises!!) {
                    chaineDeChar += "${prise}/"
                }
                chaineDeChar = chaineDeChar.subSequence(0, chaineDeChar.length - 1).toString()
                newTraitementPrises = chaineDeChar
            }


            newMedoc = Medoc(
                traitementUUID,
                "",
                newTraitement.CodeCIS,
                newTraitement.nomTraitement,
                newTraitement.dosageNb.toString(),
                newTraitement.dosageUnite,
                newTraitement.dateFinTraitement.toString(),
                newTraitement.typeComprime,
                newTraitement.comprimesRestants,
                newTraitement.expire,
                newTraitementEffetsSec,
                newTraitementPrises,
                newTraitement.totalQuantite,
                newTraitement.dateDbtTraitement.toString()
            )

            val queue2 = LinkedBlockingQueue<Boolean>()
            Thread {
                val uuidUserCourant = userDatabaseInterface.getUsersConnected(true).first().uuid
                newMedoc.uuidUser = uuidUserCourant
                when (isAddingTraitement) {
                    "true" -> {
                        val res = medocDatabaseInterface.insertMedoc(newMedoc)
                        println("insert statut : ${res.second}")
                    }

                    "false" -> {
                        Log.d("Listeeeeeeee", newMedoc.toString())
                        medocDatabaseInterface.updateMedoc(newMedoc)

                    }
                }
                queue2.add(true)
            }.start()
            queue2.take()


            val heurePremierePrise = newTraitement.getProchainePrise(null).heurePrise
            val jourPremierePrise = newTraitement.dateDbtTraitement
            val date = LocalDate.now().toString()
            val numero = newTraitement.getProchainePrise(null).numeroPrise
            if (jourPremierePrise != null) {
                NotificationService.createFirstNotif(
                    view.context,
                    heurePremierePrise,
                    jourPremierePrise,
                    newTraitement,
                    Pair(date, numero)
                )
            }

        }


        /* ##############################################################
        #           Partie affichage de tous les traitements            #
        ################################################################# */

        val queue = LinkedBlockingQueue<MutableList<Traitement>>()
        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread {
            val listeTraitement: MutableList<Traitement> = mutableListOf()

            val listeMedoc = medocDatabaseInterface.getAllMedocByUserId(
                userDatabaseInterface.getUsersConnected(true).first().uuid
            )

            val ancunTraintement = view.findViewById<View>(R.id.textAucunTraitement)
            if (listeMedoc.isEmpty()) {
                ancunTraintement.visibility = View.VISIBLE
            } else {
                ancunTraintement.visibility = View.GONE
            }
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
                            traitementPrise[0],
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

                    newTraitementFinDeTraitement = LocalDate.parse(date, formatter)
                }

                var newTraitementDbtDeTraitement: LocalDate? = null

                if (medoc.dateDbtTraitement != "null") {
                    Log.d("test", medoc.dateDbtTraitement.toString())
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val date = medoc.dateDbtTraitement

                    newTraitementDbtDeTraitement = LocalDate.parse(date, formatter)
                }

                val traitement = Traitement(
                    medoc.CodeCIS,
                    medoc.nom,
                    medoc.dosageNB.toInt(),
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

                listeTraitement.add(traitement)

            }
            queue.add(listeTraitement)
        }.start()

        // Récupération de tous les traitements grâce au Thread au dessus
        val mesTraitements = queue.take()


        val traitementsTries =
            mesTraitements.sortedBy { it.dateDbtTraitement }.sortedBy { it.expire }.toMutableList()


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewListeEffetsSecondaires)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter =
            ListeTraitementAdapterR(traitementsTries) { clickedTraitement, click ->
                when (click) {
                    "Info" -> {
                        val bundle = Bundle()
                        bundle.putString(
                            "codeCIS",
                            (clickedTraitement.CodeCIS ?: return@ListeTraitementAdapterR).toString()
                        )
                        val destinationFragment = InfoMedicamentFragment()
                        destinationFragment.arguments = bundle
                        val fragTransaction = parentFragmentManager.beginTransaction()
                        fragTransaction.replace(R.id.FL, destinationFragment)
                        fragTransaction.addToBackStack(null)
                        fragTransaction.commit()

                    }

                    "Supr" -> {
                        Thread {
                            medocDatabaseInterface.deleteMedoc(
                                medocDatabaseInterface.getOneMedocById(
                                    clickedTraitement.UUID!!
                                ).first()
                            )
                        }.start()
                    }

                    "Modif" -> {
                        val bundle = Bundle()

                        bundle.putString("isAddingTraitement", "false")
                        bundle.putString("uuidUpdateTraitement", "$")


                        bundle.putSerializable(
                            "traitement",
                            clickedTraitement
                        )

                        val schema_prise1: String
                        val provenance: String
                        if (clickedTraitement.dosageUnite == "auBesoin") {
                            schema_prise1 = "auBesoin"
                            provenance = "auBesoin"
                        } else if (clickedTraitement.dosageUnite == "quotidiennement") {
                            schema_prise1 = "Quotidiennement"
                            provenance = "quotidiennement"
                        } else {
                            schema_prise1 = "Intervalle"
                            provenance = "intervalleRegulier"
                        }

                        val dureePriseFin: String =
                            if (clickedTraitement.dateFinTraitement == null) {
                                "sf"
                            } else {
                                "date"
                            }
                        //("fusionner schema_prise1 et provenance dans le processus d'add traitement")
                        //pas nécésaire ça marche bien comme ça
                        bundle.putString("schema_prise1", schema_prise1)
                        bundle.putString("provenance", provenance)
                        bundle.putString("dureePriseDbt", "ajd")
                        bundle.putString("dureePriseFin", dureePriseFin)


                        val destinationFragment = AjoutManuelRecapitulatif()
                        destinationFragment.arguments = bundle
                        val fragTransaction = parentFragmentManager.beginTransaction()
                        fragTransaction.replace(R.id.FL, destinationFragment)
                        fragTransaction.addToBackStack(null)
                        fragTransaction.commit()
                    }
                }


            }


        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        //Ajout de la fonctionnalité de retour à la page précédente
        val retour = view.findViewById<ImageView>(R.id.annulerListeEffetsSecondaires)
        retour.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, MainTraitementsFragment())
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
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, MainTraitementsFragment())
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}
