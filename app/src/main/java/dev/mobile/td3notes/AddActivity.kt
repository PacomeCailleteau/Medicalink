package dev.mobile.td3notes

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.provider.ContactsContract.Intents
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.PickContact
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import java.util.*

class AddActivity : AppCompatActivity() {
    private lateinit var noteRb: RatingBar
    private lateinit var nomTv: EditText
    private lateinit var commentaire: EditText
    private lateinit var pick: ImageButton
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
        pick = findViewById(R.id.pick)
        cancel = findViewById(R.id.cancel)
        ok = findViewById(R.id.ok)
        findViewById<View>(R.id.pick).setOnClickListener {
            val versAutre = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(versAutre,51)
        }

        findViewById<Button>(R.id.cancel).setOnClickListener {
            val note = Note(nomTv.text.toString(),noteRb.rating.toDouble(),commentaire.text.toString())

            val valeurRetour = Intent().putExtra(CLE, note)
            setResult(RESULT_CANCELED, valeurRetour)
            finish()
        }

        findViewById<Button>(R.id.ok).setOnClickListener {
            val note = Note(nomTv.text.toString(),noteRb.rating.toDouble(),commentaire.text.toString())

            val valeurRetour = Intent().putExtra(CLE, note)
            setResult(RESULT_OK, valeurRetour)
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                val contactUri: Uri? = data?.data
                if (contactUri != null){
                    val cursor: Cursor? = contentResolver.query(contactUri, null, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        val nom: String =
                            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                        println(nom)
                        nomTv.text= Editable.Factory.getInstance().newEditable(nom)
                        cursor.close()
                    }
                }


            } else
                Toast.makeText(this, "BOUTON BACK pressé ?", Toast.LENGTH_LONG).show()
        }
    }
}