package com.slagkryssaren.skcc.android.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by juanl on 02/02/2018.
 */

public abstract class Model {


    protected Object inputData;
    protected Object outputData;

    private static final int IMAGE_RES = 160;

    protected static final int DIM_BATCH_SIZE = 1;
    public static final int FLOAT_BYTES = (Float.SIZE / Byte.SIZE);
    protected static final int DIM_PIXEL_SIZE = 1;

    public static int DIM_IMG_SIZE_IN_X = IMAGE_RES;
    public static int DIM_IMG_SIZE_IN_Y = IMAGE_RES;

    public static int DIM_IMG_SIZE_OUT_X = DIM_IMG_SIZE_IN_X / 2;
    public static int DIM_IMG_SIZE_OUT_Y = DIM_IMG_SIZE_IN_Y / 2;

    protected int[] intValues = new int[DIM_IMG_SIZE_IN_X * DIM_IMG_SIZE_IN_Y];


    public Context context;



    //SharedPreferences sharedPref = this.context.getSharedPreferences(Context.MODE_PRIVATE);

    public List<PointValue> values = new ArrayList<PointValue>();
    public List<PointValue> values_160 = new ArrayList<PointValue>();
    public List<PointValue> values_240 = new ArrayList<PointValue>();
    public List<PointValue> values_320 = new ArrayList<PointValue>();


    public Bitmap predictImage(Bitmap bitmap){
        return predictImage(bitmap,-1);
    }

    public abstract Bitmap predictImage(Bitmap bitmap, int position);


    /**
     * Writes Image data into a FloatArray.
     */
    abstract protected Object convertBitmapToFloatArray(Bitmap bitmap);

    /**
     * Does the opposite conversion and returns the Bitmap
     **/
    abstract protected Bitmap convertFloatArrayToBitmap(Object output);


    public static float map(float value, float min, float max) {
        return (value * (max - min)) + min;
    }

    public static float map(float value, float fromMin, float fromMax, float toMin, float toMax) {
        float relativeFromValue = (value - fromMin) / (fromMax - fromMin);
        float mappedValue = map(relativeFromValue, toMin, toMax);
        return mappedValue;
    }


    public void changeDefaultDimensions(Bitmap b){
        changeDefaultDimensions(b.getWidth(),b.getHeight());
    }
    public void changeDefaultDimensions( int dim){
        changeDefaultDimensions(dim,dim);
    }

    public void changeDefaultDimensions( int dimX, int dimY){
        DIM_IMG_SIZE_IN_X = dimX;
        DIM_IMG_SIZE_IN_Y = dimY;
        DIM_IMG_SIZE_OUT_X = DIM_IMG_SIZE_IN_X / 2;
        DIM_IMG_SIZE_OUT_Y = DIM_IMG_SIZE_IN_Y / 2;
        intValues = new int[DIM_IMG_SIZE_IN_X * DIM_IMG_SIZE_IN_Y];
    }

    public void resetDefaultDimensions(){
        changeDefaultDimensions(IMAGE_RES);
    }










    /**
     * Experiment with buffers.
     **/
    public ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(DIM_BATCH_SIZE*DIM_IMG_SIZE_IN_X*DIM_IMG_SIZE_IN_Y*FLOAT_BYTES);
        byteBuffer.order(ByteOrder.nativeOrder());

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_IN_X; i++) {
            for (int j = 0; j < DIM_IMG_SIZE_IN_Y; j++) {
                final int val = intValues[pixel];
                int red = Color.red(val);
                int green = Color.green(val);
                int blue = Color.blue(val);
                int  floatValue = (red + green + blue) / 3;
                byteBuffer.put((byte) ((floatValue >> 16) & 0xFF));
                byteBuffer.put((byte) ((floatValue >> 8) & 0xFF));
                byteBuffer.put((byte) (floatValue & 0xFF));

                min = Math.min(floatValue, min);
                max = Math.max(floatValue, max);
                pixel++;
            }
        }

        return byteBuffer;
    }

    public Bitmap convertFloatBufferToBitmap(FloatBuffer b){
        int[] intPixels = new int[DIM_IMG_SIZE_OUT_X * DIM_IMG_SIZE_OUT_Y];
        b.rewind();
        for(int pixel = 0; pixel< intPixels.length ;pixel++) {
            float floatPixel = b.get(pixel);
            int color = (int) map(1 - floatPixel, 0, 255);
            intPixels[pixel] = Color.rgb(color, color, color);
            pixel++;
        }

        Bitmap bitmap = Bitmap.createBitmap(DIM_IMG_SIZE_OUT_X, DIM_IMG_SIZE_OUT_Y, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(intPixels, 0, DIM_IMG_SIZE_OUT_X, 0, 0, DIM_IMG_SIZE_OUT_X, DIM_IMG_SIZE_OUT_Y);
        //bitmap.copyPixelsFromBuffer(b.asFloatBuffer());
        return bitmap;
    }


    /**
     * Other auxiliary functions for previous versions.
     *
     */


    //Reshapes inputData to float array which can be handled by infInterface.feed()
    public float[] reshapeFloat4to1Dimensions(float[][][][] inputData) {
        float[] reshapedData = new float[DIM_IMG_SIZE_IN_Y * DIM_IMG_SIZE_IN_X ];
        for (int i = 0; i < DIM_IMG_SIZE_IN_X; i++) {
            for (int j = 0; j < DIM_IMG_SIZE_IN_Y; j++) {
                reshapedData[i * DIM_IMG_SIZE_IN_X + j] = inputData[0][i][j][0];
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




}
