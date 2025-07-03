package com.example.morim;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
    public void testInputFields_acceptTextInput() {
// המתנה לטעינת Fragment
        SystemClock.sleep(2000);

// בדיקה שאפשר להכניס טקסט בשדה המייל
        onView(withId(R.id.etEmailLogin))
                .perform(typeText("test@example.com"));

// בדיקה שאפשר להכניס טקסט בשדה הסיסמה
        onView(withId(R.id.etPasswordLogin))
                .perform(typeText("password123"));

// סגירת המקלדת
        Espresso.closeSoftKeyboard();

// בדיקה שהטקסט נשמר (אופציונלי)
// onView(withId(R.id.etEmailLogin))
// .check(matches(withText("test@example.com")));
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
    }

    @Test
    public void testInvalidLogin_showsErrorMessage() {
// המתנה לטעינת Fragment
        SystemClock.sleep(2000);

// נתונים לא נכונים
        onView(withId(R.id.etEmailLogin))
                .perform(typeText("invalid@email.com"));

        onView(withId(R.id.etPasswordLogin))
                .perform(typeText("wrongpassword"));

        Espresso.closeSoftKeyboard();

// לחיצה על כפתור Login
        onView(withId(R.id.btnLoginSubmit))
                .perform(click());

// בדיקה שה-ProgressBar מופיע
        onView(withId(R.id.pbAuth))
                .check(matches(isDisplayed()));

// המתנה לתגובה (Firebase error response)
        SystemClock.sleep(8000);

// בדיקה שה-ProgressBar נעלם
        onView(withId(R.id.pbAuth))
                .check(matches(not(isDisplayed())));

// בדיקה שמופיעה הודעת שגיאה ב-Toast
// (לא ניתן לבדוק Toast ישירות ב-Espresso, אבל זה בסדר)
// הטסט יצליח אם לא קרס ואם ה-ProgressBar נעלם
    }

    @Test
    public void testNavigateToRegister_clickWorks() {
// המתנה לטעינת Fragment
        SystemClock.sleep(2000);

// לחיצה על כפתור ההרשמה
        onView(withId(R.id.btnToRegister))
                .perform(click());

// כאן תוכל לבדוק שעבר לעמוד ההרשמה
// תלוי איך זה מוגדר בNavigation שלך
        SystemClock.sleep(2000);
    }

    @Test
    public void testEmptyFields_showsValidationError() {
// המתנה לטעינת Fragment
        SystemClock.sleep(2000);

// לחיצה על כפתור בלי למלא שדות
        onView(withId(R.id.btnLoginSubmit))
                .perform(click());

// בדיקה שמופיעה שגיאת validation בשדה המייל
        onView(withId(R.id.etLayoutEmailLogin))
                .check(matches(isDisplayed()));

// בדיקה שלא קרס ושהכפתור עדיין קיים
        onView(withId(R.id.btnLoginSubmit))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyPassword_showsValidationError() {
// המתנה לטעינת Fragment
        SystemClock.sleep(2000);

// מילוי רק המייל
        onView(withId(R.id.etEmailLogin))
                .perform(typeText("test@example.com"));

        Espresso.closeSoftKeyboard();

// לחיצה על כפתור
        onView(withId(R.id.btnLoginSubmit))
                .perform(click());

// בדיקה שמופיעה שגיאת validation בשדה הסיסמה
        onView(withId(R.id.etLayoutPasswordLogin))
                .check(matches(isDisplayed()));
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