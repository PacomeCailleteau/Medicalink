package dev.mobile.td3notes

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class DetailActivity : AppCompatActivity() {
    private lateinit var noteRb: RatingBar
    private lateinit var nomTv: EditText
    private lateinit var commentaire: EditText
    private lateinit var cancel: Button
    private lateinit var ok: Button
    companion object {
        const val CLE = "cle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_detail)

        noteRb = findViewById(R.id.notei)
        nomTv = findViewById(R.id.nomi)
        commentaire = findViewById(R.id.commentairei)
        cancel = findViewById(R.id.cancel)
        ok = findViewById(R.id.ok)
        nomTv.isEnabled=false
        val note = intent.extras?.getParcelable<Note>("NOTE")
        println(note)
        noteRb.rating=note?.note!!.toFloat()
        commentaire.text= Editable.Factory.getInstance().newEditable(note.commentaire)
        nomTv.text= Editable.Factory.getInstance().newEditable(note.nom)


        findViewById<Button>(R.id.cancel).setOnClickListener {
            val note = Note(nomTv.text.toString(),noteRb.rating.toDouble(),commentaire.text.toString())

            val valeurRetour = Intent().putExtra(AddActivity.CLE, note)
            setResult(RESULT_CANCELED, valeurRetour)
            finish()
        }
        findViewById<Button>(R.id.ok).setOnClickListener {
            val note = Note(nomTv.text.toString(),noteRb.rating.toDouble(),commentaire.text.toString())

            val valeurRetour = Intent().putExtra(AddActivity.CLE, note)
            setResult(RESULT_OK, valeurRetour)
            finish()
        }

    }



}
