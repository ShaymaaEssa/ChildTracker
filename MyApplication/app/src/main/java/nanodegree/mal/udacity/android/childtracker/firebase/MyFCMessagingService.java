package nanodegree.mal.udacity.android.childtracker.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import nanodegree.mal.udacity.android.childtracker.GeoFence.GeofenceCircle;
import nanodegree.mal.udacity.android.childtracker.GeoFence.GeofenceList;
import nanodegree.mal.udacity.android.childtracker.MainActivity;
import nanodegree.mal.udacity.android.childtracker.R;
import nanodegree.mal.udacity.android.childtracker.activity.MainFragment;

/**
 * Created by MOSTAFA on 17/11/2016.
 */

//listen to the messaging coming from FCM and implement the code which will execute when receiving msgs
public class MyFCMessagingService extends FirebaseMessagingService {
    List<GeofenceCircle> geofenceList;

    String childId;
    String userName;
    double userLat ;
    double userLng ;


    SharedPreferences preferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //remoteMessage contain user longitude and latitude
        //here set userId, userName, userLat and userLng
        childId = remoteMessage.getData().get("user_id");
        userName = remoteMessage.getData().get("user_name");
        userLat = Double.parseDouble(remoteMessage.getData().get("user_lat"));
        userLng = Double.parseDouble(remoteMessage.getData().get("user_lng"));

        calculateDistanceUserGeofence(userLat, userLng);
        //sendNotification("hello");

    }

    private void calculateDistanceUserGeofence(double userLat, double userLng) {

        geofenceList = new GeofenceList(getApplicationContext()).getGeofenceCircleList();
        for (GeofenceCircle circle : geofenceList){

            //calculate difference between the user location and the circle center point
            //this equation is from
            // http://stackoverflow.com/questions/30719757/is-there-any-api-for-calculating-geofence-breach-other-than-android-apis
            //This formula is called the Haversine formula.
            // It takes into account the earths curvation.
            // The results are in meters.

            double diffDestance =
                    Math.sin(Math.toRadians(circle.getLat())) *
                            Math.sin(Math.toRadians(userLat)) +
                            Math.cos(Math.toRadians(circle.getLat())) *
                                    Math.cos(Math.toRadians(userLat)) *
                                    Math.cos(Math.toRadians(userLng) -
                                            Math.toRadians(circle.getLng()));
            diffDestance = diffDestance > 0 ? Math.min(1, diffDestance) : Math.max(-1, diffDestance);
            diffDestance = 3959 * 1.609 * 1000 * Math.acos(diffDestance);

            //which mean: the user location inside the geofence circle
            //and the user not entered the geofence before
            if(diffDestance <circle.getRadius() && !preferences.getBoolean("Geofence"+childId+circle.getAddressName(), false)){
                //set a flag to true so we will not get a duplicate notifications
                preferences.edit().putBoolean("Geofence"+childId+circle.getAddressName(), true).apply();


                String notificationBody = userName +" is arrived "+circle.getAddressName();
                sendNotification(notificationBody); //send notification to user that the child enter the geofence
            }
            else {
                //set a flag to false
                //the user not in the geofence
                preferences.edit().putBoolean("Geofence"+childId+circle.getAddressName(), false).apply();
            }
        }


    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);

        //FLAG_ACTIVITY_CLEAR_TOP
        //If set, and the activity being launched is already running in the current task,
        // then instead of launching a new instance of that activity, all of the other activities on top of it will be closed
        // and this Intent will be delivered to the (now on top) old activity as a new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Geofence Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
