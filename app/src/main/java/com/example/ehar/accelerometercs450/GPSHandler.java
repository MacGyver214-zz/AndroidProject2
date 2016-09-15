package com.example.ehar.accelerometercs450;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.widget.Toast;

import java.util.Observable;

/**
 * Created by MacGyver on 9/14/2016.
 * references https://developer.android.com/guide/topics/location/strategies.html
 * and https://www.quora.com/How-can-I-get-my-location-latitude-longitude-using-Location-Manager-in-Android-Studio
 */
public class GPSHandler extends Observable implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private LocationManager locationManager = null;

    private Context mainActivity;
    private MainActivity activity;

    //values to check for service statuses
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    //The two permissions I care about
    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    Location location;
    double latitude, longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; //every 0 meters
    private static final long MIN_TIME_FOR_UPDATES = 0; //every 15 secs


    public GPSHandler(Context mainActivity,MainActivity activity) {
        this.mainActivity = mainActivity;
        this.activity = activity;
        this.locationManager = (LocationManager) mainActivity.getSystemService(Activity.LOCATION_SERVICE);

        getLocation();
        setChanged();
        notifyObservers();
    }

    public Location getLocation() {

        try {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                //there is no location service available and no coordinates can be founr
            } else {

                //the .requestLocationUpdate methods below required a permissions check to satisfy the API.
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(activity, permissions, 0);
                }

                //attempt network location first
                if (isNetworkEnabled) {

                    //begin listening for location updates
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_FOR_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                //attempt GPS location second, as it will be the more accurate
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_FOR_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation();
        setChanged();
        notifyObservers();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == 0 && grantResults[1] == 0) {
            getLocation();
        }
    }
}
