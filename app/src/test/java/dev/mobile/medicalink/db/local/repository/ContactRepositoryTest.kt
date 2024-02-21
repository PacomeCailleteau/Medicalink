package dev.mobile.medicalink.db.local.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Contact
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


// NE FONCTIONNE PAS SUR LES PC DE L'IUT
//NE FONCTIONNE PAS QUAND ON RUN AVEC LE COVERAGE (je sais pas pourquoi)
//enlever les commentaires du @Config permet de retirer le warning de unknown chunk type 200
@RunWith(AndroidJUnit4::class)
//@Config(sdk = [29])
//@SmallTest
class ContactRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var contactRepository: ContactRepository
    private val defaultContact =
        Contact(
            uuid = "1",
            rpps =  10101080173,
            firstName = "Jean-Marie",
            address = "RUE DES PALIS",
            city = "MAZE",
            fullName = "Dr. Jean-Marie CLEKA",
            lastName = "CLEKA",
            phoneNumber = "",
            specialty = "Spécialiste en Médecine Générale",
            zipcode = "49630",
            email = "jmc@test.fr")

    private val defaultContact2 =
        Contact(
            uuid = "2",
            rpps = 10000401033,
            firstName = "Marlène",
            address = "11 Route du Château",
            city = "MAZE",
            lastName = "Berteau-Mevel",
            fullName = "Marlène Berteau-Mevel",
            phoneNumber = "02 41 66 17 90",
            specialty = "Sage-femme",
            zipcode = "49630",
            email = "mbv@test.fr"
        )


    @Before
    fun setupDatabase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        contactRepository = ContactRepository(db.contactDao())
    }

    @After
    fun closeDatabase() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun `test if we can get all contacts`() {
        // Should be empty
        val contacts = contactRepository.getAllContact()
        assert(contacts.isEmpty())
    }

    @Test
    fun insertContactInDatabase() {
        // I prefer to create a new val contact than using defaultContact
        val contact = defaultContact
        contactRepository.insertContact(contact)
        val contactFromDatabase = contactRepository.getOneContactById(contact.uuid,contact.rpps)
        assert(contactFromDatabase?.uuid == contact.uuid)
    }

    @Test
    fun insertContactInDatabaseWithSameId() {
        val contact = defaultContact
        val contact2 = defaultContact.copy(firstName = "copyTest", lastName = "copyTest")
        contactRepository.insertContact(contact)
        val res = contactRepository.insertContact(contact2)
        assert(!res.first)
        assert(res.second == "Contact already exists")
    }



    @Test
    fun `test update contact`() {
        val contact = defaultContact
        contactRepository.insertContact(contact)
        //Get the contact from database
        val contactFromDatabase = contactRepository.getOneContactById(contact.uuid,contact.rpps)
        assert(contactFromDatabase?.uuid == contact.uuid)
        //Update the contact
        val contactToUpdate = contactFromDatabase?.copy(firstName = "testUpdate")
        val res = contactRepository.updateContact(contactToUpdate!!)
        assert(res.first)
        assert(res.second == "Success")
        //Get the contact from database after update
        val contactFromDatabaseAfterUpdate = contactRepository.getOneContactById(contact.uuid,contact.rpps)
        assert(contactFromDatabaseAfterUpdate?.uuid == contact.uuid)
        assert(contactFromDatabaseAfterUpdate?.lastName == "testUpdate")
    }

    @Test
    fun `test update contact that doesn't exist (same as update contact but with a wrong id)`() {
        //the update method doesn't fail if the contact doesn't exist, it just doesn't update anything
        val contact = defaultContact
        contactRepository.updateContact(contact)
        //Get the contact from database
        val contactFromDatabase = contactRepository.getOneContactById(contact.uuid,contact.rpps)
        if (contactFromDatabase != null) {
            assert(contactFromDatabase.equals(null))
        }
    }

    @Test
    fun `test delete contact`() {
        val contact = defaultContact
        contactRepository.insertContact(contact)
        //Get the contact from database
        val contactFromDatabase = contactRepository.getOneContactById(contact.uuid,contact.rpps)
        assert(contactFromDatabase?.uuid == contact.uuid)
        //Delete the contact that we just get from database
        val res = contactFromDatabase?.let { contactRepository.deleteContact(it) }
        if (res != null) {
            assert(res.first)
            assert(res.second == "Success")
        }
        //Get the contact from database after delete
        val contactFromDatabaseAfterDelete = contactRepository.getOneContactById(contact.uuid,contact.rpps)
        assert(contactFromDatabaseAfterDelete!!.equals(null))
    }

    @Test
    fun `test delete contact that doesn't exist`() {
        //the delete method doesn't fail if the contact doesn't exist, it just doesn't delete anything
        val contact = defaultContact
        val contact2 = defaultContact2
        contactRepository.insertContact(contact)
        //Delete contact2 that doesn't exist in database
        val res = contactRepository.deleteContact(contact2)
        assert(res.first)
        assert(res.second == "Success")
        //Get the contact from database after delete to verify that contact is still here
        val contactFromDatabaseAfterDelete = contactRepository.getOneContactById(contact.uuid,contact.rpps)
        assert(contactFromDatabaseAfterDelete!!.uuid == contact.uuid)
    }



}








