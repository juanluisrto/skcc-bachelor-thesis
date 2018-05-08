package com.slagkryssaren.skcc.android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.slagkryssaren.skcc.android.BaseActivity;
import com.slagkryssaren.skcc.android.R;

public class CameraActivity extends BaseActivity {

    private ImageView photoTflite, photoTfMobile;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.photoTflite = (ImageView) this.findViewById(R.id.photoTfLite);
        this.photoTfMobile = (ImageView) this.findViewById(R.id.photoTfMobile);
        dispatchTakePictureIntent();
    }
    @Override
    public  void onResume() {
        super.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();
        MainActivity.adapter.tfLiteModel.resetDefaultDimensions();
        MainActivity.adapter.tfMobileModel.resetDefaultDimensions();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            MainActivity.adapter.tfLiteModel.adaptDimensions(imageBitmap);
            MainActivity.adapter.tfMobileModel.adaptDimensions(imageBitmap);
            Bitmap outputLite = MainActivity.adapter.tfLiteModel.predictImage(imageBitmap);
            //Bitmap outputMobile = MainActivity.adapter.tfMobileModel.predictImage(imageBitmap);
            photoTflite.setImageBitmap(outputLite);
            //photoTfMobile.setImageBitmap(outputMobile);
        }
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_camera;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.camera;
    }
}
