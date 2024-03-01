package dev.mobile.medicalink.fragments.traitements.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Prise


class RecapAdapterR(private val list: MutableList<Prise>) :
    RecyclerView.Adapter<RecapAdapterR.AjoutManuelViewHolder>() {

    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val heurePrise = view.findViewById<TextView>(R.id.heurePrise)
        val dosageRecap = view.findViewById<TextView>(R.id.dosageRecap)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_recap_ajout_manuel, parent, false)
        return AjoutManuelViewHolder(layout)
    }

    @SuppressLint("SetTextI18n")

    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        val item = list[position]

        Log.d("test", item.heurePrise)
        holder.heurePrise.text = item.heurePrise
        holder.dosageRecap.text = "${item.quantite} ${item.typeComprime}"

        holder.view.setOnLongClickListener {
            true
        }
    }

}
