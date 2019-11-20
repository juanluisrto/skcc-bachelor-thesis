package com.slagkryssaren.skcc.android.models;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;

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


    //protected float[][][][]
    /**
     * Name of the model file stored in Assets.
     */
    protected static final String TAG = "Tflite:";
    private static final String MODEL_PATH = "skccmodel.tflite";
    public boolean neuralAPI = false;
    /**
     * An instance of the driver class to run model inference with Tensorflow Lite.
     */
    private Interpreter tflite;

    public TfLiteModel(Activity a) throws IOException {
        inputData = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_IN_X][DIM_IMG_SIZE_IN_Y][DIM_PIXEL_SIZE];
        outputData = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_OUT_X][DIM_IMG_SIZE_OUT_Y][DIM_PIXEL_SIZE];
        this.context = a.getApplicationContext();
        tflite = new Interpreter(loadModelFile(a));
        tflite.resizeInput(0, new int[]{1, Model.DIM_IMG_SIZE_IN_X, Model.DIM_IMG_SIZE_IN_Y, 1});
    }


    @Override
    public Bitmap predictImage(Bitmap bitmap, int position) {
        if (tflite == null) {
            Log.e(TAG, "The model has not been initialized; Skipped.");
            return null;
        }
        inputData = convertBitmapToFloatArray(bitmap);
        tflite.setUseNNAPI(neuralAPI);
        long startTime = SystemClock.uptimeMillis();
        tflite.run(inputData, outputData);
        long endTime = SystemClock.uptimeMillis();
        Bitmap outputImage = convertFloatArrayToBitmap(outputData);

        Log.d(TAG,String.valueOf(tflite.getLastNativeInferenceDurationNanoseconds()));
        Log.d(TAG, "Position: " + String.valueOf(position) + " Timecost to run model inference: " + Long.toString(endTime - startTime));

        long milliseconds = endTime - startTime;
        if (position != -1) {
            values.add(new PointValue((float) position, (float) milliseconds));
        }

        return outputImage;
    }

    @Override
    protected float[][][][] convertBitmapToFloatArray(Bitmap bitmap) {
        float[][][][] input = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_IN_X][DIM_IMG_SIZE_IN_Y][DIM_PIXEL_SIZE];
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();

        for (int i = 0; i < DIM_IMG_SIZE_IN_X; i++) {
            for (int j = 0; j < DIM_IMG_SIZE_IN_Y; j++) {
                final int val = intValues[pixel];
                int red = Color.red(val);
                int green = Color.green(val);
                int blue = Color.blue(val);
                float floatValue = (red + green + blue) / 3f;

                input[0][i][j][0] = floatValue;

                min = Math.min(floatValue, min);
                max = Math.max(floatValue, max);
                pixel++;
            }
        }

        //Normalize the image
        for (int i = 0; i < DIM_IMG_SIZE_IN_X; i++) {
            for (int j = 0; j < DIM_IMG_SIZE_IN_Y; j++) {
                input[0][i][j][0] = map(input[0][i][j][0], min, max, 0, 1);
            }
        }

        long endTime = SystemClock.uptimeMillis();
        Log.d("Image", "Timecost to convert bitmap to float array: " + Long.toString(endTime - startTime));
        return input;
    }

    @Override
    protected Bitmap convertFloatArrayToBitmap(Object o) {
        float[][][][] output = (float[][][][]) o;
        int[] intPixels = new int[DIM_IMG_SIZE_OUT_X * DIM_IMG_SIZE_OUT_Y];
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_OUT_X; i++) {
            for (int j = 0; j < DIM_IMG_SIZE_OUT_Y; j++) {
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

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    public void changeDefaultDimensions(int dimX, int dimY){
        super.changeDefaultDimensions(dimX,dimY);
        inputData = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_IN_X][DIM_IMG_SIZE_IN_Y][DIM_PIXEL_SIZE];
        outputData = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_OUT_X][DIM_IMG_SIZE_OUT_Y][DIM_PIXEL_SIZE];
        int[] dims = {1,dimX,dimY,1};
        tflite.resizeInput(0,dims);
    }

    /**
     * Closes tflite to release resources.
     */
    public void close() {
        tflite.close();
        tflite = null;
    }

    @Override
    public void resetDefaultDimensions() {
        super.resetDefaultDimensions();
        tflite.resizeInput(0, new int[]{1, Model.DIM_IMG_SIZE_IN_X, Model.DIM_IMG_SIZE_IN_Y, 1});
    }


}
