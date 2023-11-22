package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R


class RecapAdapterR(private val list: MutableList<MutableList<String?>>) :
    RecyclerView.Adapter<RecapAdapterR.AjoutManuelViewHolder>() {

    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val titreCategorie = view.findViewById<TextView>(R.id.nom_categorie)
        val sousNomCategorie = view.findViewById<TextView>(R.id.sous_nom_categorie)
        val titreParaUn = view.findViewById<TextView>(R.id.titre_para_un)
        val sousParaUn = view.findViewById<TextView>(R.id.sous_para_un)
        val titreParaDeux = view.findViewById<TextView>(R.id.titre_para_deux)
        val sousParaDeux = view.findViewById<TextView>(R.id.sous_para_deux)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_add_prise, parent, false)
        return AjoutManuelViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        val item = list[position]
        if (item.isEmpty()){
            holder.titreCategorie.text=item[0]
        }else{
            holder.titreCategorie.visibility=View.GONE
        }

        if (item[1]!=null){
            holder.sousNomCategorie.text=item[1]
        }else{
            holder.sousNomCategorie.visibility=View.GONE
        }

        if (item[2]!=null){
            holder.titreParaUn.text=item[2]
        }else{
            holder.titreParaUn.visibility=View.GONE
        }

        if (item[3]!=null){
            holder.sousParaUn.text=item[3]
        }else{
            holder.sousParaUn.visibility=View.GONE
        }

        if (item[4]!=null){
            holder.titreParaDeux.text=item[4]
        }else{
            holder.titreParaDeux.visibility=View.GONE
        }

        if (item[5]!=null){
            holder.sousParaDeux.text=item[5]
        }else{
            holder.sousParaDeux.visibility=View.GONE
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

            val context = holder.itemView.context
            true
        }
    }

}
