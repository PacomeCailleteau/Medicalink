package dev.mobile.td3notes

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView


class ListeEffetsSecondairesAdapterR(private val list: MutableList<Traitement>) :
    RecyclerView.Adapter<ListeEffetsSecondairesAdapterR.TraitementViewHolder>() {

    class TraitementViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomEffet = view.findViewById<TextView>(R.id.nomEffet)
        val provoquePar = view.findViewById<TextView>(R.id.provoquePar)
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayout)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TraitementViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_liste_effets_secondaires, parent, false)
        return TraitementViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TraitementViewHolder, position: Int) {
        val tousLesEffetsSecondaires = list.flatMap { it.effetsSecondaires.orEmpty() }
        val item = tousLesEffetsSecondaires.get(position)
        holder.nomEffet.text = item
        holder.provoquePar.text="To be defined"

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
