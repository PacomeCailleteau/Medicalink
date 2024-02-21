package dev.mobile.medicalink.fragments.traitements

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.repository.CisBdpmRepository
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.adapter.ListeTraitementAdapterR
import dev.mobile.medicalink.fragments.traitements.ajouts.AjoutManuelRecapitulatif
import dev.mobile.medicalink.fragments.traitements.ajouts.AjoutSharedViewModel
import dev.mobile.medicalink.utils.GoTo
import dev.mobile.medicalink.utils.notification.NotificationService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.ArrayList


class ListeTraitementsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_liste_traitements, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())

        val viewModel = ViewModelProvider(requireActivity()).get(AjoutSharedViewModel::class.java)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.VISIBLE
        }


        // ajout des nouveaux traitements si on vient de l'OCR
        val resultList = arguments?.getSerializable("result") as? ArrayList<Traitement>

        if (resultList != null) {
            ajoutPlusieursTraitements(inflater, container, resultList)
        }

        /* ##############################################################
        #               Partie update et insert du traitement           #
        ################################################################# */
        if (viewModel.isAddingTraitement.value == true || viewModel.isAddingTraitement.value == false) {
            val newMedoc: Medoc

            var newTraitementEffetsSec: String? = null
            if (viewModel.effetsSecondaires.value != null) {
                var chaineDeChar = ""
                for (effet in viewModel.effetsSecondaires.value!!) {
                    chaineDeChar += "$effet;"
                }
                if (chaineDeChar != "") chaineDeChar =
                    chaineDeChar.subSequence(0, chaineDeChar.length - 1).toString()
                newTraitementEffetsSec = chaineDeChar
            }

            var newTraitementPrises: String? = null
            if (viewModel.prises.value != null) {
                var chaineDeChar = ""
                for (prise in viewModel.prises.value!!) {
                    chaineDeChar += "${prise}/"
                }
                if (chaineDeChar != "") chaineDeChar =
                    chaineDeChar.subSequence(0, chaineDeChar.length - 1).toString()
                newTraitementPrises = chaineDeChar
            }


            newMedoc = Medoc(
                if (viewModel.isAddingTraitement.value!!) UUID.randomUUID()
                    .toString() else viewModel.UUID.value!!,
                "",
                viewModel.nomTraitement.value ?: "",
                viewModel.codeCIS.value ?: "",
                viewModel.dosageNb.value.toString(),
                viewModel.frequencePrise.value ?: "",
                viewModel.dateFinTraitement.value?.toString() ?: "null",
                viewModel.typeComprime.value ?: "",
                viewModel.comprimesRestants.value ?: 0,
                viewModel.dateFinTraitement.value != null && viewModel.dateFinTraitement.value!! > LocalDate.now(),
                newTraitementEffetsSec ?: "null",
                newTraitementPrises ?: "null",
                viewModel.totalQuantite.value ?: 0,
                viewModel.dateDbtTraitement.value?.toString() ?: "null"
            )

            val queue2 = LinkedBlockingQueue<Boolean>()
            Thread {
                val uuidUserCourant = userDatabaseInterface.getUsersConnected(true).first().uuid
                newMedoc.uuidUser = uuidUserCourant
                if (viewModel.isAddingTraitement.value!!) {
                    medocDatabaseInterface.insertMedoc(newMedoc)
                } else {
                    medocDatabaseInterface.updateMedoc(newMedoc)
                }
                queue2.add(true)
            }.start()
            queue2.take()

            val newTraitement = viewModel.makeTraitement()
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

            viewModel.reset()
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

            for (medoc in listeMedoc) {

                var listeEffetsSec: MutableList<String>? = null
                if (medoc.effetsSecondaires != null) {
                    listeEffetsSec = medoc.effetsSecondaires.split(";").toMutableList()
                }


                val listePrise = mutableListOf<Prise>()

                if (!medoc.prises.isNullOrEmpty()) {
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
                    medoc.nom,
                    medoc.codeCIS,
                    medoc.dosageNB.toInt(),
                    medoc.frequencePrise,
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
            ListeTraitementAdapterR(traitementsTries) { clickedTraitement, isSuppr ->
                when (isSuppr) {
                    null -> {
                        Log.d("test", clickedTraitement.nomTraitement)
                        val bundle = Bundle()
                        bundle.putSerializable("medoc", clickedTraitement)
                        val destinationFragment = InfoMedocFragment()
                        destinationFragment.arguments = bundle
                        GoTo.fragment(destinationFragment, parentFragmentManager)
                    }

                    false -> {
                        viewModel.setIsAddingTraitement(false)
                        viewModel.setNomTraitement(clickedTraitement.nomTraitement)
                        viewModel.setCodeCIS(clickedTraitement.codeCIS)
                        viewModel.setDosageNb(clickedTraitement.dosageNb)
                        viewModel.setFrequencePrise(clickedTraitement.frequencePrise)
                        viewModel.setDateFinTraitement(clickedTraitement.dateFinTraitement)
                        viewModel.setTypeComprime(clickedTraitement.typeComprime)
                        viewModel.setComprimesRestants(clickedTraitement.comprimesRestants ?: 0)
                        viewModel.setEffetsSecondaires(
                            clickedTraitement.effetsSecondaires ?: mutableListOf()
                        )
                        viewModel.setPrises(clickedTraitement.prises ?: mutableListOf())
                        viewModel.setTotalQuantite(clickedTraitement.totalQuantite ?: 0)
                        viewModel.setUUID(clickedTraitement.UUID ?: "")
                        viewModel.setUUIDUSER(clickedTraitement.UUIDUSER ?: "")
                        viewModel.setDateDbtTraitement(
                            clickedTraitement.dateDbtTraitement ?: LocalDate.now()
                        )

                        if (clickedTraitement.frequencePrise == "auBesoin") {
                            viewModel.setSchemaPrise1("auBesoin")
                            viewModel.setProvenance("auBesoin")
                        } else if (clickedTraitement.frequencePrise == "quotidiennement") {
                            viewModel.setSchemaPrise1("Quotidiennement")
                            viewModel.setProvenance("quotidiennement")
                        } else {
                            viewModel.setSchemaPrise1("Intervalle")
                            viewModel.setProvenance("intervalleRegulier")
                        }
                        GoTo.fragment(AjoutManuelRecapitulatif(), parentFragmentManager)
                    }

                    true -> {
                        Thread {
                            medocDatabaseInterface.deleteMedoc(
                                medocDatabaseInterface.getOneMedocById(
                                    clickedTraitement.UUID!!
                                ).first()
                            )
                        }.start()
                    }
                }
            }
        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        //Ajout de la fonctionnalité de retour à la page précédente
        val retour = view.findViewById<ImageView>(R.id.annulerListeEffetsSecondaires)
        retour.setOnClickListener {
            GoTo.fragment(MainTraitementsFragment(), parentFragmentManager)
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun ajoutPlusieursTraitements(inflater: LayoutInflater, container: ViewGroup?, traitements: ArrayList<Traitement>) {
        val view = inflater.inflate(R.layout.fragment_liste_traitements, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())
        val cisBdpmInterface = CisBdpmRepository(db.cisBdpmDao())
        var newMedoc : Medoc
        var codeCIS : String
        val queue2 = LinkedBlockingQueue<Boolean>()
        var compteur = 0
        // faire une popup si tout n'a pas été trouvé

        traitements.forEach {
            Thread {
                codeCIS = cisBdpmInterface.getCodeCISByName(it.nomTraitement)
                if (codeCIS.isNotEmpty()) {
                    newMedoc = Medoc(
                        UUID.randomUUID().toString(),
                        "",
                        it.nomTraitement,
                        codeCIS,
                        it.dosageNb.toString(),
                        it.frequencePrise,
                        it.dateFinTraitement.toString(),
                        it.typeComprime,
                        it.comprimesRestants,
                        it.expire,
                        null,
                        null,
                        it.totalQuantite,
                        it.dateDbtTraitement.toString()
                    )
                    val uuidUserCourant = userDatabaseInterface.getUsersConnected().first().uuid
                    newMedoc.uuidUser = uuidUserCourant
                    medocDatabaseInterface.insertMedoc(newMedoc)
                    queue2.add(true)
                } else {
                    compteur++
                }
            }.start()
            queue2.take()
        }
        if (traitements.isEmpty() || compteur>0) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_duplicate_substance, null)
            val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
            builder.setView(dialogView)

            val dosageDialog = builder.create()

            val dial = dialogView.findViewById<TextView>(R.id.ajouterVrm)
            dial.text = "No prescription was detected in the picture. Please, try to use another picture of the prescription or add manually your medication."
            val jaiCompris = dialogView.findViewById<Button>(R.id.jaiCompris)

            jaiCompris.setOnClickListener {
                dosageDialog.dismiss()
            }

            dosageDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()

        val callback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                GoTo.fragment(MainTraitementsFragment(), parentFragmentManager)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}
