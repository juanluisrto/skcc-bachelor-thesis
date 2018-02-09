package com.slagkryssaren.skcc.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by juanl on 02/02/2018.
 */

public abstract class Model {

    /**
     * Tag for the {@link Log}.
     */
    protected static final String TAG = "Skcc_model:";

    private static final int DIM_BATCH_SIZE = 1;

    private static final int DIM_PIXEL_SIZE = 1;

    public static final int DIM_IMG_SIZE_IN_X = 160;
    public static final int DIM_IMG_SIZE_IN_Y = 160;

    private static final int DIM_IMG_SIZE_OUT_X = DIM_IMG_SIZE_IN_X / 2;
    private static final int DIM_IMG_SIZE_OUT_Y = DIM_IMG_SIZE_IN_Y / 2;

    private int[] intValues = new int[DIM_IMG_SIZE_IN_X * DIM_IMG_SIZE_IN_Y];


    public Context context;
    protected float[][][][] imgData = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_IN_X][DIM_IMG_SIZE_IN_Y][DIM_PIXEL_SIZE];
    protected float[][][][] outputData = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_OUT_X][DIM_IMG_SIZE_OUT_Y][DIM_PIXEL_SIZE];


    /**
     * Writes Image data into a {@code ByteBuffer}.
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
                imgData[0][i][j][0] = MathUtils.map(imgData[0][i][j][0], min, max, 0, 1);
            }
        }

        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to convert bitmap to float array: " + Long.toString(endTime - startTime));
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
                int color = (int) MathUtils.map(1 - floatPixel, 0, 255);
                intPixels[pixel] = Color.rgb(color, color, color);
                pixel++;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(DIM_IMG_SIZE_OUT_X, DIM_IMG_SIZE_OUT_Y, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(intPixels, 0, DIM_IMG_SIZE_OUT_X, 0, 0, DIM_IMG_SIZE_OUT_X, DIM_IMG_SIZE_OUT_Y);
        return bitmap;
    }

    public abstract Bitmap predictImage(Bitmap bitmap);


}
