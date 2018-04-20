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


class Adapter extends BaseAdapter {

    private int[] drawables = {
            R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4,
            R.drawable.img_5, R.drawable.img_6, R.drawable.img_7, R.drawable.img_8,
            R.drawable.img_9, R.drawable.img_10, R.drawable.img_11, R.drawable.img_12};

    public Model model;
    private Context c;


    Adapter(Context context) {
        c = context;

    }
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(c);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(drawables[position]);
        Bitmap input = BitmapFactory.decodeResource(parent.getContext().getResources(), drawables[position]);
        input = Bitmap.createScaledBitmap(input, Model.DIM_IMG_SIZE_IN_X, Model.DIM_IMG_SIZE_IN_Y, false);
        Bitmap output = model.predictImage(input,position);
        imageView.setImageBitmap(output);
        return imageView;
    }

    public int getCount() {
        return drawables.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public void onBindView(final int position, final View itemView) {
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.grid_item_iv);
        imageView.setImageResource(drawables[position]);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap input = BitmapFactory.decodeResource(view.getContext().getResources(), drawables[position]);
                input = Bitmap.createScaledBitmap(input, Model.DIM_IMG_SIZE_IN_X, Model.DIM_IMG_SIZE_IN_Y, false);
                Bitmap output = model.predictImage(input,position);
                imageView.setImageBitmap(output);
            }
        });
    }

}
