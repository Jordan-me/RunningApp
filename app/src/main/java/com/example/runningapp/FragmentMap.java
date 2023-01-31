package com.example.runningapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class FragmentMap extends Fragment {
    private SupportMapFragment supportMapFragment;
    private LatLng latLng;
    private double lat;
    private double lng;
    private GoogleMap mMap;
    private CallBack_Map callBack_map;
    private AppCompatActivity activity;

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }
    public void setCallBack_map(CallBack_Map callBack_map) {
        this.callBack_map = callBack_map;
        if(mMap != null)
        {
            callBack_map.onCallBack();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        findViews(view);
        initViews();

        return view;
    }

    public void zoom(double lat, double lng) {
        mMap.clear();
        this.lat = lat;
        this.lng = lng;

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(this.lat,this.lng);
        markerOptions.position(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                latLng,17
        ));
        mMap.addMarker(markerOptions
                .icon(bitmapDescriptorFromVector(R.drawable.ic_location)));

    }
    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(activity, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void initViews() {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                mMap = googleMap;
                if (callBack_map != null) {
                    callBack_map.onCallBack();
                    callBack_map = null;
                }
            }
        });
    }

    private void findViews(View view) {
        supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.Map_google_map);
    }

    public Polyline updateRoute(List<LatLng> points) {
        return this.mMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(Color.RED)
                .width(15));

    }

    public void drawRoute(List<LatLng> route) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(route)
                .width(5f)
                .color(Color.RED);
        // Add the Polyline to the map
        this.mMap.addPolyline(polylineOptions);
    }
}
