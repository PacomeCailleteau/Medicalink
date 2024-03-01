package dev.mobile.medicalink.fragments.traitements.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.entity.CisBdpm


class AjoutManuelSearchAdapterR(
    private val list: List<CisBdpm>,
    private val onItemClick: (CisBdpm) -> Unit
) :
    RecyclerView.Adapter<AjoutManuelSearchAdapterR.TraitementViewHolder>() {

    class TraitementViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val nomMedic: TextView = view.findViewById(R.id.nomSearch)


        fun bind(item: CisBdpm) {
            nomMedic.text = item.denomination

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TraitementViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_search, parent, false)
        return TraitementViewHolder(layout)
    }

    @SuppressLint("SetTextI18n")

    override fun onBindViewHolder(holder: TraitementViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)

        //On renvoie l'item au fragment pour qu'il récupère l'item cliqué
        holder.view.setOnClickListener {
            onItemClick.invoke(item)
        }
    }

}
