package dev.mobile.medicalink

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

//MainFragement n'est pas un fragment mais une activité
//Ici on va gérer les fragments
class MainFragment : AppCompatActivity() {

    private val rootFrag = "root_fragment"
    private lateinit var btnMessagesNav: Button
    private lateinit var btnAccueilNav: Button
    private lateinit var btnPlusNav: Button
    private lateinit var btnTraitementsNav: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_fragment)

        btnMessagesNav = findViewById(R.id.btnMessagesNav)
        btnAccueilNav = findViewById(R.id.btnAccueilNav)
        btnPlusNav = findViewById(R.id.btnPlusNav)
        btnTraitementsNav = findViewById(R.id.btnTraitementsNav)

        // Chargement du fragment par défaut
        loadFrag(HomeFragment(), 0)

        btnTraitementsNav.setOnClickListener {
            loadFrag(MainTraitementsFragment(), 1)
        }

        btnAccueilNav.setOnClickListener {
            loadFrag(HomeFragment(), 1)
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
