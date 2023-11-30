package dev.mobile.medicalink.fragments.home

import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Traitement


class HomeAdapterR(private val list: MutableList<Traitement>) :
    RecyclerView.Adapter<HomeAdapterR.AjoutManuelViewHolder>() {

    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val nomMedic = view.findViewById<TextView>(R.id.nomMedic)
        val nbComprime = view.findViewById<TextView>(R.id.nbComprime)
        val circleTick = view.findViewById<ImageView>(R.id.circleTick)

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        val item = list[position]


        holder.nomMedic.text=item.nomTraitement
        holder.nbComprime.text="${item.prises?.first()} ${item.typeComprime}"


        /*
        A check pour afficher les détails d'un traitement quand cliqué

        holder.naissance.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
            false
        }
         */

        holder.view.setOnClickListener {
            if (holder.circleTick.drawable.constantState?.equals(ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle)?.constantState) == true){
                holder.circleTick.setImageResource(R.drawable.correct)
            }else{
                holder.circleTick.setImageResource(R.drawable.circle)
            }

            true
        }
    }

}
