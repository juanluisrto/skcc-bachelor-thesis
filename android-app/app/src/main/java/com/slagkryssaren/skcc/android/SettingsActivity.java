package com.slagkryssaren.skcc.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.slagkryssaren.skcc.android.models.TfLiteModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SettingsActivity extends BaseActivity {

    private int numberOfImages = 30;
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
        SeekBar seekbar = findViewById(R.id.seekBar);
        seekbar.setMax(150);
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
                if (checkBoxLite.isChecked() || checkBoxMobile.isChecked()) {
                    ArrayList<Integer> list = new ArrayList<Integer>(150);
                    for (int i = 0; i < 150; i++) {
                        list.add(new Integer(i));
                    }
                    Collections.shuffle(list);
                    for (int i = 0; i < numberOfImages; i++) {
                        MainActivity.adapter.runInference(list.get(i), checkBoxLite.isChecked(), checkBoxMobile.isChecked(), false);
                        Toast.makeText(c, String.valueOf(numberOfImages - i - 1) + " left", Toast.LENGTH_SHORT).show();
                    }
                    if (checkBoxLite.isChecked()) {
                        MainActivity.syncValues(MainActivity.adapter.tfLiteModel);
                    }
                    if (checkBoxMobile.isChecked()) {
                        MainActivity.syncValues(MainActivity.adapter.tfMobileModel);
                    }
                }
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.exportValuesToExternalStorage();
                    Toast.makeText(c,"Data exported succesfully",Toast.LENGTH_LONG).show();
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
                    MainActivity.adapter.tfLiteModel.neuralAPI = neuralAPICheckbox.isChecked();
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
}
