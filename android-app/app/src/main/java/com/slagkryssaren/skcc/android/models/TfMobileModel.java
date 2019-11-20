package com.slagkryssaren.skcc.android.models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by juanl on 02/02/2018.
 */

public class TfMobileModel extends Model {

    protected static final String TAG = "TfMobile:";
    private static final String MODEL_PATH = "skcc_model.pb";
    private TensorFlowInferenceInterface infInterface;
    private String inputName = "conv2d_1_input";
    private String outputName = "conv2d_8/Sigmoid";


    public TfMobileModel(Activity a) {

        this.context = a.getApplicationContext();
        infInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_PATH);
        inputData = new float[DIM_IMG_SIZE_IN_X * DIM_IMG_SIZE_IN_Y];
        outputData = new float[DIM_IMG_SIZE_OUT_X * DIM_IMG_SIZE_OUT_Y];
    }


    @Override
    public Bitmap predictImage(Bitmap bitmap, int position) {
        if (infInterface == null) {
            Log.e(TAG, "The model has not been initialized; Skipped.");
            return null;
        }

        inputData = convertBitmapToFloatArray(bitmap);
        long startTime = SystemClock.uptimeMillis();
        infInterface.feed(inputName, (float[]) inputData, 1, DIM_IMG_SIZE_IN_X, DIM_IMG_SIZE_IN_Y, 1);
        infInterface.run(new String[]{outputName}, true);
        infInterface.fetch(outputName, (float[]) outputData);
        long endTime = SystemClock.uptimeMillis();
        Bitmap outputImage = convertFloatArrayToBitmap(outputData);

        long milliseconds = endTime - startTime;

        if (position != -1){
            values.add(new PointValue((float) position, (float) milliseconds));
        }
        Log.d(TAG, "Position: " + String.valueOf(position) + " Timecost to run model inference: " + Long.toString(endTime - startTime));


        return outputImage;
    }

    @Override
    protected float[] convertBitmapToFloatArray(Bitmap bitmap) {

        float[] input = new float[DIM_IMG_SIZE_IN_X * DIM_IMG_SIZE_IN_Y];

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        long startTime = SystemClock.uptimeMillis();

        for (int i = 0; i < intValues.length; i++) {

            final int val = intValues[i];
            int red = Color.red(val);
            int green = Color.green(val);
            int blue = Color.blue(val);
            float floatValue = (red + green + blue) / 3f;

            input[i] = floatValue;

            min = Math.min(floatValue, min);
            max = Math.max(floatValue, max);
        }

        //Normalize the image
        for (int i = 0; i < input.length; i++) {
            input[i] = map(input[i], min, max, 0, 1);
        }

        long endTime = SystemClock.uptimeMillis();
        Log.d("Image", "Timecost to convert bitmap to float array: " + Long.toString(endTime - startTime));

        return input;
    }

    @Override
    protected Bitmap convertFloatArrayToBitmap(Object o) {
        float[] output = (float[]) o;
        int[] intPixels = new int[DIM_IMG_SIZE_OUT_X * DIM_IMG_SIZE_OUT_Y];
        int pixel = 0;
        for (int i = 0; i < intPixels.length; i++) {
            float floatPixel = output[i];
            int color = (int) map(1 - floatPixel, 0, 255);
            intPixels[pixel] = Color.rgb(color, color, color);
            pixel++;
        }
        Bitmap bitmap = Bitmap.createBitmap(DIM_IMG_SIZE_OUT_X, DIM_IMG_SIZE_OUT_Y, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(intPixels, 0, DIM_IMG_SIZE_OUT_X, 0, 0, DIM_IMG_SIZE_OUT_X, DIM_IMG_SIZE_OUT_Y);
        return bitmap;
    }

    public void changeDefaultDimensions(int dimX, int dimY){
        super.changeDefaultDimensions(dimX,dimY);
        inputData = new float[DIM_IMG_SIZE_IN_X * DIM_IMG_SIZE_IN_Y];
        outputData = new float[DIM_IMG_SIZE_OUT_X * DIM_IMG_SIZE_OUT_Y];
    }


}

