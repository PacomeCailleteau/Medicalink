package dev.mobile.medicalink.api.rpps

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.runBlocking

class ApiRppsClientTest {

    lateinit var apiRpps : ApiRppsService

    @Before
    fun init(){
        apiRpps = ApiRppsClient().apiService
    }

    @Test
    fun apiCanBeInstanciated(){
        assertNotNull(apiRpps)
    }

    @Test
    fun apiIsAccessible() = runBlocking {
        assertNotNull( apiRpps.getPracticians(""))
    }

    @Test
    fun doesFindOne() = runBlocking {
        assertEquals( arrayListOf( dummyPractician()) ,apiRpps.getPracticians("Cleka").body())
    }
    
    @Test
    fun doesNotFindOne() = runBlocking {
        assertEquals( arrayListOf<Practician>() ,apiRpps.getPracticians("CarpenterBrut").body())
    }

    @Test
    fun doesFind3_Osselin() = runBlocking {
        val o1 = Practician(
            10001341998,
            "Jean-Claude",
            "OSSELIN",
            "Dr. Jean-Claude OSSELIN",
            "Pharmacien",
            null,
            "44340",
            "BOUGUENAIS",
            "+33240320599"
        )
        val o2 = Practician(
            10000705706,
            "Claude",
            "OSSELIN",
            "Dr. Claude OSSELIN",
            "Qualifié en Médecine Générale",
            "17 AV DE FLANDRE 75954",
            "75019",
            "PARIS 19E ARRONDISSEMENT",
            null
        )

        val o3 = Practician(
            10100192334,
            "Matthieu",
            "OSSELIN",
            "Dr. Matthieu OSSELIN",
            "Spécialiste en Médecine Générale",
            "10 RUE ANITA CONTI",
            "56000",
            "VANNES",
            null
        )

        assertEquals( arrayListOf(o1,o2,o3) ,apiRpps.getPracticians("OSSELIN").body())

    }

    companion object {
        fun dummyPractician() : Practician {
            var rpps =  10101080173
            var firstName = "Jean-Marie"
            var address = "RUE DES PALIS"
            var city = "MAZE"
            var fullName = "Dr. Jean-Marie CLEKA"
            var lastName = "CLEKA"
            var phoneNumber = null
            var specialty = "Spécialiste en Médecine Générale"
            var zipcode = "49630"

            return Practician(
                rpps = rpps,
                firstName = firstName,
                address = address,
                city = city,
                fullName = fullName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                specialty = specialty,
                zipcode = zipcode
            )
        }
    }
}

