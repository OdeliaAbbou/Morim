// app/src/test/java/com/example/morim/AuthActivityUnitTest.java
package com.example.morim;

import android.app.Application;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.HiltTestApplication;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@HiltAndroidTest
@RunWith(RobolectricTestRunner.class)
@Config(
        application = HiltTestApplication.class,  // utilise HiltTestApplication à la place de MorimApp
        manifest    = Config.NONE,                // ignore votre AndroidManifest.xml
        sdk         = {33}                        // force Robolectric à API 33 (max supporté)
)
public class AuthActivityUnitTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    private AuthActivity activity;

    @Before
    public void setUp() {
        // 1) Initialiser FirebaseApp pour éviter l'erreur "Default FirebaseApp is not initialized"
        Application app = (Application) ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(app);

        // 2) Injecter les dépendances Hilt
        hiltRule.inject();

        // 3) Créer et démarrer l'Activity
        activity = Robolectric.buildActivity(AuthActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void defaultViewsAreNotNull() {
        // Vérifie que les vues de login existent bien
        EditText email    = activity.findViewById(R.id.etEmailLogin);
        EditText password = activity.findViewById(R.id.etPasswordLogin);
        Button   loginBtn = activity.findViewById(R.id.btnLoginSubmit);

        assertNotNull("Le champ email ne doit pas être null", email);
        assertNotNull("Le champ mot de passe ne doit pas être null", password);
        assertNotNull("Le bouton login ne doit pas être null", loginBtn);
    }

    @Test
    public void packageName_isCorrect() {
        // Vérifie que le packageName correspond à votre applicationId
        Context ctx = ApplicationProvider.getApplicationContext();
        assertEquals(
                "com.example.tastyrecipe",
                ctx.getPackageName()
        );
    }
}
