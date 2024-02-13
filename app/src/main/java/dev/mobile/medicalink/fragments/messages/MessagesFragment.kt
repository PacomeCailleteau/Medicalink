package dev.mobile.medicalink.fragments.messages

import android.os.Build
import android.os.Bundle
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
import dev.mobile.medicalink.db.local.entity.ContactMedecin
import dev.mobile.medicalink.db.local.repository.ContactMedecinRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import java.util.concurrent.LinkedBlockingQueue


class MessagesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ajouterContact: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        val db = AppDatabase.getInstance(view.context.applicationContext)
        val contactMedecinInterface = ContactMedecinRepository(db.contactMedecinDao())
        val userInterface = UserRepository(db.userDao())

        var listeContactMedecin = listOf<ContactMedecin>()

        // On récupère tous les contacts du user connecté
        val queue = LinkedBlockingQueue<Boolean>()
        Thread {
            val user = userInterface.getUsersConnected()
            if (user.size == 1) {
                listeContactMedecin =
                    contactMedecinInterface.getContactMedecinByUserUuid(user[0].uuid)
                queue.put(true)
            } else {
                queue.put(false)
            }
        }.start()

        queue.take()

        listeContactMedecin = listOf(
            ContactMedecin("10", "10", "Jean", "Dupont", "a@a.a", "0123456789", "123456789", "", "", ""),
            ContactMedecin("11", "10", "Jean2", "Dupont2", "a@a.a", "0123456789", "123456789", "", "", ""),
            ContactMedecin("12", "10", "Jean3", "Dupont3", "a@a.a", "0123456789", "123456789", "", "", ""),
            ContactMedecin("13", "10", "Jean4", "Dupont4", "a@a.a", "0123456789", "123456789", "", "", ""),
            ContactMedecin("14", "10", "Jean5", "Dupont5", "a@a.a", "0123456789", "123456789", "", "", ""),
            ContactMedecin("15", "10", "Jean6", "Dupont6", "a@a.a", "0123456789", "123456789", "", "", ""),
            ContactMedecin("16", "10", "Jean7", "Dupont7", "a@a.a", "0123456789", "123456789", "", "", ""),
        )

        recyclerView = view.findViewById(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = MessagesFragmentAdapterR(listeContactMedecin)

        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        ajouterContact = view.findViewById(R.id.ajouterContact)
        ajouterContact.setOnClickListener {
            val fragment = AjoutContactMedecinFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.FL, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }
}
















