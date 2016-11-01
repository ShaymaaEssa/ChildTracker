package nanodegree.mal.udacity.android.childtracker;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity implements

        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,GoogleMap.OnMarkerClickListener,OnMapReadyCallback,
        NotifyFollowersLocations{

    //for map
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQ_PERMISSION = 0;
    MapFragment mapFragment;
    GoogleMap googleMap;

    //google api client
    GoogleApiClient googleApiClient;

    //to get location
    private Location lastLocation;
    double currentLocLat;
    double currentLocLng;

    private LocationRequest locationRequest;

    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  900000; //milli second
    private final int FASTEST_INTERVAL = 500000;

    //marker for the map
    private Marker locationMarker;

    //get Followers Location - Retrofit | DB Connection-
    RemoteDBConnection remoteDBConnection;

    //marker for followers
    Marker[] followerMarker;

    //for navigation drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    Toolbar toolbar ;

    NowLocation nowLocationObject;

    final int FIFTEEN_INTERVAL = 3* 60 * 1000;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_main_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_main_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                return false;
            }
        });

//        registerReceiver(
//                new NetworkChangeReceiver(),
//                new IntentFilter(
//                        ConnectivityManager.CONNECTIVITY_ACTION));

        createGoogleApi();
    }

    //create google api instance
    private void createGoogleApi() {
        Log.d(TAG,"createGoogleApi()");
        if (googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    private void initMap() {
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.frag_mainactivity_map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //call google api connection when starting the activity
        googleApiClient.connect();

        if (googleMap != null)
            googleMap.clear();
//        if (followerMarker != null){
//            for (Marker m : followerMarker) {
//                m.remove();
//
//            }
//        }
        getFollowersLocation();

        //timer to execute code of retrieving followers location every 15 min from the database and update map
        timer = new Timer();
        TimerTask updateFollowersLocationFifteenMinutes = new TimerTask() {
            @Override
            public void run() {
                getFollowersLocation();
            }
        };
        timer.schedule(updateFollowersLocationFifteenMinutes,FIFTEEN_INTERVAL,FIFTEEN_INTERVAL);
    }

    private void getFollowersLocation() {
        if (remoteDBConnection == null) {
            remoteDBConnection = new RemoteDBConnection(getApplicationContext(), this);
        }
        remoteDBConnection.createRetrofitConnection();

    }

    //stop google api connection when stopping the activity
    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick("+latLng +")");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.setOnMarkerClickListener(this);


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getTitle() );
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i(TAG, "onConnected()");
        //to get current location of user
        //getLastKnowLocation();
        nowLocationObject = new NowLocation(this, new WriteLocation() {
            @Override
            public void writeLastLocation(Location location) {
                writeActualLocation(location);
            }
        }, new RequestPermission() {
            @Override
            public void askActivityPermission() {
                askPermission();
            }
        },googleApiClient);

        nowLocationObject.getLastKnowLocation();
    }

//    private void getLastKnowLocation() {
//        Log.d(TAG,"getLastKnownLocation()");
//
//        if (checkPermission()){  //if user gives the right permission
//            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//            if (lastLocation != null){
//                Log.i(TAG,"Lastknow Location: lng: "+ lastLocation.getLongitude()+" lat: "+lastLocation.getLatitude());
//                writeLastLocation();
//                startLocationUpdates();
//            }
//            else{
//                Log.w(TAG,"No Location retrieved yet");
//                startLocationUpdates();
//            }
//
//        }
//        else askPermission(); //ask user to give the wanted permissions
//    }

//    private void startLocationUpdates() {
//        locationRequest = new LocationRequest().create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(UPDATE_INTERVAL)
//                .setFastestInterval(FASTEST_INTERVAL);
//
//        if (checkPermission())
//            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
//    }

//    private void writeLastLocation() {
//        writeActualLocation(lastLocation);
//    }

    private void writeActualLocation(Location lastLocation) {
        currentLocLat = lastLocation.getLatitude();
        currentLocLng = lastLocation.getLongitude();
        Toast.makeText(this,currentLocLat +" , "+currentLocLng, Toast.LENGTH_SHORT).show();
        markerLocation(new LatLng(currentLocLat,currentLocLng),"Current Location"); //to put marker in the current location on the map
    }

    private void markerLocation(LatLng latLng , String title) {
        //String title = latLng.latitude +","+latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
        if (googleMap != null){
            if (locationMarker != null)
                locationMarker.remove();
            locationMarker = googleMap.addMarker(markerOptions);
            float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,zoom);
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
            googleMap.animateCamera(cameraUpdate);

        }
    }

//    //ASK user for giving the right permission
    private void askPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQ_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQ_PERMISSION:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    nowLocationObject.getLastKnowLocation();
                }
                else{
                    permissionDenied();
                }
                break;
            }
        }
    }

    private void permissionDenied() {
        Toast.makeText(this,"Permission Denied, App cannot work without the permissions",Toast.LENGTH_SHORT).show();
    }
//
//    private boolean checkPermission() {
//        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
//    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        lastLocation = location;
//        writeActualLocation(location);
//    }


    @Override
    public void notifyLocations(List<FollowersLocation> list) {

        if (followerMarker != null) {
            for (int i = 0; i < followerMarker.length; i++) {
                if (followerMarker[i] != null)
                    followerMarker[i].remove();
            }
        }
        //for each follower of the user draw a marker in the map
        followerMarker = new Marker[list.size()];
        for (int i =0 ;i<list.size();i++)
        {
            FollowersLocation item = list.get(i);
            //markerLocation(new LatLng(Double.parseDouble(item.getLat()),Double.parseDouble(item.getLng())),item.getUser_name());
            LatLng latLng = new LatLng(Double.parseDouble(item.getLat()),Double.parseDouble(item.getLng()));
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(item.getUser_name());
            if (googleMap != null){
                if (followerMarker[i] != null)
                    followerMarker[i].remove();
                followerMarker[i] = googleMap.addMarker(markerOptions);
                //float zoom = 14f;
                // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,zoom);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                googleMap.animateCamera(cameraUpdate);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}


