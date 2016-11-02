package nanodegree.mal.udacity.android.childtracker.activity;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nanodegree.mal.udacity.android.childtracker.FollowersLocation;
import nanodegree.mal.udacity.android.childtracker.MainActivity;
import nanodegree.mal.udacity.android.childtracker.NotifyFollowersLocations;
import nanodegree.mal.udacity.android.childtracker.NowLocation;
import nanodegree.mal.udacity.android.childtracker.R;
import nanodegree.mal.udacity.android.childtracker.RemoteDBConnection;
import nanodegree.mal.udacity.android.childtracker.RequestPermission;
import nanodegree.mal.udacity.android.childtracker.WriteLocation;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements

        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,GoogleMap.OnMarkerClickListener,OnMapReadyCallback,
        NotifyFollowersLocations {

    //for map
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQ_PERMISSION = 0;
    com.google.android.gms.maps.MapFragment mapFragment;
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


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //create google api instance
    private void createGoogleApi() {
        Log.d(TAG,"createGoogleApi()");
        if (googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

//    private void initMap() {
//        mapFragment = (MapFragment)getActivity().getFragmentManager().findFragmentById(R.id.frag_mapfragment_map);
//        mapFragment.getMapAsync(this);
//
//    }

    @Override
    public void onResume() {
        super.onResume();
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
            remoteDBConnection = new RemoteDBConnection(getContext(), this);
        }
        remoteDBConnection.createRetrofitConnection();

    }

    //stop google api connection when stopping the activity
    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();

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
        nowLocationObject = new NowLocation(getActivity(), new WriteLocation() {
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

    private void writeActualLocation(Location lastLocation) {
        currentLocLat = lastLocation.getLatitude();
        currentLocLng = lastLocation.getLongitude();
        Toast.makeText(getContext(),currentLocLat +" , "+currentLocLng, Toast.LENGTH_SHORT).show();
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
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQ_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQ_PERMISSION:{
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
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
        Toast.makeText(getContext(),"Permission Denied, App cannot work without the permissions",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

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
    public void onDestroy() {
        super.onDestroy();
        if (timer!= null)
            timer.cancel();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Inflate the layout for this fragment
        //initMap();
        createGoogleApi();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapFragmentContainer, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
    }
}
