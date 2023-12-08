package dev.mobile.medicalink.fragments.home

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.Traitement


class HomeAdapterR(private var list: MutableList<Pair<Prise, Traitement>>) :
    RecyclerView.Adapter<HomeAdapterR.AjoutManuelViewHolder>() {

    var heureCourante = list.first().first.heurePrise.split(":").first()
    fun updateData(listeTraitementUpdated : MutableList<Pair<Prise, Traitement>>) {
        list = listeTraitementUpdated
    }
    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val nomMedic = view.findViewById<TextView>(R.id.nomMedic)
        val nbComprime = view.findViewById<TextView>(R.id.nbComprime)
        val heurePrise = view.findViewById<TextView>(R.id.heurePriseAccueil)
        val circleTick = view.findViewById<ImageView>(R.id.circleTick)
        val imageMedoc = view.findViewById<ImageView>(R.id.itemListeTraitementsImage)
        val mainHeure = view.findViewById<TextView>(R.id.mainHeureMedic)
        val mainHeureLayout = view.findViewById<ConstraintLayout>(R.id.layoutMainHeure)



    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_accueil, parent, false)
        return AjoutManuelViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        val item = list[position]
        holder.nomMedic.text = item.second.nomTraitement
        holder.nbComprime.text = "${item.first.quantite} ${item.first.dosageUnite}"
        holder.heurePrise.text = item.first.heurePrise
        holder.mainHeure.text="${item.first.heurePrise.split(":").first()}h"

        if (item==list.first() || item.first.heurePrise.split(":").first()!=heureCourante){
            holder.mainHeureLayout.visibility=View.VISIBLE
            heureCourante=item.first.heurePrise.split(":").first()
        }else{
            holder.mainHeureLayout.visibility=View.GONE
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

        holder.view.setOnClickListener {
            if (holder.circleTick.drawable.constantState?.equals(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.circle
                    )?.constantState
                ) == true
            ) {
                holder.circleTick.setImageResource(R.drawable.correct)
            } else {
                holder.circleTick.setImageResource(R.drawable.circle)
            }

            true
        }
    }

}
