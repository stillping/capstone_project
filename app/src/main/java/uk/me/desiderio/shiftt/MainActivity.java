package uk.me.desiderio.shiftt;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;

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
import uk.me.desiderio.shiftt.data.repository.RateLimiter;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.ui.main.MainActivityViewModel;
import uk.me.desiderio.shiftt.ui.model.MapItem;
import uk.me.desiderio.shiftt.ui.neighbourhood.ShifttMapFragment;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;
import uk.me.desiderio.shiftt.util.SnackbarDelegate;
import uk.me.desiderio.shiftt.util.permission.LocationPermissionRequest;
import uk.me.desiderio.shiftt.util.permission.PermissionManager;
import uk.me.desiderio.shiftt.util.permission.PermissionManager.PermissionStatus;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

public class MainActivity extends AppCompatActivity implements
        FloatingActionMenu.OnItemClickListener {

    private static final int MAIN_VIEW_STATE = 943;
    private static final int NEIGHBOURG_VIEW_STATE = 645;
    private static final String SAVED_VIEW_STATE_KEY = "saved_view_state_key";

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    LocationManager locationManager;
    @Inject
    PermissionManager permissionManager;
    @Inject
    ConnectivityLiveData connectivityLiveData;

    private FloatingActionMenu floatingActionMenu;
    private MainActivityViewModel viewModel;
    private LatLng lastKnownLocation;
    private ShifttMapFragment mapFragment;
    private ProgressBar progressBar;

    @ViewState
    private int currentViewState;

    private Observer<Resource<List<MapItem>>> neighbourhoodResourceObserver;
    private Observer<Boolean> connectivityObserver;
    private SnackbarDelegate snackbarDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        floatingActionMenu = findViewById(R.id.fab_menu);

        progressBar = findViewById(R.id.main_progress_bar);
        progressBar.setVisibility(View.GONE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = (ShifttMapFragment) fragmentManager.findFragmentById(R.id.main_map_fragment);

        ImageView refreshButton = findViewById(R.id.main_refresh_button);
        refreshButton.setOnClickListener(v -> requestFreshLocation());

        ImageView locationButton = findViewById(R.id.main_locate_button);
        locationButton.setOnClickListener(v -> mapFragment.goToCurrentLocation());


        FloatingActionMenu floatingActionMenu = findViewById(R.id.fab_menu);
        floatingActionMenu.setOnItemClickListener(this);

        snackbarDelegate = new SnackbarDelegate(R.string.snackbar_connected_message_map_suffix,
                                                floatingActionMenu);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        viewModel.getLocationViewData().observe(this, locationViewData -> {
            lastKnownLocation = new LatLng(locationViewData.getLatitude(),
                                           locationViewData.getLongitude());

            boolean isAFreshLocation = RateLimiter.isAFreshLocation(locationViewData.getTime());
            if (!isAFreshLocation) {
                requestFreshLocation();
                // todo show progress bar
            }
            mapFragment.setCurrentLocation(lastKnownLocation, isAFreshLocation);
        });

        // location permision request should be carried out before the view model is initialized
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            currentViewState = savedInstanceState.getInt(SAVED_VIEW_STATE_KEY);

        } else {
            // Probably initialize members with default values for a new instance
            currentViewState = MAIN_VIEW_STATE;
        }

        updateView(currentViewState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVED_VIEW_STATE_KEY, currentViewState);
        super.onSaveInstanceState(outState);
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
                mapFragment.resetCamera();
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
        } else if (id == android.R.id.home) {
            updateView(MAIN_VIEW_STATE);
        } else if (id == R.id.action_refresh_location) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Permissions
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

    // Location

    private void requestFreshLocation() {
        // initiate process to request for fresh location
        requestLocationPermissions();
    }

    private void initLocationUpdates() {
        if (connectivityObserver == null) {
            connectivityObserver = isConnected -> {
                if (!isConnected) {
                    snackbarDelegate.showSnackbar(SnackbarDelegate.NO_CONNECTED, null);
                    connectivityLiveData.removeObserver(connectivityObserver);
                    connectivityObserver = null;
                    registerConnectedUpdates(v -> viewModel.initLocationUpdates());
                } else {
                    viewModel.initLocationUpdates();
                }
            };
        }
        connectivityLiveData.observe(this, connectivityObserver);
    }

    // neighbourhood data

    private void requestNeigbourhoodResource() {
        if (neighbourhoodResourceObserver == null) {
            neighbourhoodResourceObserver = this::processResource;
        }
        viewModel.getNeighbourhoodResource(null).observe(this, neighbourhoodResourceObserver);
    }

    private void resetNeighbourhoodDataObserver() {
        if (neighbourhoodResourceObserver != null) {
            viewModel.getNeighbourhoodResource(null).removeObserver(neighbourhoodResourceObserver);
        }
    }

    private void processResource(@NonNull Resource<List<MapItem>> resource) {
        toggleProgressBar(resource.status);
        swapViewData(resource.data);
        toggleSnackbar(resource.status);
    }

    private void registerConnectedUpdates(View.OnClickListener listener) {
        if (connectivityObserver == null) {
            connectivityObserver = isConnected -> {
                if (isConnected) {
                    snackbarDelegate.showSnackbar(SnackbarDelegate.CONNECTED, listener);
                    connectivityLiveData.removeObserver(connectivityObserver);
                    connectivityObserver = null;
                }
            };
        }
        connectivityLiveData.observe(this, connectivityObserver);
    }


    // UI

    private void updateView(@ViewState int state) {
        currentViewState = state;

        switch (currentViewState) {
            default:
            case MAIN_VIEW_STATE:
                floatingActionMenu.setVisibility(View.VISIBLE);
                showUpButton(false);
                resetViewForMainState();
                break;
            case NEIGHBOURG_VIEW_STATE:
                floatingActionMenu.setVisibility(View.GONE);
                showUpButton(true);
                requestNeigbourhoodResource();
                break;
        }
    }

    private void showUpButton(boolean shouldShow) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(shouldShow);
    }

    private void toggleSnackbar(@Resource.ResourceStatus int status) {
        if (status == Resource.ERROR) {
            snackbarDelegate.showSnackbar(SnackbarDelegate.ERROR, v -> viewModel.retry(null));
        } else if (status == Resource.NO_CONNECTION) {
            snackbarDelegate.showSnackbar(SnackbarDelegate.NO_CONNECTED, null);
            registerConnectedUpdates(v -> viewModel.retry(null));
        } else {
            // branch for Resource.LOADING || Resource.SUCCESS
            snackbarDelegate.hideSnackbar();
        }
    }

    private void swapViewData(List<MapItem> mapItemsList) {
        mapFragment.swapMapData(mapItemsList);
        showEmptyView(mapItemsList == null || mapItemsList.isEmpty());
    }

    private void resetViewForMainState() {
        mapFragment.reset();

        progressBar.setVisibility(View.GONE);

        resetNeighbourhoodDataObserver();
    }

    private void showEmptyView(boolean shouldShow) {
        mapFragment.shouldShowEmptyStateMessage(shouldShow);
    }

    private void toggleProgressBar(@Resource.ResourceStatus int status) {
        if (status == Resource.LOADING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showNoLocationDialog() {
        // TODO : need to be implemented : can ask permission path
    }

    @IntDef({MAIN_VIEW_STATE, NEIGHBOURG_VIEW_STATE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ViewState {
    }
}