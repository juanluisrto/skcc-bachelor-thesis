package com.slagkryssaren.skcc.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import co.ceryle.fitgridview.FitGridView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final int REQUEST_TAKE_PHOTO = 1;
    ToggleButton toggle;

    //Create model class
    public TfLiteModel tfLiteModel;
    public TfMobileModel tfMobileModel;
    public Adapter adapter;
    public FitGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            tfLiteModel = new TfLiteModel(this);
            tfMobileModel = new TfMobileModel(this);
            gridView = (FitGridView) findViewById(R.id.gridView);
            adapter = new Adapter(this);
            adapter.model = tfMobileModel;
            gridView.setFitGridAdapter(adapter);
        } catch (IOException e) {
            Log.w(TAG, e);
        }

        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setChecked(false);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    adapter.model = tfLiteModel;
                } else {
                    // The toggle is disabled
                    adapter.model = tfMobileModel;
                }
            }
        });
    }

    public void rebindGridView(View v) {
        gridView.setFitGridAdapter(adapter);
        gridView.update();
    }

    //Makes the intent which calls the camera to make the photo.
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        CharSequence timeStamp = DateFormat.format("yyyyMMdd_HHmmss", new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        image.getAbsolutePath();
        return image;
    }

}
