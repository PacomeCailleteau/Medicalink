package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R


class ListeEffetsSecondairesAdapterR(private val list: MutableList<Traitement>) :
    RecyclerView.Adapter<ListeEffetsSecondairesAdapterR.TraitementViewHolder>() {

    class TraitementViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomEffetSecondaire: TextView = view.findViewById(R.id.nomEffetSecondaire)
        val messageEffetSecondaire: TextView = view.findViewById(R.id.messageEffetSecondaire)
        val imageEffetSecondaire: ImageView = view.findViewById(R.id.imageEffetSecondaire)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * Fonction pour obtenir la liste de tous les effets secondaires possible sachant les
     * traitements actif, à partir de tous les traitements
     * @return La liste des effets secondaires
     */
    fun getListProvenance(): MutableMap<String, MutableList<Traitement>> {
        val effetsSecondairesMedicaments = mutableMapOf<String, MutableList<Traitement>>()

        list.forEach { traitement ->
            traitement.effetsSecondaires.orEmpty().forEach { effetSecondaire ->
                if (effetSecondaire.lowercase() in effetsSecondairesMedicaments) {
                    effetsSecondairesMedicaments[effetSecondaire.lowercase()]!!.add(traitement)
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
            .inflate(R.layout.item_journal_effet_secondaire, parent, false)
        return TraitementViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        Log.d("test", tousLesEffetsSecondaires.toString())
        if (tousLesEffetsSecondaires.isEmpty() || position >= tousLesEffetsSecondaires.size) {
            // Rendre la vue invisible si la liste est vide
            holder.view.visibility = View.GONE
            return
        }
        val item = tousLesEffetsSecondaires.get(position)
        holder.nomEffetSecondaire.text = item.substring(0, 1).uppercase() + item.substring(1)

        val maList = getListProvenance()[item]
        var monAffichage = holder.view.resources.getString(R.string.provoque_par)
        if (maList != null) {
            for (medicament in maList) {
                if (medicament == maList.last()) {
                    monAffichage += "${medicament.nomTraitement}"
                } else {
                    monAffichage += "${medicament.nomTraitement}, "
                }

            }
        }


        holder.messageEffetSecondaire.text = "$monAffichage"


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
            notifyDataSetChanged()

            val context = holder.itemView.context
            true
        }
    }

}
