package dev.mobile.medicalink


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CreationCompte_CreationManuelleTraitement_Prise_Traitement {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun creationCompte_CreationManuelleTraitement_Prise_Traitement() {
        val appCompatButton = onView(
            allOf(
                withId(R.id.button_connexion), withText("Créer mon profil"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val materialRadioButton = onView(
            allOf(
                withId(R.id.radio_button_professionnel), withText("Professionnel de santé"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group_statut),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            4
                        )
                    ),
                    0
                )
            )
        )
        materialRadioButton.perform(scrollTo(), click())

        val textInputEditText = onView(
            allOf(
                withId(R.id.input_nom),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    8
                )
            )
        )
        textInputEditText.perform(scrollTo(), replaceText("CL"), closeSoftKeyboard())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.input_prenom),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    11
                )
            )
        )
        textInputEditText2.perform(scrollTo(), replaceText("Jean-Ma"), closeSoftKeyboard())

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.input_nom), withText("CL"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    8
                )
            )
        )
        textInputEditText3.perform(scrollTo(), click())

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.input_nom), withText("CL"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    8
                )
            )
        )
        textInputEditText4.perform(scrollTo(), replaceText("CLEKA"))

        val textInputEditText5 = onView(
            allOf(
                withId(R.id.input_nom), withText("CLEKA"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        textInputEditText5.perform(closeSoftKeyboard())

        val textInputEditText6 = onView(
            allOf(
                withId(R.id.input_date_de_debut),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    12
                )
            )
        )
        textInputEditText6.perform(scrollTo(), click())

        val materialTextView = onView(
            allOf(
                withClassName(`is`("com.google.android.material.textview.MaterialTextView")),
                withText("2024"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())

        val materialTextView2 = onData(anything())
            .inAdapterView(
                allOf(
                    withClassName(`is`("android.widget.YearPickerView")),
                    childAtPosition(
                        withClassName(`is`("com.android.internal.widget.DialogViewAnimator")),
                        1
                    )
                )
            )
            .atPosition(82)
        materialTextView2.perform(scrollTo(), click())

        val materialTextView3 = onData(anything())
            .inAdapterView(
                allOf(
                    withClassName(`is`("android.widget.YearPickerView")),
                    childAtPosition(
                        withClassName(`is`("com.android.internal.widget.DialogViewAnimator")),
                        1
                    )
                )
            )
            .atPosition(82)
        materialTextView3.perform(scrollTo(), click())

        val materialButton = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        materialButton.perform(scrollTo(), click())

        val textInputEditText7 = onView(
            allOf(
                withId(R.id.input_prenom), withText("Jean-Ma"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    11
                )
            )
        )
        textInputEditText7.perform(scrollTo(), replaceText("Jean-Marie"))

        val textInputEditText8 = onView(
            allOf(
                withId(R.id.input_prenom), withText("Jean-Marie"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    11
                ),
                isDisplayed()
            )
        )
        textInputEditText8.perform(closeSoftKeyboard())

        val textInputEditText9 = onView(
            allOf(
                withId(R.id.input_email),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    3
                )
            )
        )
        textInputEditText9.perform(
            scrollTo(),
            replaceText("jm.cleka@mssante.fr"),
            closeSoftKeyboard()
        )

        val textInputEditText10 = onView(
            allOf(
                withId(R.id.input_password),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    7
                )
            )
        )
        textInputEditText10.perform(scrollTo(), replaceText("0000000"), closeSoftKeyboard())

        val materialCheckBox = onView(
            allOf(
                withId(R.id.checkbox_rgpd),
                withText("J'accepte les conditions en matière de protection des données relatives au RGPD"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    10
                )
            )
        )
        materialCheckBox.perform(scrollTo(), click())

        val textInputEditText11 = onView(
            allOf(
                withId(R.id.input_password), withText("000000"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    7
                )
            )
        )
        textInputEditText11.perform(pressImeActionButton())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.button_creer_profil), withText("Créer mon profil"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    6
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.button_connexion), withText("Me connecter"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton3.perform(click())

        val linearLayout = onView(
            allOf(
                withId(R.id.btnTraitementsNav),
                childAtPosition(
                    allOf(
                        withId(R.id.fragmentDuBas),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        linearLayout.perform(click())

        val linearLayout2 = onView(
            allOf(
                withId(R.id.btnTraitementsNav),
                childAtPosition(
                    allOf(
                        withId(R.id.fragmentDuBas),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        linearLayout2.perform(click())

        val linearLayout3 = onView(
            allOf(
                withId(R.id.cardaddtraitements),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        linearLayout3.perform(click())

        val linearLayout4 = onView(
            allOf(
                withId(R.id.cardaddmanually),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        linearLayout4.perform(click())

        val textInputEditText12 = onView(
            allOf(
                withId(R.id.add_manually_search_bar),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_manuel_search),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText12.perform(replaceText("des"), closeSoftKeyboard())

        val textInputEditText13 = onView(
            allOf(
                withId(R.id.add_manually_search_bar), withText("des"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_manuel_search),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText13.perform(click())

        val textInputEditText14 = onView(
            allOf(
                withId(R.id.add_manually_search_bar), withText("des"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_manuel_search),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText14.perform(replaceText("desl"))

        val textInputEditText15 = onView(
            allOf(
                withId(R.id.add_manually_search_bar), withText("desl"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_manuel_search),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText15.perform(closeSoftKeyboard())

        val recyclerView = onView(
            allOf(
                withId(R.id.recyclerViewSearch),
                childAtPosition(
                    withId(R.id.constraint_layout_ajout_manuel_search),
                    4
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val appCompatButton4 = onView(
            allOf(
                withId(R.id.add_manually_button), withText("Ajouter"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_manuel_search),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton4.perform(click())

        val appCompatButton5 = onView(
            allOf(
                withId(R.id.suivant1), withText("Suivant"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton5.perform(click())

        val appCompatButton6 = onView(
            allOf(
                withId(R.id.suivant1), withText("Suivant"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatButton6.perform(click())

        val appCompatButton7 = onView(
            allOf(
                withId(R.id.btn_add_nouvelle_prise), withText("Ajouter une nouvelle prise"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_shema_prise_2),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton7.perform(click())

        val linearLayout5 = onView(
            allOf(
                withId(R.id.supprimerlayout),
                childAtPosition(
                    allOf(
                        withId(R.id.layout_item_recap),
                        childAtPosition(
                            withId(R.id.recyclerViewAjoutPrise),
                            1
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        linearLayout5.perform(click())

        val textInputEditText16 = onView(
            allOf(
                withId(R.id.heurePriseInput), withText("17:00"),
                childAtPosition(
                    allOf(
                        withId(R.id.layout_item_recap),
                        childAtPosition(
                            withId(R.id.recyclerViewAjoutPrise),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText16.perform(click())

        val materialButton2 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        materialButton2.perform(scrollTo(), click())

        pressBack()

        pressBack()

        val appCompatButton8 = onView(
            allOf(
                withId(R.id.button_connexion), withText("Me connecter"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton8.perform(click())

        val linearLayout6 = onView(
            allOf(
                withId(R.id.btnTraitementsNav),
                childAtPosition(
                    allOf(
                        withId(R.id.fragmentDuBas),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        linearLayout6.perform(click())

        val linearLayout7 = onView(
            allOf(
                withId(R.id.cardtraitements),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        linearLayout7.perform(click())

        val appCompatImageView = onView(
            allOf(
                withId(R.id.annulerListeEffetsSecondaires),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(click())

        val linearLayout8 = onView(
            allOf(
                withId(R.id.cardaddtraitements),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        linearLayout8.perform(click())

        val linearLayout9 = onView(
            allOf(
                withId(R.id.cardaddmanually),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        linearLayout9.perform(click())

        val textInputEditText17 = onView(
            allOf(
                withId(R.id.add_manually_search_bar),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_manuel_search),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText17.perform(replaceText("deslo"), closeSoftKeyboard())

        val textInputEditText18 = onView(
            allOf(
                withId(R.id.add_manually_search_bar), withText("deslo"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_manuel_search),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText18.perform(click())

        val textInputEditText19 = onView(
            allOf(
                withId(R.id.add_manually_search_bar), withText("deslo"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_manuel_search),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText19.perform(replaceText("deslo"))

        val textInputEditText20 = onView(
            allOf(
                withId(R.id.add_manually_search_bar), withText("deslo"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_manuel_search),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText20.perform(closeSoftKeyboard())

        val recyclerView2 = onView(
            allOf(
                withId(R.id.recyclerViewSearch),
                childAtPosition(
                    withId(R.id.constraint_layout_ajout_manuel_search),
                    4
                )
            )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val recyclerView3 = onView(
            allOf(
                withId(R.id.recyclerViewSearch),
                childAtPosition(
                    withId(R.id.constraint_layout_ajout_manuel_search),
                    4
                )
            )
        )
        recyclerView3.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val appCompatButton9 = onView(
            allOf(
                withId(R.id.add_manually_button), withText("Ajouter"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_manuel_search),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton9.perform(click())

        val appCompatButton10 = onView(
            allOf(
                withId(R.id.suivant1), withText("Suivant"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton10.perform(click())

        val appCompatButton11 = onView(
            allOf(
                withId(R.id.suivant1), withText("Suivant"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatButton11.perform(click())

        val textInputEditText21 = onView(
            allOf(
                withId(R.id.heurePriseInput), withText("17:00"),
                childAtPosition(
                    allOf(
                        withId(R.id.layout_item_recap),
                        childAtPosition(
                            withId(R.id.recyclerViewAjoutPrise),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText21.perform(click())

        val materialButton3 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        materialButton3.perform(scrollTo(), click())

        val appCompatButton12 = onView(
            allOf(
                withId(R.id.suivant1), withText("Suivant"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraint_layout_ajout_shema_prise_2),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton12.perform(click())

        val appCompatButton13 = onView(
            allOf(
                withId(R.id.suivant1), withText("Suivant"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        appCompatButton13.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.stockActuelInput), withText("25"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout3),
                        childAtPosition(
                            withId(R.id.constraintStock),
                            5
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("60"))

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.stockActuelInput), withText("60"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout3),
                        childAtPosition(
                            withId(R.id.constraintStock),
                            5
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(closeSoftKeyboard())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.jourRappelInput), withText("7 jours"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout3),
                        childAtPosition(
                            withId(R.id.constraintStock),
                            5
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(click())

        val appCompatButton14 = onView(
            allOf(
                withId(R.id.prendreButton), withText("OK"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout8),
                        childAtPosition(
                            withId(android.R.id.custom),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton14.perform(click())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.jourRappelInput), withText("7 jours"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout3),
                        childAtPosition(
                            withId(R.id.constraintStock),
                            5
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(longClick())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.heureRappelInput), withText("09:00"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout3),
                        childAtPosition(
                            withId(R.id.constraintStock),
                            5
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(click())

        val appCompatButton15 = onView(
            allOf(
                withId(R.id.suivant1), withText("Suivant"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintStock),
                        childAtPosition(
                            withId(R.id.FL),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton15.perform(click())

        val appCompatButton16 = onView(
            allOf(
                withId(R.id.suivant1), withText("Confirmer"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.FL),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton16.perform(click())

        val linearLayout10 = onView(
            allOf(
                withId(R.id.btnAccueilNav),
                childAtPosition(
                    allOf(
                        withId(R.id.fragmentDuBas),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        linearLayout10.perform(click())

        val appCompatImageView2 = onView(
            allOf(
                withId(R.id.circleTick),
                childAtPosition(
                    allOf(
                        withId(R.id.layout_item_recap),
                        childAtPosition(
                            withId(R.id.recyclerViewHome),
                            1
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatImageView2.perform(click())

        val constraintLayout = onView(
            allOf(
                withId(R.id.prendreLinear),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout3),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            7
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        constraintLayout.perform(click())

        val linearLayout11 = onView(
            allOf(
                withId(R.id.btnAccueilNav),
                childAtPosition(
                    allOf(
                        withId(R.id.fragmentDuBas),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        linearLayout11.perform(click())

        val linearLayout12 = onView(
            allOf(
                withId(R.id.btnTraitementsNav),
                childAtPosition(
                    allOf(
                        withId(R.id.fragmentDuBas),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        linearLayout12.perform(click())

        val linearLayout13 = onView(
            allOf(
                withId(R.id.btnAccueilNav),
                childAtPosition(
                    allOf(
                        withId(R.id.fragmentDuBas),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        linearLayout13.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
