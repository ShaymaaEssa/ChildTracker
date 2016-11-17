package nanodegree.mal.udacity.android.childtracker.firebase;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by MOSTAFA on 17/11/2016.
 */

//this class is responsible for token updates
//handle the situation when the device's token has been updated "changed"

public class FCMTokenRefreshListenerService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, FCMRegistrationService.class);
        intent.putExtra("refreshed", true);
        startService(intent);
    }
}
