package com.slagkryssaren.skcc.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;

/**
 * Created by juanl on 02/02/2018.
 */

public class TfMobileModel extends Model {

    private Context context;
    private TensorFlowInferenceInterface infInterface;
    private static final String MODEL_PATH = "skcc_model.pb";
    private String inputName = "conv2d_1_input";
    private String outputName = "conv2d_9/Sigmoid";
    private float[] inputFloatValues = new float[DIM_IMG_SIZE_IN_X * DIM_IMG_SIZE_IN_Y];
    private float[] outputFloatValues = new float[DIM_IMG_SIZE_OUT_X * DIM_IMG_SIZE_OUT_Y];

    public TfMobileModel(Activity activity) throws IOException {

        context = activity.getApplicationContext();
        infInterface = new TensorFlowInferenceInterface(activity.getAssets(), MODEL_PATH);

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

    @Override
    public Bitmap predictImage(Bitmap bitmap) {
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

        Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));
        String textToShow = Long.toString(endTime - startTime) + "ms";
        Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();


        Bitmap outputImage = convertFloatArrayToBitmap(outputData);
        return outputImage;
    }
}

