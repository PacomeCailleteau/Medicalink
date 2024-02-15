package dev.mobile.medicalink.utils.medecin


import org.junit.Assert.*
import org.junit.Test


class MedecinApiTest {
    private val medecinApi = MedecinApi()


    @Test
    fun `get a medecin with wrong rpps`() {
        val medecin = medecinApi.getMedecin("caca")
        assertNull(medecin)
    }

    @Test
    fun `get a medecin with right rpps`() {
        val medecin = medecinApi.getMedecin("10101230760")
        assertNotNull(medecin)
        assertEquals("10101230760", medecin!!.rpps)
    }

    @Test
    fun `get a list of medecins with wrong prenom and nom`() {
        val medecins = medecinApi.getMedecins("caca", "caca")
        assertNull(medecins)
    }

    @Test
    fun `get a list of medecins with right prenom and nom`() {
        val medecins = medecinApi.getMedecins("Jean", "Dupont")
        assertNotNull(medecins)
        assertTrue(medecins!!.isNotEmpty())
    }

}












