package com.example.morim;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.morim.CustomViewActions.setImageFromAssets;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;

import com.example.morim.databinding.FragmentTeacherDetailsBinding;
import com.example.morim.model.Location;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.morim.ui.dialog.TeacherDetailsDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;



@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterTest {

    private Uri testImageUri;
    private Context context;


    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    private ActivityScenario<AuthActivity> scenario;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        createTestImageInternalStorage();


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

//        onView(withId(R.id.ivRegisterUserImage))
//                .perform(setImageFromAssets("testImage.jpg"));
        setRealImageToFragment();

        onView(withId(R.id.etFullNameRegister))
                .perform(scrollTo(), typeText("TestStudent555555555555"), closeSoftKeyboard());
        onView(withId(R.id.etAddressRegister))
                .perform(scrollTo(), typeText("123 Rue Exemple"), closeSoftKeyboard());
        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), typeText("0612345678"), closeSoftKeyboard());
        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("student21@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etPasswordRegister))
                .perform(scrollTo(), typeText("password123"), closeSoftKeyboard());

        onView(withId(R.id.typeStudent))
                .perform(scrollTo(), click());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        SystemClock.sleep(10000);

//        updateUserImageUrlInFirebase("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTM7wiGvv8RAkW5iV0CpLaqOM2pnpCINJa4aQ&s");
//        SystemClock.sleep(2000);

        onView(withId(R.id.titleMorim))
                .check(matches(isDisplayed()))
                .check(matches(withText("Hi TestStudent555555555555")));

        performLogout();
    }

    @Test
    public void testRegisterAsTeacher_withTestImage() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

//        onView(withId(R.id.ivRegisterUserImage))
//                .perform(setImageFromAssets("testImage.jpg"));
        setRealImageToFragment();


        onView(withId(R.id.etFullNameRegister))
                .perform(scrollTo(), typeText("testImage55"), closeSoftKeyboard());
        onView(withId(R.id.etAddressRegister))
                .perform(scrollTo(), typeText("123 Rue Exemple"), closeSoftKeyboard());
        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), typeText("0612345678"), closeSoftKeyboard());
        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("teacher2@example.com"), closeSoftKeyboard());
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
            onView(withText("Tel-Aviv"))
                    .perform(click());
        } catch (Exception e1) {
            try {
                onView(withText(containsString("Tel-Aviv")))
                        .perform(click());
            } catch (Exception e2) {
                try {
                    onView(withText(containsString("Tel")))
                            .perform(click());
                } catch (Exception e3) {
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
                .perform(typeText("300"));

        closeSoftKeyboard();

        SystemClock.sleep(3000);

        onView(withId(R.id.btnSaveChangesDialog))
                .perform(click());

        SystemClock.sleep(10000);

//        updateUserImageUrlInFirebase("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTM7wiGvv8RAkW5iV0CpLaqOM2pnpCINJa4aQ&s");
//        SystemClock.sleep(2000);


        onView(withId(R.id.titleMorim))
                .check(matches(isDisplayed()))
                .check(matches(withText("Hi testImage55")));

        performLogout();
    }

    private void performLogout() {
        try {
            FirebaseAuth.getInstance().signOut();
            SystemClock.sleep(2000);

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
            FirebaseAuth.getInstance().signOut();
            SystemClock.sleep(1000);
        }
    }

    @Test
    public void testRegisterValidationErrors() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister)).perform(click());

        onView(withId(R.id.btnRegisterSubmit)).perform(scrollTo(), click());
        onView(withId(R.id.etLayoutEmailAddressRegister))
                .check(matches(hasTextInputLayoutErrorText("Email must not be empty!")));

        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("test@example.com"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit)).perform(scrollTo(), click());
        onView(withId(R.id.etLayoutPasswordRegister))
                .check(matches(hasTextInputLayoutErrorText("Password must not be empty!")));

        onView(withId(R.id.etPasswordRegister))
                .perform(scrollTo(), typeText("password123"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit)).perform(scrollTo(), click());
        onView(withId(R.id.etLayoutFullNameRegister))
                .check(matches(hasTextInputLayoutErrorText("Full name must not be empty!")));

        onView(withId(R.id.etFullNameRegister))
                .perform(scrollTo(), typeText("John Doe"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit)).perform(scrollTo(), click());
        onView(withId(R.id.etLayoutAddressRegister))
                .check(matches(hasTextInputLayoutErrorText("Address must not be empty!")));

        onView(withId(R.id.etAddressRegister))
                .perform(scrollTo(), typeText("123 Main Street"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit)).perform(scrollTo(), click());
        onView(withId(R.id.etLayoutPhoneRegister))
                .check(matches(hasTextInputLayoutErrorText("Phone must not be empty!")));

        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), typeText("123456"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit)).perform(scrollTo(), click());
        onView(withId(R.id.etLayoutPhoneRegister))
                .check(matches(hasTextInputLayoutErrorText("Invalid phone number!")));

        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), replaceText("0612345678"), closeSoftKeyboard());
        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), replaceText("invalidemail"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit)).perform(scrollTo(), click());

        onView(withId(R.id.etLayoutEmailAddressRegister))
                .check(matches(hasTextInputLayoutErrorText("Invalid email format!")));
    }

    @Test
    public void testImageSelection() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

//        onView(withId(R.id.ivRegisterUserImage))
//                .perform(setImageFromAssets("testImage.jpg"));
        setRealImageToFragment();

        SystemClock.sleep(2000);

        // verif img not pas null
        onView(withId(R.id.ivRegisterUserImage))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUserTypeSelection() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

        onView(withId(R.id.typeStudent))
                .perform(scrollTo(), click());

        onView(withId(R.id.typeTeacher))
                .perform(scrollTo(), click());

        onView(withId(R.id.typeStudent))
                .perform(scrollTo(), click());

        onView(withId(R.id.typeStudent))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testPhoneValidationSpecificCases() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etPasswordRegister))
                .perform(scrollTo(), typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.etFullNameRegister))
                .perform(scrollTo(), typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etAddressRegister))
                .perform(scrollTo(), typeText("123 Main Street"), closeSoftKeyboard());

        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), typeText("123"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        onView(withId(R.id.etLayoutPhoneRegister))
                .check(matches(hasTextInputLayoutErrorText("Invalid phone number!")));

        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), replaceText("12345678901"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        onView(withId(R.id.etLayoutPhoneRegister))
                .check(matches(hasTextInputLayoutErrorText("Invalid phone number!")));

        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), replaceText("06123abcde"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        onView(withId(R.id.etLayoutPhoneRegister))
                .check(matches(hasTextInputLayoutErrorText("Invalid phone number!")));
    }

    @Test
    public void testEmailValidationSpecificCases() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

        onView(withId(R.id.etPasswordRegister))
                .perform(scrollTo(), typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.etFullNameRegister))
                .perform(scrollTo(), typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etAddressRegister))
                .perform(scrollTo(), typeText("123 Main Street"), closeSoftKeyboard());
        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), typeText("0612345678"), closeSoftKeyboard());

        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("testexample.com"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        onView(withId(R.id.etLayoutEmailAddressRegister))
                .check(matches(hasTextInputLayoutErrorText("Invalid email format!")));

        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), replaceText("test@"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        onView(withId(R.id.etLayoutEmailAddressRegister))
                .check(matches(hasTextInputLayoutErrorText("Invalid email format!")));

        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), replaceText("test@example"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        onView(withId(R.id.etLayoutEmailAddressRegister))
                .check(matches(hasTextInputLayoutErrorText("Invalid email format!")));
    }

    @Test
    public void testRegisterAsStudentWithoutImage() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

        onView(withId(R.id.etFullNameRegister))
                .perform(scrollTo(), typeText("StudentNoImage"), closeSoftKeyboard());
        onView(withId(R.id.etAddressRegister))
                .perform(scrollTo(), typeText("456 Test Street"), closeSoftKeyboard());
        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), typeText("0698765432"), closeSoftKeyboard());
        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("studentnoimage1@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etPasswordRegister))
                .perform(scrollTo(), typeText("password123"), closeSoftKeyboard());

        onView(withId(R.id.typeStudent))
                .perform(scrollTo(), click());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        SystemClock.sleep(10000);

        onView(withId(R.id.titleMorim))
                .check(matches(isDisplayed()))
                .check(matches(withText("Hi StudentNoImage")));

        performLogout();
    }

    @Test
    public void testFormFieldsTrimming() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

        onView(withId(R.id.etFullNameRegister))
                .perform(scrollTo(), typeText("  John Doe  "), closeSoftKeyboard());
        onView(withId(R.id.etAddressRegister))
                .perform(scrollTo(), typeText("  123 Main Street  "), closeSoftKeyboard());
        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), typeText("  0612345678  "), closeSoftKeyboard());
        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("  test1@example.com  "), closeSoftKeyboard());
        onView(withId(R.id.etPasswordRegister))
                .perform(scrollTo(), typeText("  password123  "), closeSoftKeyboard());

        onView(withId(R.id.typeStudent))
                .perform(scrollTo(), click());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        SystemClock.sleep(10000);

        onView(withId(R.id.titleMorim))
                .check(matches(isDisplayed()))
                .check(matches(withText("Hi John Doe")));

        performLogout();
    }

    @Test
    public void testErrorClearingOnRetry() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

        // Provoquer une erreur
        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        // Vérifier que l'erreur s affiche
        onView(withId(R.id.etLayoutEmailAddressRegister))
                .check(matches(hasTextInputLayoutErrorText("Email must not be empty!")));

        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("test@example.com"), closeSoftKeyboard());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        onView(withId(R.id.etLayoutPasswordRegister))
                .check(matches(hasTextInputLayoutErrorText("Password must not be empty!")));
    }


    @Test
    public void testTeacherRegistrationDialogCancellation() {
        SystemClock.sleep(2000);

        onView(withId(R.id.btnToRegister))
                .perform(click());

//        onView(withId(R.id.ivRegisterUserImage))
//                .perform(setImageFromAssets("testImage.jpg"));

        onView(withId(R.id.etFullNameRegister))
                .perform(scrollTo(), typeText("TestTeacherCancel"), closeSoftKeyboard());
        onView(withId(R.id.etAddressRegister))
                .perform(scrollTo(), typeText("123 Test Street"), closeSoftKeyboard());
        onView(withId(R.id.etPhoneRegister))
                .perform(scrollTo(), typeText("0612345678"), closeSoftKeyboard());
        onView(withId(R.id.etEmailRegister))
                .perform(scrollTo(), typeText("teachercancel@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etPasswordRegister))
                .perform(scrollTo(), typeText("password123"), closeSoftKeyboard());

        onView(withId(R.id.typeTeacher))
                .perform(scrollTo(), click());

        onView(withId(R.id.btnRegisterSubmit))
                .perform(scrollTo(), click());

        SystemClock.sleep(3000);

        onView(withId(R.id.btnDismissDialog))
                .check(matches(isDisplayed()))
                .perform(click());

        SystemClock.sleep(3000);

        onView(withId(R.id.btnBackToSignIn))
                .perform(scrollTo(), click());

        SystemClock.sleep(3000);
        onView(withId(R.id.btnToRegister))
                .check(matches(isDisplayed()));
        SystemClock.sleep(3000);



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


    public static Matcher<View> hasTextInputLayoutErrorText(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof com.google.android.material.textfield.TextInputLayout)) {
                    return false;
                }

                CharSequence error = ((com.google.android.material.textfield.TextInputLayout) view).getError();
                return error != null && error.toString().equals(expectedErrorText);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with TextInputLayout error: " + expectedErrorText);
            }
        };
    }


    private void updateUserImageUrlInFirebase(String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update("image", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d("TEST_IMAGE_UPDATE", "Image URL successfully updated to: " + imageUrl);
                })
                .addOnFailureListener(e -> {
                    Log.e("TEST_IMAGE_UPDATE", "Failed to update image URL", e);
                    throw new RuntimeException("Failed to update image URL in Firebase", e);
                });
    }


    private void createTestImageInternalStorage() {
        try {
            // Créer un Bitmap 400x400
            Bitmap testBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(testBitmap);

            // Fond bleu
            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.BLUE);
            canvas.drawRect(0, 0, 400, 400, backgroundPaint);

            // Cercle blanc au centre
            Paint circlePaint = new Paint();
            circlePaint.setColor(Color.WHITE);
            canvas.drawCircle(200, 200, 150, circlePaint);

            // Texte noir "TEST USER"
            Paint textPaint = new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(40);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("TEST", 200, 190, textPaint);
            canvas.drawText("USER", 200, 230, textPaint);

            // Sauvegarde dans le cache interne (aucune permission requise)
            File cacheDir = context.getCacheDir();
            File imageFile = new File(cacheDir, "test_user_" + System.currentTimeMillis() + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            testBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.close();

            // Stocker l'URI pour la réutiliser
            testImageUri = Uri.fromFile(imageFile);

            System.out.println("Test image created successfully: " + testImageUri);

        } catch (IOException e) {
            System.out.println("Failed to create test image: " + e.getMessage());
        }
    }

    private void setRealImageToFragment() {
        if (testImageUri != null) {
            scenario.onActivity(activity -> {
                try {
                    Fragment currentFragment = activity.getSupportFragmentManager().getPrimaryNavigationFragment();

                    if (currentFragment instanceof NavHostFragment) {
                        NavHostFragment navHost = (NavHostFragment) currentFragment;
                        Fragment activeFragment = navHost.getChildFragmentManager().getFragments().get(0);

                        if (activeFragment.getClass().getSimpleName().contains("Register")) {
                            try {
                                java.lang.reflect.Method setImageMethod =
                                        activeFragment.getClass().getMethod("setTestImageUri", Uri.class);
                                setImageMethod.invoke(activeFragment, testImageUri);
                                System.out.println("Test image set successfully!");
                            } catch (Exception e) {
                                System.out.println("Failed to set test image: " + e.getMessage());
                                System.out.println("Make sure you added setTestImageUri method to RegisterFragment");
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Could not access fragment: " + e.getMessage());
                }
            });

            SystemClock.sleep(1000);
        }
    }



}