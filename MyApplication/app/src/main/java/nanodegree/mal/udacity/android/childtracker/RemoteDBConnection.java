package nanodegree.mal.udacity.android.childtracker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



/**
 * Created by MOSTAFA on 19/10/2016.
 */

public class RemoteDBConnection {
    private Retrofit retrofit;
    private Context context;
    private RetrofitConnection retrofitConnection; //interface
    private List<FollowersLocation> followersLocations; //contain result
    private NotifyFollowersLocations notifyFollowersLocations;
    private String userId = "1"; //for debugging

    public RemoteDBConnection(Context context,NotifyFollowersLocations notifyFollowersLocations)
    {
        this.context = context;
        this.notifyFollowersLocations = notifyFollowersLocations;
    }

    public void createRetrofitConnection(){

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url_phpfiles))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        retrofitConnection = retrofit.create(RetrofitConnection.class);
        Call<FollowersLocationModel > followersLocationConnection = retrofitConnection.getFollowersLocation(userId);


        followersLocationConnection.enqueue(new Callback<FollowersLocationModel>() {
            @Override
            public void onResponse(Call<FollowersLocationModel> call, Response<FollowersLocationModel> response) {
               followersLocations = response.body().getFollowersLocationsList();
                //if followersLocations == null ---> no followers for this user
                if (followersLocations != null)
                    notifyFollowersLocations.notifyLocations(followersLocations);
            }

            @Override
            public void onFailure(Call<FollowersLocationModel> call, Throwable t) {
                Toast.makeText(context,"Failure in Retrieving Followers Locations",Toast.LENGTH_SHORT).show();
                Log.e("RetrofitError",t.getMessage());
            }
        });

    }
}
