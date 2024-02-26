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
import dev.mobile.medicalink.R.string.Telephone_non_renseigne
import dev.mobile.medicalink.api.rpps.ApiRppsClient
import dev.mobile.medicalink.api.rpps.ApiRppsService
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Contact
import dev.mobile.medicalink.db.local.repository.ContactRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.utils.MapIconeMedecin
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
    private lateinit var openMapButton: ImageView
    private lateinit var imageMedecin: ImageView

    private lateinit var db: AppDatabase
    private lateinit var contactDatabaseInterface: ContactRepository
    private lateinit var userDataBaseInterface: UserRepository
    private lateinit var apiRpps: ApiRppsService
    private var isInBase = false

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
        openMapButton = view.findViewById(R.id.btnMaps)
        imageMedecin = view.findViewById(R.id.imageMedecinInfoContact)

        db = AppDatabase.getInstance(requireContext())
        contactDatabaseInterface = ContactRepository(db.contactDao())
        userDataBaseInterface = UserRepository(db.userDao())
        apiRpps = ApiRppsClient().apiService

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        btnAjoutSupp = view.findViewById(R.id.confirmer)


        val contact = requireArguments().getSerializable("contact") as Contact

        Thread {
            val res = contactDatabaseInterface.getOneContactById(contact.uuid, contact.rpps)
            isInBase = res != null
            if (isInBase) {
                setButtonSupprimer(contact)
            } else {
                setButtonAjouter(contact)
            }
        }.start()

        btnTelephone.setOnClickListener({
            val number = textTelephone.text.toString()
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            startActivity(intent)
        })

        btnEmail.setOnClickListener(View.OnClickListener {
            val email = textEmail.text.toString()

            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            startActivity(intent)
        })

        val mapIconeMedecin = MapIconeMedecin()

        val matchingSpecialty =
            mapIconeMedecin.keys.find { contact.specialty?.contains(it, ignoreCase = true) == true }

        val imageResource = mapIconeMedecin[matchingSpecialty] ?: R.drawable.docteur
        imageMedecin.setBackgroundResource(imageResource)

        retour.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        textNomComplet.text = contact.fullName
        textRpps.text = buildString {
            append("RPPS : ")
            append(contact.rpps.toString())
        }
        textSpecialite.text = contact.specialty ?: "Spécialité non renseigné"
        if (contact.phoneNumber == null) {
            textTelephone.text = Telephone_non_renseigne.toString()
            btnTelephone.visibility = View.INVISIBLE
        } else {
            textTelephone.text = contact.phoneNumber
            btnTelephone.visibility = View.VISIBLE
        }

        if (contact.email == null) {
            lifecycleScope.launch {
                val res = apiRpps.getEmail(contact.rpps)
                if (res.isSuccessful) {
                    contact.email = res.body()?.getOrNull(0)
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
        } else {
            textEmail.text = contact.email
            btnEmail.visibility = View.VISIBLE
        }


        if (contact.address == null) {
            textAdresse.text = "Adresse non renseigné"
            openMapButton.visibility = View.GONE
        } else {
            textAdresse.text = contact.address
            openMapButton.visibility = View.VISIBLE

        }

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
        openMapButton.setOnClickListener {
            openMapWithAddress(address)
        }

        return view
    }

    private fun openMapWithAddress(address: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        Log.d("Map", "ça rentre")
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            Log.d("Map", "ça rentre deux fois")
            startActivity(mapIntent)
        } else {
            Log.d("Map", "Aucune application de cartographie n'est installée.")
        }
    }

    private fun setButtonSupprimer(c: Contact) {
        btnAjoutSupp.text = "Supprimer des contacts"
        btnAjoutSupp.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.rounded_darker_red_button_no_stroke_background,
            null
        )
        btnAjoutSupp.setOnClickListener {
            Thread {
                val bundle = Bundle()
                contactDatabaseInterface.deleteContact(c)
                val destinationFragment = ContactsFragment()
                destinationFragment.arguments = bundle
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }.start()
        }
    }

    private fun setButtonAjouter(c: Contact) {
        btnAjoutSupp.text = "Ajouter aux contacts"
        btnAjoutSupp.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.rounded_darker_blue_button_no_stroke_background,
            null
        )
        btnAjoutSupp.setOnClickListener {
            Thread {
                val bundle = Bundle()
                contactDatabaseInterface.insertContact(c)
                val destinationFragment = ContactsFragment()
                destinationFragment.arguments = bundle
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }.start()
        }
    }
}