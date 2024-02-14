package dev.mobile.medicalink


import android.widget.DatePicker
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
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
        onView(withId(R.id.text_bienvenue)).check(matches(withText("Welcome on Medicazelda !")))
    }

    @Test
    fun testPresenceImage() {
        // Vérification que l'image est bien présente
        onView(withId(R.id.image_connexion)).check(matches(isDisplayed()))
    }

    // Problème : j'ai pas trouvé comment refresh la base de données entre chaque test
    // Probleme2 : je sais pas comment faire pour avoir une base de données séparée pour les tests
    @Test
    fun testCreateUserFromMainActivity() {
        // Simulation du clic sur le bouton de création de profiles (oui oui, son id est bien button_connexion, merci Pacôme)
        onView(withId(R.id.button_connexion)).perform(click())
        // Vérification que l'activité de création de profiles est lancée
        intended(hasComponent("dev.mobile.medicalink.CreerProfilActivity"))

        // Une fois sur l'activité de création de profiles, on remplit les champs texte (nom, prenom, date de naissance, mail, mot de passe)
        onView(withId(R.id.input_nom)).perform(typeText("nom"))
        onView(withId(R.id.input_prenom)).perform(typeText("prenom"))
        onView(withId(R.id.input_email)).perform(typeText("a@b.c"))
        onView(withId(R.id.input_password)).perform(typeText("123456"))
        //On choisit la date de naissance
        onView(withId(R.id.input_date_de_debut)).perform(click())
        Thread.sleep(1000)
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(2000, 1, 1))
        onView(withText("OK")).perform(click())
        // On choisit un radio bouton pour le statut
        onView(withId(R.id.radio_button_utilisateur)).perform(click())

        closeSoftKeyboard()

        //On scroll jusqu'au bouton de création de profile, sinon, il n'est pas affiché donc on peut pas cliquer dessus
        onView(withId(R.id.button_creer_profil)).perform(ViewActions.scrollTo(), click())

        //On accepte le RGPD
        onView(withId(R.id.checkbox_rgpd)).perform(click())

        // Maintenant que tous les champs sont remplis, on clique sur le bouton de création de profile
        onView(withId(R.id.button_creer_profil)).perform(click())
        // On vérifie que l'on est bien sur l'activité principale
        intended(hasComponent("dev.mobile.medicalink.MainActivity"))

        // On a un compte alors le texte devait avoir changé
        onView(withId(R.id.text_bienvenue)).check(matches(withText("Welcome prenom !")))
    }


}




