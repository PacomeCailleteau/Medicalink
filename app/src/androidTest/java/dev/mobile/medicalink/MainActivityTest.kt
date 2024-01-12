package dev.mobile.medicalink


import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Before
    fun setUp() {
        // On initialise Intents avant chaque test
        Intents.init()
        // On lance notre application via sa première activité
        ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        // On libère Intents après chaque test
        Intents.release()
    }

    @Test
    fun testClickOnButtunCreateProfile() {
        // Simulation du clic sur le bouton de création de profiles (oui oui, son id est bien button_connexion, merci Pacôme)
        onView(withId(R.id.button_connexion)).perform(click())
        // Vérification que l'activité de création de profiles est lancée
        intended(hasComponent("dev.mobile.medicalink.CreerProfilActivity"))
    }

    @Test
    fun testTexteBienvue() {
        // Vérification que le texte de bienvenue est bien présent
        onView(withId(R.id.text_bienvenue)).check(matches(withText("Welcome on Medicalink !")))
    }

    @Test
    fun testPresenceImage () {
        // Vérification que l'image est bien présente
        onView(withId(R.id.image_connexion)).check(matches(isDisplayed()))
    }


}




