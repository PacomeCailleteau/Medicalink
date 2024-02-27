package dev.mobile.medicalink.fragments

import android.util.Log
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dev.mobile.medicalink.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner


@LargeTest
@RunWith(RobolectricTestRunner::class)
class MainFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    val activityController = buildActivity(
        MainActivity::class.java
    ).setup()

    val activity = activityController.get()        // Get the fragment (assuming we just have one)

    val fragment = activity
        .supportFragmentManager
        .fragments

    @Test
    fun isFragmentLaunched(){
        Log.d("FRAGMENTS",fragment.toString())
    }

}