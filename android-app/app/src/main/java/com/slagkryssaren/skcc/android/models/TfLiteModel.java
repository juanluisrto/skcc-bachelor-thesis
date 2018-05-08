package com.slagkryssaren.skcc.android.models;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.slagkryssaren.skcc.android.models.Model;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by juanl on 12/12/2017.
 */

public class TfLiteModel extends Model {

    /**
     * Name of the model file stored in Assets.
     */
    protected static final String TAG = "Tflite:";
    private static final String MODEL_PATH = "skccmodel.tflite";/**
     * An instance of the driver class to run model inference with Tensorflow Lite.
     */
    private Interpreter tflite;
    public boolean neuralAPI = false;

    public TfLiteModel(Activity a) throws IOException {
        this.context = a.getApplicationContext();
        tflite = new Interpreter(loadModelFile(a));

    }


    @Override
    public Bitmap predictImage(Bitmap bitmap, int position) {
        if (tflite == null) {
            Log.e(TAG, "The model has not been initialized; Skipped.");
            return null;
        }
        convertBitmapToFloatArray(bitmap);
        //ByteBuffer f = convertBitmapToByteBuffer(bitmap);

        tflite.setUseNNAPI(neuralAPI);
        long startTime = SystemClock.uptimeMillis();
        tflite.run(imgData, outputData);
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG,String.valueOf(tflite.getLastNativeInferenceDurationNanoseconds()));

        Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));

        long milliseconds = endTime - startTime;
        if (position != -1){ values.add(new PointValue((float) position, (float) milliseconds));}
        Bitmap outputImage = convertFloatArrayToBitmap(outputData);

        return outputImage;
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


    public void adaptDimensions(Bitmap b){
        super.changeDefaultDimensions(b);
        int[] dims = {1,b.getWidth(),b.getHeight(),1};
        tflite.resizeInput(0,dims);
    }

    /**
     * Closes tflite to release resources.
     */
    public void close() {
        tflite.close();
        tflite = null;
    }


}
