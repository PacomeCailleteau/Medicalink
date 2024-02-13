package dev.mobile.medicalink.fragments.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.entity.Contact

class InfosContactFragment: Fragment() {
    private lateinit var textNomComplet: TextView
    private lateinit var textRpps: TextView
    private lateinit var textSpecialite: TextView
    private lateinit var textTelephone: TextView
    private lateinit var textEmail: TextView
    private lateinit var textAdresse: TextView
    private lateinit var textZipCodeVille: TextView
    private lateinit var retour: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_descriptif_contact, container, false)
        textNomComplet = view.findViewById(R.id.nomCompletMedecin)
        textRpps = view.findViewById(R.id.rpps)
        textSpecialite = view.findViewById(R.id.spécialitéMedecin)
        textTelephone = view.findViewById(R.id.téléphoneMedecin)
        textEmail = view.findViewById(R.id.mailMedecin)
        textAdresse = view.findViewById(R.id.adresseMedecin)
        textZipCodeVille = view.findViewById(R.id.zipCodeVilleMedecin)
        retour = view.findViewById(R.id.retour_schema_contact)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        retour.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val contact = requireArguments().getSerializable("contact") as Contact

        textNomComplet.text = contact.fullname
        textRpps.text = contact.Rpps.toString()
        textSpecialite.text = contact.specialty ?: "Non renseigné"
        textTelephone.text = contact.phoneNumber ?: "Non renseigné"
        textEmail.text = contact.email ?: "Non renseigné"
        textAdresse.text = contact.address ?: "Non renseigné"
        textZipCodeVille.text = "${contact.zipcode}, ${contact.city}"


        return view
    }
}