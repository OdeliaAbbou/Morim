package com.example.morim;

import android.view.View;
import android.widget.ImageView;
import androidx.test.espresso.matcher.BoundedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class DrawableMatchers {
    public static Matcher<View> hasDrawable() {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public boolean matchesSafely(ImageView imageView) {
                return imageView.getDrawable() != null;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("ImageView should have a drawable");
            }
        };
    }
}
