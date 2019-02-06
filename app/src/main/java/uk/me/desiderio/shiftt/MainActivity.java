package uk.me.desiderio.shiftt;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.AndroidInjection;
import uk.me.desiderio.fabmenu.FloatingActionMenu;
import uk.me.desiderio.shiftt.data.repository.RateLimiter;
import uk.me.desiderio.shiftt.ui.main.MainActivityViewModel;
import uk.me.desiderio.shiftt.ui.map.ShifttMapFragment;
import uk.me.desiderio.shiftt.ui.model.LocationViewData;
import uk.me.desiderio.shiftt.ui.trendslist.TrendsListFragment;
import uk.me.desiderio.shiftt.ui.tweetlist.TweetListFragment;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;
import uk.me.desiderio.shiftt.util.SnackbarDelegate;
import uk.me.desiderio.shiftt.util.permission.LocationPermissionRequest;
import uk.me.desiderio.shiftt.util.permission.PermissionManager;
import uk.me.desiderio.shiftt.util.permission.PermissionManager.PermissionStatus;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

import static uk.me.desiderio.shiftt.TrendsListActivity.REQUEST_CODE_TRENDS;
import static uk.me.desiderio.shiftt.TweetListActivity.REQUEST_CODE_TWEETS;
import static uk.me.desiderio.shiftt.ui.tweetlist.TweetListFragment.ARGS_PLACE_FULL_NAME_KEY;

public class MainActivity extends NetworkStateResourceActivity implements
        FloatingActionMenu.OnItemClickListener, ShifttMapFragment.OnPolygonClickedListener {

    public static final int TRENDS_VIEW_STATE = 896;
    public static final int TWEETS_VIEW_STATE = 325;
    private static final int MAIN_VIEW_STATE = 943;
    private static final int NEIGHBOURG_VIEW_STATE = 645;
    private static final int NEIGHBOURG_NO_MAP_DATA_LOADING_VIEW_STATE = 646;
    private static final String SAVED_VIEW_STATE_KEY = "saved_view_state_key";
    private static final String SAVED_PLACE_FULL_NAME_KEY = "saved_place_full_name_key";
    private static final String MAP_FRAGMENT_TAG = "map_fragment_tag";


    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    LocationManager locationManager;
    @Inject
    PermissionManager permissionManager;
    @Inject
    ConnectivityLiveData connectivityLiveData;

    private FloatingActionMenu floatingActionMenu;
    private ImageView refreshButton;
    private ImageView locationButton;
    private MainActivityViewModel viewModel;
    private LatLng lastKnownLocation;
    private ShifttMapFragment mapFragment;

    @ViewState
    private int currentViewState;
    private String savedPlaceFullName;

    private Observer<Boolean> connectivityObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        floatingActionMenu = findViewById(R.id.fab_menu);
        snackbarDelegate.setAnchorView(floatingActionMenu);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.base_fragment_container,
                             ShifttMapFragment.newInstance(),
                             MAP_FRAGMENT_TAG)
                    .commitNow();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = (ShifttMapFragment) fragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG);

        refreshButton = findViewById(R.id.main_refresh_button);
        refreshButton.setOnClickListener(v -> requestFreshLocation());

        locationButton = findViewById(R.id.main_locate_button);
        locationButton.setOnClickListener(v -> mapFragment.goToCurrentLocation());


        FloatingActionMenu floatingActionMenu = findViewById(R.id.fab_menu);
        floatingActionMenu.setOnItemClickListener(this);


        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        viewModel.getLocationViewData().observe(this, locationViewData -> {
            lastKnownLocation = new LatLng(locationViewData.getLatitude(),
                                           locationViewData.getLongitude());

            boolean isAFreshLocation = RateLimiter.isAFreshLocation(locationViewData.getTime());
            if (!isAFreshLocation) {
                requestFreshLocation();
            }
            mapFragment.setCurrentLocation(lastKnownLocation, isAFreshLocation);
        });

        // location permision request should be carried out before the view model is initialized
        int initialViewState;
        if (savedInstanceState != null) {
            initialViewState = savedInstanceState.getInt(SAVED_VIEW_STATE_KEY);
            savedPlaceFullName = savedInstanceState.getString(SAVED_PLACE_FULL_NAME_KEY);
        } else {
            initialViewState = MAIN_VIEW_STATE;
        }

        updateView(initialViewState, savedPlaceFullName);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVED_VIEW_STATE_KEY, currentViewState);
        outState.putString(SAVED_PLACE_FULL_NAME_KEY, savedPlaceFullName);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (handleBackAction()) {
            super.onBackPressed();
        }
    }

    private boolean handleBackAction() {

        if (isTwoPane && currentViewState == TRENDS_VIEW_STATE) {
            updateView(MAIN_VIEW_STATE, null);
            return true;
        } else if (isTwoPane && currentViewState == TWEETS_VIEW_STATE) {
            updateView(NEIGHBOURG_NO_MAP_DATA_LOADING_VIEW_STATE, null);
            return true;
        } else if (currentViewState == NEIGHBOURG_VIEW_STATE) {
            updateView(MAIN_VIEW_STATE, null);
            return true;
        }
        return false;
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
        if (id == android.R.id.home) {
            handleBackAction();
        } else if (id == R.id.action_refresh_location) {
            requestFreshLocation();
            updateView(MAIN_VIEW_STATE, null);
            return true;
        }

        // settings behaviour provided by parent
        return super.onOptionsItemSelected(item);
    }

    /**
     * When rotation to landscape in tablets, {@link TrendsListActivity}
     * and {@link TweetListActivity} are closed the two pain {@link MainActivity}
     * <p>
     * Ensure that the state is kept and same view are shown
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isTwoPane) {
            switch (requestCode) {
                case REQUEST_CODE_TRENDS:
                    updateView(TRENDS_VIEW_STATE, null);
                    break;
                case REQUEST_CODE_TWEETS:
                    String fullName = data.getStringExtra(ARGS_PLACE_FULL_NAME_KEY);
                    updateView(TWEETS_VIEW_STATE, fullName);
                    break;
            }
        } else if (requestCode == REQUEST_CODE_TWEETS) {
            updateView(NEIGHBOURG_VIEW_STATE, null);
        }
    }

    @Override
    public void onFloatingMenuItemClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.fab_trends:
                showTrends();
                break;
            case R.id.fab_neighbourhood:
                mapFragment.resetCamera();
                updateView(NEIGHBOURG_VIEW_STATE, null);
                break;
            default:
        }
    }

    @Override
    public void onPolygonClicked(String placeFullName) {
        showTweets(placeFullName);
    }

    // NetworkStateResourceActivity impl.

    @Override
    protected SnackbarDelegate initSnackbarDelegate() {
        View rootView = findViewById(android.R.id.content);
        return new SnackbarDelegate(this, R.string.snackbar_connected_message_map_suffix,
                                    rootView);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
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
                requestPermissions();
                break;
        }
    }

    private void requestPermissions() {
        permissionManager.requestPermissions(LocationPermissionRequest.REQUIRED_PERMISIONS,
                                             LocationPermissionRequest.LOCATION_REQUEST_CODE);

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
                    snackbarDelegate.showSnackbar(SnackbarDelegate.NO_CONNECTED,
                                                  null);
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

    // UI

    private void updateView(@ViewState int state,
                            @Nullable String args) {
        if (currentViewState == state) return;

        setCurrentViewState(state);
        savedPlaceFullName = null;

        switch (state) {
            case MAIN_VIEW_STATE:
                toggleContentLayoutVisibility(false);
                showHomeViewUI(true, true);
                mapFragment.hideMapData();
                progressBar.setVisibility(View.GONE);
                break;
            case NEIGHBOURG_NO_MAP_DATA_LOADING_VIEW_STATE:
                toggleContentLayoutVisibility(false);
                showHomeViewUI(false, true);
                break;
            case NEIGHBOURG_VIEW_STATE:
                toggleContentLayoutVisibility(false);
                showHomeViewUI(false, true);
                mapFragment.showMapData();
                break;
            case TRENDS_VIEW_STATE:
                showHomeViewUI(false, false);

                showTrendsView();
                break;
            case TWEETS_VIEW_STATE:
                showHomeViewUI(false, false);
                savedPlaceFullName = args;
                showTweetView(getTweetsBundle(savedPlaceFullName));
                break;

        }
    }

    private void setCurrentViewState(int viewState) {
        currentViewState = (viewState == NEIGHBOURG_NO_MAP_DATA_LOADING_VIEW_STATE)
                ? NEIGHBOURG_VIEW_STATE
                : viewState;
    }

    private void showTweets(@NonNull String placeFullName) {
        if (isTwoPane) {
            updateView(TWEETS_VIEW_STATE, placeFullName);
        } else {
            Intent intent = new Intent(this, TweetListActivity.class);
            intent.putExtras(getTweetsBundle(placeFullName));
            startActivityForResult(intent, REQUEST_CODE_TWEETS);
        }
    }

    private Bundle getTweetsBundle(String placeFullName) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_PLACE_FULL_NAME_KEY, placeFullName);
        return bundle;
    }

    private void showTweetView(@NonNull Bundle bundle) {
        if (isTwoPane) {
            toggleContentLayoutVisibility(true);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.base_additional_content_container, TweetListFragment.newInstance(bundle))
                    .commitNow();
        } else {
            Intent intent = new Intent(this, TweetListActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_CODE_TWEETS);
        }
    }

    private void showTrends() {
        if (isTwoPane) {
            updateView(TRENDS_VIEW_STATE, null);
        } else {
            Intent intent = new Intent(this, TrendsListActivity.class);
            startActivityForResult(intent, REQUEST_CODE_TRENDS);
        }
    }

    private void showTrendsView() {
        if (isTwoPane) {
            toggleContentLayoutVisibility(true);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.base_additional_content_container, TrendsListFragment.newInstance())
                    .commitNow();
        } else {
            Intent intent = new Intent(this, TrendsListActivity.class);
            startActivityForResult(intent, REQUEST_CODE_TRENDS);
        }

    }

    private void toggleContentLayoutVisibility(boolean showContent) {
        if (isTwoPane) {
            FrameLayout f = findViewById(R.id.base_additional_content_container);
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) f.getLayoutParams();
            lp.horizontalWeight = (showContent) ? 3 : 0;
            f.setLayoutParams(lp);
        }
    }

    private void showHomeViewUI(boolean shouldShowOtherButtons, boolean shouldShowLocationButton) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!shouldShowOtherButtons);

        int visibility = (shouldShowOtherButtons) ? View.VISIBLE : View.GONE;
        floatingActionMenu.setVisibility(visibility);
        refreshButton.setVisibility(visibility);

        int visLocationButton = (shouldShowLocationButton) ? View.VISIBLE : View.GONE;
        locationButton.setVisibility(visLocationButton);
    }

    private void showNoLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.noLocation_dialog_message))
                .setTitle(getString(R.string.noLocation_dialog_title));
        builder.setPositiveButton(R.string.ok, (dialog, id) -> requestPermissions());
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> finish());

        builder.create().show();
    }

    @IntDef({MAIN_VIEW_STATE, NEIGHBOURG_VIEW_STATE, TWEETS_VIEW_STATE, TRENDS_VIEW_STATE, NEIGHBOURG_NO_MAP_DATA_LOADING_VIEW_STATE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ViewState {
    }
}