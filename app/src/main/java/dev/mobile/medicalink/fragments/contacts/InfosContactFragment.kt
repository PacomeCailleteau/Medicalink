package dev.mobile.medicalink.fragments.contacts

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.entity.Contact
import java.io.IOException

class InfosContactFragment : Fragment(), OnMapReadyCallback {
    private lateinit var textNomComplet: TextView
    private lateinit var textRpps: TextView
    private lateinit var textSpecialite: TextView
    private lateinit var textTelephone: TextView
    private lateinit var btnTelephone: ImageView
    private lateinit var textEmail: TextView
    private lateinit var btnEmail: ImageView
    private lateinit var textAdresse: TextView
    private lateinit var textZipCodeVille: TextView
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var isMapTouched = false
    private lateinit var btnItineraire: ImageView
    private lateinit var retour: ImageView
    private lateinit var scroll: ScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_descriptif_contact, container, false)
        textNomComplet = view.findViewById(R.id.nomCompletMedoc)
        textRpps = view.findViewById(R.id.cis)
        textSpecialite = view.findViewById(R.id.spécialitéMedecin)
        textTelephone = view.findViewById(R.id.téléphoneMedecin)
        btnTelephone = view.findViewById(R.id.btnTelephone)
        textEmail = view.findViewById(R.id.mailMedecin)
        btnEmail = view.findViewById(R.id.btnEmail)
        textAdresse = view.findViewById(R.id.adresseMedecin)
        textZipCodeVille = view.findViewById(R.id.zipCodeVilleMedecin)
        mapView = view.findViewById(R.id.mapView)
        retour = view.findViewById(R.id.retour_schema_contact)
        scroll = view.findViewById(R.id.scroll_info_contact)

        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
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

        mapView.onCreate(savedInstanceState)
        mapView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Lorsque l'utilisateur commence à toucher la carte, désactivez le défilement du ScrollView
                    isMapTouched = true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Lorsque l'utilisateur arrête de toucher la carte, réactivez le défilement du ScrollView
                    isMapTouched = false
                }
            }
            false
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

        // Adresse à afficher sur la carte
        val address = textAdresse.text.toString() + ", " + textZipCodeVille.text.toString()

        val latLng = getLatLngFromAddress(address)
        if (latLng == null) {
            mapView.visibility = View.GONE
        }else{
            latLng.let {
                configureMap(it, address)
            }

            mapView.getMapAsync { map ->
                googleMap = map
                // Déplacer la caméra de la carte à l'emplacement spécifié
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                // Ajouter un marqueur à l'adresse avec le titre de l'adresse
                googleMap.addMarker(MarkerOptions().position(latLng).title(address))

                // Ajoutez le listener pour gérer le défilement du ScrollView pendant les interactions avec la carte
                googleMap.setOnCameraMoveStartedListener { reason ->
                    if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE && isMapTouched) {
                        view?.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                }

                googleMap.setOnCameraIdleListener {
                    if (isMapTouched) {
                        scroll.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }
        }

        return view
    }

    private fun getLatLngFromAddress(address: String): LatLng? {
        val geocoder = Geocoder(requireContext())
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses.isNotEmpty()) {
                val latitude = addresses[0].latitude
                val longitude = addresses[0].longitude
                return LatLng(latitude, longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun configureMap(latLng: LatLng, address: String) {
        mapView.getMapAsync { map ->
            googleMap = map

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            googleMap.addMarker(MarkerOptions().position(latLng).title(address))
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        //Rien pour l'instant
    }
}
