package com.slagkryssaren.skcc.android;

/**
 * Created by miksto on 2018-01-24.
 */

public class MathUtils {


    public static float map(float value, float min, float max) {
        return (value * (max - min)) + min;
    }

    public static float map(float value, float fromMin, float fromMax, float toMin, float toMax) {
        float relativeFromValue = (value - fromMin) / (fromMax - fromMin);
        float mappedValue = map(relativeFromValue, toMin, toMax);
        return mappedValue;
    }
}
