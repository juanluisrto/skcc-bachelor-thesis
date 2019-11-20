package com.slagkryssaren.skcc.android;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;
    protected BottomNavigationView.OnNavigationItemSelectedListener listener;
    protected int currentActivity = getNavigationMenuItemId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Log.e("inside","finally");
        navigationView.postDelayed(() -> {
            int itemId = item.getItemId();
            if (itemId == R.id.stats && itemId != currentActivity) {
                startActivity(new Intent(this, StatsActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (itemId == R.id.pics && itemId != currentActivity) {
                startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (itemId == R.id.settings && itemId != currentActivity) {
                startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (itemId == R.id.camera && itemId != currentActivity) {
                startActivity(new Intent(this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }
            if (itemId != currentActivity) {
                finish();
            }
        }, 300);
        return true;
    }

    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = navigationView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if(this.getClass() == DisplayActivity.class){
            super.onBackPressed();
        }
    }

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();


}

