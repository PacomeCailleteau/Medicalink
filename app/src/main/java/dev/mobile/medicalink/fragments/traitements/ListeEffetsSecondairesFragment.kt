package dev.mobile.medicalink.fragments.traitements

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import java.time.LocalDate

class ListeEffetsSecondairesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var annuler: ImageView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_liste_effets_secondaires, container, false)

        annuler = view.findViewById(R.id.annulerListeEffetsSecondaires)

        val effetsSecondairesDoliprane = mutableListOf("Nausées", "Vomissements", "Maux de tête")
        val effetsSecondairesAspirine = mutableListOf("Irritation de l'estomac", "Hémorragie")
        val effetsSecondairesVitamineC = null
        val effetsSecondairesIbuprofene = mutableListOf("Maux d'estomac", "Saignement d'estomac")
        val effetsSecondairesAntibiotique = mutableListOf("Réaction allergique", "Diarrhée")
        val effetsSecondairesMedicamentX = mutableListOf("Somnolence", "Vertiges")
        val effetsSecondairesVitamineD = null
        val effetsSecondairesParacetamol = null
        val effetsSecondairesAntiInflammatoire = mutableListOf("Irritation de l'estomac", "Saignement d'estomac")
        val effetsSecondairesMedicamentY = mutableListOf("Somnolence", "Confusion")

        val lp = mutableListOf(
            Traitement("Doliprane", 4, "Jours", LocalDate.of(2023, 5, 25), "Comprimé",15, false, effetsSecondairesDoliprane),
            Traitement("Aspirine", 7, "Mois", LocalDate.of(2023, 6, 10), "Comprimé",10, false, effetsSecondairesAspirine),
            Traitement("Vitamine C", 30, "An", null, "Comprimé",20, false, effetsSecondairesVitamineC),
            Traitement("Ibuprofène", 5, "Jours", LocalDate.of(2023, 8, 15), "Comprimé",12, false, effetsSecondairesIbuprofene),
            Traitement("Antibiotique", 10, "Mois", null, "Comprimé",1, false, effetsSecondairesAntibiotique),
            Traitement("Médicament X", 14, "Jours", LocalDate.of(2023, 10, 5), "Comprimé",8, false, effetsSecondairesMedicamentX),
            Traitement("Vitamine D", 60, "An", LocalDate.of(2023, 9, 1), "Comprimé",25, false, effetsSecondairesVitamineD),
            Traitement("Paracétamol", 3, "Mois", null, "Comprimé",18, false, effetsSecondairesParacetamol),
            Traitement("Anti-inflammatoire", 7, "Jours", LocalDate.of(2023, 12, 1), "Comprimé",15, false, effetsSecondairesAntiInflammatoire),
            Traitement("Médicament Y", 21, "An", LocalDate.of(2024, 1, 20), "Comprimé",10, false, effetsSecondairesMedicamentY)
        )

        val traitementsTries = lp.sortedBy { it.expire }.toMutableList()
        recyclerView = view.findViewById(R.id.recyclerViewTypeMedic)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = ListeEffetsSecondairesAdapterR(traitementsTries)

        //Gestion espacement entre items RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))


        annuler.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, MainTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

}