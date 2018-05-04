package com.slagkryssaren.skcc.android.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.slagkryssaren.skcc.android.models.Model;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;

import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by juanl on 02/02/2018.
 */

public class TfMobileModel extends Model {

    private TensorFlowInferenceInterface infInterface;
    private static final String MODEL_PATH = "skcc_model.pb";
    private String inputName = "conv2d_1_input";
    private String outputName = "conv2d_9/Sigmoid";
    private float[] inputFloatValues = new float[DIM_IMG_SIZE_IN_X * DIM_IMG_SIZE_IN_Y];
    private float[] outputFloatValues = new float[DIM_IMG_SIZE_OUT_X * DIM_IMG_SIZE_OUT_Y];
    protected static final String TAG = "TfMobile:";


    public TfMobileModel(Activity a) throws IOException {

        this.context = a.getApplicationContext();
        infInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_PATH);

    }



    @Override
    public Bitmap predictImage(Bitmap bitmap, int position) {
        if (infInterface == null) {
            Log.e(TAG, "The model has not been initialized; Skipped.");
            return null;
        }
        convertBitmapToFloatArray(bitmap);
        inputFloatValues = reshapeFloat4to1Dimensions(imgData);
        long startTime = SystemClock.uptimeMillis();
        infInterface.feed(inputName, inputFloatValues, 1, DIM_IMG_SIZE_IN_X, DIM_IMG_SIZE_IN_Y, 1);
        infInterface.run(new String[]{outputName}, true);
        infInterface.fetch(outputName, outputFloatValues);
        long endTime = SystemClock.uptimeMillis();

        outputData = reshapeFloat1to4Dimensions(outputFloatValues);
        long milliseconds = endTime - startTime;
        values.add(new PointValue((float) position, (float) milliseconds));
        Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));
        String textToShow = Long.toString(endTime - startTime) + "ms";


        Bitmap outputImage = convertFloatArrayToBitmap(outputData);
        return outputImage;
    }
}

