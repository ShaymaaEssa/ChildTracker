package nanodegree.mal.udacity.android.childtracker.geocoder;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import nanodegree.mal.udacity.android.childtracker.R;

/**
 * Created by MOSTAFA on 11/11/2016.
 */

public class GeoAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 5; //max result that will display
    private Context context;
    private List<GeoSearchResult> resultList = new ArrayList<GeoSearchResult>();

    public GeoAutoCompleteAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public GeoSearchResult getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.geo_search_result, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.txt_geosearch_item)).setText(getItem(position).getAddress());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List locations = findLocations(context, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = locations;
                    filterResults.count = locations.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private List<GeoSearchResult> findLocations(Context context, String query_text) {

        List<GeoSearchResult> geo_search_results = new ArrayList<GeoSearchResult>();

        // Geocoding is the process of converting addresses (like "1600 Amphitheatre Parkway, Mountain View, CA")
        // into geographic coordinates (like latitude 37.423021 and longitude -122.083739),
        // which you can use to place markers on a map, or position the map.

        Geocoder geocoder = new Geocoder(context, context.getResources().getConfiguration().locale);
        List<Address> addresses = null;

        try {
            // Getting a maximum of 15 Address that matches the input text
            addresses = geocoder.getFromLocationName(query_text, 15);

            for(int i=0;i<addresses.size();i++){
                Address address = (Address) addresses.get(i);
                if(address.getMaxAddressLineIndex() != -1)
                {
                    geo_search_results.add(new GeoSearchResult(address));
                }
            }


        } catch (IOException e) {
            Log.e("ChildrenTrackerApp",e.getMessage());
            Toast.makeText(context,"Error in Filtering",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return geo_search_results;
    }
}
