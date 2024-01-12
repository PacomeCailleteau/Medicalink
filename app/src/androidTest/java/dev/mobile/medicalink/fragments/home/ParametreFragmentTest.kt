package dev.mobile.medicalink.fragments.home

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.mobile.medicalink.MainActivity
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.MainFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParametreFragmentTest {

    @Before
    fun setUp() {
        // Lancez l'activité avant chaque test
        ActivityScenario.launch(MainFragment::class.java)
    }

    @Test
    fun testClickOnButtunDeconnexion() {
        // Cliquez sur le bouton de déconnexion
        onView(withId(R.id.cardDeconnexion)).perform(click())
        // Vérifiez que l'activité de connexion est lancée
        intended(hasComponent("dev.mobile.medicalink.MainActivity"))
    }
}
