package dev.mobile.medicalink.fragments.traitements

import android.app.AlertDialog
import android.os.Bundle
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
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue


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
        upsert(viewModel, view, userDatabaseInterface, medocDatabaseInterface)


        /* ##############################################################
        #           Partie affichage de tous les traitements            #
        ################################################################# */
        val mesTraitements = getTreatmentToDisplay(medocDatabaseInterface, userDatabaseInterface)

        val traitementsTries =
            mesTraitements.sortedBy { it.dateDbtTraitement }.sortedBy { it.expire }.toMutableList()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewListeEffetsSecondaires)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter(traitementsTries, viewModel, medocDatabaseInterface)

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

    /**
     * Fonction qui permet d'insérer ou de mettre à jour un traitement dans la base de donnée
     * @param viewModel : le viewModel partagé entre les fragments
     * @param view : la vue actuelle
     * @param userDatabaseInterface : l'interface de la base de donnée des utilisateurs
     * @param medocDatabaseInterface : l'interface de la base de donnée des médicaments
     */
    private fun upsert(
        viewModel: AjoutSharedViewModel,
        view: View,
        userDatabaseInterface: UserRepository,
        medocDatabaseInterface: MedocRepository
    ) {
        // On ajoute/modifie seulement si la variable isAddingTraitement est à true ou false
        // Vrai veut dire qu'on ajoute un traitement
        // Faux veut dire qu'on modifie un traitement
        if (viewModel.isAddingTraitement.value == true || viewModel.isAddingTraitement.value == false) {
            val newMedoc = viewModel.toMedoc()

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

            sendNotifIfNeeded(viewModel.makeTraitement(), view)

            viewModel.reset()
        }
    }

    /**
     * Fonction qui permet d'afficher les traitements dans la base de donnée
     * @param medocDatabaseInterface : l'interface de la base de donnée des médicaments
     * @param userDatabaseInterface : l'interface de la base de donnée des utilisateurs
     * @return la liste des traitements
     */
    private fun getTreatmentToDisplay(
        medocDatabaseInterface: MedocRepository,
        userDatabaseInterface: UserRepository
    ): List<Traitement> {
        val queue = LinkedBlockingQueue<MutableList<Traitement>>()
        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread {
            val listeTraitement: MutableList<Traitement> = mutableListOf()
            val listeMedoc = medocDatabaseInterface.getAllMedocByUserId(
                userDatabaseInterface.getUsersConnected(true).first().uuid
            )
            for (medoc in listeMedoc) {
                val traitement = medoc.toTraitement()
                listeTraitement.add(traitement)
            }
            queue.add(listeTraitement)
        }.start()

        return queue.take()
    }

    /**
     * Fonction qui permet de créer un adapter pour la liste des traitements
     * Elle permet de gérer les clics sur les traitements en fonction de si on veut les modifier, les supprimer ou les consulter
     * @param traitementsTries : la liste des traitements
     * @param viewModel : le viewModel partagé entre les fragments
     * @param medocDatabaseInterface : l'interface de la base de donnée des médicaments
     */
    private fun adapter(
        traitementsTries: MutableList<Traitement>,
        viewModel: AjoutSharedViewModel,
        medocDatabaseInterface: MedocRepository
    ): ListeTraitementAdapterR {
        return ListeTraitementAdapterR(traitementsTries) { clickedTraitement, isSuppr ->
            when (isSuppr) {
                null -> {
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
                    viewModel.setUUID(clickedTraitement.uuid ?: "")
                    viewModel.setUUIDUSER(clickedTraitement.uuidUser ?: "")
                    viewModel.setDateDbtTraitement(
                        clickedTraitement.dateDbtTraitement ?: LocalDate.now()
                    )

                    when (clickedTraitement.frequencePrise) {
                        "auBesoin" -> {
                            viewModel.setSchemaPrise1("auBesoin")
                            viewModel.setProvenance("auBesoin")
                        }

                        "quotidiennement" -> {
                            viewModel.setSchemaPrise1("Quotidiennement")
                            viewModel.setProvenance("quotidiennement")
                        }

                        else -> {
                            viewModel.setSchemaPrise1("Intervalle")
                            viewModel.setProvenance("intervalleRegulier")
                        }
                    }
                    GoTo.fragment(AjoutManuelRecapitulatif(), parentFragmentManager)
                }

                true -> {
                    Thread {
                        medocDatabaseInterface.deleteMedoc(
                            medocDatabaseInterface.getOneMedocById(
                                clickedTraitement.uuid!!
                            ).first()
                        )
                    }.start()
                }
            }
        }
    }

    /**
     * Fonction qui permet d'ajouter plusieurs traitements à la base de donnée
     * @param inflater : le layoutInflater
     * @param container : le container
     * @param traitements : la liste des traitements à ajouter
     */
    private fun ajoutPlusieursTraitements(
        inflater: LayoutInflater,
        container: ViewGroup?,
        traitements: ArrayList<Traitement>
    ) {
        val view = inflater.inflate(R.layout.fragment_liste_traitements, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())
        val cisBdpmInterface = CisBdpmRepository(db.cisBdpmDao())
        var newMedoc: Medoc
        var codeCIS: String
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
            dial.text = resources.getString(R.string.pas_medoc_dans_photo)
            val jaiCompris = dialogView.findViewById<Button>(R.id.jaiCompris)

            jaiCompris.setOnClickListener {
                dosageDialog.dismiss()
            }

            dosageDialog.show()
        }
    }

    /**
     * Fonction qui permet d'envoyer une notification si besoin
     * @param newTraitement : le traitement à notifier
     * @param view : la vue actuelle
     */
    private fun sendNotifIfNeeded(newTraitement: Traitement, view: View) {
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
