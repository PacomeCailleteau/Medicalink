package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import androidx.annotation.RequiresApi
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.*
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.O)
class PriseTest {

    private lateinit var p1 : Prise
    private lateinit var p2 : Prise
    @Before
    fun init(){
        p1 = dummyPrise1()
        p2 = dummyPrise2()
    }
    @Test
    fun enMajuscule() {
        assertEquals("17:30",p1.enMajuscule())
        assertEquals("9:00",p2.enMajuscule())
    }

    @Test
    fun getName() {
        assertEquals("17:30",p1.getName())
        assertEquals("9:00",p2.getName())
    }

    @Test
    fun toStringTest() {
        assertEquals(listOf("cc64abd7-124b-4e4d-abdd-f03f64a424c0","17h30",25,"Comprimé"),p1.toString())
    }

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
            var heurePrise: String = "9:00"
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
