package uk.me.desiderio.shiftt;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.snackbar.Snackbar;
import com.twitter.sdk.android.core.models.Tweet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.AndroidInjection;
import uk.me.desiderio.fabmenu.FloatingActionMenu;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;
import uk.me.desiderio.shiftt.data.location.LocationPermissionRequest;
import uk.me.desiderio.shiftt.ui.main.MainActivityViewModel;
import uk.me.desiderio.shiftt.ui.model.MapItem;
import uk.me.desiderio.shiftt.ui.neighbourhood.ShifttMapFragment;
import uk.me.desiderio.shiftt.utils.PermissionManager;
import uk.me.desiderio.shiftt.utils.PermissionManager.PermissionStatus;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

public class MainActivity extends AppCompatActivity implements
        FloatingActionMenu.OnItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MAIN_VIEW_STATE = 943;
    private static final int NEIGHBOURG_VIEW_STATE = 645;

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    LocationManager locationManager;
    @Inject
    PermissionManager permissionManager;

    private FloatingActionMenu floatingActionMenu;
    private MainActivityViewModel viewModel;
    private LatLng lastKnownLocation;
    private ShifttMapFragment mapFragment;

    @ViewState
    private int currentViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        floatingActionMenu = findViewById(R.id.fab_menu);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = (ShifttMapFragment) fragmentManager.findFragmentById(R.id.main_map_fragment);

        FloatingActionMenu floatingActionMenu = findViewById(R.id.fab_menu);
        floatingActionMenu.setOnItemClickListener(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        viewModel.getLocationViewData().observe(this, locationViewData -> {
            Log.d(TAG, " location : latitude: " + locationViewData.getLatitude());
            lastKnownLocation = new LatLng(locationViewData.getLatitude(),
                                           locationViewData.getLongitude());
            moveMapCamera(lastKnownLocation);

        });

        // update view with lst location available
        viewModel.getLastKnownLocation();

        // location permision request should be carried out before the view model is initialized
        requestLocationPermissions();
    }

    @Override
    public void onBackPressed() {
        if (currentViewState == NEIGHBOURG_VIEW_STATE) {
            updateView(MAIN_VIEW_STATE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFloatingMenuItemClick(View v) {
        int viewId = v.getId();
        String message = " NO message";

        switch (viewId) {
            case R.id.fab_trends:
                message = "trends clicked";
                Intent intent = new Intent(this, TrendsListActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_neighbourhood:
                message = "neighbour clicked";
                updateView(NEIGHBOURG_VIEW_STATE);
                break;
            default:
        }

        Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home){
            updateView(MAIN_VIEW_STATE);
        } else if (id == R.id.action_refresh_location) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LocationPermissionRequest.LOCATION_REQUEST_CODE:
                if (hasAnyPermissionGranted(grantResults)) {
                    initLocationUpdates();
                } else {
                    showNoLocationDialog();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestLocationPermissions() {
        @PermissionStatus
        int permissionStatus = permissionManager
                .getPermissionStatus(LocationPermissionRequest.REQUIRED_PERMISIONS);

        switch (permissionStatus) {
            case PermissionManager.PERMISSION_GRANTED:
                initLocationUpdates();
                break;
            case PermissionManager.CAN_ASK_PERMISSION:
                showNoLocationDialog();
                break;
            case PermissionManager.PERMISSION_DENIED:
                permissionManager.requestPermissions(LocationPermissionRequest.REQUIRED_PERMISIONS,
                                                     LocationPermissionRequest.LOCATION_REQUEST_CODE);
                break;
        }
    }

    private boolean hasAnyPermissionGranted(int[] grantResults) {
        if (grantResults.length > 0) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initLocationUpdates() {
        viewModel.initLocationUpdates();
    }

    private Observer<List<MapItem>> neighbourhoodDataObserver;
    private void requestNeigbourhoodData() {
        if(neighbourhoodDataObserver == null) {
            neighbourhoodDataObserver = mapItemsList -> {
                Log.d(TAG, "xmaes : requestNeigbourhoodData: " + mapItemsList.size());
                mapFragment.swapMapData(mapItemsList);
            };
        }
        viewModel.requestNeigbourhoodData().observe(this, neighbourhoodDataObserver);
    }

    private void removeNeighbourhoodData() {
        viewModel.requestNeigbourhoodData().removeObserver(neighbourhoodDataObserver);
        // TODO remove neighbourhood data from map
    }

    private void showNoLocationDialog() {
        // TODO : need to be implemented : can ask permission path
    }

    private void moveMapCamera(LatLng lastKnownLocation) {
        mapFragment.moveMapCamera(lastKnownLocation);
    }

    // TODO add animation when hiding/showing the fab menu
    private void updateView(@ViewState int state) {
        currentViewState = state;

        switch (currentViewState) {
            default:
            case MAIN_VIEW_STATE:
                floatingActionMenu.setVisibility(View.VISIBLE);
                showUpButton(false);
                break;
            case NEIGHBOURG_VIEW_STATE:
                floatingActionMenu.setVisibility(View.GONE);
                showUpButton(true);
                requestNeigbourhoodData();
                break;
        }
    }

    private void showUpButton(boolean shouldShow) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(shouldShow);
        getSupportActionBar().setDisplayShowHomeEnabled(shouldShow);
    }

    @IntDef({MAIN_VIEW_STATE, NEIGHBOURG_VIEW_STATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewState {   }





}