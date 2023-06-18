package com.group1.mapdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
// Import other libraries
import android.view.View;
import android.view.WindowManager;
import android.Manifest;
import android.widget.Button;
import android.widget.Toast;
// Import google map libaries
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment mapFragment;
    private MapView mapView;
    private GoogleMap ggMap;
    private Marker userMarker;
    private Button btnMarkerSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Initialize the map fragment and retrieve the GoogleMap object
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        fusedLocationProviderClient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(this);

        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getCurrentLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                });

        mapFragment.getMapAsync(this);

        // ThongNT code
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
//        btnMarkerSwitch = findViewById(R.id.btnMarker);


        // Set the API key programmatically
        mapView.getMapAsync(googleMap -> {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setOnMapClickListener(this);
        });
        MapsInitializer.initialize(getApplicationContext());

//        btnMarkerSwitch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
//                startActivity(intent);
//            }
//        });
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your current Location");
                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        } else {
                            Toast.makeText(MainActivity.this, "Please turn on your location permission.", Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        });
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

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (userMarker != null) {
            userMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Marker Title");
        userMarker = ggMap.addMarker(markerOptions);
        userMarker.showInfoWindow();
        ggMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

    }
}