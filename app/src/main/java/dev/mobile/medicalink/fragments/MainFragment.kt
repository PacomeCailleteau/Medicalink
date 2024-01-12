package dev.mobile.medicalink.fragments


import android.os.Bundle
import android.util.Log
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
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.home.HomeFragment
import dev.mobile.medicalink.fragments.traitements.MainTraitementsFragment
import dev.mobile.medicalink.fragments.traitements.MessagesFragment

/**
 * Fragment de la barre de navigation en bas de l'application (Accueil/Traitement/Messages)
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


        //NotificationService.sendNotification(this, "depuis Main Frag", "Ça rime PAS avec Tchoupi", 5000)

        //masquer la barre de titre
        supportActionBar?.hide()


        //Create database connexion, use `userDatabaseInterface` to access to the database
        val db = AppDatabase.getInstance(this)
        val userDatabaseInterface = UserRepository(db.userDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())
        /*
        Thread {
            val res = userDatabaseInterface.getAllUsers()
            Log.d("USER", "res: $res")

            val monUser = User(
                "111111", "Professionnel", "DENIS",
                "Jack", "3 Novembre 1978", "l@gmail.com", "", false
            )

            userDatabaseInterface.insertUser(monUser)
        }.start()

         */


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
            loadFrag(MessagesFragment(), 1)

        }

    }

    // flag 0 pour ajouter, 1 pour remplacer
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
