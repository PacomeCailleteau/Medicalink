package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.O)
class PriseTest {

    private lateinit var numeroPrise: String
    private lateinit var heurePrise: String
    private var quantite by Delegates.notNull<Int>()
    private lateinit var dosageUnite: String

    companion object {
        fun dummyPrise1(): Prise {
            var numeroPrise: String = "cc64abd7-124b-4e4d-abdd-f03f64a424c0"
            var heurePrise: String = "17:30"
            var quantite = 25
            var dosageUnite: String = "Comprimé"

            return Prise(
                numeroPrise,
                heurePrise,
                quantite,
                dosageUnite
            )
        }

        fun dummyPrise2(): Prise {
            var numeroPrise: String = UUID.randomUUID().toString()
            var heurePrise: String = "9:30"
            var quantite = 24
            var dosageUnite: String = "Comprimé"

            return Prise(
                numeroPrise,
                heurePrise,
                quantite,
                dosageUnite
            )
        }

    }
}
