package nanodegree.mal.udacity.android.childtracker.GeoFence;

/**
 * Created by MOSTAFA on 17/11/2016.
 */

//this class to save the properties of the geofence
public class GeofenceCircle {
    private double lat;
    private double lng;
    private int radius;
    private String addressName;

    public GeofenceCircle(double lat, double lng, int radius,String addressName) {
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.addressName = addressName;
    }



    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return addressName;
    }
}
