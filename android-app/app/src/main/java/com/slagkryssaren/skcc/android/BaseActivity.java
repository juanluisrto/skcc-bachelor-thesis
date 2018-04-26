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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

    }

    /*BottomNavigationView.OnNavigationItemSelectedListener returnListener (){
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.e("inside","inside");
                navigationView.postDelayed(() -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.stats) {
                        startActivity(new Intent(self, StatsActivity.class));
                    } else if (itemId == R.id.pics) {
                        startActivity(new Intent(self, MainActivity.class));
                    } else if (itemId == R.id.settings) {
                        startActivity(new Intent(self, SettingsActivity.class));
                    }
                    finish();
                }, 300);
                return true;
            }
        };

        }*/


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
        Log.e("inside","finally");
        navigationView.postDelayed(() -> {
            int itemId = item.getItemId();
            if (itemId == R.id.stats) {
                startActivity(new Intent(this, StatsActivity.class));
            } else if (itemId == R.id.pics) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (itemId == R.id.settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            finish();
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

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();


}