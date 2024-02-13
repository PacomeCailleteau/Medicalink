package dev.mobile.medicalink.fragments.contacts

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.MainTraitementsFragment
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView


class ContactsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        val creerContact = view.findViewById<View>(R.id.creerContact)

        //Aller à la page AjoutManuelSearchFragment
        creerContact.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, ContactsSearchFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        val contactsDeTest = mutableListOf<Pair<String, String>>(
            Pair("Dr. Jean Mais", "Médecin généraliste"),
            Pair("Dr. Stéphanie Jolie", "Kinésithérapeuthe/Ostéopathe"),
            Pair("Dr. Antoine Laballe", "Dentiste"),
            Pair("Dr. Jeanne Lefort", "Gynécologue"),
            Pair("Dr. Lili Pote", "Psychologue"),
            Pair("Dr. Léon Maboule", "Psychiatre"),
        )

        recyclerView = view.findViewById(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = ContactsFragmentAdapterR(contactsDeTest)

        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))


        return view
    }
}