package com.slagkryssaren.skcc.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.slagkryssaren.skcc.android.models.Model;

import static com.slagkryssaren.skcc.android.MainActivity.adapter;

public class CameraActivity extends BaseActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView photoTflite, photoTfMobile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.photoTflite = (ImageView) this.findViewById(R.id.photoTfLite);
        this.photoTfMobile = (ImageView) this.findViewById(R.id.photoTfMobile);
        dispatchTakePictureIntent();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.tfLiteModel.resetDefaultDimensions();
        adapter.tfMobileModel.resetDefaultDimensions();
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
            int squareSize = Math.min(Model.DIM_IMG_SIZE_IN_X, Math.min(imageBitmap.getWidth(), imageBitmap.getHeight()));

            imageBitmap = Bitmap.createBitmap(imageBitmap,
                    imageBitmap.getWidth() / 2 - squareSize / 2,
                    imageBitmap.getHeight() / 2 - squareSize / 2,
                    squareSize,
                    squareSize);
            adapter.tfLiteModel.adaptDimensions(imageBitmap);
            adapter.tfMobileModel.adaptDimensions(imageBitmap);
            Bitmap outputLite = adapter.tfLiteModel.predictImage(imageBitmap);
            Bitmap outputMobile = adapter.tfMobileModel.predictImage(imageBitmap);
            photoTflite.setImageBitmap(outputLite);
            photoTfMobile.setImageBitmap(outputMobile);
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
