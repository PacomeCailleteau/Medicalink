package dev.mobile.medicalink

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.db.local.entity.User


class ChangerUtilisateurAdapterR(private val list: List<User>) :
    RecyclerView.Adapter<ChangerUtilisateurAdapterR.AjoutManuelViewHolder>() {

    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomUtilisateur = view.findViewById<TextView>(R.id.nomUtilisateur)
        val statutUtilisateur = view.findViewById<TextView>(R.id.statutUtilisateur)
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
        holder.nomUtilisateur.text="${item.prenom} ${item.nom}"
        holder.statutUtilisateur.text="${item.statut}"

        holder.view.setOnClickListener {
            Log.d("test","cc")
            val context = holder.itemView.context
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("userId", item.uuid)
            context.startActivity(intent)
        }
    }

}
