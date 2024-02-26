package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import androidx.annotation.RequiresApi
import dev.mobile.medicalink.fragments.traitements.PriseTest.Companion.dummyPrise1
import dev.mobile.medicalink.fragments.traitements.PriseTest.Companion.dummyPrise2
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.io.Serializable
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class TraitementTest {

    private var t1 = dummyTraitement1()
    @Test
    fun enMajuscule() {
        assertEquals(t1.nomTraitement,"ZOPICLONE ZYDUS 7,5 mg, comprimé pelliculé sécable")
    }


    @Test
    fun getName() {
        assertEquals(t1.nomTraitement, t1.getName())
    }

    @Test
    fun getProchainePrise() {
        assertEquals(dummyPrise1(),t1.getProchainePrise(dummyPrise1()))
    }

    companion object {
        public fun dummyTraitement1() : Traitement {
            var CodeCIS: Int? = 65883886
            var nomTraitement: String = "ZOPICLONE ZYDUS 7,5 mg, comprimé pelliculé sécable"
            var dosageNb: Int = 1
            var dosageUnite: String = "1"
            var dateFinTraitement: LocalDate? = LocalDate.now()
            var typeComprime: String = "Comprimé"
            var comprimesRestants: Int? = 5
            var expire: Boolean = true
            var effetsSecondaires: MutableList<String>? = null
            var prises: MutableList<Prise> = mutableListOf<Prise>(
                dummyPrise1(),
                dummyPrise2()
            )
            var totalQuantite: Int? = 30
            var UUID: String? = "3a25971c-1b71-45a5-8ae0-28c8719434bc"
            var UUIDUSER: String? = "9206ab59-43e2-4b7a-8880-f7ac37a5a41a"
            var dateDbtTraitement: LocalDate? = LocalDate.MIN

            return Traitement(
                CodeCIS,
                nomTraitement,
                dosageNb,
                dosageUnite,
                dateFinTraitement,
                typeComprime,
                comprimesRestants,
                expire,
                effetsSecondaires,
                prises,
                totalQuantite,
                UUID,
                UUIDUSER,
                dateDbtTraitement
            )
        }
    }
}
