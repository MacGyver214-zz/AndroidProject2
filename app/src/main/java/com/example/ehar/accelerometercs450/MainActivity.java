package com.example.ehar.accelerometercs450;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    public static final String X_ACCEL_VAL = "X";
    public static final String Y_ACCEL_VAL = "Y";
    public static final String Z_ACCEL_VAL = "Z";
    public static final String LAT_VAL = "LATITUDE";
    public static final String LON_VAL = "LONGITUDE";

    //accelerometer value displays
    TextView z_accel_view = null;
    TextView y_accel_view = null;
    TextView x_accel_view = null;

    //GPS value displays
    TextView latitude_view = null;
    TextView longitude_view = null;


    private AccelerometerHandler ah = null;
    private GPSHandler gps = null;

    String locationProvider = LocationManager.GPS_PROVIDER;

    //observed variables
    float[] xyz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.z_accel_view = (TextView) findViewById(R.id.z_accel_view);
        this.x_accel_view = (TextView) findViewById(R.id.x_accel_view);
        this.y_accel_view = (TextView) findViewById(R.id.y_accel_view);
        this.latitude_view = (TextView) findViewById(R.id.latitude_view);
        this.longitude_view = (TextView) findViewById(R.id.longitude_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.ah = new AccelerometerHandler(this);
        this.gps = new GPSHandler(this, this);
        this.ah.addObserver(this);
        this.gps.addObserver(this);

        String x = getPreferences(MODE_PRIVATE).getString(X_ACCEL_VAL, null);
        String y = getPreferences(MODE_PRIVATE).getString(Y_ACCEL_VAL, null);
        String z = getPreferences(MODE_PRIVATE).getString(Z_ACCEL_VAL, null);

        String lat = getPreferences(MODE_PRIVATE).getString(LAT_VAL, null);
        String lon = getPreferences(MODE_PRIVATE).getString(LON_VAL, null);

        x_accel_view.setText(x);
        y_accel_view.setText(y);
        z_accel_view.setText(z);

        latitude_view.setText(lat);
        longitude_view.setText(lon);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferences(MODE_PRIVATE).edit().putString(X_ACCEL_VAL, Float.toString(xyz[0])).apply();
        getPreferences(MODE_PRIVATE).edit().putString(Y_ACCEL_VAL, Float.toString(xyz[1])).apply();
        getPreferences(MODE_PRIVATE).edit().putString(Z_ACCEL_VAL, Float.toString(xyz[2])).apply();
        getPreferences(MODE_PRIVATE).edit().putString(LAT_VAL, Double.toString(gps.getLatitude())).apply();
        getPreferences(MODE_PRIVATE).edit().putString(LON_VAL, Double.toString(gps.getLongitude())).apply();
    }

    /* Overrides the method called by an observer to request information from an observant*/
    @Override
    public void update(Observable observable, Object o) {

        if (observable instanceof AccelerometerHandler) {
            xyz = (float[]) o;
            this.z_accel_view.setText(Float.toString(xyz[2]));
            this.y_accel_view.setText(Float.toString(xyz[1]));
            this.x_accel_view.setText(Float.toString(xyz[0]));
        }
        else{
            this.latitude_view.setText(Double.toString(gps.getLatitude()));
            this.longitude_view.setText(Double.toString(gps.getLongitude()));
        }
    }
}
