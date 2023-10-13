package dev.mobile.td3notes

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.constraintlayout.solver.state.State.Constraint
import androidx.constraintlayout.widget.ConstraintLayout

class image : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        val lenomquejeveux : ConstraintLayout = findViewById(R.id.LEID)
        val image : ImageView = ImageView(this)
        image.setImageURI(intent.data)
        lenomquejeveux.addView(image)
    }
}