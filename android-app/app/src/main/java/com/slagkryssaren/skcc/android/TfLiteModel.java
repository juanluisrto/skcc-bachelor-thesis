package com.slagkryssaren.skcc.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by juanl on 12/12/2017.
 */

public class TfLiteModel extends Model {

    /**
     * Tag for the {@link Log}.
     */
    /**
     * Name of the model file stored in Assets.
     */
    protected static final String TAG = "Tflite:";
    private static final String MODEL_PATH = "skccmodel.tflite";
    public static final int FLOAT_BYTES = (Float.SIZE / Byte.SIZE);
    /**
     * An instance of the driver class to run model inference with Tensorflow Lite.
     */
    private Interpreter tflite;


    public TfLiteModel(Activity activity) throws IOException {
        context = activity.getApplicationContext();
        tflite = new Interpreter(loadModelFile(activity));

    }


    @Override
    public Bitmap predictImage(Bitmap bitmap, int position) {
        if (tflite == null) {
            Log.e(TAG, "The model has not been initialized; Skipped.");
            return null;
        }
        convertBitmapToFloatArray(bitmap);
        float [] inputTest  = reshapeFloat4to1Dimensions(imgData);
        float [] outputTest = new float [DIM_IMG_SIZE_OUT_X * DIM_IMG_SIZE_OUT_Y];

        long startTime = SystemClock.uptimeMillis();
        tflite.run(imgData, outputData);
        long endTime = SystemClock.uptimeMillis();

        Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));
        String textToShow = Long.toString(endTime - startTime) + "ms";
        Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();

        //outputData = reshapeFloat1to4Dimensions(outputTest);
        long milliseconds = endTime - startTime;
        values.add(new PointValue((float) position, (float) milliseconds));
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
