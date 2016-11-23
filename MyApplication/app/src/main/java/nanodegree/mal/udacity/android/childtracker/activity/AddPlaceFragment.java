package nanodegree.mal.udacity.android.childtracker.activity;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import nanodegree.mal.udacity.android.childtracker.GeoFence.GeofenceCircle;
import nanodegree.mal.udacity.android.childtracker.GeoFence.GeofenceList;
import nanodegree.mal.udacity.android.childtracker.MainActivity;
import nanodegree.mal.udacity.android.childtracker.Manifest;
import nanodegree.mal.udacity.android.childtracker.R;
import nanodegree.mal.udacity.android.childtracker.geocoder.DelayAutoCompleteTextView;
import nanodegree.mal.udacity.android.childtracker.geocoder.GeoAutoCompleteAdapter;
import nanodegree.mal.udacity.android.childtracker.geocoder.GeoSearchResult;
import nanodegree.mal.udacity.android.childtracker.model.PlacesModel;
import nanodegree.mal.udacity.android.childtracker.model.PlacesModelList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPlaceFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {


    //for the search text box auto complete
    private Integer THRESHOLD = 2;
    private DelayAutoCompleteTextView delayautocomplete_searchtxt;
    private ImageView geo_autocomplete_clear; // to clear the search text
    private Button btn_geofence;

    GeoSearchResult result;

    //for map fragment
    GoogleMap googleMap;

    final static String TAG = "ChildrenTrackerApp";

    //geofence
    private Marker geoFenceMarker;
    private static final int GEOFENCE_RADIUS = 500; // in meters, I set it constant value = 500 meters
    private GoogleApiClient googleApiClient;
    private Circle geoFenceLimits;

    public AddPlaceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places2,container,false);
        setRetainInstance(true);

        geo_autocomplete_clear = (ImageView) view.findViewById(R.id.img_places2_clear);
        delayautocomplete_searchtxt = (DelayAutoCompleteTextView) view.findViewById(R.id.delayautocomplete_places2_searchtext);

        btn_geofence = (Button)view.findViewById(R.id.btn_places2_addgeofence);
        btn_geofence.setEnabled(false);

        delayautocomplete_searchtxt.setThreshold(THRESHOLD);
        delayautocomplete_searchtxt.setAdapter(new GeoAutoCompleteAdapter(getActivity())); // 'this' is Activity instance

        delayautocomplete_searchtxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                result = (GeoSearchResult) adapterView.getItemAtPosition(position);
                delayautocomplete_searchtxt.setText(result.getAddress());
                setGeoFenceMarker(new LatLng(result.getAddressObject().getLatitude(),result.getAddressObject().getLongitude()));
            }
        });

        delayautocomplete_searchtxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0)
                {
                    geo_autocomplete_clear.setVisibility(View.VISIBLE);
                }
                else
                {
                    geo_autocomplete_clear.setVisibility(View.GONE);
                }
            }
        });

        geo_autocomplete_clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                delayautocomplete_searchtxt.setText("");
                btn_geofence.setEnabled(false);
            }
        });

        btn_geofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeofenceCircle newCircle = new GeofenceCircle(result.getAddressObject().getLatitude(),
                        result.getAddressObject().getLongitude(),
                        GEOFENCE_RADIUS,result.getAddress());

                GeofenceList geofenceListObject = new GeofenceList(getActivity());
                List<GeofenceCircle> list;
                if (geofenceListObject.getGeofenceCircleList() == null){
                    list = new ArrayList<GeofenceCircle>();
                }
                else
                list = geofenceListObject.getGeofenceCircleList();
                list.add(newCircle);
                geofenceListObject.setGeofenceCircleList(list);
                //getActivity().getSupportFragmentManager().popBackStackImmediate();
                Fragment fragment = new PlacesFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.relative_places2_parent, fragment,null)
                        .addToBackStack(null)
                        .commit();
            }
        });
        createGoogleApi();
        return view;
    }

    //create googleApiClient for geofence
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( getActivity() )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }

    }

    //after user select the address on EditText SearchBox
    //set a marker on the map
    private void setGeoFenceMarker(LatLng latLng) {
        Log.i(TAG, "AddPlaceFragment_markerForGeofence("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if ( googleMap!=null ) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = googleMap.addMarker(markerOptions);
            float zoom = 20f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,zoom);
            googleMap.animateCamera(cameraUpdate);
            btn_geofence.setEnabled(true);

            //to draw a circle for geofence
            if ( geoFenceLimits != null )
                geoFenceLimits.remove();

            CircleOptions circleOptions = new CircleOptions()
                    .center( geoFenceMarker.getPosition())
                    .strokeColor(Color.argb(50, 70,70,70))
                    .fillColor( Color.argb(100, 150,150,150) )
                    .radius( GEOFENCE_RADIUS );
            geoFenceLimits = googleMap.addCircle( circleOptions );
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //to add google map fragment
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.linearlayout_places2_map, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() )
            Toast.makeText(getActivity(),"Geofence Created Successfully",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(),"Geofence failed",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setCurrentFragment(MainActivity.ADD_PLACE_FRAGMENT);
    }
}
