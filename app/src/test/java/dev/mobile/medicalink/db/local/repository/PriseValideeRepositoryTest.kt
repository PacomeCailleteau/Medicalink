package dev.mobile.medicalink.db.local.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.PriseValidee
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
//@Config(sdk = [29])
//@SmallTest
class PriseValideeRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var priseValideeRepository: PriseValideeRepository
    private val defaultPriseValidee = PriseValidee("1", "2021-01-01", "1", "statut")
    private val defaultPriseValidee2 = PriseValidee("2", "2021-02-02", "2", "statut2")

    @Before
    fun setupDatabase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        priseValideeRepository = PriseValideeRepository(db.priseValideeDao())
    }

    @After
    fun closeDatabase() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun `test if we can get all priseValidee`() {
        // Should be empty
        val priseValidee = priseValideeRepository.getAllPriseValidee()
        assert(priseValidee.isEmpty())
    }

    @Test
    fun insertPriseValideeInDatabase() {
        // I prefer to create a new val priseValidee than using defaultPriseValidee
        val priseValidee = defaultPriseValidee
        priseValideeRepository.insertPriseValidee(priseValidee)
        val priseValideeFromDatabase =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabase.size == 1)
        assert(priseValideeFromDatabase[0].uuid == priseValidee.uuid)
    }

    @Test
    fun insertPriseValideeInDatabaseWithSameId() {
        val priseValidee = defaultPriseValidee
        priseValideeRepository.insertPriseValidee(priseValidee)
        val priseValideeFromDatabase =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabase.size == 1)
        assert(priseValideeFromDatabase[0].uuid == priseValidee.uuid)
        val res = priseValideeRepository.insertPriseValidee(priseValidee)
        assert(!res.first)
        assert(res.second == "PriseValidee already exists")
    }

    @Test
    fun `test if we can get one priseValidee by id`() {
        val priseValidee = defaultPriseValidee
        priseValideeRepository.insertPriseValidee(priseValidee)
        val priseValideeFromDatabase =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabase.size == 1)
        assert(priseValideeFromDatabase[0].uuid == priseValidee.uuid)
    }

    @Test
    fun `test if we can get one priseValidee by uuidTraitement and date`() {
        val priseValidee = defaultPriseValidee
        priseValideeRepository.insertPriseValidee(priseValidee)
        val priseValideeFromDatabase = priseValideeRepository.getByUUIDTraitementAndDate(
            priseValidee.date,
            priseValidee.uuidPrise
        )
        assert(priseValideeFromDatabase.size == 1)
        assert(priseValideeFromDatabase[0].uuid == priseValidee.uuid)
    }

    @Test
    fun `update a priseValidee`() {
        val priseValidee = defaultPriseValidee
        priseValideeRepository.insertPriseValidee(priseValidee)
        val priseValideeFromDatabase =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabase.size == 1)
        assert(priseValideeFromDatabase[0].uuid == priseValidee.uuid)
        val priseValideeUpdated = priseValidee.copy(statut = "statutUpdated")
        priseValideeRepository.updatePriseValidee(priseValideeUpdated)
        val priseValideeFromDatabaseUpdated =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabaseUpdated.size == 1)
        assert(priseValideeFromDatabaseUpdated[0].statut == "statutUpdated")
    }

    @Test
    fun `update a priseValidee with wrong id`() {
        val priseValidee = defaultPriseValidee
        priseValideeRepository.insertPriseValidee(priseValidee)
        val priseValideeFromDatabase =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabase.size == 1)
        assert(priseValideeFromDatabase[0].uuid == priseValidee.uuid)
        val priseValideeUpdated = priseValidee.copy(uuid = "22222222")
        priseValideeRepository.updatePriseValidee(priseValideeUpdated)
        val priseValideeFromDatabaseUpdated =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabaseUpdated.size == 1)
        assert(priseValideeFromDatabaseUpdated[0].uuid == priseValidee.uuid)
    }

    @Test
    fun `delete a priseValidee`() {
        val priseValidee = defaultPriseValidee
        priseValideeRepository.insertPriseValidee(priseValidee)
        val priseValideeFromDatabase =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabase.size == 1)
        assert(priseValideeFromDatabase[0].uuid == priseValidee.uuid)
        priseValideeRepository.deletePriseValidee(priseValideeFromDatabase[0])
        val priseValideeFromDatabaseAfterDelete =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabaseAfterDelete.isEmpty())
    }

    @Test
    fun `delete a priseValidee with wrong id`() {
        val priseValidee = defaultPriseValidee
        priseValideeRepository.insertPriseValidee(priseValidee)
        val priseValideeFromDatabase =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabase.size == 1)
        assert(priseValideeFromDatabase[0].uuid == priseValidee.uuid)
        val priseValideeWrongId = priseValidee.copy(uuid = "22222222")
        priseValideeRepository.deletePriseValidee(priseValideeWrongId)
        val priseValideeFromDatabaseAfterDelete =
            priseValideeRepository.getOnePriseValideeById(priseValidee.uuid)
        assert(priseValideeFromDatabaseAfterDelete.size == 1)
        assert(priseValideeFromDatabaseAfterDelete[0].uuid == priseValidee.uuid)
    }

}