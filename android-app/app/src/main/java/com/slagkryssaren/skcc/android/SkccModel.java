package com.slagkryssaren.skcc.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by juanl on 12/12/2017.
 */

public class SkccModel {

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Skcc_model:";
    /**
     * Name of the model file stored in Assets.
     */
    private static final String MODEL_PATH = "skccmodel_old.tflite";
    public static final int FLOAT_BYTES = (Float.SIZE / Byte.SIZE);
    /**
     * An instance of the driver class to run model inference with Tensorflow Lite.
     */
    private Interpreter tflite;

    /**
     * Dimensions of inputs.
     */
    private static final int DIM_BATCH_SIZE = 1;

    private static final int DIM_PIXEL_SIZE = 1;

    public static final int DIM_IMG_SIZE_IN_X = 160;
    public static final int DIM_IMG_SIZE_IN_Y = 160;

    private static final int DIM_IMG_SIZE_OUT_X = DIM_IMG_SIZE_IN_X / 2;
    private static final int DIM_IMG_SIZE_OUT_Y = DIM_IMG_SIZE_IN_Y / 2;

    private int[] intValues = new int[DIM_IMG_SIZE_IN_X * DIM_IMG_SIZE_IN_Y];

    private float[][][][] imgData = null;
    private float[][][][] outputData = null;
    private Context context;

    public SkccModel(Activity activity) throws IOException {
        context = activity.getApplicationContext();
        tflite = new Interpreter(loadModelFile(activity));

        imgData = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_IN_X][DIM_IMG_SIZE_IN_Y][DIM_PIXEL_SIZE];
        outputData = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_OUT_X][DIM_IMG_SIZE_OUT_Y][DIM_PIXEL_SIZE];
    }

    /**
     * Writes Image data into a {@code ByteBuffer}.
     */
    private void convertBitmapToFloatArray(Bitmap bitmap) {
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
    private Bitmap convertFloatArrayToBitmap(float[][][][] output) {
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


    Bitmap predictImage(Bitmap bitmap) {
        if (tflite == null) {
            Log.e(TAG, "The model has not been initialized; Skipped.");
            return null;
        }
        convertBitmapToFloatArray(bitmap);

        long startTime = SystemClock.uptimeMillis();

        tflite.run(imgData, outputData);

        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));
        String textToShow = Long.toString(endTime - startTime) + "ms";
        Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();

        Bitmap outputImage = convertFloatArrayToBitmap(outputData);

        return outputImage;
    }

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /**
     * Closes tflite to release resources.
     */
    public void close() {
        tflite.close();
        tflite = null;
    }


}
