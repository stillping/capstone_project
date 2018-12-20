package uk.me.desiderio.shiftt.data.location;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import androidx.lifecycle.LiveData;

/**
 * {@link LiveData} to hold a {@link Location}. The class retrieves the location from Google's
 * Place Service using the {@link FusedLocationProviderClient}
 */

public class LocationLiveData extends LiveData<Location> {

    private static final String TAG = LocationLiveData.class.getSimpleName();

    private FusedLocationProviderClient locationProviderClient;
    private LocationCallback locationCallback;


    public LocationLiveData(FusedLocationProviderClient locationProviderClient) {
        this.locationProviderClient = locationProviderClient;
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "Place : Data : callback: " + locationResult);
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    setValue(location);
                }
            }
        };
    }

    @Override
    protected void onActive() {
        Log.d(TAG, "Place : Data : active");
        startLocationUpdates();
    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "Place : Data : Inactive");
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationProviderClient.requestLocationUpdates(locationRequest,
                                                      locationCallback,
                                                      null);
    }

    private void stopLocationUpdates() {
        locationProviderClient.removeLocationUpdates(locationCallback);
    }
}
