package uk.me.desiderio.shiftt.ui.neighbourhood;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.ui.model.MapItem;

/**
 * Fragment to show {@link GoogleMap}. It does initial map settings and provides a interface to
 * interact with {@link GoogleMap}
 */

public class ShifttMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener {

    private static final String SAVED_STATE_KEY_MAP_CAMERA = "saved_state_key_map_camera";

    private static final float DEFAULT_MAP_ZOOM = 16;
    private GoogleMap googleMap;


    private LatLng currentLocation;
    private List<Polygon> polygons;
    private CameraPosition savedStateCameraPosition;
    private Marker currentPositionMarker;

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
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(SAVED_STATE_KEY_MAP_CAMERA, googleMap.getCameraPosition());
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

    }

    /**
     * Updates map with {@link MapItem} data provided as parameter
     */
    public void swapMapData(List<MapItem> mapItems) {
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
    public void reset() {
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


    @Override
    public void onPolygonClick(Polygon polygon) {
        // todo implement polygon click
        String tag = polygon.getTag().toString();
        Toast.makeText(getContext(), " : Polygon Name : " + tag, Toast.LENGTH_SHORT).show();
    }

    public void setCurrentLocation(LatLng lastKnownLocation, boolean isFresh) {
        // todo handle isFresh location to show location icon in a different colour
        this.currentLocation = lastKnownLocation;
        addCurrentLocationMarkers();
        moveMapCameraToCurrentLocation();
    }

    public void shouldShowEmptyStateMessage(boolean shouldShow) {
        if (shouldShow) {
            currentPositionMarker.showInfoWindow();
        } else {
            currentPositionMarker.hideInfoWindow();
        }
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

    private void addCurrentLocationMarkers() {
        if (googleMap != null && currentLocation != null) {
            String label = getCurrentLocationLabel(currentLocation);
            MarkerOptions options = new MarkerOptions()
                    // todo this show when click . change strategy add/remove title
                    .title(getString(R.string.map_location_no_data))
                    .position(currentLocation)
                    .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_add));
            currentPositionMarker = googleMap.addMarker(options);

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
        if (savedStateCameraPosition != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(savedStateCameraPosition);
            googleMap.moveCamera(update);
        } else {
            animateCameraToBounds(boundsBuilder);
        }
    }

    // other utils

    private String getCurrentLocationLabel(LatLng coors) {
        return getContext().getString(R.string.map_location_label, coors.latitude, coors.longitude);
    }
}
