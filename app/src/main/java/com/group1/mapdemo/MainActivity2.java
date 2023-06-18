package com.group1.mapdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity2 extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener  {

    private MapView mapView;
    private GoogleMap ggMap;
    private Marker userMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // ThongNT code
        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);

        // Set the API key programmatically
        mapView.getMapAsync(googleMap -> {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setOnMapClickListener(this);
        });
        MapsInitializer.initialize(getApplicationContext());
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Use the GoogleMap object to perform map-related operations
        // For example, you can add markers, set camera position, etc.
        // Refer to the Google Maps Android API documentation for more details.
        //Add Marker
        ggMap = googleMap;
        LatLng location = new LatLng(37.7749, -122.4194); // Replace with your desired location coordinates
        MarkerOptions markerOptions = new MarkerOptions().position(location).title("Marker Title");
        Marker marker = ggMap.addMarker(markerOptions);
        marker.showInfoWindow();
        ggMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (ggMap != null) {
            if (userMarker != null) {
                userMarker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Marker Title");
            userMarker = ggMap.addMarker(markerOptions);
            userMarker.showInfoWindow();
            ggMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}