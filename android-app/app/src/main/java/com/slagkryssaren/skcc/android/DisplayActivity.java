package com.slagkryssaren.skcc.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bitmap outputTfLite = (Bitmap) intent.getParcelableExtra("outputTfLite");
        Bitmap outputTfMobile = (Bitmap) intent.getParcelableExtra("outputTfMobile");
        String timeTflite = intent.getStringExtra("timeTflite");
        String timeTfmobile = intent.getStringExtra("timeTfmobile");
        int originalDrawable = intent.getIntExtra("originalDrawable",0);
        int targetDrawable = intent.getIntExtra("targetDrawable",0);

        ImageView originalView = findViewById(R.id.original);
        ImageView targetView = findViewById(R.id.target);
        ImageView tfLiteView = findViewById(R.id.tflite);
        ImageView tfMobileView = findViewById(R.id.tfmobile);
        TextView textTimeTflite = findViewById(R.id.timeTflite);
        TextView textTimeTfmobile = findViewById(R.id.timeTfmobile);


        tfLiteView.setImageBitmap(outputTfLite);
        tfMobileView.setImageBitmap(outputTfMobile);

        originalView.setImageDrawable(getResources().getDrawable(originalDrawable));
        targetView.setImageDrawable(getResources().getDrawable(targetDrawable));
        textTimeTflite.setText(timeTflite);
        textTimeTfmobile.setText(timeTfmobile);

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_display;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.pics;
    }
}
