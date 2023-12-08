package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R


class ListeEffetsSecondairesAdapterR(private val list: MutableList<Traitement>) :
    RecyclerView.Adapter<ListeEffetsSecondairesAdapterR.TraitementViewHolder>() {

    class TraitementViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomEffet: TextView = view.findViewById(R.id.numeroPrise)
        val provoquePar: TextView = view.findViewById(R.id.provoquePar)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getListProvenance(): MutableMap<String, MutableList<Traitement>> {
        // Créez une carte (Map) pour stocker les associations entre les effets secondaires et les médicaments.
        val effetsSecondairesMedicaments = mutableMapOf<String, MutableList<Traitement>>()

        // Parcourez la liste de traitements (lp).
        list.forEach { traitement ->
            traitement.effetsSecondaires.orEmpty().forEach { effetSecondaire ->
                // Vérifiez si l'effet secondaire est déjà dans la carte.
                if (effetSecondaire.lowercase() in effetsSecondairesMedicaments) {
                    // S'il est présent, ajoutez le traitement à la liste existante.
                    effetsSecondairesMedicaments[effetSecondaire.lowercase()]!!.add(traitement)
                } else {
                    // S'il n'est pas présent, créez une nouvelle liste et ajoutez le traitement.
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TraitementViewHolder, position: Int) {
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
        holder.nomEffet.text = item.substring(0, 1).uppercase() + item.substring(1)

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


        holder.provoquePar.text = "$monAffichage"


        /*
        A check pour afficher les détails d'un traitement quand cliqué

        holder.naissance.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
            false
        }
         */

        //TODO("faire ça plus proprement")
        holder.view.setOnLongClickListener {
            notifyDataSetChanged()

            val context = holder.itemView.context
            true
        }
    }

}
