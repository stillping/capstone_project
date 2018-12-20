package uk.me.desiderio.shiftt.ui.neighbourhood;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.ui.model.MapItem;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

/**
 * Fragment to show {@link GoogleMap}. It does initial map settings and provides a interface to
 * interact with {@link GoogleMap}
 */

public class ShifttMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolygonClickListener {

    public static final String SHIFTT_MAP_FRAGMENT_TAG = "map_fragment_tag";
    private static final String TAG = ShifttMapFragment.class.getSimpleName();
    private static final float DEFAULT_MAP_ZOOM = 14;
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
    public void onMapReady(GoogleMap googleMap) {
        initMap(googleMap);
    }

    @SuppressLint("MissingPermission")
    private void initMap(GoogleMap gMap) {
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

    public void swapMapData(List<MapItem> mapItems) {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        mapItems
                .forEach(mapItem -> {
                    Polygon polygon = addPoligon(mapItem.name, mapItem.coordinates);
                    includePolygonInBounds(polygon, boundsBuilder);
                });
        moveCameraToBounds(boundsBuilder.build());
    }

    private void moveCameraToBounds(LatLngBounds bounds) {
        Log.d(TAG, "swapMapData: campana northeast lat: " + bounds.northeast.latitude + " lng:" +
                " " + bounds.northeast.longitude);
        Log.d(TAG, "swapMapData: campana southwest lat: " + bounds.southwest.latitude + " lng:" +
                " " + bounds.southwest.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(),0));
    }

    private void includePolygonInBounds(Polygon polygon, LatLngBounds.Builder boundsBuilder) {
        polygon.getPoints()
                .forEach(latLng -> {
                    boundsBuilder.include(latLng);
                    Log.d(TAG, "includePolygonInBounds: campana lat: " + latLng.latitude + " lng:" +
                            " " + latLng.longitude);
                });
    }

    private Polygon addPoligon(String name, List<LatLng> coorsList) {
        PolygonOptions options =
                new PolygonOptions()
                        .fillColor(R.color.colorAccent)
                        .geodesic(true)
                        .addAll(coorsList);
        Polygon polygon = googleMap.addPolygon(options);
        polygon.setClickable(true);
        polygon.setTag(name);
        return polygon;
    }

    @SuppressWarnings("unused")
    private void addMarkers(String accountName, LatLng coors) {
        MarkerOptions options = new MarkerOptions()
                .title(accountName)
                .position(coors)
                .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_add));
        googleMap.addMarker(options);
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        String tag = polygon.getTag().toString();
        Log.d(TAG, "poligon tag name: " + tag);
        Toast.makeText(getContext(), " : Polygon Name : " + tag, Toast.LENGTH_SHORT).show();
    }
}
