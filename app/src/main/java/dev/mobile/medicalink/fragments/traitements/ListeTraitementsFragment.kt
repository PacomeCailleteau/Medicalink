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
import dev.mobile.medicalink.ChangerUtilisateurAdapterR
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
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

        var isAddingTraitement  = arguments?.getString("isAddingTraitement")
        var uuidUpdateTraitement  = arguments?.getString("uuidUpdateTraitement")



        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.VISIBLE
        }




        if (isAddingTraitement=="true" || isAddingTraitement=="false"){
            var newTraitement = arguments?.getSerializable("newTraitement") as Traitement

            var newMedoc : Medoc
            var traitementUUID : String = UUID.randomUUID().toString()
            when (isAddingTraitement){
                "true" -> {
                    traitementUUID = UUID.randomUUID().toString()
                }
                "false" -> {
                    traitementUUID=newTraitement.UUID.toString()
                }
            }

            var newTraitementEffetsSec : String? = null
            if (newTraitement.effetsSecondaires!=null){
                var chaineDeChar = ""
                for (effet in newTraitement.effetsSecondaires!!){
                    chaineDeChar+="$effet;"
                }
                chaineDeChar=chaineDeChar.subSequence(0,chaineDeChar.length-1).toString()
                newTraitementEffetsSec=chaineDeChar
            }

            var newTraitementPrises : String? = null

            if (newTraitement.prises!=null){
                var chaineDeChar = ""
                for (prise in newTraitement.prises!!){
                    chaineDeChar+="${prise.toString()}/"
                }
                chaineDeChar=chaineDeChar.subSequence(0,chaineDeChar.length-1).toString()
                newTraitementPrises=chaineDeChar
            }


            newMedoc = Medoc(
                traitementUUID,
                "",
                newTraitement.nomTraitement,
                newTraitement.dosageNb.toString(),
                newTraitement.dosageUnite,
                newTraitement.dateFinTraitement.toString(),
                newTraitement.typeComprime,
                newTraitement.comprimesRestants,
                newTraitement.expire,
                newTraitementEffetsSec,
                newTraitementPrises,
                newTraitement.totalQuantite
            )

            val queue2 = LinkedBlockingQueue<Boolean>()
            Thread{
                var uuidUserCourant = userDatabaseInterface.getUsersConnected(true).first().uuid
                newMedoc.uuidUser=uuidUserCourant
                when (isAddingTraitement){
                    "true" -> {
                        medocDatabaseInterface.insertMedoc(newMedoc)
                    }
                    "false" -> {
                        medocDatabaseInterface.updateMedoc(newMedoc)
                    }
                }
                queue2.add(true)
            }.start()
            queue2.take()

        }

        val queue = LinkedBlockingQueue<MutableList<Traitement>>()

        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread{
            val listeTraitement : MutableList<Traitement> = mutableListOf()

            val listeMedoc = medocDatabaseInterface.getAllMedocByUserId(userDatabaseInterface.getUsersConnected(true).first().uuid)

            for (medoc in listeMedoc){

                var listeEffetsSec : MutableList<String>? = null
                if (medoc.effetsSecondaires!=null){
                    listeEffetsSec = medoc.effetsSecondaires.split(";").toMutableList()
                }


                val listePrise = mutableListOf<Prise>()

                if (medoc.prises != null){
                    for (prise in medoc.prises.split("/")){
                        val traitementPrise : MutableList<String> = prise.split(";").toMutableList()
                        val maPrise = Prise(traitementPrise[0].toInt(),traitementPrise[1],traitementPrise[2].toInt(),traitementPrise[3])
                        listePrise.add(maPrise)
                    }
                }

                var newTraitementFinDeTraitement : LocalDate? = null

                if (medoc.dateFinTraitement!="null") {
                    Log.d("test",medoc.dateFinTraitement.toString())
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val date = medoc.dateFinTraitement

                    //convert String to LocalDate

                    //convert String to LocalDate
                    newTraitementFinDeTraitement = LocalDate.parse(date, formatter)
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
                    medoc.uuidUser
                )

                listeTraitement.add(traitement)

            }
            queue.add(listeTraitement)
        }.start()

        val mesTraitements = queue.take()


        val traitementsTries = mesTraitements.sortedBy { it.expire }.toMutableList()


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewListeEffetsSecondaires)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ListeTraitementAdapterR(traitementsTries) { clickedTraitement ->

            val bundle = Bundle()

            bundle.putString("isAddingTraitement", "false")
            bundle.putString("uuidUpdateTraitement", "$")


            bundle.putSerializable("traitement", Traitement(clickedTraitement.nomTraitement, clickedTraitement.dosageNb,clickedTraitement.dosageUnite,clickedTraitement.dateFinTraitement,clickedTraitement.typeComprime,clickedTraitement.comprimesRestants,clickedTraitement.expire,clickedTraitement.effetsSecondaires,clickedTraitement.prises,clickedTraitement.totalQuantite,clickedTraitement.UUID,clickedTraitement.UUIDUSER))

            val schema_prise1 : String
            val provenance : String
            if (clickedTraitement.dosageUnite=="auBesoin"){
                schema_prise1 = "auBesoin"
                provenance = "auBesoin"
            }else if (clickedTraitement.dosageUnite=="quotidiennement"){
                schema_prise1 = "Quotidiennement"
                provenance = "quotidiennement"
            }else{
                schema_prise1 = "Intervalle"
                provenance = "intervalleRegulier"
            }
            var dureePriseFin : String

            if (clickedTraitement.dateFinTraitement==null) {
                dureePriseFin = "sf"
            }else{
                dureePriseFin = "date"
            }
            //TODO("fusionner schema_prise1 et provenance dans le processus d'add traitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "ajd")
            bundle.putString("dureePriseFin", "$dureePriseFin")


            val destinationFragment = AjoutManuelRecapitulatif()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()

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

        // Attacher le gestionnaire du bouton de retour arrière de l'appareil
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