package com.slagkryssaren.skcc.android;

/**
 * Created by juanl on 21/11/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.slagkryssaren.skcc.android.models.Model;
import com.slagkryssaren.skcc.android.models.TfLiteModel;
import com.slagkryssaren.skcc.android.models.TfMobileModel;

import java.io.IOException;


public class Adapter extends BaseAdapter implements AdapterView.OnItemClickListener{


    //Create model classes
    public TfLiteModel tfLiteModel;
    public TfMobileModel tfMobileModel;
    private Context context;


    Adapter(@NonNull Activity a) {
        context = a;
        try {
            tfLiteModel = new TfLiteModel(a);
            tfMobileModel = new TfMobileModel(a);
        } catch (IOException e) {
            Log.w("error", e);
        }

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);

            //imageView.setMaxHeight(.getWidth());
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(drawables[position]);
        //imageView.setPaddingRelative(8,8,8,8);

        return imageView;
    }

    public int getCount() {
        return drawables.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        runInference(position,true,true,true);
    }

    void runInference(int position, boolean tflite, boolean tfmobile, boolean display){
        Bitmap input = BitmapFactory.decodeResource(context.getResources(), drawables[position]);
        input = Bitmap.createScaledBitmap(input, Model.DIM_IMG_SIZE_IN_X, Model.DIM_IMG_SIZE_IN_Y, false);
        Bitmap outputTfLite = null;
        Bitmap outputTfMobile = null;
        if(tflite){
            outputTfLite = tfLiteModel.predictImage(input,position);
        }
        if (tfmobile){
            outputTfMobile = tfMobileModel.predictImage(input,position);
        }
        if (display){
            context.startActivity(new Intent(context, DisplayActivity.class)
                    .putExtra("outputTfMobile",outputTfMobile)
                    .putExtra("outputTfLite",outputTfLite)
                    .putExtra("timeTflite",String.valueOf(tfLiteModel.values.get(tfLiteModel.values.size()-1).getY()))
                    .putExtra("timeTfmobile",String.valueOf(tfMobileModel.values.get(tfMobileModel.values.size()-1).getY()))
                    .putExtra("originalDrawable",drawables[position])
                    .putExtra("targetDrawable", targets[position]));

        }
    }

    public int[] drawables = {
            R.drawable.img_0,  R.drawable.img_1,  R.drawable.img_2,  R.drawable.img_3,  R.drawable.img_4,  R.drawable.img_5,
            R.drawable.img_6,  R.drawable.img_7,  R.drawable.img_8,  R.drawable.img_9,  R.drawable.img_10,  R.drawable.img_11,
            R.drawable.img_12,  R.drawable.img_13,  R.drawable.img_14,  R.drawable.img_15,  R.drawable.img_16,  R.drawable.img_17,
            R.drawable.img_18,  R.drawable.img_19,  R.drawable.img_20,  R.drawable.img_21,  R.drawable.img_22,  R.drawable.img_23,
            R.drawable.img_24,  R.drawable.img_25,  R.drawable.img_26,  R.drawable.img_27,  R.drawable.img_28,  R.drawable.img_29,
            R.drawable.img_30,  R.drawable.img_31,  R.drawable.img_32,  R.drawable.img_33,  R.drawable.img_34,  R.drawable.img_35,
            R.drawable.img_36,  R.drawable.img_37,  R.drawable.img_38,  R.drawable.img_39,  R.drawable.img_40,  R.drawable.img_41,
            R.drawable.img_42,  R.drawable.img_43,  R.drawable.img_44,  R.drawable.img_45,  R.drawable.img_46,  R.drawable.img_47,
            R.drawable.img_48,  R.drawable.img_49};

    public int[] targets = {
            R.drawable.target_0,  R.drawable.target_1,  R.drawable.target_2,  R.drawable.target_3,  R.drawable.target_4,  R.drawable.target_5,
            R.drawable.target_6,  R.drawable.target_7,  R.drawable.target_8,  R.drawable.target_9,  R.drawable.target_10,  R.drawable.target_11,
            R.drawable.target_12,  R.drawable.target_13,  R.drawable.target_14,  R.drawable.target_15,  R.drawable.target_16,  R.drawable.target_17,
            R.drawable.target_18,  R.drawable.target_19,  R.drawable.target_20,  R.drawable.target_21,  R.drawable.target_22,  R.drawable.target_23,
            R.drawable.target_24,  R.drawable.target_25,  R.drawable.target_26,  R.drawable.target_27,  R.drawable.target_28,  R.drawable.target_29,
            R.drawable.target_30,  R.drawable.target_31,  R.drawable.target_32,  R.drawable.target_33,  R.drawable.target_34,  R.drawable.target_35,
            R.drawable.target_36,  R.drawable.target_37,  R.drawable.target_38,  R.drawable.target_39,  R.drawable.target_40,  R.drawable.target_41,
            R.drawable.target_42,  R.drawable.target_43,  R.drawable.target_44,  R.drawable.target_45,  R.drawable.target_46,  R.drawable.target_47,
            R.drawable.target_48,  R.drawable.target_49};

    public int[] drawables_240 = {
            R.drawable.img_240_0,  R.drawable.img_240_1,  R.drawable.img_240_2,  R.drawable.img_240_3,  R.drawable.img_240_4,  R.drawable.img_240_5,
            R.drawable.img_240_6,  R.drawable.img_240_7,  R.drawable.img_240_8,  R.drawable.img_240_9,  R.drawable.img_240_10,  R.drawable.img_240_11,
            R.drawable.img_240_12,  R.drawable.img_240_13,  R.drawable.img_240_14,  R.drawable.img_240_15,  R.drawable.img_240_16,  R.drawable.img_240_17,
            R.drawable.img_240_18,  R.drawable.img_240_19,  R.drawable.img_240_20,  R.drawable.img_240_21,  R.drawable.img_240_22,  R.drawable.img_240_23,
            R.drawable.img_240_24,  R.drawable.img_240_25,  R.drawable.img_240_26,  R.drawable.img_240_27,  R.drawable.img_240_28,  R.drawable.img_240_29,
            R.drawable.img_240_30,  R.drawable.img_240_31,  R.drawable.img_240_32,  R.drawable.img_240_33,  R.drawable.img_240_34,  R.drawable.img_240_35,
            R.drawable.img_240_36,  R.drawable.img_240_37,  R.drawable.img_240_38,  R.drawable.img_240_39,  R.drawable.img_240_40,  R.drawable.img_240_41,
            R.drawable.img_240_42,  R.drawable.img_240_43,  R.drawable.img_240_44,  R.drawable.img_240_45,  R.drawable.img_240_46,  R.drawable.img_240_47,
            R.drawable.img_240_48,  R.drawable.img_240_49};

    public int[] drawables_320 = {
            R.drawable.img_320_0,  R.drawable.img_320_1,  R.drawable.img_320_2,  R.drawable.img_320_3,  R.drawable.img_320_4,  R.drawable.img_320_5,
            R.drawable.img_320_6,  R.drawable.img_320_7,  R.drawable.img_320_8,  R.drawable.img_320_9,  R.drawable.img_320_10,  R.drawable.img_320_11,
            R.drawable.img_320_12,  R.drawable.img_320_13,  R.drawable.img_320_14,  R.drawable.img_320_15,  R.drawable.img_320_16,  R.drawable.img_320_17,
            R.drawable.img_320_18,  R.drawable.img_320_19,  R.drawable.img_320_20,  R.drawable.img_320_21,  R.drawable.img_320_22,  R.drawable.img_320_23,
            R.drawable.img_320_24,  R.drawable.img_320_25,  R.drawable.img_320_26,  R.drawable.img_320_27,  R.drawable.img_320_28,  R.drawable.img_320_29,
            R.drawable.img_320_30,  R.drawable.img_320_31,  R.drawable.img_320_32,  R.drawable.img_320_33,  R.drawable.img_320_34,  R.drawable.img_320_35,
            R.drawable.img_320_36,  R.drawable.img_320_37,  R.drawable.img_320_38,  R.drawable.img_320_39,  R.drawable.img_320_40,  R.drawable.img_320_41,
            R.drawable.img_320_42,  R.drawable.img_320_43,  R.drawable.img_320_44,  R.drawable.img_320_45,  R.drawable.img_320_46,  R.drawable.img_320_47,
            R.drawable.img_320_48,  R.drawable.img_320_49};

}
