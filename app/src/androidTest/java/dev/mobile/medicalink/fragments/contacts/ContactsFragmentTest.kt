package dev.mobile.medicalink.fragments.contacts

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dev.mobile.medicalink.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import dev.mobile.medicalink.R
import junit.framework.TestCase.assertEquals


@RunWith(AndroidJUnit4::class)
@LargeTest
class ContactsFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // On vérifie que la barre du main fragment est visible et que les boutons sont cliquables

    @Test
    fun isFragmentLaunched(){
        onView(withId(R.layout.fragment_contacts)).check(matches(isDisplayed()))
    }

    @Test
    fun areElementsInTheRightState() {
        onView(withId(R.id.creerContact)).check(matches(isDisplayed()))
        onView(withId(R.id.titreContact)).check(matches(isDisplayed()))
        onView(withId(R.id.btnTraitementsNav)).check(matches(isDisplayed()))
        onView(withId(R.id.btnAccueilNav)).check(matches(isDisplayed()))
        onView(withId(R.id.btnMessagesNav)).check(matches(isDisplayed()))

    }

    // On vérifie avec une liste de contacts nulle

    @Test
    fun isListEmpty(){
        assertEquals(ContactsAdapterR(listOf(), onItemClick = {} ).itemCount, 0)
    }

    @Test
    fun add1Contact(){
        // On clique sur la recherche de médecin
        onView(withId(R.id.creerContact)).perform(click())
        // On écrit cléka dans la barre de recherche
        onView(withId(R.layout.item_search_contact)).perform(typeText("cleka"), click())

        // On séléctionne le premier dans la liste
        onView(withId(R.id.recyclerViewSearch)).perform(click())
        // On vérifie que l'email du médecin corresond bien
        onView(withId(R.id.mailMedecin)).check(matches(withText("jean-marie.cleka@medecin.mssante?fr")))
        // On confirme l'insertion du médecin
        onView(withId(R.id.confirmer)).perform(click())
        // On vérifie que la taille de la recyclerView est à 1
        onView(withId(R.id.creerContact)).perform(click())
    }



    // On vérifie avec une liste de contact contenant 1 contact

    // On vérifie avec une liste de contact contenant 3 contacts


}

