package nanodegree.mal.udacity.android.childtracker.model;

import java.util.ArrayList;
import java.util.List;

import nanodegree.mal.udacity.android.childtracker.GeoFence.AddItemToListView;

/**
 * Created by MOSTAFA on 14/11/2016.
 */

public class PlacesModelList {
    private static PlacesModelList instance;
    private static AddItemToListView onItemAddHandler;

    List<PlacesModel> list = new ArrayList<PlacesModel>();

    public static PlacesModelList getInstance() {
        if (null == instance) {
            instance = new PlacesModelList();
        }
        return instance;
    }

    public void setOnItemAddedHandler(AddItemToListView handler) {
        onItemAddHandler = handler;
    }

    public void addItem(PlacesModel data) {
        if (null != onItemAddHandler)
            onItemAddHandler.notifyDataInListView(data);
    }
}
