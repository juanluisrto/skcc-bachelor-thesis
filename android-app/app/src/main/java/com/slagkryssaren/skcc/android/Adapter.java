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
import java.util.ArrayList;


public class Adapter extends BaseAdapter implements AdapterView.OnItemClickListener{


    //Create model classes
    public TfLiteModel tfLiteModel;
    public TfMobileModel tfMobileModel;
    private Context c;


    Adapter(@NonNull Activity a) {
        c = a.getApplicationContext();
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
            imageView = new ImageView(c);
            imageView.setLayoutParams(parent.getLayoutParams());
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(drawables[position]);
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
        MainActivity.syncValues(tfMobileModel);
        MainActivity.syncValues(tfLiteModel);

    }

    void runInference(int position, boolean tflite, boolean tfmobile, boolean display){
        Bitmap input = BitmapFactory.decodeResource(c.getResources(), drawables[position]);
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
            c.startActivity(new Intent(c, DisplayActivity.class)
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
            R.drawable.img_48,  R.drawable.img_49,  R.drawable.img_50,  R.drawable.img_51,  R.drawable.img_52,  R.drawable.img_53,
            R.drawable.img_54,  R.drawable.img_55,  R.drawable.img_56,  R.drawable.img_57,  R.drawable.img_58,  R.drawable.img_59,
            R.drawable.img_60,  R.drawable.img_61,  R.drawable.img_62,  R.drawable.img_63,  R.drawable.img_64,  R.drawable.img_65,
            R.drawable.img_66,  R.drawable.img_67,  R.drawable.img_68,  R.drawable.img_69,  R.drawable.img_70,  R.drawable.img_71,
            R.drawable.img_72,  R.drawable.img_73,  R.drawable.img_74,  R.drawable.img_75,  R.drawable.img_76,  R.drawable.img_77,
            R.drawable.img_78,  R.drawable.img_79,  R.drawable.img_80,  R.drawable.img_81,  R.drawable.img_82,  R.drawable.img_83,
            R.drawable.img_84,  R.drawable.img_85,  R.drawable.img_86,  R.drawable.img_87,  R.drawable.img_88,  R.drawable.img_89,
            R.drawable.img_90,  R.drawable.img_91,  R.drawable.img_92,  R.drawable.img_93,  R.drawable.img_94,  R.drawable.img_95,
            R.drawable.img_96,  R.drawable.img_97,  R.drawable.img_98,  R.drawable.img_99,  R.drawable.img_100,  R.drawable.img_101,
            R.drawable.img_102,  R.drawable.img_103,  R.drawable.img_104,  R.drawable.img_105,  R.drawable.img_106,  R.drawable.img_107,
            R.drawable.img_108,  R.drawable.img_109,  R.drawable.img_110,  R.drawable.img_111,  R.drawable.img_112,  R.drawable.img_113,
            R.drawable.img_114,  R.drawable.img_115,  R.drawable.img_116,  R.drawable.img_117,  R.drawable.img_118,  R.drawable.img_119,
            R.drawable.img_120,  R.drawable.img_121,  R.drawable.img_122,  R.drawable.img_123,  R.drawable.img_124,  R.drawable.img_125,
            R.drawable.img_126,  R.drawable.img_127,  R.drawable.img_128,  R.drawable.img_129,  R.drawable.img_130,  R.drawable.img_131,
            R.drawable.img_132,  R.drawable.img_133,  R.drawable.img_134,  R.drawable.img_135,  R.drawable.img_136,  R.drawable.img_137,
            R.drawable.img_138,  R.drawable.img_139,  R.drawable.img_140,  R.drawable.img_141,  R.drawable.img_142,  R.drawable.img_143,
            R.drawable.img_144,  R.drawable.img_145,  R.drawable.img_146,  R.drawable.img_147,  R.drawable.img_148,  R.drawable.img_149};

    public int[] targets = {
            R.drawable.target_0,  R.drawable.target_1,  R.drawable.target_2,  R.drawable.target_3,  R.drawable.target_4,  R.drawable.target_5,
            R.drawable.target_6,  R.drawable.target_7,  R.drawable.target_8,  R.drawable.target_9,  R.drawable.target_10,  R.drawable.target_11,
            R.drawable.target_12,  R.drawable.target_13,  R.drawable.target_14,  R.drawable.target_15,  R.drawable.target_16,  R.drawable.target_17,
            R.drawable.target_18,  R.drawable.target_19,  R.drawable.target_20,  R.drawable.target_21,  R.drawable.target_22,  R.drawable.target_23,
            R.drawable.target_24,  R.drawable.target_25,  R.drawable.target_26,  R.drawable.target_27,  R.drawable.target_28,  R.drawable.target_29,
            R.drawable.target_30,  R.drawable.target_31,  R.drawable.target_32,  R.drawable.target_33,  R.drawable.target_34,  R.drawable.target_35,
            R.drawable.target_36,  R.drawable.target_37,  R.drawable.target_38,  R.drawable.target_39,  R.drawable.target_40,  R.drawable.target_41,
            R.drawable.target_42,  R.drawable.target_43,  R.drawable.target_44,  R.drawable.target_45,  R.drawable.target_46,  R.drawable.target_47,
            R.drawable.target_48,  R.drawable.target_49,  R.drawable.target_50,  R.drawable.target_51,  R.drawable.target_52,  R.drawable.target_53,
            R.drawable.target_54,  R.drawable.target_55,  R.drawable.target_56,  R.drawable.target_57,  R.drawable.target_58,  R.drawable.target_59,
            R.drawable.target_60,  R.drawable.target_61,  R.drawable.target_62,  R.drawable.target_63,  R.drawable.target_64,  R.drawable.target_65,
            R.drawable.target_66,  R.drawable.target_67,  R.drawable.target_68,  R.drawable.target_69,  R.drawable.target_70,  R.drawable.target_71,
            R.drawable.target_72,  R.drawable.target_73,  R.drawable.target_74,  R.drawable.target_75,  R.drawable.target_76,  R.drawable.target_77,
            R.drawable.target_78,  R.drawable.target_79,  R.drawable.target_80,  R.drawable.target_81,  R.drawable.target_82,  R.drawable.target_83,
            R.drawable.target_84,  R.drawable.target_85,  R.drawable.target_86,  R.drawable.target_87,  R.drawable.target_88,  R.drawable.target_89,
            R.drawable.target_90,  R.drawable.target_91,  R.drawable.target_92,  R.drawable.target_93,  R.drawable.target_94,  R.drawable.target_95,
            R.drawable.target_96,  R.drawable.target_97,  R.drawable.target_98,  R.drawable.target_99,  R.drawable.target_100,  R.drawable.target_101,
            R.drawable.target_102,  R.drawable.target_103,  R.drawable.target_104,  R.drawable.target_105,  R.drawable.target_106,  R.drawable.target_107,
            R.drawable.target_108,  R.drawable.target_109,  R.drawable.target_110,  R.drawable.target_111,  R.drawable.target_112,  R.drawable.target_113,
            R.drawable.target_114,  R.drawable.target_115,  R.drawable.target_116,  R.drawable.target_117,  R.drawable.target_118,  R.drawable.target_119,
            R.drawable.target_120,  R.drawable.target_121,  R.drawable.target_122,  R.drawable.target_123,  R.drawable.target_124,  R.drawable.target_125,
            R.drawable.target_126,  R.drawable.target_127,  R.drawable.target_128,  R.drawable.target_129,  R.drawable.target_130,  R.drawable.target_131,
            R.drawable.target_132,  R.drawable.target_133,  R.drawable.target_134,  R.drawable.target_135,  R.drawable.target_136,  R.drawable.target_137,
            R.drawable.target_138,  R.drawable.target_139,  R.drawable.target_140,  R.drawable.target_141,  R.drawable.target_142,  R.drawable.target_143,
            R.drawable.target_144,  R.drawable.target_145,  R.drawable.target_146,  R.drawable.target_147,  R.drawable.target_148,  R.drawable.target_149};

}
