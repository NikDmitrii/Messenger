package app.nik.messenger

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule



@RunWith(AndroidJUnit4::class)
class AuthFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testAuthFragmentViews() {
        // Проверяем, что элементы пользовательского интерфейса присутствуют и видимы
        onView(withId(R.id.code_picker)).check(matches(isDisplayed()))
        onView(withId(R.id.number_text)).check(matches(isDisplayed()))
        onView(withId(R.id.verify_code)).check(matches(isDisplayed()))
        onView(withId(R.id.info_text)).check(matches(isDisplayed()))
        onView(withId(R.id.send_btn)).check(matches(isDisplayed()))
        onView(withId(R.id.verify_btn)).check(matches(isDisplayed()))
        onView(withId(R.id.resend_btn)).check(matches(isDisplayed()))

          // Проверяем начальное состояние кнопок
        onView(withId(R.id.verify_btn)).check(matches(not(isEnabled())))
        onView(withId(R.id.resend_btn)).check(matches(not(isEnabled())))
    }
}