package uk.me.desiderio.shiftt.ui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.AndroidSupportInjection;
import uk.me.desiderio.shiftt.NetworkStateResourceActivity;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.TweetListActivity;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.ui.model.MapItem;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

import static uk.me.desiderio.shiftt.ui.tweetlist.TweetListFragment.ARGS_PLACE_FULL_NAME_KEY;

/**
 * Fragment to show {@link GoogleMap}. It does initial map settings and provides a interface to
 * interact with {@link GoogleMap}
 */

public class ShifttMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener {

    private static final String SAVED_STATE_KEY_MAP_CAMERA = "saved_state_key_map_camera";
    private static final String SAVED_STATE_KEY_CURRENT_BOUNDS = "saved_state_key_current_bounds";
    private static final String MARKER_TAG_CURRENT_POSITION = "current_position_marker_tag";

    private static final float DEFAULT_MAP_ZOOM = 16;

    @Inject
    ViewModelFactory viewModelFactory;

    private MapDataViewModel viewModel;

    private GoogleMap googleMap;


    private LatLng currentLocation;
    private List<Polygon> polygons;
    private CameraPosition savedStateCameraPosition;
    private Marker currentPositionMarker;
    private LatLngBounds currentLatLngBounds;

    private Observer<Resource<List<MapItem>>> neighbourhoodResourceObserver;

    public static ShifttMapFragment newInstance() {
        return new ShifttMapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        polygons = new ArrayList<>();
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

        if (savedInstanceState != null) {
            savedStateCameraPosition = savedInstanceState.getParcelable(SAVED_STATE_KEY_MAP_CAMERA);
            if (savedInstanceState.containsKey(SAVED_STATE_KEY_CURRENT_BOUNDS)) {
                currentLatLngBounds = savedInstanceState.getParcelable(SAVED_STATE_KEY_CURRENT_BOUNDS);
            }
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.
                of(this, viewModelFactory).get(MapDataViewModel.class);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(SAVED_STATE_KEY_MAP_CAMERA, googleMap.getCameraPosition());
        }
        if (currentLatLngBounds != null) {
            outState.putParcelable(SAVED_STATE_KEY_CURRENT_BOUNDS, currentLatLngBounds);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initMap(googleMap);
        addCurrentLocationMarkers();
        updateMapCameraPosition();
    }

    @SuppressLint("MissingPermission")
    private void initMap(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setOnPolygonClickListener(this);
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        disableCurrentLocationMarkerClick();

    }

    /**
     * Updates map with {@link MapItem} data provided as parameter
     */
    private void swapMapData(List<MapItem> mapItems) {
        if (mapItems != null && !mapItems.isEmpty()) {
            resetMapPoligons();
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            mapItems
                    .forEach(mapItem -> {
                        Polygon polygon = addPoligon(mapItem.name, mapItem.coordinates);
                        includePolygonInBounds(polygon, boundsBuilder);
                        polygons.add(polygon);
                    });
            updateMapCameraOnBounds(boundsBuilder.build());
        }
    }

    /**
     * reset map removing polygon and reseting camera position to user location
     */
    private void reset() {
        currentLatLngBounds = null;
        resetMapPoligons();
        moveMapCameraToCurrentLocation();
    }

    /**
     * resets camera position flag so that the saved camera position is ignored and the default one
     * is used
     */
    public void resetCamera() {
        savedStateCameraPosition = null;
    }

    /**
     * animates map to current location if no polygons are displayed.
     * otherwise, animates to bounds containing all polygons
     */
    public void goToCurrentLocation() {
        if (currentLatLngBounds != null) {
            animateCameraToBounds(currentLatLngBounds);
        } else {
            moveMapCameraToCurrentLocation();
        }
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        String tag = polygon.getTag().toString();
        Intent intent = new Intent(getContext(), TweetListActivity.class);
        intent.putExtra(ARGS_PLACE_FULL_NAME_KEY, tag);
        startActivity(intent);
    }

    public void setCurrentLocation(LatLng lastKnownLocation, boolean isFresh) {
        this.currentLocation = lastKnownLocation;
        addCurrentLocationMarkers();
        moveMapCameraToCurrentLocation();
    }

    private void shouldShowEmptyStateMessage(boolean shouldShow) {
        if (shouldShow) {
            currentPositionMarker.showInfoWindow();
        } else {
            currentPositionMarker.hideInfoWindow();
        }
    }

    public void showMapData() {
        requestNeigbourhoodResource();
    }

    public void hideMapData() {
        resetNeighbourhoodDataObserver();
        reset();
    }

    private void requestNeigbourhoodResource() {
        if (neighbourhoodResourceObserver == null) {
            neighbourhoodResourceObserver = this::processResource;
        }
        viewModel.getMapItemsResource(null, true).observe(this,
                                                          neighbourhoodResourceObserver);
    }

    private void resetNeighbourhoodDataObserver() {
        if (neighbourhoodResourceObserver != null) {
            viewModel.getMapItemsResource(null, false)
                    .removeObserver(neighbourhoodResourceObserver);
        }
    }

    private void processResource(@NonNull Resource<List<MapItem>> resource) {
        swapMapData(resource.data);
        showEmptyView(resource.data, resource.status);
        updateGlobalViewStateOnResource(resource);
    }

    private void showEmptyView(List data, @Resource.ResourceStatus int status) {
        boolean shouldShow = status != Resource.LOADING && (data == null || data.isEmpty());
        shouldShowEmptyStateMessage(shouldShow);
    }


    // Map Polygons

    private Polygon addPoligon(String name, List<LatLng> coorsList) {
        PolygonOptions options =
                new PolygonOptions()
                        .fillColor(getContext().getColor(R.color.colorAccentLight_a50))
                        .strokeColor(getContext().getColor(R.color.colorAccent_a70))
                        .strokeWidth(4)
                        .geodesic(true)
                        .addAll(coorsList);
        Polygon polygon = googleMap.addPolygon(options);
        polygon.setClickable(true);
        polygon.setTag(name);
        return polygon;
    }

    private void includePolygonInBounds(Polygon polygon, LatLngBounds.Builder boundsBuilder) {
        polygon.getPoints()
                .forEach(boundsBuilder::include);
    }

    private void resetMapPoligons() {
        polygons.forEach(Polygon::remove);
        polygons.clear();
    }

    // Map Markers

    private void disableCurrentLocationMarkerClick() {
        googleMap.setOnMarkerClickListener(marker -> {
            return marker.getTag().equals(MARKER_TAG_CURRENT_POSITION);
            // do nothing
        });
    }

    private void addCurrentLocationMarkers() {
        if (googleMap != null && currentLocation != null) {
            MarkerOptions options = new MarkerOptions()
                    .title(getString(R.string.map_location_no_data))
                    .position(currentLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_loc));
            currentPositionMarker = googleMap.addMarker(options);
            currentPositionMarker.setTag(MARKER_TAG_CURRENT_POSITION);
        }
    }

    @SuppressWarnings("unused")
    private void addMarkers(String accountName, LatLng coors) {
        MarkerOptions options = new MarkerOptions()
                .title(accountName)
                .position(coors)
                .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_add));
        googleMap.addMarker(options);
    }

    // Map Camera

    private void updateMapCameraPosition() {
        if (savedStateCameraPosition != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(savedStateCameraPosition);
            googleMap.moveCamera(update);
        } else {
            moveMapCameraToCurrentLocation();
        }
    }

    private void moveMapCameraToCurrentLocation() {
        if (googleMap != null && currentLocation != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    currentLocation, DEFAULT_MAP_ZOOM), 500, null);
        }
    }

    private void animateCameraToBounds(LatLngBounds bounds) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 500, null);
    }

    private void updateMapCameraOnBounds(LatLngBounds boundsBuilder) {
        currentLatLngBounds = boundsBuilder;
        if (savedStateCameraPosition != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(savedStateCameraPosition);
            googleMap.moveCamera(update);
        } else {
            animateCameraToBounds(currentLatLngBounds);
        }
    }

    // other utils

    private String getCurrentLocationLabel(LatLng coors) {
        return getContext().getString(R.string.map_location_label, coors.latitude, coors.longitude);
    }

    private void updateGlobalViewStateOnResource(@NonNull Resource<List<MapItem>> resource) {
        ((NetworkStateResourceActivity) getActivity()).updateViewStateOnResource(resource,
                                                                                 v -> viewModel.retry(null));
    }
}
