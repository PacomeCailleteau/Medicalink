package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.O)
class PriseTest {

    private lateinit var numeroPrise: String
    private lateinit var heurePrise: String
    private var quantite by Delegates.notNull<Int>()
    private lateinit var dosageUnite: String


}
