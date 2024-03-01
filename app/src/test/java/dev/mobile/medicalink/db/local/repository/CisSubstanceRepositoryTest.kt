package dev.mobile.medicalink.db.local.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.CisSubstance
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class CisSubstanceRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var cisSubstanceRepository: CisSubstanceRepository
    private val defaultCis = CisSubstance(
        "11111111",
        "elementPharmaceutique",
        666,
        "denominationSubstance",
        "dosageSubstance",
        "referenceDosage",
        "natureComposant",
        27
    )

    @Before
    fun setupDatabase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        cisSubstanceRepository = CisSubstanceRepository(db.cisSubstanceDao())
    }

    @After
    fun closeDatabase() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun `test if we can get all cisSubstance`() {
        // Should be empty
        val cisSubstance = cisSubstanceRepository.getAllCisSubstances()
        assertTrue(cisSubstance.isEmpty())
    }

    @Test
    fun insertCisSubstance() {
        val toAdd = defaultCis
        cisSubstanceRepository.insertCisSubstance(toAdd)
        val fromDb = cisSubstanceRepository.getOneCisSubstanceById(toAdd.codeCIS)
        assertNotNull(fromDb)
        assertEquals(toAdd.codeCIS, fromDb!!.codeCIS)
    }

    @Test
    fun `get all substance by codeSubstance`() {
        val toAdd = defaultCis
        val toAdd2 = defaultCis.copy(codeCIS = "22222222")
        val toAdd3 = defaultCis.copy(codeCIS = "33333333", codeSubstance = 777)
        cisSubstanceRepository.insertCisSubstance(toAdd)
        cisSubstanceRepository.insertCisSubstance(toAdd2)
        cisSubstanceRepository.insertCisSubstance(toAdd3)
        val fromDb = cisSubstanceRepository.getAllCisSubstancesByCodeSubstance(666)
        assertEquals(2, fromDb.size)
    }

    @Test
    fun `update a cisSubstance`() {
        val toAdd = defaultCis
        cisSubstanceRepository.insertCisSubstance(toAdd)
        val fromDb = cisSubstanceRepository.getOneCisSubstanceById(toAdd.codeCIS)
        assertNotNull(fromDb)
        assertEquals(toAdd.codeCIS, fromDb!!.codeCIS)

        val updated = fromDb.copy(denominationSubstance = "newDenomination")
        cisSubstanceRepository.updateCisSubstance(updated)
        val updatedFromDb = cisSubstanceRepository.getOneCisSubstanceById(toAdd.codeCIS)
        assertNotNull(updatedFromDb)
        assertEquals(updated.denominationSubstance, updatedFromDb!!.denominationSubstance)
    }

    @Test
    fun `update a cisSubstance that doesn't exist`() {
        val toUpdate = defaultCis
        cisSubstanceRepository.updateCisSubstance(toUpdate)

        // Comme Room ne renvoie pas d'erreur si on update un élément qui n'existe pas, on va vérifier qu'il n'a pas ajouter un élément à la place d'en update un
        // On vérifie donc qu'il n'y a pas d'effet de bord
        val fromDb = cisSubstanceRepository.getOneCisSubstanceById(toUpdate.codeCIS)
        assertNull(fromDb)
    }

    @Test
    fun `delete a cisSubstance`() {
        val toAdd = defaultCis
        cisSubstanceRepository.insertCisSubstance(toAdd)
        val fromDb = cisSubstanceRepository.getOneCisSubstanceById(toAdd.codeCIS)
        assertNotNull(fromDb)
        assertEquals(toAdd.codeCIS, fromDb!!.codeCIS)

        val result = cisSubstanceRepository.deleteCisSubstance(fromDb)
        assertTrue(result.first)
        assertEquals("Success", result.second)

        val deletedFromDb = cisSubstanceRepository.getOneCisSubstanceById(toAdd.codeCIS)
        assertNull(deletedFromDb)
    }

    @Test
    fun `test insert from csv`() {
        // Warning : this test will fail if you don't have the csv file in the assets folder
        val context = ApplicationProvider.getApplicationContext<Context>()
        cisSubstanceRepository.insertFromCsv(context)
        val cisSubstance = cisSubstanceRepository.getAllCisSubstances()
        assertFalse(cisSubstance.isEmpty())
    }


}















