package com.example.runningapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class RunActivity extends AppCompatActivity implements LocationListener {
    private double totalDistance;
    private LocationManager locationManager;
    private FragmentMap fragmentMap;
    private Polyline route;
    private List<LatLng> points;
    private Location lastKnownLocation;
    private LocationManager.CallBack_Location callBack_location;
    private final double MAX_DISTANCE_TO_TOAST = 0.1;
    private final int MIN_POINTS = 5;

    private MaterialButton run_BTN_start;
    private MaterialButton run_BTN_stop;
    private MaterialTextView run_TXT_distance;
    private MaterialTextView run_TXT_time;
    private boolean isOnRun;
    private TimerTask timerTask;
    private long timeElapsed;
    private Toolbar run_toolbar;


//    Location attributes
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient mFusedLocationClient;

    public RunActivity setCallBack_location() {
        callBack_location = new LocationManager.CallBack_Location() {
            @Override
            public void setCurrentLocation(double latitude, double longitude) {
                lastKnownLocation = new Location("");
                lastKnownLocation.setLatitude(latitude);
                lastKnownLocation.setLongitude(longitude);
            }
        };
        return this;
    }
    @Override
    protected void onStart() {
        super.onStart();
        fragmentMap.getActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        fragmentMap = new FragmentMap();
        fragmentMap.setActivity(this);
        getSupportFragmentManager().beginTransaction().add(R.id.run_FRAME_map, fragmentMap).commit();

        hideSystemUI(this);
        findViews();
        activateLocationPermission();
        initViews();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastLocation();
            } else {
                // Permission denied
                // You can show a message to the user or handle the case in any other way
                showLocationError();
            }
        }
    }
    private void showLocationError() {
        Toast.makeText(this, "This app needs location permission to function correctly", Toast.LENGTH_LONG).show();
    }
    private void activateLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user
                // as to why the permission is needed
                showLocationError();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Permission has already been granted
            getLastLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Use the location data here
                            lastKnownLocation = location;
                        } else {
                            // Location data is not available
                            // You can show a message to the user or handle the case in any other way
                            showLocationError();
                        }
                    }
                });
    }

    private void findViews() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        run_BTN_start = findViewById(R.id.run_BTN_start);
        run_BTN_stop = findViewById(R.id.run_BTN_stop);
        run_TXT_time = findViewById(R.id.run_TXT_time);
        run_TXT_distance = findViewById(R.id.run_TXT_distance);
        run_toolbar = findViewById(R.id.run_toolbar);
        setSupportActionBar(run_toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        totalDistance = 0;

    }
    private void initViews() {
        locationManager =  new LocationManager(this);
        locationManager.getCurrentLocation(callBack_location);
        points = new ArrayList<>();
        run_BTN_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                run_BTN_start.setVisibility(View.INVISIBLE);
                run_BTN_stop.setVisibility(View.VISIBLE);
                isOnRun = true;
                timeElapsed = 0;
                startTimer();
                startRun();
            }
        });
        run_BTN_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRun();
            }
        });
        run_TXT_distance.setText("" + totalDistance + " KM");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Run");
        run_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void stopRun() {
        if (this.points.isEmpty() || this.points.size() < MIN_POINTS) {
            showDialog();
            return;
        }
        isOnRun = false;
        this.timerTask.cancel();
        run_BTN_start.setVisibility(View.VISIBLE);
        run_BTN_stop.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this,SaveRunActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SaveRunActivity.RUN_DISTANCE,this.run_TXT_distance.getText().toString());
        bundle.putString(SaveRunActivity.RUN_TIME,this.run_TXT_time.getText().toString());
        intent.putExtra("Bundle", bundle);
        intent.putExtra(SaveRunActivity.RUN_ROUTE, new Gson().toJson(this.points));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Not moving yet?")
                .setMessage("Runner needs a longer run activity to upload. Please continue or start over")
                .setPositiveButton("Resume", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                    run_BTN_start.setVisibility(View.VISIBLE);
                    run_BTN_stop.setVisibility(View.INVISIBLE);
                    onBackPressed();
                })
                .create().show();
    }


    private void startTimer(){
        Timer timer = new Timer();
        long DELAY = 1000;
        timer.scheduleAtFixedRate(timerTask = new TimerTask() {
            @Override
            public void run() {
                // increment the time elapsed
                timeElapsed++;
                // update the timer text view with the new time
                runOnUiThread(() -> {
                    if(isOnRun){
                        run_TXT_time.setText(getFormattedTime(timeElapsed));
                    }
                   else {
                       finish();
                       return;
                    }
                });
            }
        }, 0, DELAY);
    }

    private String getFormattedTime(long timeInSeconds) {
        int hours = (int) (timeInSeconds / (3600));
        int minutes = (int) (timeInSeconds % (3600)) / (60);
        int seconds = (int) (timeInSeconds % (60));
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void startRun() {
        if (isLocationEnabled()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
                fragmentMap.setCallBack_map(new CallBack_Map() {
                    @Override
                    public void onCallBack() {
                        fragmentMap.zoom(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                    }
                });
                startTracking();
            } else {
                // Permission not granted, show a message to the user
                Toast.makeText(this, "Location permission is needed to start the run", Toast.LENGTH_SHORT).show();
                openAppSettings();
            }
        } else {
            // Location service is not enabled, show a message to the user
            Toast.makeText(this, "Location service is not enabled", Toast.LENGTH_SHORT).show();
            openLocationSettings();
        }
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                // Update the tracker with the new location data
                updateTracker(location);
            }
        };
    };

    @SuppressLint("MissingPermission")
    private void startTracking() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setFastestInterval(3000); // 3 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }
    /**
     * use this function when trying to use location and the permission not granted
     */
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
    /**
     * use this function when trying to use location and the location service is disable
     */
    private void openLocationSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isLocationEnabled() {
        android.location.LocationManager locationManager = (android.location.LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
        updateTracker(location);
    }

    /**
     * updates the tracker and the route on the map based on the current location
     * @param location: the current user's location
     */
    private void updateTracker(Location location) {
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        points.add(current);

        // Update the route on the map
        if (points.size() > 1) {
            this.route = fragmentMap.updateRoute(points);
        }
        // Calculate the distance traveled
        double distance = calculateDistance();
        // Convert the distance to kilometers
        distance /= 1000;
        if(totalDistance - distance > MAX_DISTANCE_TO_TOAST){
            totalDistance += distance;
            Toast.makeText(this, "WOW! You Passed " + (int)MAX_DISTANCE_TO_TOAST*100 + " meters Successfully", Toast.LENGTH_SHORT).show();
        }
        // Display the distance on the screen
        run_TXT_distance.setText(String.format("%.2f KM",distance));
    }

    /**
     *  calculates the distance traveled during a run based on a list of location points.
     * @return: total running distance
     */
    private double calculateDistance() {
        double distance = 0;
        LatLng lastPoint = null;
        for (LatLng point : this.points) {
            if (lastPoint != null) {
                Location lastLocation = new Location("");
                lastLocation.setLatitude(lastPoint.latitude);
                lastLocation.setLongitude(lastPoint.longitude);
                Location currentLocation = new Location("");
                currentLocation.setLatitude(point.latitude);
                currentLocation.setLongitude(point.longitude);
                distance += lastLocation.distanceTo(currentLocation);
            }
            lastPoint = point;
        }
        return distance;
    }

    private void hideSystemUI(Activity activity) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        // Dim the Status and Navigation Bars
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE);

        // Without - cut out display
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI(this);
    }
}