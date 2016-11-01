package nanodegree.mal.udacity.android.childtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by MOSTAFA on 28/10/2016.
 */
//this class is broadcast receiver which will called when network connection changed "whether available or not"
//it contains alarm manager to send user location every specific time interval to the database

public class NetworkChangeReceiver extends BroadcastReceiver {
    AlarmManager alarmManager;
    PendingIntent wakeupIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Myprog_Broadcast", "BroadCast Receiver Working");
        Intent locationIntent = new Intent(context,LocationUpdateService.class);
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        wakeupIntent = PendingIntent.getService(context,0,locationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (isInternetConnected(context)){ //check if the internet connection available
            //start service now for doing once
            context.startService(locationIntent);

            // schedule service for every 15 minutes : AlarmManager.INTERVAL_FIFTEEN_MINUTES
            //schecule service for every 10 minutes: (10*60*1000)
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, wakeupIntent);
            Log.i("Myprog_Broadcast", "alarmmanager is set");

        }else {
            alarmManager.cancel(wakeupIntent);
            Log.i("Myprog_Broadcast", "alarmmanager is canceled");
        }
    }

    private boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }


}
