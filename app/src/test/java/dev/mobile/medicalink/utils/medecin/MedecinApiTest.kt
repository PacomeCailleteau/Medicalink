package dev.mobile.medicalink.utils.medecin

import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test


class MedecinApiTest {
    private lateinit var medecinApi: MedecinApi
    @Before
    fun setUp() {
        medecinApi = MedecinApi()
    }


    @Test
    fun `get a medecin with wrong rpps`() {
        val medecin = medecinApi.getMedecin("123456789")
        assertNull(medecin)
    }

}