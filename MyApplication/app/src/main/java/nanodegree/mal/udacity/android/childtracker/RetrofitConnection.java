package nanodegree.mal.udacity.android.childtracker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by MOSTAFA on 19/10/2016.
 */

public interface RetrofitConnection {

    @FormUrlEncoded
    @POST("FollowersLocation.php")
    Call<FollowersLocationModel> getFollowersLocation(@Field("user_id") String userId);
    //Call<List<FollowersLocation>> getFollowersLocation(@Field("user_id") String followerId);

}
