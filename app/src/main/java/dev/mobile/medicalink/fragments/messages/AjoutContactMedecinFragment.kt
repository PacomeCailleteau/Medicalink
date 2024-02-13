package dev.mobile.medicalink.fragments.messages

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.utils.medecin.Medecin
import dev.mobile.medicalink.utils.medecin.MedecinApi
import java.util.concurrent.LinkedBlockingQueue


class AjoutContactMedecinFragment : Fragment() {

    private lateinit var retour: ImageView
    private lateinit var supprimerSearch: ImageView
    private lateinit var searchByRpps : TextInputEditText
    private lateinit var searchByName : TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var search : Button
    private lateinit var itemAdapter : AjoutContactMedecinFragmentAdapterR
    private val medecinApi = MedecinApi()


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajout_contact_medecin, container, false)

        retour = view.findViewById(R.id.retour_to_messages)
        supprimerSearch = view.findViewById(R.id.supprimerSearch)
        searchByRpps = view.findViewById(R.id.search_by_rpps)
        searchByName = view.findViewById(R.id.search_by_prenom_nom)
        search = view.findViewById(R.id.rechercherMedecin)
        recyclerView = view.findViewById(R.id.recyclerViewSearch)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        itemAdapter = AjoutContactMedecinFragmentAdapterR(listOf())
        recyclerView.adapter = itemAdapter

        search.setOnClickListener {
            // On ferme le clavier
            val imm = requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            val rpps = searchByRpps.text.toString()
            val name = searchByName.text.toString()
            val medecins = rechercheMedecin(rpps, name)
            itemAdapter.setList(medecins)
            itemAdapter.notifyDataSetChanged()
        }


        /* Suppression des champs de recherche */
        supprimerSearch.setOnClickListener {
            searchByRpps.text?.clear()
            searchByName.text?.clear()
        }

        /* Retour vers la page précédente (MessagesFragment) */
        retour.setOnClickListener {
            val fragment = MessagesFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.FL, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        return view
    }

    private fun rechercheMedecin(rpps: String, name: String) : List<Medecin> {
        var lstMed = listOf<Medecin>()
        val queue = LinkedBlockingQueue<Boolean>()
        Thread {
            if (rpps.isNotEmpty()) {
                    val medecin = medecinApi.getMedecin(rpps)
                    if (medecin != null) {
                        lstMed = listOf(medecin)
                    }
            } else if (name.isNotEmpty()) {
                val prenom = name.split(" ")[0]
                val nom = name.split(" ")[1]
                lstMed = medecinApi.getMedecins(prenom, nom)!!
            }
            queue.put(true)
        }.start()
        queue.take()
        return lstMed
    }

}