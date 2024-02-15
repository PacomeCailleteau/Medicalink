package dev.mobile.medicalink.fragments.contacts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dev.mobile.medicalink.R
import dev.mobile.medicalink.api.rpps.ApiRppsClient
import dev.mobile.medicalink.api.rpps.ApiRppsService
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Contact
import dev.mobile.medicalink.db.local.repository.ContactRepository
import kotlinx.coroutines.launch

class InfosContactFragment : Fragment() {
    private lateinit var textNomComplet: TextView
    private lateinit var textRpps: TextView
    private lateinit var textSpecialite: TextView
    private lateinit var textTelephone: TextView
    private lateinit var btnTelephone: ImageView
    private lateinit var textEmail: TextView
    private lateinit var btnEmail: ImageView
    private lateinit var textAdresse: TextView
    private lateinit var textZipCodeVille: TextView
    private lateinit var retour: ImageView
    private lateinit var scroll: ScrollView
    private lateinit var btnAjoutSupp: AppCompatButton

    private lateinit var db: AppDatabase
    private lateinit var contactDatabaseInterface: ContactRepository
    private lateinit var apiRpps: ApiRppsService
    private var isInBase = false
    private lateinit var contact: Contact

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_descriptif_contact, container, false)
        textNomComplet = view.findViewById(R.id.nomCompletMedecin)
        textRpps = view.findViewById(R.id.rpps)
        textSpecialite = view.findViewById(R.id.spécialitéMedecin)
        textTelephone = view.findViewById(R.id.téléphoneMedecin)
        btnTelephone = view.findViewById(R.id.btnTelephone)
        textEmail = view.findViewById(R.id.mailMedecin)
        btnEmail = view.findViewById(R.id.btnEmail)
        textAdresse = view.findViewById(R.id.adresseMedecin)
        textZipCodeVille = view.findViewById(R.id.zipCodeVilleMedecin)
        retour = view.findViewById(R.id.retour_schema_contact)
        scroll = view.findViewById(R.id.scroll_info_contact)

        db = AppDatabase.getInstance(requireContext())
        contactDatabaseInterface = ContactRepository(db.contactDao())
        apiRpps = ApiRppsClient().apiService

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        btnAjoutSupp = view.findViewById(R.id.confirmer)

        val contact = requireArguments().getSerializable("contact") as Contact
        Log.d("Contact", contact.toString())

        lifecycleScope.launch {
            val res = contactDatabaseInterface.getOneContactById(contact.uuid, contact.Rpps)
            isInBase = res != null
            Log.d("isInBase", isInBase.toString())
            if (isInBase) {
                setButtonSupprimer(contact)
            } else {
                setButtonAjouter(contact)
            }
        }

        btnTelephone.setOnClickListener(View.OnClickListener {
            val number = textTelephone.text.toString()
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            startActivity(intent)
        })

        btnEmail.setOnClickListener(View.OnClickListener {
            val email = textEmail.text.toString()

            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            startActivity(intent)
        })

        retour.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        textNomComplet.text = contact.fullname
        textRpps.text = buildString {
            append("RPPS : ")
            append(contact.Rpps.toString())
        }
        textSpecialite.text = contact.specialty ?: "Spécialité non renseigné"
        if (contact.phoneNumber == null) {
            textTelephone.text = "Téléphone non renseigné"
            btnTelephone.visibility = View.INVISIBLE
        } else {
            textTelephone.text = contact.phoneNumber
            btnTelephone.visibility = View.VISIBLE
        }

        lifecycleScope.launch{
            val res = apiRpps.getEmail(contact.Rpps)
            Log.d("Email", res.body().toString())
            if (res.isSuccessful) {
                contact.email = res.body()?.emailAddresses?.get(0)
                if (contact.email == null) {
                    btnEmail.visibility = View.INVISIBLE
                    textEmail.text = "Email non renseigné"
                } else {
                    textEmail.text = contact.email
                    btnEmail.visibility = View.VISIBLE
                }
            } else {
                textEmail.text = "Email non renseigné"
                btnEmail.visibility = View.INVISIBLE
            }
        }
        textAdresse.text = contact.address ?: "Adresse non renseigné"
        if (contact.zipcode == "") {
            contact.zipcode = null
        }
        if (contact.zipcode == null && contact.city == null) {
            textZipCodeVille.text = "Ville non renseigné"
        } else if (contact.zipcode == null || contact.city == null) {
            textZipCodeVille.text = buildString {
                append(contact.zipcode ?: "")
                append(contact.city ?: "")
            }
        } else {
            textZipCodeVille.text = buildString {
                append(contact.zipcode)
                append(", ")
                append(contact.city)
            }
        }

        // Adresse à afficher sur la carte
        val address = textAdresse.text.toString() + ", " + textZipCodeVille.text.toString()


        return view
    }

    private fun setButtonSupprimer(c: Contact) {
        btnAjoutSupp.text = "Supprimer des contacts"
        btnAjoutSupp.background = ResourcesCompat.getDrawable(resources, R.drawable.rounded_darker_red_button_no_stroke_background, null)
        btnAjoutSupp.setOnClickListener {
            Thread {
                val res = contactDatabaseInterface.deleteContact(c)
                Log.d("Suppression", res.toString())
                setButtonAjouter(c)
            }.start()
        }
    }

    private fun setButtonAjouter(c: Contact) {
        btnAjoutSupp.text = "Ajouter aux contacts"
        btnAjoutSupp.background = ResourcesCompat.getDrawable(resources, R.drawable.rounded_darker_blue_button_no_stroke_background, null)
        btnAjoutSupp.setOnClickListener {
            Thread {
                val res = contactDatabaseInterface.insertContact(c)
                Log.d("Ajout", res.toString())
                setButtonSupprimer(c)
            }.start()
        }
    }
}
