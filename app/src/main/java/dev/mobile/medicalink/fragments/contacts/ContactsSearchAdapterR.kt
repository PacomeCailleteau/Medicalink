package dev.mobile.medicalink.fragments.contacts

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.entity.Contact


class ContactsSearchAdapterR(
    private val list: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) :
    RecyclerView.Adapter<ContactsSearchAdapterR.TraitementViewHolder>() {

    class TraitementViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomContact: TextView = view.findViewById(R.id.nomSearch)


        fun bind(item: Contact) {
            nomContact.text = item.fullname
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TraitementViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)

        //On renvoie l'item au fragment pour qu'il récupère l'item cliqué
        holder.view.setOnClickListener {
            onItemClick.invoke(item)
        }
    }

}
