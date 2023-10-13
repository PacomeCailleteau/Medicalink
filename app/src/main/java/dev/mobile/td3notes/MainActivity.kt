package dev.mobile.td3notes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.annotations.Contract

class MainActivity : AppCompatActivity() {
    private lateinit var notes: Notes
    private lateinit var nomTv: TextView
    private lateinit var noteRb: RatingBar
    private lateinit var recule: ImageButton
    private lateinit var avance: ImageButton
    private lateinit var detail: ImageButton
    private lateinit var ajoute: ImageButton




    val secondRawLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // INPUT = un ActivityResult
            result -> Toast.makeText(this, "reçu : $result", Toast.LENGTH_LONG).show()
        notes.ajouter(result.data?.extras?.get(AddActivity.CLE) as Note)
    }

    val firstRawLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // INPUT = un ActivityResult
            result -> Toast.makeText(this, "reçu : $result", Toast.LENGTH_LONG).show()
        var monRESULT=result.data?.extras?.get(DetailActivity.CLE) as Note
        notes.courante()!!.nom=monRESULT.nom
        notes.courante()!!.note=monRESULT.note
        notes.courante()!!.commentaire=monRESULT.commentaire
        afficheNote(notes.courante())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nomTv = findViewById(R.id.nom)
        noteRb = findViewById(R.id.note)
        recule = findViewById(R.id.recule)
        avance = findViewById(R.id.avance)
        notes = Notes.init()
        detail = findViewById(R.id.detail)
        ajoute = findViewById(R.id.ajoute)


        afficheNote(notes.courante() ?: Note.FAKE_NOTE)

        val supprime = findViewById<ImageButton>(R.id.supprime)
        supprime.setOnClickListener {
            notes.supprimer()
            afficheNote(notes.courante())
        }
        recule.setOnClickListener{ afficheNote(notes.precedente()) }
        avance.setOnClickListener{ afficheNote(notes.suivante()) }
        findViewById<View>(R.id.detail).setOnClickListener {
            // INPUT = un Intent
            firstRawLauncher.launch(
                Intent(this@MainActivity, DetailActivity::class.java)
                    .putExtra("NOTE",Note(notes.courante()?.nom,notes.courante()!!.note,notes.courante()?.commentaire))
            )
        }

        findViewById<View>(R.id.ajoute).setOnClickListener {
            // INPUT = un Intent
            secondRawLauncher.launch(
                Intent(this@MainActivity, AddActivity::class.java)
            )
        }
    }

    fun afficheNote(note: Note?) {
            nomTv.text = note?.nom ?: Note.FAKE_NOTE.nom
            noteRb.rating = note?.note?.toFloat() ?: Note.FAKE_NOTE.note.toFloat()
    }

}