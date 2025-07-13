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
// המתנה לטעינת Fragment
        SystemClock.sleep(2000);
        onView(withId(R.id.pbAuth))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void testValidLogin_withRealFirebaseUser() {
// המתנה לטעינת Fragment
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

// 1) Cliquer sur le premier item de la RecyclerView
        onView(withId(R.id.rvTeachers))
                .perform(actionOnItemAtPosition(0, click()));

// 2) Cliquer sur le bouton “Schedule” qui apparaît dans le détail du prof
        onView(withId(R.id.btnSchedule))
                .perform(click());
        SystemClock.sleep(2000);


        // 2) Clique sur le “10” DANS la boîte de dialogue
        onView(withText("10"))
                .inRoot(isDialog())       // s’assure qu’on regarde DANS le dialog
                .perform(click());
        SystemClock.sleep(2000);

// 5) Dans le dialog des heures, cliquer sur “9:00 AM”
        onView(withText("12:00 AM"))
                .inRoot(isDialog())
                .perform(click());
        SystemClock.sleep(2000);

// 6) Valider le créneau en appuyant sur “SCHEDULE”
        onView(withText("SCHEDULE"))
                .inRoot(isDialog())
                .perform(click());
// 8) Taper “Tests” dans le champ de texte
        onView(allOf(
                instanceOf(EditText.class),
                isDisplayed()        // ou un autre matcher View pour cibler ton EditText
        ))
                .inRoot(isDialog())
                .perform(typeText("Tests 4"), closeSoftKeyboard());
// 9) Valider en cliquant sur “OK”
        onView(withText("OK"))
                .inRoot(isDialog())
                .perform(click());
        SystemClock.sleep(2000);

        /////////verifie
        // Clique sur le bouton Meetings dans la BottomNavigationView
        onView(withId(R.id.bottom_navigation))
                .perform(click());
        // Clique sur l’item myMeetingsFragment
        onView(allOf(withId(R.id.myMeetingsFragment), isDisplayed()))
                .perform(click());

        SystemClock.sleep(3000); // laisse le temps aux données de s'afficher

        // Vérifie qu’un meeting avec le texte "Tests" existe dans le RecyclerView
        onView(withText("Tests 4"))
                .check(matches(isDisplayed()));

    }

//    @Test
//    public void testMeetingAppearsInMyMeetings() {
//        // Clique sur le bouton Meetings dans la BottomNavigationView
//        onView(withId(R.id.bottom_navigation))
//                .perform(click());
//        // Clique sur l’item myMeetingsFragment
//        onView(allOf(withId(R.id.myMeetingsFragment), isDisplayed()))
//                .perform(click());
//
//        SystemClock.sleep(3000); // laisse le temps aux données de s'afficher
//
//        // Vérifie qu’un meeting avec le texte "Tests" existe dans le RecyclerView
//        onView(withText("Tests 2"))
//                .check(matches(isDisplayed()));
//    }
//


    @After
    public void tearDown() {
// ניקוי אחרי הטסט
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }

        if (scenario != null) {
            scenario.close();
        }
    }
}