package dev.mobile.medicalink.api.rpps
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before

class PracticianTest {

    lateinit var p1 : Practician
    lateinit var p2 : Practician

    @Before
    fun init(){
        p1 = dummyPractician1()
        p2 = dummyPractician2()
    }
    // Testing Creation
    @Test
    fun createOne() {
        assertEquals(dummyPractician1(),p1 )
    }

    @Test
    fun modifyOne() {
        p1.address = "3 Rue Maréchal Joffre"
        assertEquals("3 Rue Maréchal Joffre",p1.address)
    }

    // Testing attributes
    @Test
    fun getRpps(){
        assertEquals(dummyPractician1().rpps,p1.rpps )
    }

    @Test
    fun getFirstName(){
        assertEquals(dummyPractician2().firstName,p2.firstName )
    }
    @Test
    fun getLastName(){
        assertEquals(dummyPractician1().lastName,p1.lastName)
    }
    @Test
    fun getFullName(){
        assertEquals(dummyPractician2().fullName,p2.fullName )
    }
    @Test
    fun getSpecialty(){
        assertEquals(dummyPractician1().specialty,p1.specialty)
    }
    @Test
    fun getAddress(){
        assertEquals(dummyPractician2().address,p2.address )
    }
    @Test
    fun getZipcode(){
        assertEquals(dummyPractician1().zipcode,p1.zipcode)
    }
    @Test
    fun getCity(){
        assertEquals(dummyPractician2().city,p2.city )
    }

    @Test
    fun getPhoneNumber(){
        assertEquals(dummyPractician1().phoneNumber,p1.phoneNumber)
    }

    @Test
    fun samePhoneNumber(){
        assertEquals(p1.address,p2.address)
    }

    @Test
    fun differentRpps(){
        assertNotEquals(p1.rpps,p2.rpps)
    }

    internal companion object{

        fun dummyPractician1() : Practician {
            val rpps =  10101080173
            val firstName = "Jean-Marie"
            val address = "RUE DES PALIS"
            val city = "MAZE"
            val fullName = "Dr. Jean-Marie CLEKA"
            val lastName = "CLEKA"
            val phoneNumber = ""
            val specialty = "Spécialiste en Médecine Générale"
            val zipcode = "49630"

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

        fun dummyPractician2(): Practician {
            val rpps = 10000401033
            val firstName = "Marlène"
            val address = "11 Route du Château"
            val city = "MAZE"
            val fullName = "Marlène Berteau-Mevel"
            val lastName = "Berteau-Mevel"
            val phoneNumber = "02 41 66 17 90"
            val specialty = "Sage-femme"
            val zipcode = "49630"

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