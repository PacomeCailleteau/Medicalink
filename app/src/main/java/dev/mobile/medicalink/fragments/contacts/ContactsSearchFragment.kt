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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.api.rpps.ApiRppsClient
import dev.mobile.medicalink.api.rpps.ApiRppsService
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Contact
import dev.mobile.medicalink.db.local.repository.ContactRepository
import dev.mobile.medicalink.fragments.traitements.AddTraitementsFragment
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import java.util.concurrent.LinkedBlockingQueue
import androidx.lifecycle.lifecycleScope
import dev.mobile.medicalink.fragments.traitements.AjoutManuelSearchAdapterR
import kotlinx.coroutines.launch

class ContactsSearchFragment : Fragment() {


    private lateinit var contactSearchBar: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactButtonLauncher: ActivityResultLauncher<Intent>
    private lateinit var supprimerSearch: ImageView
    private lateinit var ItemList: List<Contact>
    private lateinit var itemAdapter: ContactsSearchAdapterR


    private lateinit var retour: ImageView

    private lateinit var apiRpps: ApiRppsService

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
        apiRpps = ApiRppsClient().apiService

        //Récupération de la liste des Médicaments pour l'afficher
        val queue = LinkedBlockingQueue<List<Contact>>()
        Thread {
            val listContact = contactDatabaseInterface.getAllContact()
            Log.d("Contact list", listContact.toString())
            queue.add(listContact)
        }.start()
        ItemList = queue.take()

        contactSearchBar = view.findViewById(R.id.add_manually_search_bar)
        supprimerSearch = view.findViewById(R.id.supprimerSearch)

        supprimerSearch.setOnClickListener {
            contactSearchBar.setText("")
        }
        // addManuallySearchBar.setText(traitement.nomTraitement)
        /* Regex : laisser en commentaire pour l'instant
        addManuallySearchBar.filters =
            arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                source?.let {
                    if (it.contains("\n")) {
                        // Bloquer le collage de texte
                        return@InputFilter ""
                    }
                }
                null
            })

        val rootLayout = view.findViewById<View>(R.id.constraint_layout_ajout_manuel_search)
        rootLayout.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                clearFocusAndHideKeyboard(v)
            }
            return@setOnTouchListener false
        }

        val regex = Regex(
            pattern = "^[a-zA-ZéèàêîôûäëïöüçÉÈÀÊÎÔÛÄËÏÖÜÇ\\d\\s-]*$",
            options = setOf(RegexOption.IGNORE_CASE)
        )

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            val input = source.subSequence(start, end).toString()
            val currentText =
                dest.subSequence(0, dstart).toString() + dest.subSequence(dend, dest.length)
            val newText = currentText.substring(0, dstart) + input + currentText.substring(dstart)

            if (regex.matches(newText)) {
                null // Caractères autorisés
            } else {
                dest.subSequence(dstart, dend)
            }
        }
        */
        /* Regex : laisser en commentaire pour l'instant
        addManuallySearchBar.filters = arrayOf(filter)
         */
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
                val searchText = s.toString() // Utilisez editable directement pour éviter une référence nulle
                if (searchText.isNotEmpty()) {
                    lifecycleScope.launch {
                        val response = apiRpps.getPractician(searchText)
                        if (response.isSuccessful) {
                            // Mettez à jour votre UI ici avec la réponse
                            val itemList = response.body()
                            itemAdapter = ContactsSearchAdapterR(ItemList) { clickedItem ->
                                afficherContact(clickedItem)
                            }
                            recyclerView.adapter = itemAdapter
                        } else {
                            // Gérez l'erreur ici
                        }
                    }
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
}
