package dev.mobile.medicalink.fragments.traitements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.adapter.ListeEffetsSecondairesAdapterR
import java.util.concurrent.LinkedBlockingQueue

class ListeEffetsSecondairesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var annuler: ImageView

    private lateinit var textAucunEffetSec: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_liste_effets_secondaires, container, false)

        annuler = view.findViewById(R.id.annulerListeEffetsSecondaires)
        textAucunEffetSec = view.findViewById(R.id.textAucunEffetsSec)

        val mesTraitements = getCurrentUserTreatments()

        val traitementsTries = mesTraitements.sortedBy { it.expire }.toMutableList()

        val effetsSecondairesMedicaments = getEffetsSecondairesMedicaments(traitementsTries)

        if (effetsSecondairesMedicaments.isEmpty()) {
            textAucunEffetSec.visibility = View.VISIBLE
        } else {
            textAucunEffetSec.visibility = View.GONE
        }

        recyclerView = view.findViewById(R.id.recyclerViewTypeMedic)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = ListeEffetsSecondairesAdapterR(traitementsTries)

        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))


        annuler.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, MainTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

    /**
     * Récupère les traitements de l'utilisateur courant
     * @return MutableList<Traitement>
     */
    private fun getCurrentUserTreatments(): MutableList<Traitement> {
        val db = AppDatabase.getInstance(requireContext())
        val medocDatabaseInterface = MedocRepository(db.medocDao())
        val userDatabaseInterface = UserRepository(db.userDao())

        val queue = LinkedBlockingQueue<MutableList<Traitement>>()
        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread {
            val listeTraitement: MutableList<Traitement> = mutableListOf()

            //On récuềre l'uuid de l'utilisateur courant
            val uuidUser = userDatabaseInterface.getUsersConnected()[0].uuid


            val listeMedoc = medocDatabaseInterface.getAllMedocByUserId(uuidUser)

            for (medoc in listeMedoc) {
                val traitement = medoc.toTraitement()
                listeTraitement.add(traitement)
            }
            queue.add(listeTraitement)
        }.start()
        return queue.take()
    }

    private fun getEffetsSecondairesMedicaments(
        traitementsTries: List<Traitement>,
    ): MutableMap<String, MutableList<Traitement>> {
        val effetsSecondairesMedicaments: MutableMap<String, MutableList<Traitement>> =
            mutableMapOf()
        traitementsTries.forEach { traitement ->
            traitement.effetsSecondaires.orEmpty().forEach { effetSecondaire ->
                // Vérifie si l'effet secondaire est déjà dans la carte
                if (effetSecondaire.lowercase() in effetsSecondairesMedicaments) {
                    // S'il est présent, ajoutez le traitement à la liste existante
                    effetsSecondairesMedicaments[effetSecondaire.lowercase()].let {
                        it?.add(traitement)
                    }
                } else {
                    // S'il n'est pas présent, créé une nouvelle liste et ajoute le traitement
                    effetsSecondairesMedicaments[effetSecondaire.lowercase()] =
                        mutableListOf(traitement)
                }
            }
        }
        return effetsSecondairesMedicaments
    }

}
