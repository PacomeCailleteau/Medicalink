package dev.mobile.medicalink.fragments.traitements

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.entity.CisBdpm
import dev.mobile.medicalink.db.local.entity.User
import java.time.LocalDate


class AjoutManuelSearchAdapterR(
    private val list: List<CisBdpm>,
    private val onItemClick: (CisBdpm) -> Unit
) :
    RecyclerView.Adapter<AjoutManuelSearchAdapterR.TraitementViewHolder>() {

    class TraitementViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomMedic: TextView = view.findViewById(R.id.nomSearch)


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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TraitementViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)

        holder.view.setOnClickListener {
            onItemClick.invoke(item)
        }
    }

}
