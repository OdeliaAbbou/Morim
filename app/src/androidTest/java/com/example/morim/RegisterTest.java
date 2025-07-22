package com.example.morim;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.morim.CustomViewActions.setImageFromAssets;
import static com.example.morim.CustomViewActions.setTextInTextView;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;

import com.example.morim.databinding.FragmentTeacherDetailsBinding;
import com.example.morim.model.Location;

import android.content.Intent;
import android.os.SystemClock;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ApplicationProvider;
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
import com.example.morim.ui.dialog.TeacherDetailsDialog;

import java.lang.reflect.Field;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterTest {

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    private ActivityScenario<AuthActivity> scenario;

    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
        SystemClock.sleep(1000);

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                AuthActivity.class
        );
        intent.putExtra("LOGOUT", true);
        scenario = ActivityScenario.launch(intent);
    }

    @Test
    public void testRegisterAsStudent_withTestImage() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

        onView(withId(R.id.ivRegisterUserImage))
                .perform(setImageFromAssets("testImage.jpg"));

        onView(withId(R.id.etFullNameRegister))
                .perform(scrollTo(), typeText("TestStudent"), closeSoftKeyboard());
        onView(withId(R.id.etAddressRegister))
                .perform(scrollTo(), typeText("123 Rue Exemple"), closeSoftKeyboard());
        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), typeText("0612345678"), closeSoftKeyboard());
        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("student15@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etPasswordRegister))
                .perform(scrollTo(), typeText("password123"), closeSoftKeyboard());

        onView(withId(R.id.typeStudent))
                .perform(scrollTo(), click());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        SystemClock.sleep(10000);

        // 🔍 Vérifie que le nom de l'utilisateur s'affiche après la connexion
        onView(withId(R.id.titleMorim))
                .check(matches(isDisplayed()))
                .check(matches(withText("Hi TestStudent")));

        // 🔓 LOGOUT après le test réussi pour préparer le test suivant
        performLogout();
    }

    @Test
    public void testRegisterAsTeacher_withTestImage() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

        onView(withId(R.id.ivRegisterUserImage))
                .perform(setImageFromAssets("testImage.jpg"));

        onView(withId(R.id.etFullNameRegister))
                .perform(scrollTo(), typeText("TestTeacher"), closeSoftKeyboard());
        onView(withId(R.id.etAddressRegister))
                .perform(scrollTo(), typeText("123 Rue Exemple"), closeSoftKeyboard());
        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), typeText("0612345678"), closeSoftKeyboard());
        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("teacher13@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etPasswordRegister))
                .perform(scrollTo(), typeText("password123"), closeSoftKeyboard());

        onView(withId(R.id.typeTeacher))
                .perform(scrollTo(), click());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        SystemClock.sleep(3000);

        onView(withId(R.id.subjectSpinnerLayout))
                .perform(click());
        SystemClock.sleep(3000);

        onView(withText("Cooking"))
                .inRoot(isPlatformPopup())
                .perform(click());
        SystemClock.sleep(3000);

        //////////////localisation
        onView(withId(R.id.autocomplete_fragment))
                .perform(click());
        SystemClock.sleep(1000);
        onView(withClassName(endsWith("EditText")))
                .perform(typeText("Tel Aviv"), closeSoftKeyboard());
        SystemClock.sleep(3000);

        try {
            // Essayer d'abord avec "Tel-Aviv" (format avec tiret)
            onView(withText("Tel-Aviv"))
                    .perform(click());
        } catch (Exception e1) {
            try {
                // Si ça ne marche pas, essayer avec containsString
                onView(withText(containsString("Tel-Aviv")))
                        .perform(click());
            } catch (Exception e2) {
                try {
                    // Alternative : sélectionner par position (premier élément)
                    onView(withText(containsString("Tel")))
                            .perform(click());
                } catch (Exception e3) {
                    // Dernier recours : utiliser un sélecteur générique
                    onView(allOf(
                            isDisplayed(),
                            withText(containsString("Tel"))
                    )).perform(click());
                }
            }
        }

        SystemClock.sleep(5000);

        onView(withId(R.id.etEducationDetails))
                .perform(typeText("Master in Math"));

        onView(withId(R.id.etPrice))
                .perform(typeText("150"));

        closeSoftKeyboard();

        SystemClock.sleep(3000);

        onView(withId(R.id.btnSaveChangesDialog))
                .perform(click());

        SystemClock.sleep(10000);

        // 🔍 Vérifie que le nom de l'utilisateur s'affiche après la connexion
        onView(withId(R.id.titleMorim))
                .check(matches(isDisplayed()))
                .check(matches(withText("Hi TestTeacher")));

        // 🔓 LOGOUT après le test réussi pour préparer le test suivant
        performLogout();
    }

    /**
     * Méthode utilitaire pour effectuer la déconnexion
     * Assure un état propre entre les tests
     */
    private void performLogout() {
        try {
            // Option 1: Déconnexion via Firebase Auth (programmativement)
            FirebaseAuth.getInstance().signOut();
            SystemClock.sleep(2000);

            // Option 2: Si votre app a un bouton de logout dans l'UI, vous pouvez l'utiliser
            // Par exemple, si vous avez un menu ou un bouton de déconnexion :
            /*
            try {
                // Ouvrir le menu/profil
                onView(withId(R.id.btnProfile)) // ou l'ID de votre bouton profil
                        .perform(click());

                SystemClock.sleep(1000);

                // Cliquer sur logout
                onView(withId(R.id.btnLogout)) // ou l'ID de votre bouton logout
                        .perform(click());

                SystemClock.sleep(2000);
            } catch (Exception e) {
                // Si l'UI logout échoue, utiliser Firebase Auth
                FirebaseAuth.getInstance().signOut();
            }
            */

            // Fermer l'activité actuelle et redémarrer AuthActivity
            if (scenario != null) {
                scenario.close();
            }

            // Relancer AuthActivity pour le test suivant
            Intent intent = new Intent(
                    ApplicationProvider.getApplicationContext(),
                    AuthActivity.class
            );
            intent.putExtra("LOGOUT", true);
            scenario = ActivityScenario.launch(intent);

            SystemClock.sleep(2000);

        } catch (Exception e) {
            // En cas d'erreur, assurer au minimum la déconnexion Firebase
            FirebaseAuth.getInstance().signOut();
            SystemClock.sleep(1000);
        }
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