//If you have the time, continue on Task 4.; Receive location updates
//https://google-developer-training.github.io/android-developer-advanced-course-practicals/unit-4-add-geo-features-to-your-apps/lesson-7-location/7-1-p-use-the-device-location/7-1-p-use-the-device-location.html#task2intro
package com.example.chidi.mylocationapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements FetchAddressTask.OnTaskCompleted{

    FetchAddressTask.OnTaskCompleted listen;
    TextView mLocationTextView;
    TextView tvtime;
    TextView tvlongitude;
    TextView tvlatitude;
    FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    Button btCoordinates;
    String TAG;
    int REQUEST_LOCATION_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationTextView = findViewById(R.id.textview_location);
        tvtime = findViewById(R.id.textTime);
        tvlongitude = findViewById(R.id.textViewLongitude);
        tvlatitude = findViewById(R.id.textViewLatitude);
        btCoordinates = findViewById(R.id.buttonCoordinates);

        btCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

    }



   @Override
    public void onTaskCompleted(String result) {
        // Update the UI
        mLocationTextView.setText(getString(R.string.address_text,
                result, System.currentTimeMillis()));
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
          //  Log.d(TAG, "getLocation: permissions granted");

            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                //the reverse geocoding async task is started..
                                new FetchAddressTask(MainActivity.this, MainActivity.this)
                                        .execute(location);
                                //

                                mLastLocation = location;
                                /*mLocationTextView.setText(
                                        getString(R.string.location_text,
                                                mLastLocation.getLatitude(),
                                                mLastLocation.getLongitude(),
                                                mLastLocation.getTime()));*/
                                tvtime.setText(
                                        getString(R.string.textViewTime,
                                                mLastLocation.getTime()
                                        ));

                                tvlongitude.setText(
                                        getString(R.string.textViewLongitude,
                                                mLastLocation.getLongitude()
                                                ));
                                tvlatitude.setText(
                                        getString(R.string.textViewLatitude,
                                                mLastLocation.getLatitude()
                                        ));


                            } else {
                                mLocationTextView.setText(R.string.no_location);
                            }
                        }
                    });

        }
      //  mLocationTextView.setText(getString(R.string.address_text, System.currentTimeMillis()));
        mLocationTextView.setText(getString(R.string.address_text, getString(R.string.loading),System.currentTimeMillis()));

    }
//This is not necessary, the code works perfectly without it, the method is actually never callec......
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
