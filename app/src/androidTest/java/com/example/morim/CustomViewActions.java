package com.example.morim;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static java.util.EnumSet.allOf;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;

import java.io.IOException;
import java.io.InputStream;
import static org.hamcrest.Matchers.allOf;

public class CustomViewActions {

    /**
     * ViewAction לטעינת תמונה מתיקיית assets
     * @param imageName שם הקובץ בתיקיית assets (לדוגמה: "testImage.jpg")
     * @return ViewAction שטוען את התמונה ל-ImageView
     */
    public static ViewAction setImageFromAssets(String imageName) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ImageView.class);
            }

            @Override
            public String getDescription() {
                return "Set image from assets: " + imageName;
            }

            @Override
            public void perform(UiController uiController, View view) {
                ImageView imageView = (ImageView) view;
                try {
// נסה קודם מתיקיית assets של הטסטים
                    Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
                    InputStream inputStream = null;

                    try {
                        inputStream = testContext.getAssets().open(imageName);
                    } catch (IOException e) {
// אם לא נמצא, נסה מתיקיית assets של האפליקציה הראשית
                        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                        inputStream = appContext.getAssets().open(imageName);
                    }

                    Drawable drawable = Drawable.createFromStream(inputStream, null);
                    imageView.setImageDrawable(drawable);
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load image from assets: " + imageName + ". Make sure the image exists in either test assets or main assets.", e);
                }
            }
        };
    }

    /**
     * ViewAction לבדיקה שהתמונה נטענה בהצלחה
     * @return ViewAction שבודק שיש תמונה ב-ImageView
     */
    public static ViewAction checkImageLoaded() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ImageView.class);
            }

            @Override
            public String getDescription() {
                return "Check if image is loaded in ImageView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ImageView imageView = (ImageView) view;
                if (imageView.getDrawable() == null) {
                    throw new RuntimeException("No image loaded in ImageView");
                }
            }
        };
    }

    /**
     * ViewAction לכתיבת טקסט בתוך TextView (לצורכי בדיקות בלבד)
     * @param value הטקסט שרוצים להציג
     */
    public static ViewAction setTextInTextView(final String value) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TextView.class));
            }

            @Override
            public String getDescription() {
                return "Set text in a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((TextView) view).setText(value);
            }
        };
    }

}
