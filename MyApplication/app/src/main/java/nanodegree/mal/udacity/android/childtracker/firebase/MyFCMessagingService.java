package nanodegree.mal.udacity.android.childtracker.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by MOSTAFA on 17/11/2016.
 */

//listen to the messaging coming from FCM and implement the code which will execute when receiving msgs
public class MyFCMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
