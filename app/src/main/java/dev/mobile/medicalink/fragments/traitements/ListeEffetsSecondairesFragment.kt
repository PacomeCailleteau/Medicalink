package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.EffetSecondaire
import dev.mobile.medicalink.db.local.repository.EffetSecondaireRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.LinkedBlockingQueue

class ListeEffetsSecondairesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var annuler: ImageView

    private lateinit var ajoutEffetSecondaire: AppCompatButton
    private lateinit var textAucunEffetSec: TextView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_liste_effets_secondaires, container, false)
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())
        val effetsSecondaireInterface = EffetSecondaireRepository(db.effetSecondaireDao())

        annuler = view.findViewById(R.id.annulerListeEffetsSecondaires)
        textAucunEffetSec = view.findViewById(R.id.textAucunEffetsSec)
        ajoutEffetSecondaire = view.findViewById(R.id.ajouterEffetSecondaire)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.VISIBLE
        }

        val queue = LinkedBlockingQueue<List<EffetSecondaire>>()

        //Récupération des effets secondaires en les transformant en une liste d'effets secondaires pour les afficher
        Thread {
            val uuidUser = userDatabaseInterface.getUsersConnected()[0].uuid

            val listeEffetSecondaire = effetsSecondaireInterface.getEffetSecondairesByUuid(uuidUser)

            val aucunEffetSecondaire = view.findViewById<View>(R.id.textAucunEffetsSec)
            if (listeEffetSecondaire.isEmpty()) {
                aucunEffetSecondaire.visibility = View.VISIBLE
            } else {
                aucunEffetSecondaire.visibility = View.GONE
            }

            queue.add(listeEffetSecondaire)

        }.start()

        ajoutEffetSecondaire.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, AjoutEffetSecondaireFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        annuler.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, MainTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        fun afficherEffetSecondaire(itemClicked: EffetSecondaire) {
            val bundle = Bundle()
            bundle.putSerializable(
                "effetSecondaire",
                itemClicked
            )
            val destinationFragment = InfoEffetSecondaireFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        var listeEffetSecondaire = queue.take()

        //TODO(Faire le tri des effets secondaires par date de manière décroissante)
        listeEffetSecondaire = listeEffetSecondaire.sortedByDescending {
            LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern("dd/MM/yyyy HH'h'mm"))
        }.toMutableList()



        Log.d("test", listeEffetSecondaire.toString())
        recyclerView = view.findViewById(R.id.recyclerViewEffetSecondaire)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter =
            ListeEffetsSecondairesAdapterR(listeEffetSecondaire) { clickedItem ->
                afficherEffetSecondaire(
                    clickedItem
                )
            }

        Thread {
            val uuid = userDatabaseInterface.getUsersConnected()[0].uuid
            val listEffetSecondaire = effetsSecondaireInterface.getEffetSecondairesByUuid(uuid)
            val aucunEffetSecondaire = view.findViewById<View>(R.id.textAucunEffetsSec)
            if (listEffetSecondaire.isEmpty()) {
                aucunEffetSecondaire.visibility = View.VISIBLE
            } else {
                aucunEffetSecondaire.visibility = View.GONE
            }
            recyclerView.adapter =
                ListeEffetsSecondairesAdapterR(listEffetSecondaire) { clickedItem ->
                    afficherEffetSecondaire(
                        clickedItem
                    )
                }
        }.start()

        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))


        return view
    }

}
