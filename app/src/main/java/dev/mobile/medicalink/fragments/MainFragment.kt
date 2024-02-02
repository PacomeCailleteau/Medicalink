package dev.mobile.medicalink.fragments


import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.home.HomeFragment
import dev.mobile.medicalink.fragments.traitements.MainTraitementsFragment
import dev.mobile.medicalink.fragments.traitements.ContactsFragment

/**
 * Fragment de la barre de navigation en bas de l'application (Accueil/Traitement/Messages)
 * * Main Fragment est une activité !!!
 */
class MainFragment : AppCompatActivity() {

    private val rootFrag = "root_fragment"

    private lateinit var menu: ConstraintLayout
    private lateinit var btnAccueilNav: LinearLayout
    private lateinit var imageAccueil: ImageView
    private lateinit var btnTraitementsNav: LinearLayout
    private lateinit var imageTraitements: ImageView
    private lateinit var btnMessagesNav: LinearLayout
    private lateinit var imageMessages: ImageView
    private lateinit var textAccueil: TextView
    private lateinit var textTraitements: TextView
    private lateinit var textMessages: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_fragment)

        //masquer la barre de titre
        supportActionBar?.hide()

        // Récupération des éléments de la vue
        menu = findViewById(R.id.fragmentDuBas)
        btnAccueilNav = findViewById(R.id.btnAccueilNav)
        imageAccueil = findViewById(R.id.imageViewAccueil)
        btnTraitementsNav = findViewById(R.id.btnTraitementsNav)
        imageTraitements = findViewById(R.id.imageViewTraitement)
        btnMessagesNav = findViewById(R.id.btnMessagesNav)
        imageMessages = findViewById(R.id.imageViewMessages)
        textAccueil = findViewById(R.id.textViewAccueil)
        textTraitements = findViewById(R.id.textViewTraitement)
        textMessages = findViewById(R.id.textViewMessages)


        // Chargement du fragment par défaut et des bonnes couleurs
        loadFrag(HomeFragment(), 0)
        changeMenu(0)

        // Ajout des listeners sur les boutons de la barre de navigation
        btnAccueilNav.setOnClickListener {
            changeMenu(0)
            loadFrag(HomeFragment(), 1)
        }

        btnTraitementsNav.setOnClickListener {
            changeMenu(1)
            loadFrag(MainTraitementsFragment(), 1)
        }

        btnMessagesNav.setOnClickListener {
            changeMenu(2)
            loadFrag(ContactsFragment(), 1)
        }

    }

    /**
     * Fonction permettant de charger un fragment dans le fragment container
     * @param fragmentName : Fragment à charger
     * @param flag : 0 si on ajoute le fragment, 1 si on le remplace
     */
    private fun loadFrag(fragmentName: Fragment, flag: Int) {
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()

        if (flag == 0) {
            ft.add(R.id.FL, fragmentName)
            fm.popBackStack(rootFrag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            ft.addToBackStack(rootFrag)
        } else {
            ft.replace(R.id.FL, fragmentName)
            ft.addToBackStack(null)
        }

        ft.commit()
    }

    /**
     * Fonction permettant de changer la couleur des boutons de la barre de navigation
     * @param dest : 0 si on veut mettre en surbrillance le bouton Accueil, 1 pour Traitements, 2 pour Messages
     */
    private fun changeMenu(dest: Int) {
        // On remet tous à la normal
        imageAccueil.setImageResource(R.drawable.accueil)
        imageTraitements.setImageResource(R.drawable.traitements)
        imageMessages.setImageResource(R.drawable.messages)
        textAccueil.setTextColor(ContextCompat.getColor(this, R.color.black))
        textTraitements.setTextColor(ContextCompat.getColor(this, R.color.black))
        textMessages.setTextColor(ContextCompat.getColor(this, R.color.black))


        // On met en surbrillance le bouton cliqué
        when (dest) {
            0 -> {
                imageAccueil.setImageResource(R.drawable.accueilreverse)
                imageAccueil.setColorFilter(ContextCompat.getColor(this, R.color.evenDarkerBlue))

                imageTraitements.setColorFilter(ContextCompat.getColor(this, R.color.black))
                imageMessages.setColorFilter(ContextCompat.getColor(this, R.color.black))

                textAccueil.setTextColor(ContextCompat.getColor(this, R.color.evenDarkerBlue))
            }

            1 -> {
                imageTraitements.setImageResource(R.drawable.documentsreverse)
                imageTraitements.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.evenDarkerBlue
                    )
                )

                imageAccueil.setColorFilter(ContextCompat.getColor(this, R.color.black))
                imageMessages.setColorFilter(ContextCompat.getColor(this, R.color.black))

                textTraitements.setTextColor(ContextCompat.getColor(this, R.color.evenDarkerBlue))
            }

            2 -> {
                imageMessages.setImageResource(R.drawable.enveloppereverse)
                imageMessages.setColorFilter(ContextCompat.getColor(this, R.color.evenDarkerBlue))

                imageAccueil.setColorFilter(ContextCompat.getColor(this, R.color.black))
                imageTraitements.setColorFilter(ContextCompat.getColor(this, R.color.black))

                textMessages.setTextColor(ContextCompat.getColor(this, R.color.evenDarkerBlue))
            }
        }


    }
}
