package com.slagkryssaren.skcc.android;

/**
 * Created by juanl on 21/11/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import co.ceryle.fitgridview.FitGridAdapter;

class Adapter extends FitGridAdapter {

    private int[] drawables = {
            R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4,
            R.drawable.img_5, R.drawable.img_6, R.drawable.img_7, R.drawable.img_8,
            R.drawable.img_9, R.drawable.img_10, R.drawable.img_11, R.drawable.img_12};

    public Model model;

    Adapter(Context context) {
        super(context, R.layout.grid_item_iv);
    }

    @Override
    public void onBindView(final int position, final View itemView) {
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.grid_item_iv);
        imageView.setImageResource(drawables[position]);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap input = BitmapFactory.decodeResource(view.getContext().getResources(), drawables[position]);
                input = Bitmap.createScaledBitmap(input, Model.DIM_IMG_SIZE_IN_X, Model.DIM_IMG_SIZE_IN_Y, false);
                Bitmap output = model.predictImage(input);
                imageView.setImageBitmap(output);
            }
        });
    }

}