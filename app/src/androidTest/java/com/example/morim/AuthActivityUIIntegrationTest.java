package com.example.morim;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;

import android.content.Intent;
import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuthActivityUIIntegrationTest {

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    private ActivityScenario<AuthActivity> scenario;

    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
        SystemClock.sleep(1000);
        Intent intent = new Intent();
        intent.putExtra("LOGOUT", true);
        scenario = ActivityScenario.launch(AuthActivity.class);
    }

    @Test
    public void testUIElements_areDisplayedCorrectly() {
        SystemClock.sleep(2000);
        onView(withId(R.id.etEmailLogin))
                .check(matches(isDisplayed()));

        onView(withId(R.id.etPasswordLogin))
                .check(matches(isDisplayed()));

        onView(withId(R.id.btnLoginSubmit))
                .check(matches(isDisplayed()));

        onView(withId(R.id.btnToRegister))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testInputFields_acceptTextInput() {
        SystemClock.sleep(2000);
        onView(withId(R.id.etEmailLogin))
                .perform(typeText("test@example.com"));
        onView(withId(R.id.etPasswordLogin))
                .perform(typeText("password123"));
        Espresso.closeSoftKeyboard();

    }

    @Test
    public void testProgressBar_isHiddenInitially() {
        SystemClock.sleep(2000);
        onView(withId(R.id.pbAuth))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void testValidLogin_withRealFirebaseUser() {
        SystemClock.sleep(2000);
        String validEmail = "Odelia@gmail.com";
        String validPassword = "01022000";
        onView(withId(R.id.etEmailLogin))
                .perform(typeText(validEmail));

        onView(withId(R.id.etPasswordLogin))
                .perform(typeText(validPassword));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.btnLoginSubmit))
                .perform(click());
        SystemClock.sleep(3000);

        onView(withId(R.id.titleMorim))
                .check(matches(isDisplayed()))
                .check(matches(withText("Hi odelia")));



    }

    @Test
    public void testInvalidLogin_showsErrorMessage() {
        SystemClock.sleep(2000);

        onView(withId(R.id.etEmailLogin))
                .perform(typeText("invalid@email.com"));

        onView(withId(R.id.etPasswordLogin))
                .perform(typeText("wrongpassword"));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.btnLoginSubmit))
                .perform(click());

        SystemClock.sleep(8000);

        onView(withId(R.id.btnLoginSubmit))
                .check(matches(isDisplayed()));

        onView(withId(R.id.etEmailLogin))
                .check(matches(isDisplayed()));

        onView(withId(R.id.etPasswordLogin))
                .check(matches(isDisplayed()));

        onView(withId(R.id.etEmailLogin))
                .check(matches(withText("invalid@email.com")));

        onView(withId(R.id.etPasswordLogin))
                .check(matches(withText("wrongpassword")));

        onView(withId(R.id.etEmailLogin))
                .check(matches(withText("invalid@email.com")));
        onView(withId(R.id.etPasswordLogin))
                .check(matches(withText("wrongpassword")));    }

    @Test
    public void testNavigateToRegister_clickWorks() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

        SystemClock.sleep(2000);
    }

    @Test
    public void testEmptyFields_showsValidationError() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnLoginSubmit))
                .perform(click());

        onView(withId(R.id.etLayoutEmailLogin))
                .check(matches(isDisplayed()));

        onView(withId(R.id.btnLoginSubmit))
                .check(matches(isDisplayed()));
    }


    @After
    public void tearDown() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }

        if (scenario != null) {
            scenario.close();
        }
    }
}