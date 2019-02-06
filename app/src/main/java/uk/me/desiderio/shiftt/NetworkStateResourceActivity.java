package uk.me.desiderio.shiftt;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;
import uk.me.desiderio.shiftt.util.SnackbarDelegate;

/**
 * parent activity for the app activities
 * provides progress bar and snackbar and handles its behaviour based on
 * {@link Resource} object provided by the data layer
 */

public abstract class NetworkStateResourceActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    protected SnackbarDelegate snackbarDelegate;
    protected ProgressBar progressBar;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject
    ConnectivityLiveData connectivityLiveData;
    private Observer<Boolean> onConnectedObserver;

    protected boolean isTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar = findViewById(R.id.base_progressBar);

        snackbarDelegate = initSnackbarDelegate();

        isTwoPane = getResources().getBoolean(R.bool.isTwoPane);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mini, menu);
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
        } else {
            // android.R.id.home default framework behaviour
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * defines task to be run to update view state on a new data layer response
     */
    public <T> void updateViewStateOnResource(@NonNull Resource<List<T>> resource,
                                              View.OnClickListener listener) {
        toggleProgressBar(resource.status);
        toggleSnackbar(resource.status, listener);
    }

    /**
     * registers for a back to connection event.
     * a snackbar will be shown with related message and an action defined by
     * the listener provided as a parameter
     */
    public void registerConnectedUpdates(View.OnClickListener listener) {
        if (onConnectedObserver == null) {
            onConnectedObserver = isConnected -> {
                if (isConnected) {
                    snackbarDelegate.showSnackbar(SnackbarDelegate.CONNECTED, listener);
                    connectivityLiveData.removeObserver(onConnectedObserver);
                    onConnectedObserver = null;
                }
            };
        }
        connectivityLiveData.observe(this, onConnectedObserver);
    }

    private void toggleSnackbar(@Resource.ResourceStatus int status,
                                View.OnClickListener listener) {
        if (status == Resource.ERROR) {
            snackbarDelegate.showSnackbar(SnackbarDelegate.ERROR, listener);
        } else if (status == Resource.NO_CONNECTION) {
            snackbarDelegate.showSnackbar(SnackbarDelegate.NO_CONNECTED, null);
            registerConnectedUpdates(listener);
        } else {
            // branch for Resource.LOADING || Resource.SUCCESS
            snackbarDelegate.hideSnackbar();
        }
    }

    private void toggleProgressBar(@Resource.ResourceStatus int status) {
        if (status == Resource.LOADING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    protected abstract SnackbarDelegate initSnackbarDelegate();

    @LayoutRes
    protected int getLayoutRes() {
        // default basic layout, main activity will provide a different one
        return R.layout.resource_base_activity;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
