package dev.mobile.medicalink.fragments.traitements.ajoutmanuel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
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
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.dao.CisBdpmDao
import dev.mobile.medicalink.db.local.entity.CisBdpm
import dev.mobile.medicalink.db.local.repository.CisBdpmRepository
import dev.mobile.medicalink.fragments.traitements.AddTraitementsFragment
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread


class AjoutManuelSearchFragment : Fragment() {


    private lateinit var addManuallySearchBar: TextInputEditText
    private lateinit var addManuallyButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var addManuallyButtonLauncher: ActivityResultLauncher<Intent>
    private lateinit var supprimerSearch: ImageView
    private lateinit var filteredItemList: List<CisBdpm>
    private lateinit var itemAdapter: AjoutManuelSearchAdapterR

    private lateinit var CisBdpmDatabaseInterface: CisBdpmRepository

    private lateinit var retour: ImageView

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajout_manuel_search, container, false)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        val db = AppDatabase.getInstance(view.context.applicationContext)
        CisBdpmDatabaseInterface = CisBdpmRepository(db.cisBdpmDao())

        val traitement = arguments?.getSerializable("traitement") as Traitement
        val isAddingTraitement = arguments?.getString("isAddingTraitement")
        val schema_prise1 = arguments?.getString("schema_prise1")
        var provenance = arguments?.getString("provenance")
        val dureePriseDbt = arguments?.getString("dureePriseDbt")
        val dureePriseFin = arguments?.getString("dureePriseFin")

        addManuallySearchBar = view.findViewById(R.id.add_manually_search_bar)
        addManuallyButton = view.findViewById(R.id.add_manually_button)
        supprimerSearch = view.findViewById(R.id.supprimerSearch)

        supprimerSearch.setOnClickListener {
            addManuallySearchBar.setText("")
        }
        addManuallySearchBar.setText(traitement.nomTraitement)


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
            pattern = "^[a-zA-Z,/()'.%éèàêîôûäëïöüçÉÈÀÊÎÔÛÄËÏÖÜÇ\\d\\s-]*$",
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

        updateButtonState()

        addManuallySearchBar.filters = arrayOf(filter)

        addManuallySearchBar.addTextChangedListener(textWatcher(traitement))
        addManuallyButtonLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                }
            }


        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSearch)

        recyclerView.layoutManager = LinearLayoutManager(context)
        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))




        addManuallyButton.setOnClickListener {
            val bundle = Bundle()
            println("Traitement : ${addManuallySearchBar.text.toString()}")
            println("TraitementCis : ${traitement.CodeCIS}")

            traitement.effetsSecondaires = null
            traitement.nomTraitement = addManuallySearchBar.text.toString()

            bundle.putSerializable(
                "traitement",
                traitement
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schema_prise1")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelTypeMedic()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        retour = view.findViewById(R.id.retour_schema_prise2)

        retour.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, AddTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        updateItems("", traitement)

        return view
    }

    fun textWatcher(traitement: Traitement): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ne rien faire avant la modification du texte
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateItems(addManuallySearchBar.text.toString(), traitement)
            }

            override fun afterTextChanged(editable: Editable?) {
                updateButtonState()
            }
        }
    }


    /**
     * Mise à jour de l'état du bouton "Ajouter" pour l'activer uniquement quand le champ de recherche n'est pas vide
     */
    private fun updateButtonState() {
        val allFieldsFilled = addManuallySearchBar.text!!.isNotBlank()

        if (allFieldsFilled) {
            addManuallyButton.isEnabled = true
            addManuallyButton.alpha = 1.0f
        } else {
            addManuallyButton.isEnabled = false
            addManuallyButton.alpha = 0.3.toFloat()
        }
    }

    fun clearFocusAndHideKeyboard(view: View) {
        // Parcours tous les champs de texte, efface le focus
        val editTextList = listOf(addManuallySearchBar) // Ajoute tous tes champs ici
        for (editText in editTextList) {
            editText.clearFocus()
        }

        // Cache le clavier
        val imm =
            requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, AddTraitementsFragment())
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /**
     * Fonction de filtrage de la liste de médicaments sur une chaine de caractère (ici le contenu de la barre de recherche)
     * @param query la chaine de caractère sur laquelle on filtre la liste des médicaments
     */
    private fun updateItems(query: String, traitement: Traitement) {
        Thread {
            val itemList = CisBdpmDatabaseInterface.search100(query)
            requireActivity().runOnUiThread {
                itemAdapter = AjoutManuelSearchAdapterR(itemList) { clickedItem ->
                    updateSearchBar(
                        clickedItem,
                        traitement
                    )
                }
                recyclerView.adapter = itemAdapter
                itemAdapter.notifyDataSetChanged()
            }
        }.start()

    }

    /**
     * Fonction utilisé pour mettre à jour le contenu de la barre de recherche
     * (utilisé quand on clique sur un médicament pour l'ajouter directement dans la barre de recherche)
     * @param query la chaine de caractère représentant le médicament sur lequel on a cliqué, à remplacer dans la barre de recherche
     */
    private fun updateSearchBar(query: CisBdpm, traitement: Traitement) {
        addManuallySearchBar.setText(query.denomination)
        traitement.CodeCIS = query.CodeCIS
    }

}
