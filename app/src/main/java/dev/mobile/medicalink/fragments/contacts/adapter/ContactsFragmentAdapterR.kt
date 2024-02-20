package dev.mobile.medicalink.fragments.contacts.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.ContactMedecin
import dev.mobile.medicalink.db.local.repository.ContactMedecinRepository
import dev.mobile.medicalink.fragments.contacts.AfficheDetailsMedecinFragment
import java.util.concurrent.LinkedBlockingQueue

class ContactsFragmentAdapterR(private var list: List<ContactMedecin>) :
    RecyclerView.Adapter<ContactsFragmentAdapterR.MessagesFragmentViewHolder>() {

    class MessagesFragmentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val prenom: TextView = view.findViewById(R.id.PrenomMedecinMessage)
        val nom: TextView = view.findViewById(R.id.NomMedecinMessage)
        val rpps: TextView = view.findViewById(R.id.RppsMedecinMessage)
        val phone: ImageView = view.findViewById(R.id.PhoneMedecinMessage)
        val email: ImageView = view.findViewById(R.id.EmailMedecinMessage)
        val delete: ImageView = view.findViewById(R.id.DeleteMedecinMessage)
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

    
    override fun onBindViewHolder(holder: MessagesFragmentViewHolder, position: Int) {
        val item = list[position]
        holder.prenom.text = item.firstname
        holder.nom.text = item.lastname
        holder.rpps.text = item.rpps

        // On ouvre le fragment de détails du médecin lors du clic sur le contact
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

        // On demande une confirmation avant de supprimer le contact
        holder.delete.setOnClickListener {
            showConfirmSuppressDialog(holder.view.context, item)
        }

        // On ouvre l'application téléphone lors du clic sur l'icone téléphone
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

        // On ouvre l'application mail lors du clic sur l'icone mail
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


    /**
     * Fonction pour la fenetre de confirmation lors de la suppression d'un traitement
     * @param context : Context
     * @param item : ContactMedecin
     */
    private fun showConfirmSuppressDialog(
        context: Context,
        item: ContactMedecin,
    ) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_confirmation_suppression, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val dosageDialog = builder.create()

        val dialog = dialogView.findViewById<TextView>(R.id.titreHeurePrise)
        // On réutilise la dialog box de suppression d'un medicament alors on doit changer le texte
        dialog.text = context.getString(R.string.voulez_vous_vraiment_retirer_ce_contact)
        val nonButton = dialogView.findViewById<Button>(R.id.sauterButton)
        val ouiButton = dialogView.findViewById<Button>(R.id.prendreButton)

        // On ferme la dialog box lors du clic sur le bouton annuler
        nonButton.setOnClickListener {
            dosageDialog.dismiss()
        }

        // On supprime le contact lors du clic sur le bouton valider
        ouiButton.setOnClickListener {
            val queue = LinkedBlockingQueue<Boolean>()
            Thread {
                val db = AppDatabase.getInstance(context.applicationContext)
                val contactMedecinInterface = ContactMedecinRepository(db.contactMedecinDao())
                contactMedecinInterface.deleteContactMedecin(item)
                queue.put(true)
            }.start()
            queue.take()
            list = list.filter { it.rpps != item.rpps }
            notifyDataSetChanged()
            dosageDialog.dismiss()
        }

        dosageDialog.show()
    }

}