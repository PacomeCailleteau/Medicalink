package dev.mobile.medicalink.fragments.contacts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.api.rpps.ApiRppsClient
import dev.mobile.medicalink.api.rpps.ApiRppsService
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Contact
import dev.mobile.medicalink.db.local.repository.ContactRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingQueue

class ContactsSearchFragment : Fragment() {


    private lateinit var contactSearchBar: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactButtonLauncher: ActivityResultLauncher<Intent>
    private lateinit var supprimerSearch: ImageView
    private lateinit var ItemList: List<Contact>
    private lateinit var itemAdapter: ContactsSearchAdapterR


    private lateinit var retour: ImageView

    private lateinit var apiRpps: ApiRppsService
    private lateinit var uuid: String

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_search, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        val db = AppDatabase.getInstance(view.context.applicationContext)
        val contactDatabaseInterface = ContactRepository(db.contactDao())
        val userDatabaseInterface = UserRepository(db.userDao())
        apiRpps = ApiRppsClient().apiService
        Thread {
            uuid = userDatabaseInterface.getUsersConnected()[0].uuid
        }.start()

        ItemList = emptyList()

        contactSearchBar = view.findViewById(R.id.add_manually_search_bar)
        supprimerSearch = view.findViewById(R.id.supprimerSearch)

        supprimerSearch.setOnClickListener {
            contactSearchBar.setText("")
        }

        contactSearchBar.addTextChangedListener(textWatcher)
        contactButtonLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                }
            }


        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSearch)

        itemAdapter = ContactsSearchAdapterR(ItemList) { clickedItem ->
            afficherContact(clickedItem)
        }
        recyclerView.adapter = itemAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        retour = view.findViewById(R.id.retour_schema_prise2)

        retour.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, ContactsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            lifecycleScope.launch {
                itemAdapter = ContactsSearchAdapterR(getPracticiansToContact(uuid, contactSearchBar.text.toString())) { clickedItem ->
                    afficherContact(clickedItem)
                }
                recyclerView.adapter = itemAdapter
            }
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    }

    fun clearFocusAndHideKeyboard(view: View) {
        // Parcours tous les champs de texte, efface le focus
        val editTextList = listOf(contactSearchBar) // Ajoute tous tes champs ici
        for (editText in editTextList) {
            editText.clearFocus()
        }

        // Cache le clavier
        val imm =
            requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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

    override fun onResume() {
        super.onResume()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, ContactsFragment())
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    suspend fun getPracticiansToContact(uuid: String, search: String): List<Contact> {
        val response = apiRpps.getPracticians(search)
        if (response.isSuccessful) {
            val itemList = response.body()
            Log.d("Practician list", itemList.toString())
            var itemListContact = mutableListOf<Contact>()
            if (itemList != null) {
                itemListContact = itemList.map {
                    Contact.fromPractician(
                        uuid,
                        it
                    )
                } as MutableList<Contact>
            }
            Log.d("Contact list", itemListContact.toString())
            return itemListContact
        } else {
            return emptyList()
        }
    }
}
