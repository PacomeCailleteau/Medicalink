package dev.mobile.medicalink.fragments.contacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.contacts.adapter.AjoutContactMedecinFragmentAdapterR
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.utils.medecin.Medecin
import dev.mobile.medicalink.utils.medecin.MedecinApi


class AjoutContactMedecinFragment : Fragment() {

    private lateinit var retour: ImageView
    private lateinit var supprimerSearch: ImageView
    private lateinit var searchByRpps: TextInputEditText
    private lateinit var searchByName: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var search: Button
    private lateinit var itemAdapter: AjoutContactMedecinFragmentAdapterR
    private lateinit var progressBarMedecin: ProgressBar
    private val medecinApi = MedecinApi()


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_contact_medecin, container, false)
        // Récupération des éléments de la vue et mise en place du recyclerView
        retour = view.findViewById(R.id.retour_to_messages)
        supprimerSearch = view.findViewById(R.id.supprimerSearch)
        searchByRpps = view.findViewById(R.id.search_by_rpps)
        searchByName = view.findViewById(R.id.search_by_prenom_nom)
        search = view.findViewById(R.id.rechercherMedecin)
        progressBarMedecin = view.findViewById(R.id.progressBarMedecin)
        recyclerView = view.findViewById(R.id.recyclerViewSearch)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        itemAdapter = AjoutContactMedecinFragmentAdapterR(listOf())
        recyclerView.adapter = itemAdapter
        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        // Recherche des médecins
        // Appel l'api lors du clic sur le bouton de recherche et ferme le clavier
        search.setOnClickListener {
            // On ferme le clavier
            val imm =
                requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            val rpps = searchByRpps.text.toString()
            val name = searchByName.text.toString()
            progressBarMedecin.visibility = View.VISIBLE
            recyclerView.alpha = 0.3F
            Thread {
                val medecins = rechercheMedecin(rpps, name)
                requireActivity().runOnUiThread {
                    progressBarMedecin.visibility = View.GONE
                    recyclerView.alpha = 1F
                    itemAdapter.setList(medecins)
                    itemAdapter.notifyDataSetChanged()
                }
            }.start()
        }


        /* Suppression des champs de recherche */
        supprimerSearch.setOnClickListener {
            searchByRpps.text?.clear()
            searchByName.text?.clear()
        }

        // On dépile le fragment lors du clic sur le bouton de retour pour revenir au précédent
        retour.setOnClickListener {
            val fragmentManager = this.parentFragmentManager
            fragmentManager.popBackStack()
        }

        return view
    }

    /**
     * Recherche des médecins (le rpps est prioritaire sur le nom/prenom)
     * @param rpps : rpps du médecin
     * @param name : nom du médecin
     * @return la liste des médecins trouvés
     */
    private fun rechercheMedecin(rpps: String, name: String): List<Medecin> {
        var lstMed = mutableListOf<Medecin>()
        try {
            if (rpps.isNotEmpty()) {
                val medecin = medecinApi.getMedecin(rpps)
                if (medecin != null) {
                    lstMed = mutableListOf(medecin)
                }
            } else if (name.isNotEmpty()) {
                val sep = name.split(" ")
                val prenom = sep[0]
                val nom = if (sep.size > 1) sep[1] else null
                val res1 = medecinApi.getMedecins(prenom, nom)
                val res2 = medecinApi.getMedecins(nom, prenom)
                if (res1 != null) {
                    lstMed.addAll(res1)
                }
                if (res2 != null) {
                    lstMed.addAll(res2)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                this.context,
                "Erreur lors de la recherche, veuillez être plus précis",
                Toast.LENGTH_SHORT
            ).show()
        }
        return lstMed
    }

}