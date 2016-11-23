package nanodegree.mal.udacity.android.childtracker;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MOSTAFA on 23/10/2016.
 */

public class MyPreferences {
    public static final String MY_PREFERENCES = "my_preferences";
    static boolean firstTime ;
    static SharedPreferences reader ;

    //user info
    public static final String USER_ID = "User_Id";
    public static final String USER_NAME = "User_Name";
    public static final String USER_EMAIL = "User_Email";

    public MyPreferences(Context context){
        reader = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
    }
    public static boolean isFirst(Context context){
        reader = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        firstTime = reader.getBoolean("is_first", true);
        return firstTime;
    }

    //to set first time
    public static void setFirst(boolean isFirstVar){
        final SharedPreferences.Editor editor = reader.edit();
        editor.putBoolean("is_first", isFirstVar);
        editor.commit();
    }

    public static void setUserInfo(String userId, String userName, String userEmail){
        final SharedPreferences.Editor editor = reader.edit();
        editor.putString(USER_ID, userId);
        editor.putString(USER_NAME,userName);
        editor.putString(USER_EMAIL,userEmail);
        editor.commit();
    }
}
