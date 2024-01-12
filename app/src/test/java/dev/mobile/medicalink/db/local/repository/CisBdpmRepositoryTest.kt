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
    private lateinit var CisBdpmRepository: CisBdpmRepository
    private val defaultCis = CisBdpm(
        11111111,
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

        CisBdpmRepository = CisBdpmRepository(db.cisBdpmDao())
    }

    @After
    fun closeDatabase() {
        db.close()
    }

    @Test
    fun `test if we can get all cisBdpm`() {
        // Should be empty
        val cisBdpm = CisBdpmRepository.getAllCisBdpm()
        assert(cisBdpm.isEmpty())
    }

    @Test
    fun insertCisBdpmInDatabase() {
        // I prefer to create a new val cisBdpm than using defaultCis
        val cisBdpm = defaultCis
        CisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabase.size == 1)
        assert(cisBdpmFromDatabase[0].CodeCIS == cisBdpm.CodeCIS)
    }

    @Test
    fun insertCisBdpmInDatabaseWithSameId() {
        val cisBdpm = defaultCis
        CisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabase.size == 1)
        assert(cisBdpmFromDatabase[0].CodeCIS == cisBdpm.CodeCIS)
        CisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase2 = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabase2.size == 1)
        assert(cisBdpmFromDatabase2[0].CodeCIS == cisBdpm.CodeCIS)
    }

    @Test
    fun `update a cisBdpm`() {
        val cisBdpm = defaultCis
        CisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabase.size == 1)
        assert(cisBdpmFromDatabase[0].CodeCIS == cisBdpm.CodeCIS)
        val cisBdpmUpdated = cisBdpm.copy(denomination = "denominationUpdated")
        CisBdpmRepository.updateCisBdpm(cisBdpmUpdated)
        val cisBdpmFromDatabaseUpdated = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabaseUpdated.size == 1)
        assert(cisBdpmFromDatabaseUpdated[0].denomination == "denominationUpdated")
    }

    @Test
    fun `update a cisBdpm with wrong id`() {
        val cisBdpm = defaultCis
        CisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabase.size == 1)
        assert(cisBdpmFromDatabase[0].CodeCIS == cisBdpm.CodeCIS)
        val cisBdpmUpdated = cisBdpm.copy(CodeCIS = 22222222)
        CisBdpmRepository.updateCisBdpm(cisBdpmUpdated)
        val cisBdpmFromDatabaseUpdated = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabaseUpdated.size == 1)
        assert(cisBdpmFromDatabaseUpdated[0].CodeCIS == cisBdpm.CodeCIS)
    }

    @Test
    fun `delete a cisBdpm`() {
        val cisBdpm = defaultCis
        CisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabase.size == 1)
        assert(cisBdpmFromDatabase[0].CodeCIS == cisBdpm.CodeCIS)
        CisBdpmRepository.deleteCisBdpm(cisBdpm)
        val cisBdpmFromDatabaseDeleted = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabaseDeleted.isEmpty())
    }

    @Test
    fun `delete a cisBdpm with wrong id`() {
        val cisBdpm = defaultCis
        CisBdpmRepository.insertCisBdpm(cisBdpm)
        val cisBdpmFromDatabase = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabase.size == 1)
        assert(cisBdpmFromDatabase[0].CodeCIS == cisBdpm.CodeCIS)
        val cisBdpmWrongId = cisBdpm.copy(CodeCIS = 22222222)
        CisBdpmRepository.deleteCisBdpm(cisBdpmWrongId)
        val cisBdpmFromDatabaseDeleted = CisBdpmRepository.getOneCisBdpmById(cisBdpm.CodeCIS)
        assert(cisBdpmFromDatabaseDeleted.size == 1)
        assert(cisBdpmFromDatabaseDeleted[0].CodeCIS == cisBdpm.CodeCIS)
    }

    @Test
    fun `test insert from csv`() {
        // Warning : this test will fail if you don't have the csv file in the assets folder
        val context = ApplicationProvider.getApplicationContext<Context>()
        CisBdpmRepository.insertFromCsv(context)
        val cisBdpmFromDatabase = CisBdpmRepository.getAllCisBdpm()
        assert(cisBdpmFromDatabase.isNotEmpty())
    }


}










