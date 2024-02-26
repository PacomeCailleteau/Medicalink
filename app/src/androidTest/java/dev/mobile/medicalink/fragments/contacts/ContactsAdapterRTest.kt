package dev.mobile.medicalink.fragments.contacts

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dev.mobile.medicalink.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.entity.Contact
import junit.framework.TestCase.assertEquals
import org.junit.Before

@LargeTest
@RunWith(AndroidJUnit4::class)
class ContactsAdapterRTest {

    private lateinit var listeContact : MutableList<Contact>


    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init(){
        listeContact = mutableListOf()
    }

    @Test
    fun emptyList(){
        assertEquals(ContactsAdapterR(listeContact, onItemClick = {} ).itemCount, 0)
    }

    // On vérifie que la barre du main fragment est visible et que les boutons sont cliquables

    // On vérifie avec une liste de contacts nulle

    // On vérifie avec une liste de contact contenant 1 contact

    // On vérifie avec une liste de contact contenant 3 contacts

}