package uk.me.desiderio.shiftt.ui.neighbourhood;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

/**
 * Fragment to show {@link GoogleMap}. It does initial map settings and provides a interface to
 * interact with {@link GoogleMap}
 */

public class ShifttMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = ShifttMapFragment.class.getSimpleName();

    private static final float DEFAULT_MAP_ZOOM = 14;

    public static final String SHIFTT_MAP_FRAGMENT_TAG = "map_fragment_tag";

    // TODO this seems is not injected
    private ViewModelFactory viewModelFactory;
    private GoogleMap googleMap;

    public static ShifttMapFragment newInstance() {
        return new ShifttMapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shiftt_map_fragment,
                                         container,
                                         false);

        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.
                findFragmentById(R.id.google_map_fragment);

        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NeighbourhoodViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(NeighbourhoodViewModel.class);
        // TODO: Use the ViewModel
        viewModel.getMessage().observe(this, message -> {
            //textView.setText(message);
        });

        viewModel.getMessage().setValue("Hello World, Autumn is coming [Neighbours]");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initMap(googleMap);
    }

    @SuppressLint("MissingPermission")
    public void initMap(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public void moveMapCamera(LatLng lastKnownLocation) {
        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    lastKnownLocation, DEFAULT_MAP_ZOOM));
        }

    }

    private void setGoogleMapBounds() {
        LatLngBounds searchBounds = new LatLngBounds(
                new LatLng(-44, 113), new LatLng(-10, 154));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(searchBounds, 0));
        // TODO : not sure if this can be called at the same time
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchBounds.getCenter(), DEFAULT_MAP_ZOOM));
    }
}
