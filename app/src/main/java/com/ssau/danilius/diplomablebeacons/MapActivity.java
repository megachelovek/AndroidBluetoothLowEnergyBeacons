package com.ssau.danilius.diplomablebeacons;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapActivity extends AppCompatActivity {
    private Button enableZoomBtn;
    private DrawableMapView drawbleView;
    private CustomMapView touchImageView;

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        drawbleView = (DrawableMapView) findViewById(R.id.drawble_view);
        enableZoomBtn = (Button) findViewById(R.id.enable_zoom);
        touchImageView = (CustomMapView) findViewById(R.id.zoom_iv);
        drawbleView.setDrawingEnabled(false);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_main:
                                Intent mapIntent = new Intent(MapActivity.this, MainActivity.class);
                                startActivity(mapIntent);
                                return true;
                            case R.id.navigation_map:
                                return true;
                            case R.id.navigation_settings:
                                Intent settingIntent = new Intent(MapActivity.this, SettingsActivity.class);
                                startActivity(settingIntent);
                                return true;
                        }
                        return false;
                    }
                };
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Button.OnClickListener clickButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.enable_zoom:
                        if (enableZoomBtn.getText().equals("disable zoom")) {
                            touchImageView.setZoomEnable(false);
                            drawbleView.setDrawingEnabled(true);
                            enableZoomBtn.setText("enable zoom");
                        } else {
                            touchImageView.setZoomEnable(true);
                            drawbleView.setDrawingEnabled(false);
                            enableZoomBtn.setText("disable zoom");
                        }
                        break;

                    default:
                        break;
                }
            }
        };
        enableZoomBtn.setOnClickListener(clickButtonListener);

    }

}