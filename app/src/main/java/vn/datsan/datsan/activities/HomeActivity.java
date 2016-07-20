package vn.datsan.datsan.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.datsan.datsan.R;
import vn.datsan.datsan.serverdata.FieldDataManager;
import vn.datsan.datsan.serverdata.storage.CloudDataStorage;
import vn.datsan.datsan.ui.appviews.LoginPopup;
import vn.datsan.datsan.utils.AppLog;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener {
    private static final String TAG = HomeActivity.class.getName();

    private GoogleMap mMap;
    private TextView userName;
    private Button loginLogout;
    @BindView(R.id.searchResultView)
    View searchResultView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        ButterKnife.bind(this);
        FirebaseAuth.getInstance().addAuthStateListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                String name = CloudDataStorage.getInstance().genUniqFileName();
                AppLog.log(AppLog.LogType.LOG_ERROR, TAG, name);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);

        loginLogout = (Button) headerview.findViewById(R.id.loginLogout);
        loginLogout.setOnClickListener(onLoginLogoutBtnClicked);
        userName = (TextView) headerview.findViewById(R.id.userName);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        reloadView();
    }

    private void reloadView() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous()) {
            userName.setText("Annonymous");
            loginLogout.setText("Login");
        } else {
            userName.setText(user.getEmail());
            loginLogout.setText("Log out");
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(10.777098, 106.695487);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//        GeoFire geoFire = new GeoFire(new Firebase(Constants.FIREBASE_URL));
//        geoFire.setLocation("mycity", new GeoLocation(10.777098, 106.695487));

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        AppLog.log(AppLog.LogType.LOG_DEBUG, TAG, "onCreateOptionMenu");
        MenuItem itemSearch = menu.findItem(R.id.mapview_menu_search);

    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
    EditText searchEdit = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
    searchEdit.setFocusable(true);
    searchEdit.setFocusableInTouchMode(true);
    searchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            AppLog.log(AppLog.LogType.LOG_DEBUG, TAG, "OnFocusChanged");
            if (hasFocus) {
                searchResultView.setVisibility(View.VISIBLE);
            } else {
                searchResultView.setVisibility(View.GONE);
            }
        }
    });

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            AppLog.log(AppLog.LogType.LOG_DEBUG, TAG, query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            AppLog.log(AppLog.LogType.LOG_DEBUG, TAG, newText);
            return false;
        }
    });

    searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            AppLog.log(AppLog.LogType.LOG_DEBUG, TAG, "OnQueryTextFocusChange");
        }
    });
    searchView.setOnSearchClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchResultView.setVisibility(View.VISIBLE);
        }
    });

    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            searchResultView.setVisibility(View.GONE);
            return false;
        }
    });

    searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            AppLog.log(AppLog.LogType.LOG_DEBUG, TAG, "OnFocusChanged");
            if (hasFocus) {
                searchResultView.setVisibility(View.VISIBLE);
            } else {
                searchResultView.setVisibility(View.GONE);
            }
        }
    });

        MenuItemCompat.setOnActionExpandListener(itemSearch,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        // Return true to allow the action view to expand
                        searchResultView.setVisibility(View.VISIBLE);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        // When the action view is collapsed, reset the query
                        searchResultView.setVisibility(View.GONE);
                        // Return true to allow the action view to collapse
                        return true;
                    }
                });
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (id == R.id.nav_profile) {
            if (user != null)
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public View.OnClickListener onLoginLogoutBtnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null || user.isAnonymous()) {
                new LoginPopup(HomeActivity.this).show();
            } else {
                FirebaseAuth.getInstance().signOut();
            }
        }
    };

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        AppLog.log(AppLog.LogType.LOG_ERROR, TAG, "AuthChanged");
        reloadView();
    }
}