package it.asfitness.FitUnionAndroidClienti.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.Date;

import it.asfitness.FitUnionAndroidClienti.fragment.GymFragment;
import it.asfitness.FitUnionAndroidClienti.R;
import it.asfitness.FitUnionAndroidClienti.fragment.MapGymFragment;
import it.asfitness.FitUnionAndroidClienti.fragment.QrCodeGeneratorFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GymFragment.OnListFragmentInteractionListener,QrCodeGeneratorFragment.OnFragmentInteractionListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    TextView email;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        displayView(R.id.nav_gym_list);
        getPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_button_map, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_map) {
            Fragment fragment = new MapGymFragment().newInstance();
            menu.findItem(R.id.action_list).setVisible(false);

            replaceFragment(fragment);
            if (menu != null) {
                menu.findItem(R.id.action_list).setVisible(true);
                menu.findItem(R.id.action_map).setVisible(false);
            }
        } else if (id == R.id.action_list) {
            Fragment fragment = new GymFragment(this).newInstance(this);
            replaceFragment(fragment);
            if (menu != null) {
                menu.findItem(R.id.action_list).setVisible(false);
                menu.findItem(R.id.action_map).setVisible(true);

            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void getPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
            finish();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displayView(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayView(int id) {
        String title = getString(R.string.app_name);
        Fragment fragment = null;
        switch (id){
            case R.id.nav_gym_list:
                fragment = new GymFragment(this).newInstance(this);
                if(menu != null){
                    menu.findItem(R.id.action_map).setVisible(true);
                    menu.findItem(R.id.action_list).setVisible(false);
                }
                break;
            case R.id.nav_my_card:
                fragment = new QrCodeGeneratorFragment().newInstance();
                if(menu != null){
                    menu.findItem(R.id.action_map).setVisible(false);
                    menu.findItem(R.id.action_list).setVisible(false);
                }
                break;
            case R.id.nav_logout:
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        MainActivity.this.startActivity(intent);
                        finish();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                    }
                });
        }
        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        if (fragment != null) {
            replaceFragment(fragment);
         //   DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
          //  drawer.closeDrawer(GravityCompat.START);
        }
    }



    public void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }





    @Override
    protected void onResume() {
        super.onResume();
        displayView(R.id.nav_gym_list);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(double latitude, double longitude) {
        // Creates an Intent that will load a map of San Francisco
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr="+latitude+","+longitude));
        startActivity(intent);
    }
}
