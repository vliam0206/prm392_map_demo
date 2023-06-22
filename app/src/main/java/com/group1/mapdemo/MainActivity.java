package com.group1.mapdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
// Import other libraries
import android.view.View;
import android.view.WindowManager;
import android.Manifest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
// Import google map libaries
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, LocationListener, GoogleMap.OnMapClickListener {
    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment mapFragment;
    ImageButton btnZoomIn,btnZoomOut,btnSearch,btnCurrent, btnDistance;
    Button test;

    EditText et,fromEt,toEt;
    double latitude, longitude;
    double endLatitude, endLongitude;
    private Marker currentLocationMarker;
    private Marker userMarker;
    LatLng curentLocation;
    LatLng destination,origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Initialize the map fragment and retrieve the GoogleMap object
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        mapFragment.getMapAsync(this); // Add this line to register the OnMapReadyCallback
        MapsInitializer.initialize(getApplicationContext());


        btnZoomIn = (ImageButton) findViewById(R.id.zoomin);
        btnZoomOut =(ImageButton) findViewById(R.id.zoomout);
        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.zoomIn());
            }
        });
        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.zoomOut());
            }
        });
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        et = (EditText) findViewById(R.id.et_location);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                String location = et.getText().toString();
                List<Address> addressList;
                MarkerOptions mo= new MarkerOptions();
                if(!location.trim().equals("")){
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                        if(addressList.size() == 0){
                            throw new IOException();
                        }
                        for (int i=0;i<addressList.size();i++){
                            Address myAddress = addressList.get(i);
                            LatLng latLng = new LatLng(myAddress.getLatitude(),myAddress.getLongitude());
                            mo.position(latLng);
                            mo.title("Your search result");
                            mMap.addMarker(mo);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                        destination = new LatLng(addressList.get(0).getLatitude(),addressList.get(0).getLongitude());
                        origin = curentLocation;
                    } catch (IOException e) {
                        setNotification("Not found any matched");
                    }catch (IllegalArgumentException e){
                        setNotification("Invalid input");
                    }
                    destination = new LatLng(addressList.get(0).getLatitude(),addressList.get(0).getLongitude());
                    origin = curentLocation;
                }
            }
        });
        test = (Button) findViewById(R.id.btnTest);
        fromEt = (EditText) findViewById(R.id.et_from);
        toEt = (EditText)findViewById(R.id.et_to);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if((fromEt.getText().toString().trim().equalsIgnoreCase("") || toEt.getText().toString().trim().equalsIgnoreCase("")) && !et.getText().toString().trim().equals("") ){
                   et.setText("");
                   Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + origin.latitude+ "," + origin.longitude + "&daddr=" + destination.latitude + "," + destination.longitude));
                   startActivity(intent);
               }
               if(!fromEt.getText().toString().trim().equalsIgnoreCase("") && ! toEt.getText().toString().trim().equalsIgnoreCase("")){
                   String from = fromEt.getText().toString();
                   String to = toEt.getText().toString();
                   Geocoder geocoder = new Geocoder(MainActivity.this);
                   try {
                       fromEt.setText("");
                       toEt.setText("");
                       List<Address> fromAddress = geocoder.getFromLocationName(from,1);
                       List<Address> toAddress = geocoder.getFromLocationName(to,1);
                       if(fromAddress.size() == 0 || toAddress.size() == 0){
                           throw new IOException();
                       }
                       LatLng fromLatLng = new LatLng(fromAddress.get(0).getLatitude(),fromAddress.get(0).getLongitude());
                       LatLng toLatLng = new LatLng(toAddress.get(0).getLatitude(),toAddress.get(0).getLongitude());
                       Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + fromLatLng.latitude+ "," + fromLatLng.longitude + "&daddr=" + toLatLng.latitude + "," + toLatLng.longitude));
                       startActivity(intent);
                   } catch (IOException e) {
                       setNotification("Not found any matched");
                   } catch (IllegalArgumentException e){
                       setNotification("Invalid input");
                       throw new RuntimeException(e);
                   }
               }
            }
        });
        btnCurrent = (ImageButton) findViewById(R.id.btn_current);
        btnCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getCurrentLocation();
            }
        });

        btnDistance = (ImageButton) findViewById(R.id.btnDistance);

        btnDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(endLatitude, endLongitude));
                markerOptions.title("Destination");
                Location location1 = new Location("");
                location1.setLatitude(latitude);
                location1.setLongitude(longitude);
                Location location2 = new Location("");
                location2.setLatitude(endLatitude);
                location2.setLongitude(endLongitude);
                float distance = location1.distanceTo(location2) / 1000;
                markerOptions.snippet("Distance: " + distance + "km");
                Marker marker =  mMap.addMarker(markerOptions);
                marker.showInfoWindow();
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions markerOptions2 = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
        });
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
                        mMap.setOnMarkerClickListener(MainActivity.this);
                        mMap.setOnMarkerDragListener(MainActivity.this);
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            curentLocation = latLng;
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
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapClickListener(this);
        // Use the GoogleMap object to perform map-related operations
        // For example, you can add markers, set camera position, etc.
        // Refer to the Google Maps Android API documentation for more details.

        LatLng location;
        if (curentLocation != null) {
            location = curentLocation;
        } else {
            location = new LatLng(37.7749, -122.4194);
        }

        MarkerOptions markerOptions = new MarkerOptions().position(location).title("Marker Title");
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        marker.setDraggable(true);
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        return false;
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        endLatitude = marker.getPosition().latitude;
        endLongitude = marker.getPosition().longitude;
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (userMarker != null) {
            userMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Marker Title");
        userMarker = mMap.addMarker(markerOptions);
        userMarker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }
    public void setNotification(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}