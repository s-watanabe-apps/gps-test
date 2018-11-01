package com.example.gps_test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements LocationListener {

    EditText textGpsLocation;

    LocationManager locationManager;

    private static final long MIN_DISTANCE_UPDATES = 0;
    private static final long MIN_TIME_UPDATES = 0;

    private static final int PROVIDER_NETWORK = 1;
    private static final int PROVIDER_GPS = 2;
    private static final int PROVIDER_PASSIVE = 3;

    private static final int REQUEST_PERMISSION_PROVIDER_NETWORK = 1;
    private static final int REQUEST_PERMISSION_PROVIDER_GPS = 2;
    private static final int REQUEST_PERMISSION_PROVIDER_PASSIVE = 3;

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textGpsLocation = findViewById(R.id.gps_location);

        // Network Providerを使って位置情報を取得
        findViewById(R.id.gps_start_network).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION_PROVIDER_NETWORK);
                    return;
                } else{
                    locationStart(PROVIDER_NETWORK);
                }
            }
        });

        // Gps Providerを使って位置情報を取得
        findViewById(R.id.gps_start_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION_PROVIDER_GPS);
                    return;
                } else{
                    locationStart(PROVIDER_GPS);
                }
            }
        });

        // Passive Providerを使って位置情報を取得
        findViewById(R.id.gps_start_passive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION_PROVIDER_PASSIVE);
                    return;
                } else{
                    locationStart(PROVIDER_PASSIVE);
                }
            }
        });
    }

    /**
     * 許可ダイアログの結果がここに返ってくる
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int provider;
        if (requestCode == REQUEST_PERMISSION_PROVIDER_NETWORK) {
            provider = PROVIDER_NETWORK;
        } else if(requestCode == REQUEST_PERMISSION_PROVIDER_GPS) {
            provider = PROVIDER_GPS;
        } else if(requestCode == REQUEST_PERMISSION_PROVIDER_PASSIVE) {
            provider = PROVIDER_PASSIVE;
        } else{
            addText("requestCode[" + requestCode + "] Unknown!!");
            return;
        }

        addText("grantResults[0]:" + grantResults[0]);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationStart(provider);
        }
    }

    /**
     * 位置情報の取得開始
     * @param provider
     */
    public void locationStart(int provider) {
        addText("Get Location Start");

        try{
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean isEnabled;
            String locationProvider;
            if(provider == PROVIDER_NETWORK) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
                isEnabled = locationManager.isProviderEnabled(locationProvider);
                addText("isProviderEnabled[NETWORK_PROVIDER] => " + String.valueOf(isEnabled));
            } else if(provider == PROVIDER_GPS){
                locationProvider = LocationManager.GPS_PROVIDER;
                isEnabled = locationManager.isProviderEnabled(locationProvider);
                addText("isProviderEnabled[GPS_PROVIDER] => " + String.valueOf(isEnabled));
            } else if(provider == PROVIDER_PASSIVE) {
                locationProvider = LocationManager.PASSIVE_PROVIDER;
                isEnabled = locationManager.isProviderEnabled(locationProvider);
                addText("isProviderEnabled[PASSIVE_PROVIDER] => " + String.valueOf(isEnabled));
            } else{
                throw new Exception("Failed!! Provider[" + provider + "] Unknown.");
            }

            // 指定されたプロバイダが使用できるか
            if(!isEnabled) {
                throw new Exception("Failed!! This provider can not be used.");
            }

            // 位置情報取得のための権限が許可されているか
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                throw new Exception("Failed!! Permission to use location information is not allowed.");
            }

            addText("min_time:" + MIN_TIME_UPDATES);
            addText("min_distance:" + MIN_DISTANCE_UPDATES);
            locationManager.requestLocationUpdates(locationProvider, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, this);

            if (locationManager != null) {
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (location != null) {
                    addText("Success!! lat:" + location.getLatitude() + ", lon:" + location.getLongitude());
                } else{
                    throw new Exception("Failed!! location acquisition failure.");
                }
            }

        } catch(Exception e) {
            addText(e.getMessage());
        }

        addText("Get Location End");
        addText("------------------------------------");
    }

    /**
     * ログ
     * @param text
     */
    public void addText(String text) {
        Log.d(getApplicationContext().getPackageName(), text);
        textGpsLocation.setText(textGpsLocation.getText().toString().trim().equals("") ? text : textGpsLocation.getText() + "\n" + text);
        textGpsLocation.setSelection(textGpsLocation.getText().length());
    }

    /**
     * onLocationChanged
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        addText("locationChanged => lat:" + location.getLatitude() + ", lon:" + location.getLongitude());
    }

    /**
     * onStatusChanged
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    /**
     * onProviderEnabled
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    /**
     * onProviderDisabled
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }
}
