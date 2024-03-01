package dev.mobile.medicalink.fragments.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.ContactMedecinRepository


class AfficheDetailsMedecinFragment : Fragment() {
    private lateinit var rpps: TextView
    private lateinit var prenom: TextView
    private lateinit var nom: TextView
    private lateinit var specialty: TextView
    private lateinit var email: TextView
    private lateinit var phone: TextView
    private lateinit var address: TextView
    private lateinit var zipCode: TextView
    private lateinit var city: TextView
    private lateinit var gender: TextView
    private lateinit var retour: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_affiche_details_medecin, container, false)
        //Récupération des éléments de la vue
        rpps = view.findViewById(R.id.RppsDetailMed)
        prenom = view.findViewById(R.id.PrenomDetailMed)
        nom = view.findViewById(R.id.NomDetailMed)
        specialty = view.findViewById(R.id.SpecialiteDetailMed)
        email = view.findViewById(R.id.EmailDetailMed)
        phone = view.findViewById(R.id.PhoneDetailMed)
        address = view.findViewById(R.id.AddressDetailMed)
        zipCode = view.findViewById(R.id.ZipCodeDetailMed)
        city = view.findViewById(R.id.CityDetailMed)
        gender = view.findViewById(R.id.GenderDetailMed)
        retour = view.findViewById(R.id.RetourDetailMed)

        val rppsMedecin = arguments?.getString("rpps")
        val db = AppDatabase.getInstance(view.context.applicationContext)
        val contactMedecinInterface = ContactMedecinRepository(db.contactMedecinDao())

        // Récupération des informations du médecin de la base de données pour les afficher
        Thread {
            val medecin = contactMedecinInterface.getOneContactMedecinById(rppsMedecin!!)
            if (medecin != null) {
                rpps.text = getString(R.string.rpps_s, medecin.rpps)
                prenom.text = getString(R.string.prenom_s, medecin.firstname)
                nom.text = getString(R.string.nom_s, medecin.lastname)
                specialty.text = getString(R.string.specialite_s, medecin.specialty)
                email.text = getString(R.string.email_s, medecin.email)
                phone.text = getString(R.string.phone_s, medecin.phoneNumber)
                address.text = getString(R.string.adresse_s, medecin.address)
                zipCode.text = getString(R.string.code_postal_s, medecin.zipCode)
                city.text = getString(R.string.ville_s, medecin.city)
                gender.text = getString(R.string.sexe_s, medecin.gender)
            }
        }.start()

        retour.setOnClickListener {
            val fragmentManager = this.parentFragmentManager
            fragmentManager.popBackStack()
        }

        return view
    }

}