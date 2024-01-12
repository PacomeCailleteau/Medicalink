package dev.mobile.medicalink.db.local.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.User
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
class UserRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var userRepository: UserRepository
    private val defaultUser = User("1", "Utilisateur", "test", "test", "test", "a@b.c", "test", false)
    private val defaultUser2 = User("2", "Utilisateur", "test2", "test2", "test2", "a@b.c", "test2", true)

    @Before
    fun setupDatabase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        userRepository = UserRepository(db.userDao())
    }

    @After
    fun closeDatabase() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun `test if we can get all users`() {
        // Should be empty
        val users = userRepository.getAllUsers()
        assert(users.isEmpty())
    }

    @Test
    fun insertUserInDatabase() {
        // I prefer to create a new val user than using defaultUser
        val user = defaultUser
        userRepository.insertUser(user)
        val userFromDatabase = userRepository.getOneUserById(user.uuid)
        assert(userFromDatabase.size == 1)
        assert(userFromDatabase[0].uuid == user.uuid)
    }

    @Test
    fun insertUserInDatabaseWithSameId() {
        val user = defaultUser
        val user2 = defaultUser.copy(prenom = "copyTest", nom = "copyTest")
        userRepository.insertUser(user)
        val res = userRepository.insertUser(user2)
        assert(!res.first)
        assert(res.second == "User already exists")
    }

    @Test
    fun `get all connected users (can't be more than one in our app)`() {
        val user = defaultUser
        val user2 = defaultUser2
        userRepository.insertUser(user)
        userRepository.insertUser(user2)
        val connectedUsers = userRepository.getUsersConnected()
        assert(connectedUsers.size == 1)
        assert(connectedUsers[0].uuid == user2.uuid)
    }

    @Test
    fun `check if a password is valid (it only check for the connected user)`() {
        // As the method isValidPassword only check for the connected user, we need to add a connected user in database
        // use defaultUser2 because it's the only one connected
        val user = defaultUser2
        userRepository.insertUser(user)
        // Check if the password is valid
        val isValid = userRepository.isValidPassword("test2")
        println(isValid)
        assert(isValid.first)
        assert(isValid.second == "Success")
    }

    @Test
    fun `check if a password is valid (it only check for the connected user) with wrong password`() {
        // As the method isValidPassword only check for the connected user, we need to add a connected user in database
        // use defaultUser2 because it's the only one connected
        val user = defaultUser2
        userRepository.insertUser(user)
        // Check if the password is valid
        val isValid = userRepository.isValidPassword("wrongPassword")
        assert(!isValid.first)
        assert(isValid.second == "Success") //The second one is about the error message of the database but here it's a success
    }

    @Test
    fun `check if a password is valid (it only check for the connected user) with no connected user`() {
        // Check if the password is valid
        val isValid = userRepository.isValidPassword("ça va pas marcher héhé :)")
        println(isValid)
        assert(!isValid.first)
        assert(isValid.second == "Unknown Error : List is empty.")
    }

    @Test
    fun `test update user`() {
        val user = defaultUser
        userRepository.insertUser(user)
        //Get the user from database
        val userFromDatabase = userRepository.getOneUserById(user.uuid)
        assert(userFromDatabase.size == 1)
        assert(userFromDatabase[0].uuid == user.uuid)
        //Update the user
        val userToUpdate = userFromDatabase[0].copy(nom = "testUpdate")
        val res = userRepository.updateUser(userToUpdate)
        assert(res.first)
        assert(res.second == "Success")
        //Get the user from database after update
        val userFromDatabaseAfterUpdate = userRepository.getOneUserById(user.uuid)
        assert(userFromDatabaseAfterUpdate.size == 1)
        assert(userFromDatabaseAfterUpdate[0].uuid == user.uuid)
        assert(userFromDatabaseAfterUpdate[0].nom == "testUpdate")
    }

    @Test
    fun `test update user that doesn't exist (same as update user but with a wrong id)`() {
        //the update method doesn't fail if the user doesn't exist, it just doesn't update anything
        val user = defaultUser
        userRepository.updateUser(user)
        //Get the user from database
        val userFromDatabase = userRepository.getOneUserById(user.uuid)
        assert(userFromDatabase.isEmpty())
    }

    @Test
    fun `test delete user`() {
        val user = defaultUser
        userRepository.insertUser(user)
        //Get the user from database
        val userFromDatabase = userRepository.getOneUserById(user.uuid)
        assert(userFromDatabase.size == 1)
        assert(userFromDatabase[0].uuid == user.uuid)
        //Delete the user that we just get from database
        val res = userRepository.deleteUser(userFromDatabase[0])
        assert(res.first)
        assert(res.second == "Success")
        //Get the user from database after delete
        val userFromDatabaseAfterDelete = userRepository.getOneUserById(user.uuid)
        assert(userFromDatabaseAfterDelete.isEmpty())
    }

    @Test
    fun `test delete user that doesn't exist`() {
        //the delete method doesn't fail if the user doesn't exist, it just doesn't delete anything
        val user = defaultUser
        val user2 = defaultUser2
        userRepository.insertUser(user)
        //Delete user2 that doesn't exist in database
        val res = userRepository.deleteUser(user2)
        assert(res.first)
        assert(res.second == "Success")
        //Get the user from database after delete to verify that user is still here
        val userFromDatabaseAfterDelete = userRepository.getOneUserById(user.uuid)
        assert(userFromDatabaseAfterDelete.size == 1)
        assert(userFromDatabaseAfterDelete[0].uuid == user.uuid)
    }

    @Test
    fun `test setConnected` () {
        //This function is used to set the connected user, it also set not connected all the other users
        val user = defaultUser
        val user2 = defaultUser2
        userRepository.insertUser(user)
        userRepository.insertUser(user2)
        //Set user connected
        //It should also set user2 not connected
        val res = userRepository.setConnected(user)
        assert(res.first)
        assert(res.second == "Success")
        //Verify that user is connected
        val userFromDatabase = userRepository.getOneUserById(user.uuid)
        assert(userFromDatabase.size == 1)
        assert(userFromDatabase[0].uuid == user.uuid)
        userFromDatabase[0].isConnected?.let { assert(it) }
        //Verify that user2 is not connected
        val user2FromDatabase = userRepository.getOneUserById(user2.uuid)
        assert(user2FromDatabase.size == 1)
        assert(user2FromDatabase[0].uuid == user2.uuid)
        user2FromDatabase[0].isConnected?.let { assert(!it) }
    }



}








