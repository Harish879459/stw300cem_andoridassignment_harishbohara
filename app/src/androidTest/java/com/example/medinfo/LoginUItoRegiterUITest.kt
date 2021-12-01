package com.example.medinfo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@LargeTest
@RunWith(JUnit4::class)
class LoginUItoRegiterUITest {

    @get:Rule
    val uiTest = ActivityScenarioRule(LoginActivity::class.java);

    @Test
    fun gotoRegisterUITest() {
        Thread.sleep(1500);
        onView(withId(R.id.registerInfo)).perform(click());
        Thread.sleep(2000);
    }

}