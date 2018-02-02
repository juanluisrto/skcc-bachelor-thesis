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
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

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

    private ImageTransform it = new ImageTransform();

    private Context context;

    public SkccModel(Activity activity) throws IOException {
        context = activity.getApplicationContext();
        tflite = new Interpreter(loadModelFile(activity));

    }



    Bitmap predictImage(Bitmap bitmap) {
        if (tflite == null) {
            Log.e(TAG, "The model has not been initialized; Skipped.");
            return null;
        }
        it.convertBitmapToFloatArray(bitmap);

        long startTime = SystemClock.uptimeMillis();
        tflite.run(it.imgData, it.outputData);
        long endTime = SystemClock.uptimeMillis();

        Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));
        String textToShow = Long.toString(endTime - startTime) + "ms";
        Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();

        Bitmap outputImage = it.convertFloatArrayToBitmap(it.outputData);

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
