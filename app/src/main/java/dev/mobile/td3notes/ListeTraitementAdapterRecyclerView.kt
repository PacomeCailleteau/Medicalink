package dev.mobile.td3notes

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TraitementAdapterR(private val list: MutableList<Traitement>) :
    RecyclerView.Adapter<TraitementAdapterR.TraitementViewHolder>() {

    class TraitementViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomTraitement = view.findViewById<TextView>(R.id.nomTraitement)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TraitementViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_liste_traitements, parent, false)
        return TraitementViewHolder(layout)
    }

    override fun onBindViewHolder(holder: TraitementViewHolder, position: Int) {
        val item = list.get(position)
        holder.nomTraitement.text = item.nomTraitement

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
            item.enMajuscule()
            notifyDataSetChanged()

            val context = holder.itemView.context
            true
        }
    }

    }
