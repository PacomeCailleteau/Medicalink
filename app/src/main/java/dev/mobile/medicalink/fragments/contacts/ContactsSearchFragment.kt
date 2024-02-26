package dev.mobile.medicalink.fragments.contacts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class ContactsSearchFragment : Fragment() {


    private lateinit var contactSearchBar: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactButtonLauncher: ActivityResultLauncher<Intent>
    private lateinit var supprimerSearch: ImageView
    private lateinit var ItemList: List<Contact>
    private lateinit var itemAdapter: ContactsSearchAdapterR
    private lateinit var erreurRecherche: TextView

    private lateinit var retour: ImageView

    private lateinit var apiRpps: ApiRppsService
    private lateinit var uuid: String
    private var searchJob: Job? = null

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_search, container, false)

        contactSearchBar = view.findViewById(R.id.add_manually_search_bar)
        supprimerSearch = view.findViewById(R.id.supprimerSearch)
        recyclerView = view.findViewById(R.id.recyclerViewSearch)
        retour = view.findViewById(R.id.retour_schema_prise2)
        erreurRecherche = view.findViewById(R.id.textViewErreurRecherche)

        val db = AppDatabase.getInstance(view.context.applicationContext)
        val contactDatabaseInterface = ContactRepository(db.contactDao())
        val userDatabaseInterface = UserRepository(db.userDao())
        apiRpps = ApiRppsClient().apiService
        Thread {
            uuid = userDatabaseInterface.getUsersConnected()[0].uuid
        }.start()

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        ItemList = emptyList()

        supprimerSearch.setOnClickListener {
            contactSearchBar.setText("")
        }

        contactSearchBar.addTextChangedListener(textWatcher)
        contactButtonLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                }
            }

        itemAdapter = ContactsSearchAdapterR(ItemList) { clickedItem ->
            afficherContact(clickedItem)
        }
        recyclerView.adapter = itemAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

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
            // Ne rien faire avant la modification du texte
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Ne rien faire avant la fin de la modification du texte
        }

        override fun afterTextChanged(editable: Editable?) {
            searchJob?.cancel() // Annuler la recherche précédente
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(300) // Attendre 600ms de debounce
                updateSearchResults(editable.toString())
            }
        }
    }

    private suspend fun updateSearchResults(query: String) {
        erreurRecherche.visibility = View.GONE
        var results = emptyList<Contact>()
        if (isOnline(requireContext())) {
            if (Regex("^(\\D*|\\d{11})\$").containsMatchIn(query)) {
                if (query.length < 3) {
                    erreurRecherche.text = "Veuillez entrer au moins 3 caractères"
                    erreurRecherche.visibility = View.VISIBLE

                } else {
                    results = getPracticiansToContact(uuid, query)
                    if (results.isEmpty()) {
                        erreurRecherche.text = "Aucun résultat"
                        erreurRecherche.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            erreurRecherche.text = "Vérifiez votre connexion Internet"
            erreurRecherche.visibility = View.VISIBLE
        }
        itemAdapter = ContactsSearchAdapterR(results) { clickedItem ->
            afficherContact(clickedItem)
        }
        recyclerView.adapter = itemAdapter
    }

    private suspend fun getPracticiansToContact(uuid: String, search: String): List<Contact> {
        return try {
            val response = apiRpps.getPracticians(search)
            if (response.isSuccessful) {
                response.body()?.map { Contact.fromPractician(uuid, it) } ?: emptyList()
            } else {
                withContext(Dispatchers.Main) {
                    erreurRecherche.text = "Erreur du serveur"
                    erreurRecherche.visibility = View.VISIBLE
                }
                emptyList()
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                erreurRecherche.text = "Erreur de connexion"
                erreurRecherche.visibility = View.VISIBLE
            }
            emptyList()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                erreurRecherche.text = "Une erreur est survenue"
                erreurRecherche.visibility = View.VISIBLE
            }
            emptyList()
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

    private fun afficherContact(itemClicked: Contact) {
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

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}
