package uk.me.desiderio.shiftt.data.repository;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import uk.me.desiderio.shiftt.data.ShifttSharedPreferences;
import uk.me.desiderio.shiftt.data.database.ShifttDatabase;
import uk.me.desiderio.shiftt.data.location.FusedLocationLiveData;
import uk.me.desiderio.shiftt.data.location.LocationQueryData;
import uk.me.desiderio.shiftt.ui.model.LocationViewData;
import uk.me.desiderio.shiftt.util.AppExecutors;

/**
 * Repository to hadle location data
 * Location data is persisted as preference
 * Position data and the retrieval time is manamged by the application
 * Search area data is set by the user. This includes:
 *  - radius ( in km or miles ) of the area
 *  - the units of this value
 *
 *  Request to the Google Location API is done with the use of a {@link FusedLocationLiveData}
 *
 *  Location updates are provided using {@link LiveData}
 *
 *  It also provides a utils method to retrieve current location to be used for
 *  the Twitter API requests
 *
 *  Database is clear when a new location is received
 */
@Singleton
public class LocationRepository {

    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final AppExecutors appExecutors;
    private final ShifttSharedPreferences sharedPreferences;
    private final MediatorLiveData<LocationViewData> locationLiveData;
    private final ShifttDatabase database;

    @Inject
    public LocationRepository(ShifttDatabase database,
                              FusedLocationProviderClient fusedLocationProviderClient,
                              ShifttSharedPreferences sharedPreferences,
                              AppExecutors appExecutors) {
        this.database = database;
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.sharedPreferences = sharedPreferences;
        this.appExecutors = appExecutors;

        locationLiveData = new MediatorLiveData<>();
    }

    /**
     * provides {@link LocationViewData} with location data to be display in view
     * as a {@link LiveData}
     */
    public MutableLiveData<LocationViewData> getLocationLiveData() {
        // init with last known location
        LocationViewData viewData = retrieveLocationViewDataFromPreferences();
        locationLiveData.setValue(viewData);

        return locationLiveData;
    }

    public void getCurrentFusedLocation() {
        FusedLocationLiveData fusedLocationLiveData = new FusedLocationLiveData(fusedLocationProviderClient);
        this.locationLiveData.addSource(fusedLocationLiveData, location -> {
            if (location != null) {
                this.locationLiveData.removeSource(fusedLocationLiveData);
                persistLatestKnownLocation(location);
                this.locationLiveData.setValue(retrieveLocationViewDataFromPreferences());
                clearAllData();
            }
        });
    }

    // clear all data after a new location has been retrieved
    private void clearAllData() {
        appExecutors.getDiskIO().execute(database::clearAllTables);
    }

    // Shared Preference -------
    private LocationViewData retrieveLocationViewDataFromPreferences() {
        LocationQueryData loc = getLocationQueryDataFromPreferences();
        return new LocationViewData(loc.lat, loc.lng, loc.time);
    }

    private void persistLatestKnownLocation(Location location) {
        sharedPreferences.setLastKnownLocation(location.getLatitude(),
                                               location.getLongitude(),
                                               location.getTime() / 1000);
    }

    /**
     * gets {@link LocationQueryData} object with current persisted query data needed for
     * Twitter API requests  */
    public LocationQueryData getLocationQueryDataFromPreferences() {
        return new LocationQueryData(sharedPreferences.getLastKnownLatitude(),
                                     sharedPreferences.getLastKnownLongitude(),
                                     sharedPreferences.getLastKnownLocationTime(),
                                     sharedPreferences.getSearchRadiusUnits(),
                                     sharedPreferences.getSearchRadiusSize());
    }
}