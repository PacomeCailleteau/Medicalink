package dev.mobile.medicalink.fragments.traitements.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Traitement


class ListeEffetsSecondairesAdapterR(private val list: MutableList<Traitement>) :
    RecyclerView.Adapter<ListeEffetsSecondairesAdapterR.TraitementViewHolder>() {

    class TraitementViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomEffet: TextView = view.findViewById(R.id.numeroPrise)
        val provoquePar: TextView = view.findViewById(R.id.provoquePar)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * Fonction pour obtenir la liste de tous les effets secondaires possible sachant les
     * traitements actif, à partir de tous les traitements
     * @return La liste des effets secondaires
     */
    private fun getListProvenance(): MutableMap<String, MutableList<Traitement>> {
        val effetsSecondairesMedicaments = mutableMapOf<String, MutableList<Traitement>>()

        list.forEach { traitement ->
            traitement.effetsSecondaires.orEmpty().forEach { effetSecondaire ->
                if (effetSecondaire.lowercase() in effetsSecondairesMedicaments) {
                    effetsSecondairesMedicaments[effetSecondaire.lowercase()].let {
                        it?.add(traitement)
                    }
                } else {
                    effetsSecondairesMedicaments[effetSecondaire.lowercase()] =
                        mutableListOf(traitement)
                }
            }
        }

        return effetsSecondairesMedicaments
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TraitementViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_liste_effets_secondaires, parent, false)
        return TraitementViewHolder(layout)
    }


    override fun onBindViewHolder(holder: TraitementViewHolder, position: Int) {
        val textAucunEffetSecondaire = holder.view.findViewById<TextView>(R.id.textAucunEffetsSec)
        if (list.isEmpty()) {
            if (textAucunEffetSecondaire != null) {
                textAucunEffetSecondaire.visibility = View.VISIBLE
            }
            return
        }

        if (textAucunEffetSecondaire != null) {
            textAucunEffetSecondaire.visibility = View.GONE
        }

        val tousLesEffetsSecondaires = list
            .flatMap { it.effetsSecondaires.orEmpty() }
            .map { it.lowercase().trim() }
            .distinct()

        if (tousLesEffetsSecondaires.isEmpty() || position >= tousLesEffetsSecondaires.size) {
            // Rendre la vue invisible si la liste est vide
            holder.view.visibility = View.GONE
            return
        }
        val item = tousLesEffetsSecondaires[position]
        holder.nomEffet.text = item.substring(0, 1).uppercase() + item.substring(1)

        val traitements = getListProvenance()[item]

        holder.provoquePar.text = getAffichage(holder, traitements)


        /*
        A check pour afficher les détails d'un traitement quand cliqué

        holder.naissance.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
            false
        }
         */

        holder.view.setOnLongClickListener {
            true
        }
    }

    private fun getAffichage(holder: TraitementViewHolder, maList: List<Traitement>?): String {
        var monAffichage = holder.view.resources.getString(R.string.provoque_par)
        if (maList != null) {
            for (medicament in maList) {
                monAffichage += if (medicament == maList.last()) {
                    medicament.nomTraitement
                } else {
                    "${medicament.nomTraitement}, "
                }
            }
        }
        return monAffichage
    }

}
