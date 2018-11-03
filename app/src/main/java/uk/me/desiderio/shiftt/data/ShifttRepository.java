package uk.me.desiderio.shiftt.data;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import uk.me.desiderio.shiftt.data.location.LocationLiveData;
import uk.me.desiderio.shiftt.model.LocationViewData;

/**
 * Handles application's data operations
 */

public class ShifttRepository {

    private static final String TAG = ShifttRepository.class.getSimpleName();
    private static final int TEN_MINUTES = 1000 * 60 * 10;
    private final FusedLocationProviderClient fusedLocationProviderClient;

    private ShifttSharedPreferences sharedPreferences;
    private MediatorLiveData<LocationViewData> locationViewData;

    // TODO implement this
    private boolean isLocationDataStale(long locationTime) {
        return false;
    }

    // todo HERE set current time and define stale. based on that request start the location live
    // data
    @Inject
    public ShifttRepository(FusedLocationProviderClient fusedLocationProviderClient,
                            ShifttSharedPreferences sharedPreferences) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.sharedPreferences = sharedPreferences;
        this.locationViewData = new MediatorLiveData<>();
    }

    // TODO check if there is location services and connectivity
    public void initLocationUpdates(){
        LocationLiveData locationLiveData = new LocationLiveData(fusedLocationProviderClient);

        locationViewData.addSource(locationLiveData, location -> {
            if(location != null) {
                locationViewData.removeSource(locationLiveData);
                storeLLatestKnownLocation(location);
                locationViewData.setValue(retrieveLocationViewDataFromPreferences());
        }});
    }

    private void storeLLatestKnownLocation(Location location) {
        sharedPreferences.setLastKnownLocation(location.getLatitude(),
                                               location.getLongitude(),
                                               location.getTime());
    }

    private LocationViewData retrieveLocationViewDataFromPreferences() {
        double lat = sharedPreferences.getLastKnownLatitude();
        double lon = sharedPreferences.getLastKnownLongitude();
        long time = sharedPreferences.getLastKnownLocationTime();

        // TODO add logic to decide whether the data is stale or not based on time
        return new LocationViewData(lat, lon, false);
    }

    public void updateLastKnownLocation() {
        LocationViewData viewData = retrieveLocationViewDataFromPreferences();
        locationViewData.setValue(viewData);
    }

    public MutableLiveData<LocationViewData> getLocationViewData() {
        return locationViewData;
    }
}
