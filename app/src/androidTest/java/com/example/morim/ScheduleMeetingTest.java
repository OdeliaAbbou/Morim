package com.example.morim;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.content.Intent;
import android.os.SystemClock;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;
import static androidx.test.espresso.contrib.NavigationViewActions.navigateTo;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScheduleMeetingTest {

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
    public void testProgressBar_isHiddenInitially() {
        SystemClock.sleep(2000);
        onView(withId(R.id.pbAuth))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void ScheduleMeetingAndVerifyIt() {
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

        onView(withId(R.id.rvTeachers))
                .perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.btnSchedule))
                .perform(click());
        SystemClock.sleep(2000);


        onView(withText("31"))
                .inRoot(isDialog())
                .perform(click());
        SystemClock.sleep(4000);

        onView(withText("22:00 PM"))
                .inRoot(isDialog())
                .perform(click());
        SystemClock.sleep(2000);

        onView(withText("SCHEDULE"))
                .inRoot(isDialog())
                .perform(click());
        onView(allOf(
                instanceOf(EditText.class),
                isDisplayed()
        ))
                .inRoot(isDialog())
                .perform(typeText("Tests 4"), closeSoftKeyboard());
        onView(withText("OK"))
                .inRoot(isDialog())
                .perform(click());
        SystemClock.sleep(2000);

        onView(withId(R.id.bottom_navigation))
                .perform(click());
        SystemClock.sleep(500);

        onView(allOf(withId(R.id.myMeetingsFragment), isDisplayed()))
                .perform(click());


        SystemClock.sleep(3000);

        onView(withText("Tests 4"))
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