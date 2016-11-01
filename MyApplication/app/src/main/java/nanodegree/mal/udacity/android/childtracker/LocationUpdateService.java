package nanodegree.mal.udacity.android.childtracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by MOSTAFA on 28/10/2016.
 */

//this service is a background service which will send location of the user to the database every 15 min with distance exceed 100 m
public class LocationUpdateService extends Service implements android.location.LocationListener {
    private enum State {
        IDLE, WORKING;
    }

    private static State state;
    private LocationManager locationManager;
    private PowerManager.WakeLock wakeLock;

    boolean gps_enabled = false;
    boolean network_enabled = false;

    final static long UPDATE_EVERY_TIME = 15*60*1000; //15 mins
    final static float UPDATE_EVERY_DISTANCE = 100f; //100 meter

    String url;
    String userId = "2"; //for test

    static {
        state = State.IDLE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //PowerManager Class : gives you control of the power state of the device.
        // newWakeLock()---> This will create a PowerManager.WakeLock object.
        // we use WakeLock ----> so that CPU will not go to sleep while Service is running.
        //PARTIAL_WAKE_LOCK holds CPU lock, not the screen

        Log.i("Myprog_locupdateservice", "Background service is up");
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationUpdateService");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //onStartCommand ---> Called by the system every time a client explicitly starts the service by calling startService(Intent).
    //this method will return int which represent how the service will handle the case of killing by system
    //if return type = "Service.START_STICKY" ---> the system will rerun the service as soon as possible
    //if return type = "Service.START_NOT_STICKY" --> the system will not rerun the service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Myprog_locupdateservice", "On start command");
        if (state == State.IDLE) {
            state = State.WORKING;

            //acquire() ---> Ensures that the device is on at the level requested when the wake lock was created.
            this.wakeLock.acquire();

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return START_NOT_STICKY;
            }

            try {
                if (gps_enabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_EVERY_TIME, UPDATE_EVERY_DISTANCE, this);
                    Log.i("Myprog_locupdateservice", "Gps enabled");
                }
                else if (network_enabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_EVERY_TIME, UPDATE_EVERY_DISTANCE, this);
                    Log.i("Myprog_locupdateservice", "network enabled");


                }
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Error creating location service: " + ex.getMessage(), Toast.LENGTH_SHORT).show();

                Log.i("Myprog_locupdateservice", "Error creating location service");
            }

        }

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Myprog_locupdateservice", "Background service is destroyed");
        state = State.IDLE;
        if (this.wakeLock.isHeld())
            this.wakeLock.release();
    }

    private void sendToServer(final Location location) {

        Log.i("Myprog_locupdateservice", "Send data to server");
        // send to server in background thread. you might want to start AsyncTask here
        location.getTime();
        url = "http://medicalapp.site88.net/ChildTracker/InsertCurrentLocation.php";
        StringRequest registerUserRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Current Location inserted")){
                    Log.i("LocationUpdateService","Current Location Inserted");
                }

                else {
                    Log.i("LocationUpdateService","Error in Location Insertion");
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("LocationUpdateService","Error in Location Insertion Connection"+ error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap map  = new HashMap();
                map.put("userId",userId); //for test
                map.put("lat",Double.toString(location.getLatitude()));
                map.put("lng",Double.toString(location.getLongitude()));
                return map;
            }
        };

        Volley.newRequestQueue(this).add(registerUserRequest);
        onSendingFinished();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            locationManager.removeUpdates(this); // you will want to listen for updates only once
            sendToServer(location);
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error creating location service: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onSendingFinished() {
        // call this after sending finished to stop the service
        this.stopSelf(); //stopSelf will call onDestroy and the WakeLock releases.
        //Be sure to call this after everything is done (handle exceptions and other stuff) so you release a wakeLock
        //or you will end up draining battery like hell
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}
