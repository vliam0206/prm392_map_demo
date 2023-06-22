# Using google map api

Created Date: June 18, 2023 1:08 AM
Created by: Trúc Lam Võ
Reviewed: No
Type: Self-learning

# References

- Get current location: [https://www.youtube.com/watch?v=q9rQwXA9umQ](https://www.youtube.com/watch?v=q9rQwXA9umQ)
- [https://www.youtube.com/watch?v=WouAQmqJI_I](https://www.youtube.com/watch?v=WouAQmqJI_I)

# Setup google map api

## Step 1: Set up google map api in Google Cloud Console

1. Create new project at: **[https://console.cloud.google.com/](https://console.cloud.google.com/)**

1. Enable the Google Maps Android API by searching for "Maps SDK for Android" in the library section and enabling it for your project.
2. Create an API key by going to the credentials section and selecting "Create Credentials" > "API Key". Make sure to restrict the API key to only be used by your Android app by specifying the package name and SHA-1 certificate fingerprint. (Not suitable for team work)
3. To get SHA-1 certificate fingerprint of android app, open terminal tab in android studio and add following code:
    
    ```xml
    ./gradlew signingReport
    ```
    

## Step 2: Add the necessary permissions and dependencies

1. In **`AndroidManifest.xml`** file, add the following permissions inside the **`<manifest>`** element:
    
    ```xml
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    ```
    
2. Add api key to file **`AndroidManifest.xml`** inside <application> tag:
    
    ```xml
    <application ...>
    //...
    <meta-data
                android:name="com.google.android.geo.API_KEY"
                android:value="YOUR_API_KEY"/>
    </application>
    ```
    
3. Open the **`build.gradle`** file (usually located in the app module) and add the following dependency in the **`dependencies`** block:
    
    ```xml
    implementation 'com.google.android.gms:play-services-maps:18.1.0'
    ```
    

## Step 3: Modify the MainActivity

1. In the **`MainActivity`** class, add the necessary imports at the top of the file:
    
    ```java
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.OnMapReadyCallback;
    ```
    
2. Implement the **`OnMapReadyCallback`** interface for **`MainActivity`** class:
    
    ```java
    public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    ```
    
3. Inside the **`onCreate`** method, add the following code to initialize the map fragment and retrieve the **`GoogleMap`** object:
    
    ```java
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    
            // Initialize the map fragment and retrieve the GoogleMap object
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    ```
    
4. Implement the **`onMapReady`** method to handle the map initialization and perform any map-related operations:
    
    ```java
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Use the GoogleMap object to perform map-related operations
        // For example, you can add markers, set camera position, etc.
        // Refer to the Google Maps Android API documentation for more details.
    }
    ```
    
    ## Step 4: Add the MapFragment to the layout file
    
    1. 1. Open the **`activity_main.xml`** layout file and add the following code to include the **`MapFragment`**:
        
        ```xml
        <fragment
                android:id="@+id/map"
                android:name="com.google.android.gms.maps.SupportMapFragment"
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>
        ```
        
    
    # Get current location in map
    
    1. Import library in **`build.gradle`**
        
        ```xml
        implementation 'com.google.android.gms:play-services-location:21.0.1'
        implementation 'com.karumi:dexter:6.2.1'
        ```
        
    2. Add the following permissions inside the **`<manifest>`** element in **`AndroidManifest.xml`**
        
        ```xml
        <uses-permission android:name="android.permission.INTERNET" />
        ```
        
    3. In **`MainActivity`** class, import library:
        
        ```java
        import android.Manifest;
        import com.google.android.gms.location.FusedLocationProviderClient;
        ```
        
    4. Declare local variable:
        
        ```java
        private FusedLocationProviderClient fusedLocationProviderClient;
        private SupportMapFragment mapFragment;
        ```
        
    5. Add code to onCreate()
        
        ```java
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
                        }).check();
        
                mapFragment.getMapAsync(this);
            }
        ```
        
    6. Write getCurrentLocation() method
        
        ```java
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
            }
        ```