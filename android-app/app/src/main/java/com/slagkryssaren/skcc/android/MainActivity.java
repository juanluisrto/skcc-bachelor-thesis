package com.slagkryssaren.skcc.android;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import java.io.IOException;
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    //Create model classes
    public TfLiteModel tfLiteModel;
    public TfMobileModel tfMobileModel;
    public Adapter adapter;
    public GridView gridview;
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("chart", MODE_PRIVATE);
        //navigationView.getMenu().getItem(2).setChecked(true);
        gridview = (GridView) findViewById(R.id.gridview);
        adapter = new Adapter(this);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                adapter.getItem(position);
            }

        });

        try {
            tfLiteModel = new TfLiteModel(this);
            tfMobileModel = new TfMobileModel(this);
            adapter.model = tfLiteModel;
        } catch (IOException e) {
            Log.w(TAG, e);
        }

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.pics;
    }


}


