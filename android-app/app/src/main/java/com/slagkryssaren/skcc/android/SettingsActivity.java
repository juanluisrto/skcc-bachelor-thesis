package com.slagkryssaren.skcc.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.slagkryssaren.skcc.android.models.Model;
import com.slagkryssaren.skcc.android.models.TfLiteModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.slagkryssaren.skcc.android.MainActivity.adapter;

public class SettingsActivity extends BaseActivity {

    private int numberOfImages = 10;
    private Context c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = getApplicationContext();
        CheckBox checkBoxLite = findViewById(R.id.checkLite);
        CheckBox checkBoxMobile = findViewById(R.id.checkMobile);
        CheckBox neuralAPICheckbox = findViewById(R.id.AnnAPI);
        Button runButton = findViewById(R.id.runInference);
        Button exportButton = findViewById(R.id.exportData);
        Button resetButton = findViewById(R.id.resetData);
        ProgressBar spinner = findViewById(R.id.loading);
        TextView resolution = findViewById(R.id.resolution);
        resolution.setText(resolution.getText() + String.valueOf(Model.DIM_IMG_SIZE_IN_X) + "x" + String.valueOf(Model.DIM_IMG_SIZE_IN_X));
        SeekBar seekbar = findViewById(R.id.seekBar);
        seekbar.setMax(50);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView number = findViewById(R.id.numberOfImages);
                number.setText(String.valueOf(i));
                numberOfImages = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                if (checkBoxLite.isChecked() || checkBoxMobile.isChecked()) {
                   /*  ArrayList<Integer> list = new ArrayList<Integer>(MainActivity.adapter.drawables.length);
                    for (int i = 0; i < MainActivity.adapter.drawables.length; i++) {
                        list.add(new Integer(i));
                    }
                    Collections.shuffle(list);*/

                    /*
                        Reasignar drawable con drawable_240 /320
                        cambiar tamaño de pixeles.
                        ejecutar
                        ejecutar otra vez solo  tflite con NNAPI activada
                         */
                    //Toast.makeText(c, String.valueOf(numberOfImages - i - 1) + " left", Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < numberOfImages; i++) {
                        adapter.runInference(i, checkBoxLite.isChecked(), checkBoxMobile.isChecked(), false);
                    }
                    adapter.tfLiteModel.values_160.addAll(adapter.tfLiteModel.values);
                    adapter.tfLiteModel.values.clear();
                    adapter.tfMobileModel.values_160.addAll(adapter.tfMobileModel.values);
                    adapter.tfMobileModel.values.clear();


                    int [] drawables_aux = new int[adapter.drawables.length];
                    System.arraycopy( adapter.drawables, 0, drawables_aux, 0, adapter.drawables.length );

                    //Starts iteration for 240px
                    adapter.tfMobileModel.changeDefaultDimensions(240);
                    adapter.tfLiteModel.changeDefaultDimensions(240);
                    System.arraycopy( adapter.drawables_240, 0, adapter.drawables, 0, adapter.drawables.length );
                    for (int i = 0; i < numberOfImages; i++) {
                        adapter.runInference(i, checkBoxLite.isChecked(), checkBoxMobile.isChecked(), false);
                    }
                    adapter.tfLiteModel.values_240.addAll(adapter.tfLiteModel.values);
                    adapter.tfLiteModel.values.clear();
                    adapter.tfMobileModel.values_240.addAll(adapter.tfMobileModel.values);
                    adapter.tfMobileModel.values.clear();

                    //Starts iteration for 320px
                    adapter.tfMobileModel.changeDefaultDimensions(320);
                    adapter.tfLiteModel.changeDefaultDimensions(320);
                    System.arraycopy( adapter.drawables_320, 0, adapter.drawables, 0, adapter.drawables.length );
                    for (int i = 0; i < numberOfImages; i++) {
                        adapter.runInference(i, checkBoxLite.isChecked(), checkBoxMobile.isChecked(), false);
                    }
                    adapter.tfLiteModel.values_320.addAll(adapter.tfLiteModel.values);
                    adapter.tfLiteModel.values.clear();
                    adapter.tfMobileModel.values_320.addAll(adapter.tfMobileModel.values);
                    adapter.tfMobileModel.values.clear();

                    adapter.tfMobileModel.resetDefaultDimensions();
                    adapter.tfLiteModel.resetDefaultDimensions();
                    adapter.tfLiteModel.values.addAll(adapter.tfLiteModel.values_160);
                    adapter.tfMobileModel.values.addAll(adapter.tfMobileModel.values_160);
                    System.arraycopy(drawables_aux, 0, adapter.drawables, 0, adapter.drawables.length );

                    if (checkBoxLite.isChecked()) {
                        MainActivity.syncValues(adapter.tfLiteModel);
                    }
                    if (checkBoxMobile.isChecked()) {
                        MainActivity.syncValues(adapter.tfMobileModel);
                    }
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(c, "Done!", Toast.LENGTH_LONG).show();
                }
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.exportValuesToExternalStorage();
                    Toast.makeText(c,"Data exported succesfully to Downloads",Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MainActivity.resetValues();
                Toast.makeText(c,"The data points have been succesfully erased",Toast.LENGTH_LONG).show();
            }
        });
        neuralAPICheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    adapter.tfLiteModel.neuralAPI = neuralAPICheckbox.isChecked();
            }
        });

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_settings;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.settings;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView resolution = findViewById(R.id.resolution);
        resolution.setText("Image resolution: " + String.valueOf(Model.DIM_IMG_SIZE_IN_X) + "x" + String.valueOf(Model.DIM_IMG_SIZE_IN_X));

    }
}
