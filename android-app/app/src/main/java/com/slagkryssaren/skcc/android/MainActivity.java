package com.slagkryssaren.skcc.android;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.GridView;
import com.slagkryssaren.skcc.android.models.TfLiteModel;
import com.slagkryssaren.skcc.android.models.TfMobileModel;

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
        gridview = (GridView) findViewById(R.id.gridview);
        adapter = new Adapter(this,this);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(adapter);

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


