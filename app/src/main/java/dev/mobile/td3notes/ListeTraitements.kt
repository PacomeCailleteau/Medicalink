package dev.mobile.td3notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListeTraitements : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_traitements)

        val lp = mutableListOf<Traitement>(
            Traitement("Doliprane"),
            Traitement("Paracétamol"),
            Traitement("Efferalgan"),
            Traitement("Imodium"),
        )

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewListeTraitement)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TraitementAdapterR(lp)

        //Gestion espacement entre items RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))
    }
}