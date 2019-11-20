package com.slagkryssaren.skcc.android;
import android.Manifest;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.slagkryssaren.skcc.android.models.Model;
import com.slagkryssaren.skcc.android.models.TfLiteModel;
import com.slagkryssaren.skcc.android.models.TfMobileModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import lecho.lib.hellocharts.model.PointValue;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static Adapter adapter;
    public GridView gridview;
    static SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("chart", MODE_PRIVATE);
        gridview = (GridView) findViewById(R.id.gridview);
        adapter = new Adapter(this);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(adapter);
        gridview.setVerticalSpacing(gridview.getHorizontalSpacing());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        MainActivity.syncValues(adapter.tfMobileModel);
        MainActivity.syncValues(adapter.tfLiteModel);
    }



    @Override
    int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.pics;
    }

    public static void syncValues(Model m){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<PointValue>>(){}.getType();
        String oldJson = mPrefs.getString(m.getClass().getName(), "");;

        ArrayList<PointValue> savedValues = gson.fromJson(oldJson,listType);
        if(savedValues!=null) {
            for (PointValue s : savedValues) {
                if (!m.values.contains(s)) {
                    m.values.add(s);
                }
            }
        }
            //m.values.addAll(savedValues);}

        String newJson = gson.toJson(m.values);
        mPrefs.edit().putString(m.getClass().getName(),newJson).commit();
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    public static File getFileInDownloadsDir() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "skccDataExport_pixel_" + new Random().nextInt(1000) +".txt");
        return file;
    }

    static void exportValuesToExternalStorage() throws IOException {
        if (isExternalStorageWritable()){
            File file = getFileInDownloadsDir();
            //file.mkdirs();
            file.createNewFile();
            try
            {
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append("Device:" + android.os.Build.MODEL + "\n");
                myOutWriter.append("NeuralNetworks API enabled:" + String.valueOf(adapter.tfLiteModel.neuralAPI) + "\n");
                myOutWriter.append("Input image size: " + String.valueOf(Model.DIM_IMG_SIZE_IN_X)
                                    + "x" + String.valueOf(Model.DIM_IMG_SIZE_IN_Y) + "\n\n");
                myOutWriter.append("\n\nTfMobile_160px\n");
                for (PointValue p : adapter.tfMobileModel.values_160){
                    String data = p.getX() + "  " + p.getY() + "\n";
                    myOutWriter.append(data);
                }
                myOutWriter.append("\n\nTfMobile_240px\n");
                for (PointValue p : adapter.tfMobileModel.values_240){
                    String data = p.getX() + "  " + p.getY() + "\n";
                    myOutWriter.append(data);
                }
                myOutWriter.append("\n\nTfMobile_320px\n");
                for (PointValue p : adapter.tfMobileModel.values_320){
                    String data = p.getX() + "  " + p.getY() + "\n";
                    myOutWriter.append(data);
                }
                myOutWriter.append("\n\nTfLite_160px\n");
                for (PointValue p : adapter.tfLiteModel.values_160){
                    String data = p.getX() + "  " + p.getY() + "\n";
                    myOutWriter.append(data);
                }
                myOutWriter.append("\n\nTfLite_240px\n");
                for (PointValue p : adapter.tfLiteModel.values_240){
                    String data = p.getX() + "  " + p.getY() + "\n";
                    myOutWriter.append(data);
                }
                myOutWriter.append("\n\nTfLite_320px\n");
                for (PointValue p : adapter.tfLiteModel.values_320){
                    String data = p.getX() + "  " + p.getY() + "\n";
                    myOutWriter.append(data);
                }
                myOutWriter.close();

                fOut.flush();
                fOut.close();
            }
            catch (IOException e)
            {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
    }

    static void resetValues(){
        adapter.tfLiteModel.values.clear();
        adapter.tfMobileModel.values.clear();
        mPrefs.edit().remove(TfMobileModel.class.getName())
                     .remove(TfLiteModel.class.getName())
                     .commit();
        //syncValues(adapter.tfLiteModel);
        //syncValues(adapter.tfMobileModel);
    }

}


