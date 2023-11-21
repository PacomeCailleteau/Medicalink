package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R


class AjoutManuelTypeMedicAdapterR(private val list: MutableList<String>,var selected : String) :
    RecyclerView.Adapter<AjoutManuelTypeMedicAdapterR.AjoutManuelTypeMedicViewHolder>() {

    class AjoutManuelTypeMedicViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val textTypeMedic = view.findViewById<TextView>(R.id.nomTypeMedic)
        val layoutTypeMedic = view.findViewById<LinearLayout>(R.id.layoutTypeMedic)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelTypeMedicViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_type_medic, parent, false)
        return AjoutManuelTypeMedicViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutManuelTypeMedicViewHolder, position: Int) {
        val item = list.get(position)

        holder.textTypeMedic.text=item

        if (selected==item){
            holder.layoutTypeMedic.setBackgroundResource(R.drawable.rounded_blue_button_blue_stroke_background)
        }else{
            holder.layoutTypeMedic.setBackgroundResource(R.drawable.rounded_white_button_blue_stroke_background)
        }

        holder.layoutTypeMedic.setOnClickListener {
            selected=item
            Log.d("LLLL",selected)
            notifyDataSetChanged()
        }


        /*
        A check pour afficher les détails d'un traitement quand cliqué

        holder.naissance.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
            false
        }
         */

    }

    }
