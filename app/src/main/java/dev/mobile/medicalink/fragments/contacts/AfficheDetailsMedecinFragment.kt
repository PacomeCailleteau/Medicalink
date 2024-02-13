package dev.mobile.medicalink.fragments.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import dev.mobile.medicalink.R


class AfficheDetailsMedecinFragment : Fragment() {
    private lateinit var rpps : TextView
    private lateinit var prenom : TextView
    private lateinit var nom : TextView
    private lateinit var email : TextView
    private lateinit var phone : TextView
    private lateinit var address : TextView
    private lateinit var zipCode : TextView
    private lateinit var city : TextView
    private lateinit var gender : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_affiche_details_medecin, container, false)
        rpps = view.findViewById(R.id.RppsDetailMed)
        prenom = view.findViewById(R.id.PrenomDetailMed)
        nom = view.findViewById(R.id.NomDetailMed)
        email = view.findViewById(R.id.EmailDetailMed)
        phone = view.findViewById(R.id.PhoneDetailMed)
        address = view.findViewById(R.id.AddressDetailMed)
        zipCode = view.findViewById(R.id.ZipCodeDetailMed)
        city = view.findViewById(R.id.CityDetailMed)
        gender = view.findViewById(R.id.GenderDetailMed)




        return view
    }

}