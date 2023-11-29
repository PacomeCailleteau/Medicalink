package dev.mobile.medicalink

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import java.util.concurrent.LinkedBlockingQueue

class ChangerUtilisateur : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changer_utilisateur)


        val db = AppDatabase.getInstance(this.applicationContext)
        val userDatabaseInterface = UserRepository(db.userDao())


        val queue = LinkedBlockingQueue<List<User>>()

        //Récupération des traitements (nommé médocs dans la base de donnée) en les transformant en une liste de traitement pour les afficher
        Thread{
            val listeUserBDD = userDatabaseInterface.getAllUsers()

            queue.add(listeUserBDD)
        }.start()

        val mesUsers = queue.take()



        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewChangerUtilisateur)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ChangerUtilisateurAdapterR(mesUsers)

        // Gestion de l'espacement entre les éléments du RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))

    }
}