package nanodegree.mal.udacity.android.childtracker.model;

import nanodegree.mal.udacity.android.childtracker.GeoFence.AddItemToListView;

/**
 * Created by MOSTAFA on 09/11/2016.
 */

public class PlacesModel {

    private String placeName;
    private double placeLat;
    private double placeLng;



    public PlacesModel (String placeName , double placeLat , double placeLng){
        this.placeName = placeName;
        this.placeLat = placeLat;
        this.placeLng = placeLng;
    }



    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(double placeLat) {
        this.placeLat = placeLat;
    }

    public double getPlaceLng() {
        return placeLng;
    }

    public void setPlaceLng(double placeLng) {
        this.placeLng = placeLng;
    }

    @Override
    public String toString() {
        return getPlaceName();
    }
}
