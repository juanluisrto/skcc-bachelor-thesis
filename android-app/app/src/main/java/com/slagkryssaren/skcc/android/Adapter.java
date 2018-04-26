package com.slagkryssaren.skcc.android;

/**
 * Created by juanl on 21/11/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;


class Adapter extends BaseAdapter {

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

    public Model model;
    private Context c;
    private ArrayList<ImageView> imageViews = new ArrayList<>(150);


    Adapter(Context context) {
        c = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(c);
            imageView.setLayoutParams(parent.getLayoutParams());//new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
            imageViews.add(imageView);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(drawables[position]);
        return imageView;
    }

    public int getCount() {
        return drawables.length;
    }

    public Object getItem(int position) {
        ImageView imageView = imageViews.get(position);
        Bitmap input = BitmapFactory.decodeResource(c.getResources(), drawables[position]);
        input = Bitmap.createScaledBitmap(input, Model.DIM_IMG_SIZE_IN_X, Model.DIM_IMG_SIZE_IN_Y, false);
        Bitmap output = model.predictImage(input,position);
        imageView.setImageBitmap(output);
        //imageView.setLayoutParams();
        //imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(8, 8, 8, 8);
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


}
