package dev.mobile.medicalink

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.db.local.entity.User


class ChangerUtilisateurAdapterR(
    private val list: List<User>,
    private val onItemClick: (User) -> Unit
) :
    RecyclerView.Adapter<ChangerUtilisateurAdapterR.AjoutManuelViewHolder>() {

    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomUtilisateur = view.findViewById<TextView>(R.id.nomUtilisateur)
        val statutUtilisateur = view.findViewById<TextView>(R.id.statutUtilisateur)
    }

    interface onItemClickListener {
        fun onItemClick(user: User)
    }

    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener) {
        this.setOnItemClickListener(listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_changer_utilisateur, parent, false)
        return AjoutManuelViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        val item = list[position]
        holder.nomUtilisateur.text = "${item.prenom} ${item.nom}"
        holder.statutUtilisateur.text = "${item.statut}"

        holder.view.setOnClickListener {

            onItemClick.invoke(item)

        }
    }

}
