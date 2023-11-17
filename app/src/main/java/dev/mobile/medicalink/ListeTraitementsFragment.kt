package dev.mobile.medicalink

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate


class ListeTraitementsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_liste_traitements, container, false)

        var isAddingTraitement  = arguments?.getString("isAddingTraitement")




        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.VISIBLE
        }

        val effetsSecondairesDoliprane = mutableListOf("Nausées", "Vomissements", "Maux de tête")
        val effetsSecondairesAspirine = mutableListOf("Irritation de l'estomac", "Hémorragie")
        val effetsSecondairesVitamineC = mutableListOf("Aucun effet secondaire connu")
        val effetsSecondairesIbuprofène = mutableListOf("Maux d'estomac", "Saignement d'estomac")
        val effetsSecondairesAntibiotique = mutableListOf("Réaction allergique", "Diarrhée")
        val effetsSecondairesMédicamentX = mutableListOf("Somnolence", "Vertiges")
        val effetsSecondairesVitamineD = mutableListOf("Aucun effet secondaire connu")
        val effetsSecondairesParacétamol = mutableListOf("Aucun effet secondaire connu")
        val effetsSecondairesAntiInflammatoire = mutableListOf("Irritation de l'estomac", "Saignement d'estomac")
        val effetsSecondairesMédicamentY = mutableListOf("Somnolence", "Confusion")

        val lp = mutableListOf(
            Traitement("Doliprane", 4, "Jours", LocalDate.of(2023, 5, 25), "Comprimé",15, false, effetsSecondairesDoliprane),
            Traitement("Aspirine", 7, "Mois", LocalDate.of(2023, 6, 10), "Comprimé",10, false, effetsSecondairesAspirine),
            Traitement("Vitamine C", 30, "An", null, "Comprimé",20, false, effetsSecondairesVitamineC),
            Traitement("Ibuprofène", 5, "Jours", LocalDate.of(2023, 8, 15), "Comprimé",12, false, effetsSecondairesIbuprofène),
            Traitement("Antibiotique", 10, "Mois", null, "Comprimé",1, false, effetsSecondairesAntibiotique),
            Traitement("Médicament X", 14, "Jours", LocalDate.of(2023, 10, 5), "Comprimé",8, false, effetsSecondairesMédicamentX),
            Traitement("Vitamine D", 60, "An", LocalDate.of(2023, 9, 1), "Comprimé",25, false, effetsSecondairesVitamineD),
            Traitement("Paracétamol", 3, "Mois", null, "Comprimé",18, false, effetsSecondairesParacétamol),
            Traitement("Anti-inflammatoire", 7, "Jours", LocalDate.of(2023, 12, 1), "Comprimé",15, false, effetsSecondairesAntiInflammatoire),
            Traitement("Médicament Y", 21, "An", LocalDate.of(2024, 1, 20), "Comprimé",10, false, effetsSecondairesMédicamentY)
        )

        if (isAddingTraitement=="true"){
            var newTraitement = arguments?.getSerializable("newTraitement") as Traitement
            Log.d("test",newTraitement.nomTraitement)
            lp.add(newTraitement)
        }




        val traitementsTries = lp.sortedBy { it.expire }.toMutableList()
        println(traitementsTries.first().nomTraitement)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewListeEffetsSecondaires)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ListeTraitementAdapterR(traitementsTries)

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

        //Ajout de la fonctionnalité de retour à la page précédente
        val retour = view.findViewById<ImageView>(R.id.annulerListeEffetsSecondaires)
        retour.setOnClickListener {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, MainTraitementsFragment())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

}