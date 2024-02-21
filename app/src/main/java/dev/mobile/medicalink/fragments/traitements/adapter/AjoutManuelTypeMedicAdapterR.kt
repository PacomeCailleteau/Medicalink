package dev.mobile.medicalink.fragments.traitements.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.ajouts.AjoutSharedViewModel


class AjoutManuelTypeMedicAdapterR(
    private val list: MutableList<String>,
    private var selected: String,
    private val viewModel: AjoutSharedViewModel
) :
    RecyclerView.Adapter<AjoutManuelTypeMedicAdapterR.AjoutManuelTypeMedicViewHolder>() {

    class AjoutManuelTypeMedicViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val textTypeMedic = view.findViewById<TextView>(R.id.nomTypeMedic)
        val layoutTypeMedic = view.findViewById<LinearLayout>(R.id.layoutTypeMedic)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AjoutManuelTypeMedicViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_type_medic, parent, false)
        return AjoutManuelTypeMedicViewHolder(layout)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: AjoutManuelTypeMedicViewHolder, position: Int) {
        val item = list[position]

        holder.textTypeMedic.text = item

        if (selected == item) {
            holder.layoutTypeMedic.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
        } else {
            holder.layoutTypeMedic.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
        }

        holder.layoutTypeMedic.setOnClickListener {
            selected = item
            viewModel.setTypeComprime(selected)
            notifyDataSetChanged()
        }
    }

}
