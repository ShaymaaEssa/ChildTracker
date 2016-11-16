package nanodegree.mal.udacity.android.childtracker.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nanodegree.mal.udacity.android.childtracker.GeoFence.AddItemToListView;
import nanodegree.mal.udacity.android.childtracker.R;
import nanodegree.mal.udacity.android.childtracker.model.PlacesModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment implements AddItemToListView{

    ListView listView_places;
    TextView txt_nodata;
    Button btn_addPlace;

    List<PlacesModel> places ;
    ArrayAdapter<PlacesModel> adapter;

    public PlacesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places1, container, false);
        listView_places = (ListView)view.findViewById(R.id.listview_places_placesname);
        txt_nodata = (TextView)view.findViewById(R.id.txt_places_nodata);
        btn_addPlace = (Button)view.findViewById(R.id.btn_places_addplace);

        adapter = new ArrayAdapter<PlacesModel>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, places);

        btn_addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddNewPlaceFragment();
            }
        });

        return view;
    }

    private void openAddNewPlaceFragment() {
        Fragment fragment = new AddPlaceFragment();
        this.getFragmentManager().beginTransaction()
                .replace(R.id.relativelayout_places1_parentlayout, fragment,null)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        places = new ArrayList<PlacesModel>();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (places.size() == 0){
            txt_nodata.setVisibility(View.VISIBLE);
            listView_places.setVisibility(View.INVISIBLE);


        }
        else {
            txt_nodata.setVisibility(View.INVISIBLE);
            listView_places.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void notifyDataInListView(PlacesModel placeItem) {
        places.add(placeItem);
        adapter.notifyAll();
    }
}
