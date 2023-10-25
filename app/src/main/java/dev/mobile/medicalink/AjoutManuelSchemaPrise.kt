package dev.mobile.medicalink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import com.google.android.material.textfield.TextInputEditText

class AjoutManuelSchemaPrise : AppCompatActivity() {

    private lateinit var quotidiennementButton: Button
    private lateinit var intervalleRegulierButton: Button
    private lateinit var auBesoinButton: Button

    private lateinit var retour: ImageView

    private lateinit var nextLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajout_manuel_schema_prise)

        quotidiennementButton = findViewById(R.id.quotidiennement_button)
        intervalleRegulierButton = findViewById(R.id.intervalle_regulier_button)
        auBesoinButton = findViewById(R.id.au_besoin_button)

        retour = findViewById(R.id.retour_schema_prise)

        retour.setOnClickListener {
            finish()
        }



    }
}