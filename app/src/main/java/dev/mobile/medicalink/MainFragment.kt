package dev.mobile.medicalink

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

//MainFragement n'est pas un fragment mais une activité
//Ici on va gérer les fragments
class MainFragment : AppCompatActivity() {

    private val rootFrag = "root_fragment"

    private lateinit var btnAccueilNav: LinearLayout
    private lateinit var imageAccueil: ImageView
    private lateinit var textAccueil: TextView
    private lateinit var btnTraitementsNav: LinearLayout
    private lateinit var imageTraitements: ImageView
    private lateinit var textTraitements: TextView
    private lateinit var btnMessagesNav: LinearLayout
    private lateinit var imageMessages: ImageView
    private lateinit var textMessages: TextView
    private lateinit var btnPlusNav: LinearLayout
    private lateinit var imagePlus: ImageView
    private lateinit var textPlus: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_fragment)

        btnAccueilNav = findViewById(R.id.btnAccueilNav)
        imageAccueil=findViewById(R.id.imageViewAccueil)
        textAccueil=findViewById(R.id.textViewAccueil)
        btnTraitementsNav = findViewById(R.id.btnTraitementsNav)
        imageTraitements=findViewById(R.id.imageViewTraitement)
        textTraitements=findViewById(R.id.textViewTraitement)
        btnMessagesNav = findViewById(R.id.btnMessagesNav)
        imageMessages=findViewById(R.id.imageViewMessages)
        textMessages=findViewById(R.id.textViewMessages)
        btnPlusNav = findViewById(R.id.btnPlusNav)
        imagePlus=findViewById(R.id.imageViewPlus)
        textPlus=findViewById(R.id.textViewPlus)


        imageAccueil.setImageResource(R.drawable.accueilreverse)
        imageTraitements.setImageResource(R.drawable.traitements)
        imageMessages.setImageResource(R.drawable.messages)
        imagePlus.setImageResource(R.drawable.plus)

        // Chargement du fragment par défaut
        loadFrag(HomeFragment(), 0)

        btnAccueilNav.setOnClickListener {
            imageAccueil.setImageResource(R.drawable.accueilreverse)
            imageTraitements.setImageResource(R.drawable.traitements)
            imageMessages.setImageResource(R.drawable.messages)
            imagePlus.setImageResource(R.drawable.plus)

            textAccueil.setTextColor(android.graphics.Color.parseColor("#3F4791"))
            textTraitements.setTextColor(android.graphics.Color.parseColor("#000000"))
            textMessages.setTextColor(android.graphics.Color.parseColor("#000000"))
            textPlus.setTextColor(android.graphics.Color.parseColor("#000000"))

            loadFrag(HomeFragment(), 1)
        }

        btnTraitementsNav.setOnClickListener {
            imageAccueil.setImageResource(R.drawable.accueil)
            imageTraitements.setImageResource(R.drawable.documentsreverse)
            imageMessages.setImageResource(R.drawable.messages)
            imagePlus.setImageResource(R.drawable.plus)

            textAccueil.setTextColor(android.graphics.Color.parseColor("#000000"))
            textTraitements.setTextColor(android.graphics.Color.parseColor("#3F4791"))
            textMessages.setTextColor(android.graphics.Color.parseColor("#000000"))
            textPlus.setTextColor(android.graphics.Color.parseColor("#000000"))

            loadFrag(MainTraitementsFragment(), 1)

        }



        btnMessagesNav.setOnClickListener {
            imageAccueil.setImageResource(R.drawable.accueil)
            imageTraitements.setImageResource(R.drawable.traitements)
            imageMessages.setImageResource(R.drawable.enveloppereverse)
            imagePlus.setImageResource(R.drawable.plus)

            textAccueil.setTextColor(android.graphics.Color.parseColor("#000000"))
            textTraitements.setTextColor(android.graphics.Color.parseColor("#000000"))
            textMessages.setTextColor(android.graphics.Color.parseColor("#3F4791"))
            textPlus.setTextColor(android.graphics.Color.parseColor("#000000"))

        }

        btnPlusNav.setOnClickListener {
            imageAccueil.setImageResource(R.drawable.accueil)
            imageTraitements.setImageResource(R.drawable.traitements)
            imageMessages.setImageResource(R.drawable.messages)
            imagePlus.setImageResource(R.drawable.plusreverse)

            textAccueil.setTextColor(android.graphics.Color.parseColor("#000000"))
            textTraitements.setTextColor(android.graphics.Color.parseColor("#000000"))
            textMessages.setTextColor(android.graphics.Color.parseColor("#000000"))
            textPlus.setTextColor(android.graphics.Color.parseColor("#3F4791"))

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
}
