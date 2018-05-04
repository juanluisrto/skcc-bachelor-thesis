package com.slagkryssaren.skcc.android.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by juanl on 02/02/2018.
 */

public abstract class Model {

    /**
     * Tag for the {@link Log}.
     */


    private static final int DIM_BATCH_SIZE = 1;
    public static final int FLOAT_BYTES = (Float.SIZE / Byte.SIZE);
    private static final int DIM_PIXEL_SIZE = 1;

    public static final int DIM_IMG_SIZE_IN_X = 160;
    public static final int DIM_IMG_SIZE_IN_Y = 160;

    public static final int DIM_IMG_SIZE_OUT_X = DIM_IMG_SIZE_IN_X / 2;
    public static final int DIM_IMG_SIZE_OUT_Y = DIM_IMG_SIZE_IN_Y / 2;

    private int[] intValues = new int[DIM_IMG_SIZE_IN_X * DIM_IMG_SIZE_IN_Y];


    public Context context;
    protected float[][][][] imgData = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_IN_X][DIM_IMG_SIZE_IN_Y][DIM_PIXEL_SIZE];
    protected float[][][][] outputData = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_OUT_X][DIM_IMG_SIZE_OUT_Y][DIM_PIXEL_SIZE];


    //SharedPreferences sharedPref = this.context.getSharedPreferences(Context.MODE_PRIVATE);

    public List<PointValue> values = new ArrayList<PointValue>();


    public ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(DIM_BATCH_SIZE*DIM_IMG_SIZE_IN_X*DIM_IMG_SIZE_IN_Y*FLOAT_BYTES);
        byteBuffer.order(ByteOrder.nativeOrder());

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_IN_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_IN_Y; ++j) {
                final int val = intValues[pixel];
                int red = Color.red(val);
                int green = Color.green(val);
                int blue = Color.blue(val);
                int  floatValue = (red + green + blue) / 3;
                byteBuffer.put((byte) ((floatValue >> 16) & 0xFF));
                byteBuffer.put((byte) ((floatValue >> 8) & 0xFF));
                byteBuffer.put((byte) (floatValue & 0xFF));

                min = Math.min(floatValue, min);
                max = Math.max(floatValue, max);
                pixel++;
            }
        }

       /* //Normalize the image
        for (int i = 0; i < DIM_IMG_SIZE_IN_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_IN_Y; ++j) {
                imgData[0][i][j][0] = map(imgData[0][i][j][0], min, max, 0, 1);
            }
        }*/
        return byteBuffer;
    }


    /**
     * Writes Image data into a FloatArray.
     */
    protected void convertBitmapToFloatArray(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();

        for (int i = 0; i < DIM_IMG_SIZE_IN_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_IN_Y; ++j) {
                final int val = intValues[pixel];
                int red = Color.red(val);
                int green = Color.green(val);
                int blue = Color.blue(val);
                float floatValue = (red + green + blue) / 3f;

                imgData[0][i][j][0] = floatValue;

                min = Math.min(floatValue, min);
                max = Math.max(floatValue, max);
                pixel++;
            }
        }

        //Normalize the image
        for (int i = 0; i < DIM_IMG_SIZE_IN_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_IN_Y; ++j) {
                imgData[0][i][j][0] = map(imgData[0][i][j][0], min, max, 0, 1);
            }
        }

        long endTime = SystemClock.uptimeMillis();
        Log.d("Image", "Timecost to convert bitmap to float array: " + Long.toString(endTime - startTime));
    }

    /**
     * Does the opposite conversion and returns the Bitmap
     **/
    protected Bitmap convertFloatArrayToBitmap(float[][][][] output) {
        int[] intPixels = new int[DIM_IMG_SIZE_OUT_X * DIM_IMG_SIZE_OUT_Y];

        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_OUT_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_OUT_Y; ++j) {
                float floatPixel = output[0][i][j][0];
                int color = (int) map(1 - floatPixel, 0, 255);
                intPixels[pixel] = Color.rgb(color, color, color);
                pixel++;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(DIM_IMG_SIZE_OUT_X, DIM_IMG_SIZE_OUT_Y, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(intPixels, 0, DIM_IMG_SIZE_OUT_X, 0, 0, DIM_IMG_SIZE_OUT_X, DIM_IMG_SIZE_OUT_Y);
        return bitmap;
    }
    //Reshapes inputData to float array which can be handled by infInterface.feed()
    public float[] reshapeFloat4to1Dimensions(float[][][][] inputData) {
        float[] reshapedData = new float[DIM_IMG_SIZE_IN_Y * DIM_IMG_SIZE_IN_X];
        for (int i = 0; i < DIM_IMG_SIZE_IN_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_IN_Y; ++j) {
                reshapedData[j * DIM_IMG_SIZE_IN_Y + i] = inputData[0][i][j][0];
            }
        }
        return reshapedData;
    }

    //Reshapes inputData from float [] to float [][][][]
    public float[][][][] reshapeFloat1to4Dimensions(float[] inputData) {
        float[][][][] reshapedData = new float[1][DIM_IMG_SIZE_OUT_X][DIM_IMG_SIZE_OUT_Y][1];
        for (int i = 0; i < DIM_IMG_SIZE_OUT_X; i++) {
            for (int j = 0; j < DIM_IMG_SIZE_OUT_Y; j++) {
                reshapedData[0][i][j][0] = inputData[j * DIM_IMG_SIZE_OUT_Y + i];
            }
        }
        return reshapedData;
    }

    public static float map(float value, float min, float max) {
        return (value * (max - min)) + min;
    }

    public static float map(float value, float fromMin, float fromMax, float toMin, float toMax) {
        float relativeFromValue = (value - fromMin) / (fromMax - fromMin);
        float mappedValue = map(relativeFromValue, toMin, toMax);
        return mappedValue;
    }


    public Bitmap predictImage(Bitmap bitmap){
        return predictImage(bitmap,-1);
    }

    public abstract Bitmap predictImage(Bitmap bitmap, int position);


}
