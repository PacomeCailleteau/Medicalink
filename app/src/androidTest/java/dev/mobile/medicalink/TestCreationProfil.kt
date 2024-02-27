package dev.mobile.medicalink


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
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
class TestCreationProfil {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testCreationProfil() {
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
                withId(R.id.radio_button_utilisateur), withText("Utilisateur"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group_statut),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            4
                        )
                    ),
                    1
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
        textInputEditText.perform(scrollTo(), click())

        val textInputEditText2 = onView(
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
        textInputEditText2.perform(scrollTo(), replaceText("OSS"), closeSoftKeyboard())

        val textInputEditText3 = onView(
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
        textInputEditText3.perform(scrollTo(), replaceText("Arthur"), closeSoftKeyboard())

        val textInputEditText4 = onView(
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
        textInputEditText4.perform(scrollTo(), click())

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
            .atPosition(101)
        materialTextView2.perform(scrollTo(), click())

        val appCompatImageButton = onView(
            allOf(
                withClassName(`is`("androidx.appcompat.widget.AppCompatImageButton")),
                withContentDescription("Mois suivant"),
                childAtPosition(
                    allOf(
                        withClassName(`is`("android.widget.DayPickerView")),
                        childAtPosition(
                            withClassName(`is`("com.android.internal.widget.DialogViewAnimator")),
                            0
                        )
                    ),
                    2
                )
            )
        )
        appCompatImageButton.perform(scrollTo(), click())

        val appCompatImageButton2 = onView(
            allOf(
                withClassName(`is`("androidx.appcompat.widget.AppCompatImageButton")),
                withContentDescription("Mois suivant"),
                childAtPosition(
                    allOf(
                        withClassName(`is`("android.widget.DayPickerView")),
                        childAtPosition(
                            withClassName(`is`("com.android.internal.widget.DialogViewAnimator")),
                            0
                        )
                    ),
                    2
                )
            )
        )
        appCompatImageButton2.perform(scrollTo(), click())

        val textInputEditText5 = onView(
            allOf(
                withId(R.id.input_nom), withText("OSS"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    8
                )
            )
        )
        textInputEditText5.perform(scrollTo(), replaceText("OSSELIN"))

        val textInputEditText6 = onView(
            allOf(
                withId(R.id.input_nom), withText("OSSELIN"),
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
        textInputEditText6.perform(closeSoftKeyboard())

        val textInputEditText7 = onView(
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
        textInputEditText7.perform(scrollTo(), click())

        val materialTextView3 = onView(
            allOf(
                withClassName(`is`("com.google.android.material.textview.MaterialTextView")),
                withText("Mar. 27 févr."),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialTextView3.perform(click())

        val materialTextView4 = onView(
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
        materialTextView4.perform(click())

        val materialTextView5 = onData(anything())
            .inAdapterView(
                allOf(
                    withClassName(`is`("android.widget.YearPickerView")),
                    childAtPosition(
                        withClassName(`is`("com.android.internal.widget.DialogViewAnimator")),
                        1
                    )
                )
            )
            .atPosition(101)
        materialTextView5.perform(scrollTo(), click())

        val appCompatImageButton3 = onView(
            allOf(
                withClassName(`is`("androidx.appcompat.widget.AppCompatImageButton")),
                withContentDescription("Mois suivant"),
                childAtPosition(
                    allOf(
                        withClassName(`is`("android.widget.DayPickerView")),
                        childAtPosition(
                            withClassName(`is`("com.android.internal.widget.DialogViewAnimator")),
                            0
                        )
                    ),
                    2
                )
            )
        )
        appCompatImageButton3.perform(scrollTo(), click())

        val appCompatImageButton4 = onView(
            allOf(
                withClassName(`is`("androidx.appcompat.widget.AppCompatImageButton")),
                withContentDescription("Mois suivant"),
                childAtPosition(
                    allOf(
                        withClassName(`is`("android.widget.DayPickerView")),
                        childAtPosition(
                            withClassName(`is`("com.android.internal.widget.DialogViewAnimator")),
                            0
                        )
                    ),
                    2
                )
            )
        )
        appCompatImageButton4.perform(scrollTo(), click())

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

        val textInputEditText8 = onView(
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
        textInputEditText8.perform(
            scrollTo(),
            replaceText("arthur.osselin09@orange.fr"),
            closeSoftKeyboard()
        )

        val textInputEditText9 = onView(
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
        textInputEditText9.perform(scrollTo(), replaceText("0000000"), closeSoftKeyboard())

        val textInputEditText10 = onView(
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
        textInputEditText10.perform(scrollTo(), replaceText("0000000"))

        val textInputEditText11 = onView(
            allOf(
                withId(R.id.input_password), withText("0000000"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        textInputEditText11.perform(closeSoftKeyboard())

        val textInputEditText12 = onView(
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
        textInputEditText12.perform(scrollTo(), replaceText("0000000"))

        val textInputEditText13 = onView(
            allOf(
                withId(R.id.input_password), withText("0000000"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        textInputEditText13.perform(closeSoftKeyboard())

        val textInputEditText14 = onView(
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
        textInputEditText14.perform(pressImeActionButton())

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
