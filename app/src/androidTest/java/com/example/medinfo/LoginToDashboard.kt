package com.example.medinfo

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@LargeTest
@RunWith(JUnit4::class)
class LoginToDashboard {
    @get:Rule
    val uiTestRule = ActivityScenarioRule(LoginActivity::class.java);

    @Test
    fun testLogin() {
        onView(withId(R.id.etEmail))
            .perform(clearText())
            .perform(typeText("ramshah@gmail.com"));
        Thread.sleep(1500);

        onView(withId(R.id.etPassword))
            .perform(clearText())
            .perform(typeText("ramshah"));
        Thread.sleep(1500);
        closeSoftKeyboard();

        onView(withId(R.id.btnLogin))
            .perform(click());

        Thread.sleep(2000);
    }
}