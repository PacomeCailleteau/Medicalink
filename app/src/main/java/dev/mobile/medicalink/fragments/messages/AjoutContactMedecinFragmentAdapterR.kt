package dev.mobile.medicalink.fragments.messages

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.utils.medecin.Medecin

class AjoutContactMedecinFragmentAdapterR(private var list: List<Medecin>) :
    RecyclerView.Adapter<AjoutContactMedecinFragmentAdapterR.AjoutContactMedecinViewHolder>() {

    class AjoutContactMedecinViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val rpps = view.findViewById<TextView>(R.id.RppsMed)
        val prenom = view.findViewById<TextView>(R.id.PrenomMed)
        val nom = view.findViewById<TextView>(R.id.NomMed)
        val email = view.findViewById<TextView>(R.id.EmailMed)
        val phoneNumber = view.findViewById<TextView>(R.id.PhoneMed)
        val address = view.findViewById<TextView>(R.id.AddressMed)
        val zipCode = view.findViewById<TextView>(R.id.ZipCodeMed)
        val city = view.findViewById<TextView>(R.id.CityMed)
        val gender = view.findViewById<TextView>(R.id.GenderMed)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list: List<Medecin>) {
        this.list = list
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutContactMedecinViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_contact_message, parent, false)
        return AjoutContactMedecinViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutContactMedecinViewHolder, position: Int) {
        val item = list[position]
        val contextGetString = holder.view.context
        holder.rpps.text = contextGetString.getString(R.string.rpps_s, item.rpps)
        holder.prenom.text = contextGetString.getString(R.string.prenom_s, item.firstname)
        holder.nom.text = contextGetString.getString(R.string.nom_s, item.lastname)
        holder.email.text = contextGetString.getString(R.string.email_s, item.email)
        holder.phoneNumber.text = contextGetString.getString(R.string.phone_s, item.phoneNumber)
        holder.address.text = contextGetString.getString(R.string.adresse_s, item.address)
        holder.zipCode.text = contextGetString.getString(R.string.code_postal_s, item.zipCode)
        holder.city.text = contextGetString.getString(R.string.ville_s, item.city)
        holder.gender.text = contextGetString.getString(R.string.sexe_s, item.gender)

    }

}