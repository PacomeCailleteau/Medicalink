package dev.mobile.medicalink.fragments.contacts.adapter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.entity.ContactMedecin
import dev.mobile.medicalink.fragments.contacts.AfficheDetailsMedecinFragment

class ContactsFragmentAdapterR(private val list: List<ContactMedecin>) :
    RecyclerView.Adapter<ContactsFragmentAdapterR.MessagesFragmentViewHolder>() {

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
            val bundle = Bundle()
            bundle.putString("rpps", item.rpps)
            val destinationFragment = AfficheDetailsMedecinFragment()
            destinationFragment.arguments = bundle
            val fragmentManager = holder.view.context as FragmentActivity
            val fragmentTransaction = fragmentManager.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.FL, destinationFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        holder.phone.setOnClickListener {
            val phone = item.phoneNumber
            if (phone == "unknown") {
                Toast.makeText(holder.view.context, "Numéro de téléphone inconnu", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // On ouvre l'application téléphone
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = android.net.Uri.parse("tel:$phone")
                holder.view.context.startActivity(intent)
            }
        }

        holder.email.setOnClickListener {
            val email = item.email
            if (email == "unknown") {
                Toast.makeText(holder.view.context, "Email inconnu", Toast.LENGTH_SHORT).show()
            } else {
                // On ouvre l'application mail
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                try {
                    holder.view.context.startActivity(
                        Intent.createChooser(
                            intent,
                            "Envoyer un mail"
                        )
                    )
                } catch (e: android.content.ActivityNotFoundException) {
                    Log.e("Mail", "Aucune application pour envoyer un mail")
                }
            }
        }
    }

}