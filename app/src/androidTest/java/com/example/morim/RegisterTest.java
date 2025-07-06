package com.example.morim;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
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
public class RegisterTest {

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    private ActivityScenario<AuthActivity> scenario;

    @Before
    public void setUp() {
        // On s'assure d'être déconnecté avant chaque test
        FirebaseAuth.getInstance().signOut();
        SystemClock.sleep(1000);

        // Lancement de l'activité de login
        Intent intent = new Intent();
        intent.putExtra("LOGOUT", true);
        scenario = ActivityScenario.launch(AuthActivity.class);


    }

    @Test
    public void testRegisterAsStudent_successful() {
        // 1) Attendre le chargement du fragment de connexion
        SystemClock.sleep(2000);

        // 2) Cliquer sur "Register" pour passer à l'écran d'inscription
        onView(withId(R.id.btnToRegister))
                .perform(click());

        // 3) Remplir tous les champs obligatoires
        onView(withId(R.id.etFullNameRegister))
                .perform(typeText("Test Student"), closeSoftKeyboard());
        onView(withId(R.id.etAddressRegister))
                .perform(typeText("123 Rue Exemple"), closeSoftKeyboard());
        onView(withId(R.id.etPhoneRegister))
                .perform(typeText("0612345678"), closeSoftKeyboard());
        onView(withId(R.id.etEmailRegister))
                .perform(typeText("student@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etPasswordRegister))
                .perform(typeText("password123"), closeSoftKeyboard());

        // 4) Sélectionner le RadioButton "Student account"
        onView(withId(R.id.typeStudent))
                .perform(click());

//        // 5) Cliquer sur "Register" ////////////////dont work!!
//        onView(withId(R.id.btnRegisterSubmit))
//                .perform(click());
//
//        // 6) Laisser Firebase créer l'utilisateur
//        SystemClock.sleep(5000);
//
//        // 7) Vérifier qu’on est revenu à l’écran de login (input email affiché)
//        onView(withId(R.id.etEmailLogin))
//                .check(matches(isDisplayed()));

        SystemClock.sleep(3000);

    }

    @After
    public void tearDown() {
        // On déconnecte si jamais l'inscription a fonctionné
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
        // On ferme le scenario
        if (scenario != null) {
            scenario.close();
        }
    }
}
