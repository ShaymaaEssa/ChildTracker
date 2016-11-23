package nanodegree.mal.udacity.android.childtracker;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import nanodegree.mal.udacity.android.childtracker.activity.AddPlaceFragment;
import nanodegree.mal.udacity.android.childtracker.activity.FragmentDrawer;
import nanodegree.mal.udacity.android.childtracker.activity.MainFragment;
import nanodegree.mal.udacity.android.childtracker.activity.JoinParentFragment;
import nanodegree.mal.udacity.android.childtracker.activity.PlacesFragment;
import nanodegree.mal.udacity.android.childtracker.firebase.FCMRegistrationService;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{

    private Toolbar toolbar;
    private FragmentDrawer fragmentDrawer;
    private String userId;

    //to handle rotation of the device and open the same fragment
    public static final int MAIN_FRAGMENT = 1;
    public static final int PLACES_FRAGMENT = 2;
    public static final int JOIN_PARENT_FRAGMENT = 3;
    public static final int ADD_PLACE_FRAGMENT = 5;



    private static int currentFragment = MAIN_FRAGMENT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            //to register location at the first use of application
            startService(new Intent(this, LocationUpdateService.class));

            startService(new Intent(this, FCMRegistrationService.class));

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            userId = this.getSharedPreferences(MyPreferences.MY_PREFERENCES, Context.MODE_PRIVATE).getString(MyPreferences.USER_ID, "0");


            fragmentDrawer = (FragmentDrawer)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            fragmentDrawer.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
            fragmentDrawer.setFragmentDrawerListener(this);
        if (savedInstanceState == null) {
            // display the first navigation drawer view on app launch
            displayView(1);
        }
        else {
            currentFragment = savedInstanceState.getInt("CurrentFragment",1);
            displayView(currentFragment);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position-1) { //we subtract 1 because of  header position
            case 0:
                fragment = new MainFragment();
                title = "MAP";
                break;
            case 1:
                fragment = new PlacesFragment();
                title = "Add Place";
                break;
            case 2:
                fragment = new JoinParentFragment();
                title = "Join Parent";
                break;
            case 3 :
                shareApplicationVia();
                break;
            case 4:
                fragment = new AddPlaceFragment();
                title = "Add Place";

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    private void shareApplicationVia() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Please Setup Children Tracker Application and enter Parent Id = "+userId;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Children Tracker App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CurrentFragment",currentFragment);
    }

    public static void setCurrentFragment(int currentFragment) {
        MainActivity.currentFragment = currentFragment;
    }
}

