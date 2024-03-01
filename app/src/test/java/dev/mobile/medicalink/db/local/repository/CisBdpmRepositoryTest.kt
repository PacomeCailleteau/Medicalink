package dev.mobile.medicalink.db.local.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.CisBdpm
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class CisBdpmRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var cisBdpmRepository: CisBdpmRepository
    private val defaultCis = CisBdpm(
        "11111111",
        "denomination",
        "formePharmaceutique",
        "voiesAdministration",
        "statutAdministratifAMM",
        "typeProcedureAMM",
        "etatCommercialisation",
        "dateAMM",
        "statutBdm",
        "numeroAutorisationEuropeenne",
        "titulaire",
        "surveillanceRenforcee"
    )


    @Before
    fun setupDatabase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        cisBdpmRepository = CisBdpmRepository(db.cisBdpmDao())
    }

    @After
    fun closeDatabase() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun `test if we can get all cisBdpm`() {
        // Should be empty
        val cisBdpm = cisBdpmRepository.getAllCisBdpm()
        assertTrue(cisBdpm.isEmpty())
    }

    @Test
    fun insertCisBdpmInDatabase() {
        // I prefer to create a new val cisBdpm than using defaultCis
        val cisBdpm = defaultCis
        cisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertEquals(cisBdpmFromDatabase.size, 1)
        assertEquals(cisBdpmFromDatabase[0].codeCIS, cisBdpm.codeCIS)
    }

    @Test
    fun insertCisBdpmInDatabaseWithSameId() {
        val cisBdpm = defaultCis
        cisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertEquals(cisBdpmFromDatabase.size, 1)
        assertEquals(cisBdpmFromDatabase[0].codeCIS, cisBdpm.codeCIS)
        cisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase2 = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertEquals(cisBdpmFromDatabase2.size, 1)
        assertEquals(cisBdpmFromDatabase2[0].codeCIS, cisBdpm.codeCIS)
    }

    @Test
    fun `update a cisBdpm`() {
        val cisBdpm = defaultCis
        cisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertEquals(cisBdpmFromDatabase.size, 1)
        assertEquals(cisBdpmFromDatabase[0].codeCIS, cisBdpm.codeCIS)
        val cisBdpmUpdated = cisBdpm.copy(denomination = "denominationUpdated")
        cisBdpmRepository.updateCisBdpm(cisBdpmUpdated)
        val cisBdpmFromDatabaseUpdated = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertEquals(cisBdpmFromDatabaseUpdated.size, 1)
        assertEquals(cisBdpmFromDatabaseUpdated[0].denomination, "denominationUpdated")
    }

    @Test
    fun `update a cisBdpm with wrong id`() {
        val cisBdpm = defaultCis
        cisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertEquals(cisBdpmFromDatabase.size, 1)
        assertEquals(cisBdpmFromDatabase[0].codeCIS, cisBdpm.codeCIS)
        val cisBdpmUpdated = cisBdpm.copy(codeCIS = "22222222")
        cisBdpmRepository.updateCisBdpm(cisBdpmUpdated)
        val cisBdpmFromDatabaseUpdated = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertEquals(cisBdpmFromDatabaseUpdated.size, 1)
        assertEquals(cisBdpmFromDatabaseUpdated[0].codeCIS, cisBdpm.codeCIS)
    }

    @Test
    fun `delete a cisBdpm`() {
        val cisBdpm = defaultCis
        cisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertEquals(cisBdpmFromDatabase.size, 1)
        assertEquals(cisBdpmFromDatabase[0].codeCIS, cisBdpm.codeCIS)
        cisBdpmRepository.deleteCisBdpm(cisBdpm)
        val cisBdpmFromDatabaseDeleted = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertTrue(cisBdpmFromDatabaseDeleted.isEmpty())
    }

    @Test
    fun `delete a cisBdpm with wrong id`() {
        val cisBdpm = defaultCis
        cisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertEquals(cisBdpmFromDatabase.size, 1)
        assertEquals(cisBdpmFromDatabase[0].codeCIS, cisBdpm.codeCIS)
        val cisBdpmWrongId = cisBdpm.copy(codeCIS = "22222222")
        cisBdpmRepository.deleteCisBdpm(cisBdpmWrongId)
        val cisBdpmFromDatabaseDeleted = cisBdpmRepository.getOneCisBdpmById(cisBdpm.codeCIS)
        assertEquals(cisBdpmFromDatabaseDeleted.size, 1)
        assertEquals(cisBdpmFromDatabaseDeleted[0].codeCIS, cisBdpm.codeCIS)
    }

    @Test
    fun `test insert from csv`() {
        // Warning : this test will fail if you don't have the csv file in the assets folder
        val context = ApplicationProvider.getApplicationContext<Context>()
        cisBdpmRepository.insertFromCsv(context)
        val cisBdpmFromDatabase = cisBdpmRepository.getAllCisBdpm()
        assertTrue(cisBdpmFromDatabase.isNotEmpty())
    }


}










