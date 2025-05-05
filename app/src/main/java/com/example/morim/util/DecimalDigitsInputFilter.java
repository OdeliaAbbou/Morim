package com.example.morim.util;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalDigitsInputFilter implements InputFilter {

    private final int decimalDigits;

    /**
     * Constructor.
     *
     * @param decimalDigits Maximum number of digits allowed after the decimal point.
     */
    public DecimalDigitsInputFilter(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int dotPos = -1;
        int len = dest.length();
        for (int i = 0; i < len; i++) {
            char c = dest.charAt(i);
            if (c == '.') {
                dotPos = i;
                break;
            }
        }
        if (dotPos >= 0) { // If the text already contains a decimal point
            if (dstart <= dotPos) { // If we're inserting before the decimal point
                return null;
            } else if (dend - dotPos > decimalDigits) {
                // If we're inserting after the decimal point
                // and adding this character goes beyond the maximum allowed
                return "";
            }
        } else {
            // If no decimal point exists yet, check if the new input is a decimal point and validate further.
            if (source.equals(".") && dest.length() == 0) {
                return "0.";
            }
        }

        return null; // keep original
    }
}
