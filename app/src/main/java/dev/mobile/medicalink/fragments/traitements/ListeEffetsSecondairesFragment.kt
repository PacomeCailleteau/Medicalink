package dev.mobile.medicalink.fragments.traitements

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import java.time.LocalDate
import java.util.concurrent.LinkedBlockingQueue

class ListeEffetsSecondairesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var annuler: ImageView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_liste_effets_secondaires, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())

        annuler = view.findViewById(R.id.annulerListeEffetsSecondaires)


        val queue = LinkedBlockingQueue<MutableList<Traitement>>()

        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread{
            val listeTraitement : MutableList<Traitement> = mutableListOf()

            //TODO("Changer l'uuid utilisateur par l'utilisateur courant")
            val listeMedoc = medocDatabaseInterface.getAllMedocByUserId("111111")

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

                val traitement = Traitement(
                    medoc.nom,
                    medoc.dosageNB?.toInt(),
                    medoc.dosageUnite,
                    LocalDate.of(2023,12,12),
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
        recyclerView = view.findViewById(R.id.recyclerViewTypeMedic)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = ListeEffetsSecondairesAdapterR(traitementsTries)

        //Gestion espacement entre items RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))


        annuler.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, MainTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

}