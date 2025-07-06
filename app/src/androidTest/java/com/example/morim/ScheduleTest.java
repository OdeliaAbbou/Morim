package com.example.morim;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsEqual.equalTo;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.content.Intent;
import android.os.SystemClock;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;

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
public class ScheduleTest {

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    private ActivityScenario<AuthActivity> scenario;

    @Before
    public void setUp() {
// ניקוי Firebase state לפני כל טסט
        FirebaseAuth.getInstance().signOut();

// המתנה קצרה לוודא שה-signOut הושלם
        SystemClock.sleep(1000);

// יצירת Intent עם דגל למניעת redirect אוטומטי
        Intent intent = new Intent();
        intent.putExtra("LOGOUT", true);

// השקת Activity
        scenario = ActivityScenario.launch(AuthActivity.class);
    }

    @Test
    public void testUIElements_areDisplayedCorrectly() {
// בדיקה שכל רכיבי ה-UI מופיעים

// המתנה לטעינת Fragment
        SystemClock.sleep(2000);

// בדיקה שהשדות מופיעים
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

// בדיקה שה-ProgressBar מוסתר בהתחלה
        onView(withId(R.id.pbAuth))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void testValidLogin_withRealFirebaseUser() {
// המתנה לטעינת Fragment
        SystemClock.sleep(2000);

// שים משתמש אמיתי שקיים במערכת Firebase שלך
        String validEmail = "Odelia@gmail.com"; // שנה למייל אמיתי
        String validPassword = "01022000"; // שנה לסיסמה אמיתית

// הכנסת נתונים
        onView(withId(R.id.etEmailLogin))
                .perform(typeText(validEmail));

        onView(withId(R.id.etPasswordLogin))
                .perform(typeText(validPassword));

        Espresso.closeSoftKeyboard();

// לחיצה על כפתור Login
        onView(withId(R.id.btnLoginSubmit))
                .perform(click());

// בדיקה שה-ProgressBar מופיע (טעינה)
// onView(withId(R.id.pbAuth))
// .check(matches(isDisplayed()));

// המתנה ארוכה יותר לפעולה להסתיים (Firebase לוקח זמן)
        SystemClock.sleep(8000);

// בדיקה שה-ProgressBar נעלם (הפעולה הסתיימה)
// onView(withId(R.id.pbAuth))
// .check(matches(not(isDisplayed())));

// בדיקה שמופיע Toast עם הודעת הצלחה
// (הטסט יסתיים כאן כי האפליקציה תעבור ל-MainActivity)


// 1) Cliquer sur le premier item de la RecyclerView
        onView(withId(R.id.rvTeachers))
                .perform(actionOnItemAtPosition(0, click()));

// 2) Cliquer sur le bouton “Schedule” qui apparaît dans le détail du prof
        onView(withId(R.id.btnSchedule))
                .perform(click());

        // 2) Clique sur le “10” DANS la boîte de dialogue
        onView(withText("10"))
                .inRoot(isDialog())       // <--- s’assure qu’on regarde DANS le dialog
                .perform(click());
// 5) Dans le dialog des heures, cliquer sur “9:00 AM”
        onView(withText("9:00 AM"))
                .inRoot(isDialog())
                .perform(click());
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
                .perform(typeText("Tests"), closeSoftKeyboard());
// 9) Valider en cliquant sur “OK”
        onView(withText("OK"))
                .inRoot(isDialog())
                .perform(click());
        SystemClock.sleep(2000);

    }


       @Test
    public void testNavigateToRegister_clickWorks() { //senregistrer
// המתנה לטעינת Fragment
        SystemClock.sleep(2000);

// לחיצה על כפתור ההרשמה
        onView(withId(R.id.btnToRegister))
                .perform(click());

// כאן תוכל לבדוק שעבר לעמוד ההרשמה
// תלוי איך זה מוגדר בNavigation שלך
        SystemClock.sleep(2000);
    }


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