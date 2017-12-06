package com.slagkryssaren.skcc.android;

/**
 * Created by juanl on 21/11/2017.
 */

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import co.ceryle.fitgridview.FitGridAdapter;

class Adapter extends FitGridAdapter {

    private int[] drawables = {
            R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4,
            R.drawable.img_5, R.drawable.img_6, R.drawable.img_7, R.drawable.img_8,
            R.drawable.img_9, R.drawable.img_10, R.drawable.img_11, R.drawable.img_12};

    private Context context;

    Adapter(Context context) {
        super(context, R.layout.grid_item_iv);
        this.context = context;
    }

    @Override
    public void onBindView(final int position, View itemView) {
        ImageView iv = (ImageView) itemView.findViewById(R.id.grid_item_iv);
        iv.setImageResource(drawables[position]);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}