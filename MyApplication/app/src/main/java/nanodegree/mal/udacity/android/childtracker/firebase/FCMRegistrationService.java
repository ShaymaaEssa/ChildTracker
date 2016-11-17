package nanodegree.mal.udacity.android.childtracker.firebase;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import nanodegree.mal.udacity.android.childtracker.MyPreferences;

/**
 * Created by MOSTAFA on 17/11/2016.
 */

//this class is for sending the device token to the server
// in order to have the ability to send notifications to this device

public class FCMRegistrationService extends IntentService {

    SharedPreferences preferences;
    String userId;
    public FCMRegistrationService() {
        super("FCM");
        userId = getApplicationContext().getSharedPreferences(MyPreferences.MY_PREFERENCES, Context.MODE_PRIVATE).getString(MyPreferences.USER_ID,"0");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get Default Shard Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // get token from Firebase
        String token = FirebaseInstanceId.getInstance().getToken();

        // check if intent is null or not if it isn't null we will ger refreshed value and
        // if its true we will override token_sent value to false and apply
        if (intent.getExtras() != null) {
            boolean refreshed = intent.getExtras().getBoolean("refreshed");
            if (refreshed) preferences.edit().putBoolean("token_sent", false).apply();
        }

        // if token_sent value is false then use method sendTokenToServer to send token to server
        if (!preferences.getBoolean("token_sent", false))
            sendTokenToServer(token);
    }

    //send the device token to the server
    private void sendTokenToServer(final String token) {
        String ADD_TOKEN_URL = "http://medicalapp.site88.net/ChildTracker/addnewtoken.php";
        StringRequest request = new StringRequest(Request.Method.POST, ADD_TOKEN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int responseCode = Integer.parseInt(response);
                if (responseCode == 1) {
                    preferences.edit().putBoolean("token_sent", true).apply();
                    Log.i("Registration Service", "Response : Send Token Success");
                    stopSelf(); //the service must stop itself by calling stopSelf()

                } else {
                    preferences.edit().putBoolean("token_sent", false).apply();
                    Log.i("Registration Service", "Response : Send Token Failed");
                    stopSelf();


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                preferences.edit().putBoolean("token_sent", false).apply();
                Log.i("Registration Service", "Error :Send Token Failed");
                stopSelf();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",userId);
                params.put("token", token);
                return params;

            }
        };

        Volley.newRequestQueue(this).add(request);

    }
}
