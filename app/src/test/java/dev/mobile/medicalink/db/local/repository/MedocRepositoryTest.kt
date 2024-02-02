package dev.mobile.medicalink.db.local.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.entity.User
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
//@Config(sdk = [29])
//@SmallTest
class MedocRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var medocRepository: MedocRepository
    private val userOfDefaultMedoc =
        User("1", "Utilisateur", "test", "test", "test", "a@b.c", "test", false)
    private val userOfDefaultMedoc2 =
        User("2", "Utilisateur", "test2", "test2", "test2", "a@b.c", "test2", true)
    private val defaultMedoc = Medoc(
        "1",
        "1",
        null,
        "nom",
        "dosageNB",
        "dosageUnite",
        "dateFinTraitement",
        "typeComprime",
        1,
        true,
        "effetsSecondaires",
        "prises",
        1,
        "dateDbtTraitement"
    )
    private val defaultMedoc2 = Medoc(
        "2",
        "2",
        null,
        "nom2",
        "dosageNB2",
        "dosageUnite2",
        "dateFinTraitement2",
        "typeComprime2",
        2,
        false,
        "effetsSecondaires2",
        "prises2",
        2,
        "dateDbtTraitement2"
    )

    @Before
    fun setupDatabase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        medocRepository = MedocRepository(db.medocDao())

        //First we'll insert a user in the database
        //It's because we need a user to insert a medoc
        // !!! WARNING !!! We need to be sure that UserRepository has been tested before
        val userRepository = UserRepository(db.userDao())
        userRepository.insertUser(userOfDefaultMedoc)
        userRepository.insertUser(userOfDefaultMedoc2)
    }

    @After
    fun closeDatabase() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun `test if we can get all medoc`() {
        // Should be empty
        val medoc = medocRepository.getAllMedocs()
        assert(medoc.isEmpty())
    }

    @Test
    fun insertMedocInDatabase() {
        // I prefer to create a new val medoc than using defaultMedoc
        val medoc = defaultMedoc
        val res = medocRepository.insertMedoc(medoc)
        println(res)
        assert(res.first)
        assert(res.second == "Success")
        val medocFromDatabase = medocRepository.getOneMedocById(medoc.uuid)
        assert(medocFromDatabase.size == 1)
        assert(medocFromDatabase[0].uuid == medoc.uuid)
    }

    @Test
    fun insertMedocInDatabaseWithSameId() {
        val medoc = defaultMedoc
        medocRepository.insertMedoc(medoc)
        val medocFromDatabase = medocRepository.getOneMedocById(medoc.uuid)
        assert(medocFromDatabase.size == 1)
        assert(medocFromDatabase[0].uuid == medoc.uuid)
    }

    @Test
    fun `get all medoc by user id`() {
        val medoc = defaultMedoc
        val medoc2 = defaultMedoc2
        medocRepository.insertMedoc(medoc)
        medocRepository.insertMedoc(medoc2)
        val medocs = medocRepository.getAllMedocByUserId(medoc.uuidUser)
        assert(medocs.size == 1)
        assert(medocs[0].uuidUser == medoc.uuidUser)
    }

    @Test
    fun `get all medoc by user id with no medoc`() {
        val medoc = defaultMedoc
        val medoc2 = defaultMedoc2
        medocRepository.insertMedoc(medoc)
        medocRepository.insertMedoc(medoc2)
        val medocs = medocRepository.getAllMedocByUserId("3")
        assert(medocs.isEmpty())
    }

    @Test
    fun `update a medoc`() {
        val medoc = defaultMedoc
        medocRepository.insertMedoc(medoc)
        val medocFromDatabase = medocRepository.getOneMedocById(medoc.uuid)
        assert(medocFromDatabase.size == 1)
        assert(medocFromDatabase[0].uuid == medoc.uuid)
        val medocUpdated = medoc.copy(nom = "nomUpdated")
        medocRepository.updateMedoc(medocUpdated)
        val medocFromDatabaseUpdated = medocRepository.getOneMedocById(medoc.uuid)
        assert(medocFromDatabaseUpdated.size == 1)
        assert(medocFromDatabaseUpdated[0].nom == "nomUpdated")
    }

    @Test
    fun `update a medoc with wrong id`() {
        val medoc = defaultMedoc
        medocRepository.insertMedoc(medoc)
        val medocFromDatabase = medocRepository.getOneMedocById(medoc.uuid)
        assert(medocFromDatabase.size == 1)
        assert(medocFromDatabase[0].uuid == medoc.uuid)
        val medocUpdated = medoc.copy(uuid = "3")
        medocRepository.updateMedoc(medocUpdated)
        val medocFromDatabaseUpdated = medocRepository.getOneMedocById(medoc.uuid)
        assert(medocFromDatabaseUpdated.size == 1)
        assert(medocFromDatabaseUpdated[0].uuid == medoc.uuid)
    }

    @Test
    fun `delete a medoc`() {
        val medoc = defaultMedoc
        medocRepository.insertMedoc(medoc)
        val medocFromDatabase = medocRepository.getOneMedocById(medoc.uuid)
        assert(medocFromDatabase.size == 1)
        assert(medocFromDatabase[0].uuid == medoc.uuid)
        medocRepository.deleteMedoc(medocFromDatabase[0])
        val medocFromDatabaseAfterDelete = medocRepository.getOneMedocById(medoc.uuid)
        assert(medocFromDatabaseAfterDelete.isEmpty())
    }

    @Test
    fun `delete a medoc that doesn't exist`() {
        val medoc = defaultMedoc
        val medoc2 = defaultMedoc2
        medocRepository.insertMedoc(medoc)
        val medocFromDatabase = medocRepository.getOneMedocById(medoc.uuid)
        assert(medocFromDatabase.size == 1)
        assert(medocFromDatabase[0].uuid == medoc.uuid)
        medocRepository.deleteMedoc(medoc2)
        val medocFromDatabaseAfterDelete = medocRepository.getOneMedocById(medoc.uuid)
        assert(medocFromDatabaseAfterDelete.size == 1)
        assert(medocFromDatabaseAfterDelete[0].uuid == medoc.uuid)
    }


}