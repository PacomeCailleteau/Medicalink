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
import dev.mobile.medicalink.utils.MapIconeMedecin

class ContactsAdapterR(
    private val list: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) :
    RecyclerView.Adapter<ContactsAdapterR.MessagesFragmentViewHolder>() {

    class MessagesFragmentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titreMessage: TextView = view.findViewById(R.id.nomMedecin)
        val ville = view.findViewById<TextView>(R.id.villeSearch)
        val textMessage: TextView = view.findViewById(R.id.professionMedecin)
        val image: View = view.findViewById(R.id.imageMedecin)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesFragmentViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_search_contact, parent, false)
        return MessagesFragmentViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MessagesFragmentViewHolder, position: Int) {
        val item = list.get(position)

        holder.titreMessage.text = item.fullname
        if (item.city != null) {
            holder.ville.text = item.city
        } else {
            holder.ville.visibility = View.GONE
        }
        holder.textMessage.text = item.specialty

        val mapIconeMedecin = MapIconeMedecin()

        val matchingSpecialty =
            mapIconeMedecin.keys.find { item.specialty?.contains(it, ignoreCase = true) == true }

        val imageResource = mapIconeMedecin[matchingSpecialty] ?: R.drawable.docteur
        holder.image.setBackgroundResource(imageResource)


        //On renvoie l'item au fragment pour qu'il récupère l'item cliqué
        holder.view.setOnClickListener {
            onItemClick.invoke(item)
        }
    }

}