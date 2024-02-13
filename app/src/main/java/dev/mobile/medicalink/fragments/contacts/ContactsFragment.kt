package dev.mobile.medicalink.fragments.contacts

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Contact
import dev.mobile.medicalink.db.local.repository.ContactRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView


class ContactsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        val db = AppDatabase.getInstance(view.context.applicationContext)
        val contactDatabaseInterface = ContactRepository(db.contactDao())
        val userDatabaseInterface = UserRepository(db.userDao())

        val creerContact = view.findViewById<View>(R.id.creerContact)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.VISIBLE
        }

        //Aller à la page ContactSearchFragment
        creerContact.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, ContactsSearchFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        fun afficherContact(itemClicked: Contact) {
            val bundle = Bundle()
            bundle.putSerializable(
                "contact",
                itemClicked
            )
            val destinationFragment = InfosContactFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        val list_medecin = contactDatabaseInterface.getAllContact().toMutableList()

        if (list_medecin.isEmpty()) {
            Thread {
                list_medecin.add(
                    Contact(
                        userDatabaseInterface.getUsersConnected()[0].uuid,
                        10108074344,
                        "Kevin",
                        "OLYMPA-LENERAND",
                        "Kevin OLYMPA-LENERAND",
                        "Masseur-Kinésithérapeute",
                        "5 Place ABBE PIERRE",
                        "69009",
                        "LYON",
                        "+33478662149",
                        "kevin.olympa-lenerand@email.com"
                    )
                )
            }.start()
        }

        recyclerView = view.findViewById(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter =
            ContactsAdapterR(list_medecin) { clickedItem -> afficherContact(clickedItem) }

        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))


        return view
    }
}