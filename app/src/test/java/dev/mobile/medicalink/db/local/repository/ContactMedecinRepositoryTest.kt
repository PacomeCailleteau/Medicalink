package dev.mobile.medicalink.db.local.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.ContactMedecin
import dev.mobile.medicalink.db.local.entity.User
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ContactMedecinRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var contactMedecinRepository: ContactMedecinRepository
    private val defaultContactMedecin =
        ContactMedecin("1", "1", "prenom", "nom", "specialite", "email", "0123456789", "ici", "44444", "ville", "M")
    private val defaultContactMedecin2 =
        ContactMedecin("2", "2", "prenom2", "nom2", "specialite2", "email2", "9876543210", "la", "55555", "ville2", "F")
    private val userOfDefaultMedoc =
        User("1", "Utilisateur", "test", "test", "test", "a@b.c", "test", false)
    private val userOfDefaultMedoc2 =
        User("2", "Utilisateur", "test2", "test2", "test2", "a@b.c", "test2", true)


    @Before
    fun setupDatabase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        contactMedecinRepository = ContactMedecinRepository(db.contactMedecinDao())

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
    fun `test if we can get all contactMedecins`() {
        // Should be empty
        val contactMedecins = contactMedecinRepository.getAllContactMedecins()
        assertTrue(contactMedecins.isEmpty())
    }

    @Test
    fun insertContactMedecin() {
        // I prefer to create a new val contactMedecin than using defaultContactMedecin
        val contactMedecin = defaultContactMedecin
        contactMedecinRepository.insertContactMedecin(contactMedecin)
        val contactMedecinFromDatabase = contactMedecinRepository.getOneContactMedecinById(contactMedecin.rpps)
        assertNotNull(contactMedecinFromDatabase)
        assertEquals(contactMedecinFromDatabase?.rpps, contactMedecin.rpps)
    }

    @Test
    fun getContactMedecinByUserUuid() {
        val contactMedecin = defaultContactMedecin
        val contactMedecin2 = defaultContactMedecin2
        contactMedecinRepository.insertContactMedecin(contactMedecin)
        contactMedecinRepository.insertContactMedecin(contactMedecin2)
        val contactMedecins = contactMedecinRepository.getContactMedecinByUserUuid(contactMedecin.userUuid)
        assertEquals(1, contactMedecins.size)
        assertEquals(contactMedecins[0].rpps, contactMedecin.rpps)
    }

    @Test
    fun `update a contact medecin`() {
        val toAdd = defaultContactMedecin
        contactMedecinRepository.insertContactMedecin(toAdd)
        val fromDb = contactMedecinRepository.getOneContactMedecinById(toAdd.rpps)
        assertNotNull(fromDb)
        assertEquals(toAdd.rpps, fromDb!!.rpps)

        val updated = fromDb.copy(email = "ff@ff.ff")
        contactMedecinRepository.updateContactMedecin(updated)
        val updatedFromDb = contactMedecinRepository.getOneContactMedecinById(updated.rpps)
        assertNotNull(updatedFromDb)
        assertEquals(updated.email, updatedFromDb!!.email)
    }

    @Test
    fun `update a contact medecin that doesn't exist`() {
        val toUpdate = defaultContactMedecin
        contactMedecinRepository.updateContactMedecin(toUpdate)
        // Comme Room ne renvoie pas d'erreur si on update un élément qui n'existe pas, on va vérifier qu'il n'a pas ajouter un élément à la place d'en update un
        // On vérifie donc qu'il n'y a pas d'effet de bord
        val fromDb = contactMedecinRepository.getOneContactMedecinById(toUpdate.rpps)
        assertNull(fromDb)
    }

    @Test
    fun `delete a contact medecin`() {
        val toAdd = defaultContactMedecin
        contactMedecinRepository.insertContactMedecin(toAdd)
        val fromDb = contactMedecinRepository.getOneContactMedecinById(toAdd.rpps)
        assertNotNull(fromDb)
        assertEquals(toAdd.rpps, fromDb!!.rpps)

        val res = contactMedecinRepository.deleteContactMedecin(fromDb)
        assertTrue(res.first)
        assertEquals("Success", res.second)

        val deletedDromDb = contactMedecinRepository.getOneContactMedecinById(toAdd.rpps)
        assertNull(deletedDromDb)
    }



}











