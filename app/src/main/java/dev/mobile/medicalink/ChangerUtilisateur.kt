package dev.mobile.medicalink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.MainFragment
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import java.util.concurrent.LinkedBlockingQueue

class ChangerUtilisateur : Activity() {

    private lateinit var boutonAjouterProfil: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changer_utilisateur)

        boutonAjouterProfil = findViewById(R.id.boutonAjouterProfilChangerUtilisateur)

        val db = AppDatabase.getInstance(this.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())


        val queue = LinkedBlockingQueue<List<User>>()

        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread{
            val listeUserBDD = userDatabaseInterface.getAllUsers()

            Log.d("test",listeUserBDD.toString())
            queue.add(listeUserBDD)
        }.start()

        val mesUsers = queue.take()

        /*

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewChangerUtilisateur)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ChangerUtilisateurAdapterR(mesUsers)

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))
        */
        boutonAjouterProfil.setOnClickListener {
            val intent = Intent(this, CreerProfilActivity::class.java)
            startActivity(intent)
        }

    }
}