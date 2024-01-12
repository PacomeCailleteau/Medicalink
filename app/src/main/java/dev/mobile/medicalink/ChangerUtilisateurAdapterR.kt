package dev.mobile.medicalink

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.db.local.entity.User

/**
 * Adapter pour la liste des utilisateurs
 *
 * @property list
 * @property onItemClick
 * @constructor Crée un adapter pour la liste des utilisateurs
 */
class ChangerUtilisateurAdapterR(
    private val list: List<User>,
    private val onItemClick: (User) -> Unit
) :
    RecyclerView.Adapter<ChangerUtilisateurAdapterR.AjoutManuelViewHolder>() {

    /**
     * ViewHolder pour la liste des utilisateurs
     *
     * @property view
     * @constructor Crée un ViewHolder pour la liste des utilisateurs
     */
    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomUtilisateur = view.findViewById<TextView>(R.id.nomUtilisateur)
        val statutUtilisateur = view.findViewById<TextView>(R.id.statutUtilisateur)
    }

    /**
     * Retourne le nombre d'éléments dans la liste (nombre d'utilisateurs)
     */
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_changer_utilisateur, parent, false)
        return AjoutManuelViewHolder(layout)
    }

    @SuppressLint("SetTextI18n")
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
