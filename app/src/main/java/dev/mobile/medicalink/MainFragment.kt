package dev.mobile.medicalink

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.room.Room
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.utils.AlarmReceiver
import java.util.Calendar

//MainFragement n'est pas un fragment mais une activité
//Ici on va gérer les fragments
class MainFragment : AppCompatActivity() {

    private val rootFrag = "root_fragment"

    private lateinit var menu : ConstraintLayout
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


        //TEST DATABASE
        val db = AppDatabase.getInstance(this)
        //on ajoute un User dans la bd
        val userRepo = UserRepository(db.userDao())
        var res : Pair<Boolean, String>

        Thread {
            userRepo.getAllUsers().forEach {
                Log.d("User", "User: ${it.firstName} ${it.lastName}")
            }
        }.start()


        // TEST NOTIF
        // Dans votre activité principale (ou autre composant), programmez l'alarme comme suit :
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        menu=findViewById(R.id.fragmentDuBas)

        btnAccueilNav = findViewById(R.id.btnAccueilNav)
        imageAccueil=findViewById(R.id.imageViewAccueil)
        btnTraitementsNav = findViewById(R.id.btnTraitementsNav)
        imageTraitements=findViewById(R.id.imageViewTraitement)
        btnMessagesNav = findViewById(R.id.btnMessagesNav)
        imageMessages=findViewById(R.id.imageViewMessages)
        textAccueil=findViewById(R.id.textViewAccueil)
        textTraitements=findViewById(R.id.textViewTraitement)
        textMessages=findViewById(R.id.textViewMessages)


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

    private fun changeMenu(dest : Int) {
        // On remet tous à la normal
        imageAccueil.setImageResource(R.drawable.accueil)
        imageTraitements.setImageResource(R.drawable.traitements)
        imageMessages.setImageResource(R.drawable.messages)
        textAccueil.setTextColor(Color.parseColor("#000000"))
        textTraitements.setTextColor(Color.parseColor("#000000"))
        textMessages.setTextColor(Color.parseColor("#000000"))


        // On met en surbrillance le bouton cliqué
        when (dest) {
            0 -> {
                imageAccueil.setImageResource(R.drawable.accueilreverse)
                textAccueil.setTextColor(Color.parseColor("#3F4791"))
            }
            1 -> {
                imageTraitements.setImageResource(R.drawable.documentsreverse)
                textTraitements.setTextColor(Color.parseColor("#3F4791"))
            }
            2 -> {
                imageMessages.setImageResource(R.drawable.enveloppereverse)
                textMessages.setTextColor(Color.parseColor("#3F4791"))
            }
        }


    }
}
