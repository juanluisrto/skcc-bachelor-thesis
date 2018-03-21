package com.slagkryssaren.skcc.android;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.ceryle.fitgridview.FitGridView;
import lecho.lib.hellocharts.model.PointValue;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final int REQUEST_TAKE_PHOTO = 1;
    ToggleButton toggle;

    //Create model classes
    public TfLiteModel tfLiteModel;
    public TfMobileModel tfMobileModel;
    public Adapter adapter;
    public FitGridView gridView;

    SharedPreferences  mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrefs = getSharedPreferences("chart",MODE_PRIVATE);


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

    public void runStatsActivity(View v){
        Intent intent = new Intent(this,StatsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);



        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String jsonTfLiteValues = gson.toJson(tfLiteModel.values);
        String jsonTfMobileValues = gson.toJson(tfMobileModel.values);
        prefsEditor.putString("tflite", jsonTfLiteValues);
        prefsEditor.putString("tfMobile", jsonTfMobileValues);
        prefsEditor.commit();
        startActivity(intent);

    }

}
