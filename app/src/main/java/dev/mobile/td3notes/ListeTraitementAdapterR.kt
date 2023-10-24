package dev.mobile.td3notes

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView


class ListeTraitementAdapterR(private val list: MutableList<Traitement>) :
    RecyclerView.Adapter<ListeTraitementAdapterR.TraitementViewHolder>() {

    class TraitementViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomTraitement = view.findViewById<TextView>(R.id.nomEffet)
        val dosage = view.findViewById<TextView>(R.id.dosage)
        val dateExpirationTraitement = view.findViewById<TextView>(R.id.provoquePar)
        val nbComprimesRestants = view.findViewById<TextView>(R.id.nbComprimesRestants)
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayout)
        val imageView = view.findViewById<ImageView>(R.id.itemListeTraitementsImage)


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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TraitementViewHolder, position: Int) {
        val item = list.get(position)
        holder.nomTraitement.text = item.nomTraitement
        holder.dosage.text = "${item.dosageNb} par ${item.dosageUnite}"
        if (item.expire){
            holder.constraintLayout.setBackgroundResource(R.drawable.squared_gray_button_background)
            holder.imageView.setImageResource(R.drawable.medicexpire)
            holder.nbComprimesRestants.text = "Traitement expiré"
            if (item.dateFinTraitement == null){
                holder.dateExpirationTraitement.text = "Terminé"
            }else{
                holder.dateExpirationTraitement.text = "Terminé le ${item.dateFinTraitement!!.dayOfMonth}/${item.dateFinTraitement!!.monthValue}/${item.dateFinTraitement!!.year}"
            }
        }else{
            holder.constraintLayout.setBackgroundResource(R.drawable.squared_blue_button_background)
            holder.imageView.setImageResource(R.drawable.medicencours)
            holder.nbComprimesRestants.text = "${item.comprimesRestants} comprimés restants"
            if (item.dateFinTraitement == null){
                holder.dateExpirationTraitement.text = "Indéterminé"
            }else{
                holder.dateExpirationTraitement.text = "Jusqu'au ${item.dateFinTraitement!!.dayOfMonth}/${item.dateFinTraitement!!.monthValue}/${item.dateFinTraitement!!.year}"
            }

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

        holder.view.setOnLongClickListener {
            item.enMajuscule()
            notifyDataSetChanged()

            val context = holder.itemView.context
            true
        }
    }

    }
