package nanodegree.mal.udacity.android.childtracker;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by MOSTAFA on 29/10/2016.
 */

//this class to get the current location of user

public class NowLocation implements LocationListener{

    Context context;
    WriteLocation writeLocationInterface;
    RequestPermission requestPermissionInterface;
    GoogleApiClient googleApiClient;

    private static final String TAG = NowLocation.class.getSimpleName();

    private Location lastLocation;
    private LocationRequest locationRequest;
    private static final int REQ_PERMISSION = 0;

    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  900000; //milli second
    private final int FASTEST_INTERVAL = 500000;

    public NowLocation (Context context, WriteLocation writeLocationInterface,RequestPermission requestPermissionInterface, GoogleApiClient googleApiClient){
        this.context = context;
        this.writeLocationInterface = writeLocationInterface;
        this.googleApiClient = googleApiClient;
        this.requestPermissionInterface = requestPermissionInterface;
    }

    public void getLastKnowLocation() {
        Log.d(TAG,"getLastKnownLocation()");

        if (checkPermission()){  //if user gives the right permission
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null){
                Log.i(TAG,"Lastknow Location: lng: "+ lastLocation.getLongitude()+" lat: "+lastLocation.getLatitude());
                writeLocationInterface.writeLastLocation(lastLocation);
                startLocationUpdates();
            }
            else{
                Log.w(TAG,"No Location retrieved yet");
                startLocationUpdates();
            }

        }
        else askPermission(); //ask user to give the wanted permissions
    }

    private void permissionDenied() {
        Toast.makeText(context,"Permission Denied, App cannot work without the permissions",Toast.LENGTH_SHORT).show();
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    //ASK user for giving the right permission
    private void askPermission() {
        requestPermissionInterface.askActivityPermission();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest().create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (checkPermission())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        writeLocationInterface.writeLastLocation(lastLocation);

    }
}
