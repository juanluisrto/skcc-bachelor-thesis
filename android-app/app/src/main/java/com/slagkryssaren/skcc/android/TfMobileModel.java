package com.slagkryssaren.skcc.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import org.tensorflow.lite.Interpreter;

import java.io.IOException;

/**
 * Created by juanl on 02/02/2018.
 */

public class TfMobileModel {

    private Context context;
    private TensorFlowInferenceInterface infInterface;
    private static final String MODEL_PATH = "skcc_model.pb";
    private ImageTransform it = new ImageTransform();


    public TfMobileModel(Activity activity) throws IOException {

        context = activity.getApplicationContext();
        infInterface = new TensorFlowInferenceInterface(activity.getAssets(), MODEL_PATH);

    }

    Bitmap predictImage(Bitmap bitmap) {
        if (infInterface == null) {
            Log.e(it.TAG, "The model has not been initialized; Skipped.");
            return null;
        }
        it.convertBitmapToFloatArray(bitmap);
        long startTime = SystemClock.uptimeMillis();
        infInterface.feed();
        long endTime = SystemClock.uptimeMillis();
        infInterface.


        Bitmap outputImage = it.convertFloatArrayToBitmap(it.outputData);
        return outputImage;
    }


/** Continuous inference (floats used in example, can be any primitive): */

// loading new input
infIn.fillNodeFloat("input:0", INPUT_SHAPE, input); // INPUT_SHAPE is an int[] of expected shape, input is a float[] with the input data

    // running inference for given input and reading output
    String outputNode = "output:0";
    String[] outputNodes = {outputNode};
tensorflow.runInference(outputNodes);
tensorflow.readNodeFloat(outputNode, output); // output is a preallocated float[] in the size of the expected output vector


}
