package dev.mobile.td3notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.util.Date

class ListeTraitements : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_traitements)

        val lp = mutableListOf<Traitement>(
            Traitement("Doliprane",4,"Jours", LocalDate.of(2023,5,25),15,false),
            Traitement("Aspirine", 7, "Mois", LocalDate.of(2023, 6, 10), 10),
            Traitement("Vitamine C", 30, "An", null, 20),
            Traitement("Ibuprofène", 5, "Jours", LocalDate.of(2023, 8, 15), 12),
            Traitement("Antibiotique", 10, "Mois", null, 1), // Date de début inconnue
            Traitement("Médicament X", 14, "Jours", LocalDate.of(2023, 10, 5), 8),
            Traitement("Vitamine D", 60, "An", LocalDate.of(2023, 9, 1), 25),
            Traitement("Paracétamol", 3, "Mois", null, 18),
            Traitement("Anti-inflammatoire", 7, "Jours", LocalDate.of(2023, 12, 1), 15),
            Traitement("Médicament Y", 21, "An", LocalDate.of(2024, 1, 20), 10)
        )

        val traitementsTries = lp.sortedBy { it.expire }.toMutableList()
        println(traitementsTries.first().nomTraitement)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewListeTraitement)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TraitementAdapterR(traitementsTries)

        //Gestion espacement entre items RecyclerView
        val espacementEnDp = 22
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))
    }
}