package dev.mobile.medicalink.fragments.contacts.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.entity.ContactMedecin

class MessagesFragmentAdapterR(private val list: List<ContactMedecin>) :
    RecyclerView.Adapter<MessagesFragmentAdapterR.MessagesFragmentViewHolder>() {

    class MessagesFragmentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val prenom = view.findViewById<TextView>(R.id.PrenomMedecinMessage)
        val nom = view.findViewById<TextView>(R.id.NomMedecinMessage)
        val rpps = view.findViewById<TextView>(R.id.RppsMedecinMessage)
        val phone = view.findViewById<ImageView>(R.id.PhoneMedecinMessage)
        val email = view.findViewById<ImageView>(R.id.EmailMedecinMessage)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesFragmentViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessagesFragmentViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MessagesFragmentViewHolder, position: Int) {
        val item = list[position]
        holder.prenom.text = item.firstname
        holder.nom.text = item.lastname
        holder.rpps.text = item.rpps

        holder.view.setOnClickListener {
            // On ouvre une dialog pour afficher toutes les informations du médecin

        }

        holder.phone.setOnClickListener {
            //TODO(Appeler le médecin)
        }

        holder.email.setOnClickListener {
            //TODO(Envoyer un mail au médecin)
        }
    }

}