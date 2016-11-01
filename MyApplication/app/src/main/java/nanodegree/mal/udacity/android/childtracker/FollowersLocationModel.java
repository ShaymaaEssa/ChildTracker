package nanodegree.mal.udacity.android.childtracker;

import java.util.List;

/**
 * Created by MOSTAFA on 19/10/2016.
 */

public class FollowersLocationModel {
    private List<FollowersLocation> location;
    public List<FollowersLocation> getFollowersLocationsList(){
        return location;
    }
    public void setFollowersLocationsList(List<FollowersLocation> location){
        this.location = location;
    }
}
