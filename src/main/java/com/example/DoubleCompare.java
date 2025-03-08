package com.example;

public class DoubleCompare {
    public static final double EPSILON = 0.000001;

    /*
     * Checks if floats a and b are equal. Should be used everywhere instead of "=="
     */
    public static boolean approximatelyEqual(double a, double b) {
        return Math.abs(a-b) < DoubleCompare.EPSILON;
    }

    /*
     * Checks if float a is greater than b. Excludes the case where a and b
     * are similar enough to be approximately equal
     */
    public static boolean greaterThan(double a, double b) {
        return !approximatelyEqual(a, b) && a > b;
    }

    public static boolean geq(double a, double b) {
        return approximatelyEqual(a, b) || a > b;
    }

}
