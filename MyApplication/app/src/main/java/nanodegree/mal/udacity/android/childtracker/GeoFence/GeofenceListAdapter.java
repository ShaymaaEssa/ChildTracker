package nanodegree.mal.udacity.android.childtracker.GeoFence;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nanodegree.mal.udacity.android.childtracker.R;

/**
 * Created by MOSTAFA on 21/11/2016.
 */

public class GeofenceListAdapter extends ArrayAdapter<GeofenceCircle> {

    List<GeofenceCircle> geofenceCircleList;
    Context context;
    public GeofenceListAdapter(Context context, int resource, List<GeofenceCircle> geofenceCircleList) {
        super(context, resource, geofenceCircleList);

        this.geofenceCircleList  = geofenceCircleList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         super.getView(position, convertView, parent);
        View v;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.geofence_listitem, parent, false);
        GeofenceHolder geofenceHolder = new GeofenceHolder();
        geofenceHolder.geofenceAddress = (TextView)v.findViewById(R.id.txt_geofencelistitem_address);
        GeofenceCircle geofenceCircleItem = geofenceCircleList.get(position);
        if (geofenceHolder.geofenceAddress!=null)
            geofenceHolder.geofenceAddress.setText(geofenceCircleItem.getAddressName());

        return v;

    }

    @Override
    public int getCount() {
        if (geofenceCircleList == null)
            return 0;
        else return geofenceCircleList.size();

    }

    @Nullable
    @Override
    public GeofenceCircle getItem(int position) {
        if (geofenceCircleList != null){
            return geofenceCircleList.get(position);
        }

        else return null;
    }

    @Override
    public long getItemId(int position) {
        if (geofenceCircleList != null){
           return geofenceCircleList.get(position).hashCode();
        }

        else return 0;
    }

    class GeofenceHolder {
        TextView geofenceAddress;
    }
}
