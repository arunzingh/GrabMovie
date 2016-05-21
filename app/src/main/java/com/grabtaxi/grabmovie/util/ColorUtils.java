package com.grabtaxi.grabmovie.util;

import android.graphics.Color;

/**
 * Util class for color handling
 */
public class ColorUtils {

    /**
     * Returns a darker version of the supplied color.
     *
     * @see {http://stackoverflow.com/questions/4928772/android-color-darker}
     * @param color
     * @return
     */
    public static int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    /**
     * Returns a brighter version of the supplied color.
     *
     * @see {http://stackoverflow.com/questions/4928772/android-color-darker}
     * @param color
     * @return
     */
    public static int getBrighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 0.2f + 0.8f * hsv[2];
        return Color.HSVToColor(hsv);
    }
}
