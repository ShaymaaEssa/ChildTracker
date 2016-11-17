package nanodegree.mal.udacity.android.childtracker.GeoFence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOSTAFA on 17/11/2016.
 */

public class GeofenceList {
    List<GeofenceCircle> geofenceCircleList = new ArrayList<GeofenceCircle>();
    Context context;
    SharedPreferences sharedPrefs;
    static final String TAG = "GEOFENCE_LIST";
    Gson gson;

    public GeofenceList(Context context){
        this.context = context;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
    }

    //retrieve GeofenceList from Sharedpref
    public List<GeofenceCircle> getGeofenceCircleList() {
        String json = sharedPrefs.getString(TAG, null);
        geofenceCircleList = gson.fromJson(json, new TypeToken<ArrayList<GeofenceCircle>>() {}.getType());
        return geofenceCircleList;
    }

    //save the geofencelist in shared preference
    public void setGeofenceCircleList(List<GeofenceCircle> geofenceCircleList) {
        this.geofenceCircleList = geofenceCircleList;

        SharedPreferences.Editor editor = sharedPrefs.edit();
        String json = gson.toJson(geofenceCircleList);
        editor.putString(TAG, json);
        editor.commit();
    }
}
