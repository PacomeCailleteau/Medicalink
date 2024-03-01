package dev.mobile.medicalink.fragments.contacts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.ContactMedecinRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.contacts.ContactsFragment
import dev.mobile.medicalink.utils.GoTo
import dev.mobile.medicalink.utils.medecin.Medecin
import dev.mobile.medicalink.utils.medecin.MedecinApi
import java.util.concurrent.LinkedBlockingQueue

class AjoutContactMedecinFragmentAdapterR(private var list: List<Medecin>) :
    RecyclerView.Adapter<AjoutContactMedecinFragmentAdapterR.AjoutContactMedecinViewHolder>() {

    class AjoutContactMedecinViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val rpps: TextView = view.findViewById(R.id.RppsMed)
        val prenom: TextView = view.findViewById(R.id.PrenomMed)
        val nom: TextView = view.findViewById(R.id.NomMed)
        val specialty: TextView = view.findViewById(R.id.SpeMed)
        val address: TextView = view.findViewById(R.id.AddressMed)
        val valider: ImageView = view.findViewById(R.id.ValiderMed)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list: List<Medecin>) {
        this.list = list
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AjoutContactMedecinViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_contact_message, parent, false)
        return AjoutContactMedecinViewHolder(layout)
    }


    override fun onBindViewHolder(holder: AjoutContactMedecinViewHolder, position: Int) {
        val item = list[position]
        val contextGetString = holder.view.context
        holder.rpps.text = contextGetString.getString(R.string.rpps_s, item.rpps)
        holder.prenom.text = contextGetString.getString(R.string.prenom_s, item.firstname)
        holder.nom.text = contextGetString.getString(R.string.nom_s, item.lastname)
        holder.specialty.text = contextGetString.getString(R.string.specialite_s, item.specialty)
        holder.address.text = contextGetString.getString(R.string.adresse_s, item.address)

        // Ajout du contact lors du clic sur le bouton valider
        holder.valider.setOnClickListener {
            val queue = LinkedBlockingQueue<Boolean>()
            Thread {
                try {
                    val medecinApi = MedecinApi()
                    val medecin = medecinApi.getMedecin(item.rpps)
                    val db = AppDatabase.getInstance(holder.view.context.applicationContext)
                    val contactMedecinInterface = ContactMedecinRepository(db.contactMedecinDao())
                    val userInterface = UserRepository(db.userDao())
                    val userUuid = userInterface.getUsersConnected()[0].uuid
                    contactMedecinInterface.insertContactMedecin(medecin!!.asContactMedecin(userUuid))
                    queue.put(true)
                } catch (e: Exception) {
                    queue.put(false)
                }
            }.start()
            val res = queue.take()
            // Si c'est faux on fait un Toast, sinon on renvoie sur la pages de messages
            if (!res) {
                // Faire un toast
                Toast.makeText(
                    holder.view.context,
                    "Erreur lors de l'ajout du contact",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                GoTo.fragment(
                    ContactsFragment(),
                    (holder.view.context as FragmentActivity).supportFragmentManager
                )
            }

        }

    }

}