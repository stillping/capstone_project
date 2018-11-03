package uk.me.desiderio.shiftt;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.AndroidInjection;
import uk.me.desiderio.fabmenu.FloatingActionMenu;
import uk.me.desiderio.shiftt.data.location.LocationPermissionRequest;
import uk.me.desiderio.shiftt.ui.main.MainActivityViewModel;
import uk.me.desiderio.shiftt.utils.PermissionManager;
import uk.me.desiderio.shiftt.utils.PermissionManager.PermissionStatus;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

public class MainActivity extends AppCompatActivity implements
        FloatingActionMenu.OnItemClickListener {


    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    ViewModelFactory viewModelFactory;


    @Inject
    LocationManager locationManager;

    @Inject
    PermissionManager permissionManager;


    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionMenu floatingActionMenu = findViewById(R.id.fab_menu);
        floatingActionMenu.setOnItemClickListener(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        // TODO: Use the ViewModel

        viewModel.getLocationViewData().observe(this, location -> {
            Log.d(TAG, " location(2) : latitude: " + location.getLatitude());
            // TODO show location in the map
        });
        viewModel.getLastKnownLocation();

        // location permision request should be carried out before the view model is initialized
        requestLocationPermissions();
    }

    @Override
    public void onFloatingMenuItemClick(View v) {
        int viewId = v.getId();
        String message = " NO message";

        Class activityClass = null;

        switch (viewId) {
            case R.id.fab_trends:
                message = "trends clicked";
                activityClass = TrendsListActivity.class;
                break;
            case R.id.fab_neighbourhood:
                message = "neighbour clicked";
                activityClass = NeighbourhoodActivity.class;
                break;
            default:
        }

        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
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
            return true;
        } else if(id == R.id.action_refresh_location) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
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

    private void initLocationUpdates() {
        viewModel.initLocationUpdates();
    }

    private void showNoLocationDialog() {
        // TODO : need to be implemented : can ask permission path
    }

    private boolean hasAnyPermissionGranted(int[] grantResults) {
        if(grantResults.length > 0) {
            for(int result : grantResults) {
                if(result == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }
}