package nanodegree.mal.udacity.android.childtracker.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import nanodegree.mal.udacity.android.childtracker.MyPreferences;
import nanodegree.mal.udacity.android.childtracker.R;
import nanodegree.mal.udacity.android.childtracker.TestActivity;
import nanodegree.mal.udacity.android.childtracker.model.NavDrawerItem;

/**
 * Created by MOSTAFA on 01/11/2016.
 */

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder>{
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private int type;

    static SharedPreferences reader;

    private static final int HEADER_TYPE = 1;
    private static final int ITEM_TYPE = 2;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data , int type) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.type = type;

        reader = context.getSharedPreferences(MyPreferences.MY_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public NavigationDrawerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            View view = inflater.inflate(R.layout.drawer_header, parent, false);
            MyViewHolder holder = new MyViewHolder(view, viewType);
            return holder;
        } else if (viewType == ITEM_TYPE) {
            View view = inflater.inflate(R.layout.nav_drawer_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view, viewType);
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (holder.type == HEADER_TYPE){
            holder.name.setText( reader.getString(MyPreferences.USER_NAME,"Error"));
            holder.email.setText(reader.getString(MyPreferences.USER_EMAIL,"Error"));
            holder.userId.setText("User ID: "+reader.getString(MyPreferences.USER_ID,"Error"));
        }
        else if (holder.type == ITEM_TYPE) {
            NavDrawerItem current = data.get(position-1);
            holder.title.setText(current.getTitle());
            holder.icon.setImageResource(current.getIcon());
        }

    }

    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_TYPE;

        return ITEM_TYPE;
    }


    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        int type;

        //for header
        TextView name;
        TextView email;
        TextView userId;

        //for items
        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView, int type) {
            super(itemView);
            this.type = type;

            if (type == HEADER_TYPE){
                name = (TextView)itemView.findViewById(R.id.txt_drawerheader_username);
                email = (TextView)itemView.findViewById(R.id.txt_drawerheader_email);
                userId = (TextView)itemView.findViewById(R.id.txt_drawerheader_userid);
            }
            else if (type == ITEM_TYPE) {
                title = (TextView) itemView.findViewById(R.id.txt_navdrawer_menuitem);
                icon = (ImageView) itemView.findViewById(R.id.img_navdrawer_icon);
            }
        }
    }
}
