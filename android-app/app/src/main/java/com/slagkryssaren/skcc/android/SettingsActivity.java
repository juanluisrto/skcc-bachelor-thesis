package com.slagkryssaren.skcc.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
