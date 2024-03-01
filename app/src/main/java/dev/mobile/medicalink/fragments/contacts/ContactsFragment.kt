package dev.mobile.medicalink.fragments.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.ContactMedecin
import dev.mobile.medicalink.db.local.repository.ContactMedecinRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.contacts.adapter.ContactsFragmentAdapterR
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import java.util.concurrent.LinkedBlockingQueue


class ContactsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ajouterContact: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        // Récupération des éléments de la vue
        ajouterContact = view.findViewById(R.id.ajouterContact)
        recyclerView = view.findViewById(R.id.recyclerViewMessages)

        // Récupération de la base de données et des interfaces
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

        // Mise en place du recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = ContactsFragmentAdapterR(listeContactMedecin)
        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        // Ajout d'un contact lors du clic sur le bouton ajouter (plus)
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
















