package uk.me.desiderio.shiftt.ui.main;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.MainActivity;
import uk.me.desiderio.shiftt.data.repository.RateLimitsRepository;
import uk.me.desiderio.shiftt.data.repository.LocationRepository;
import uk.me.desiderio.shiftt.data.repository.TweetsRepository;
import uk.me.desiderio.shiftt.data.location.LocationQueryData;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.ui.model.LocationViewData;
import uk.me.desiderio.shiftt.ui.model.MapItem;

/**
 * {@link ViewModel} for the {@link MainActivity}
 *
 * The model gathers the neccessary location parameters from the {@link LocationRepository}
 * and makes the request for new {@link MapItem}s to the {@link TweetsRepository}
 *
 * Using {@link Transformations}, it provides the availability to refresh data request
 *
 * The model also initiates the {@link RateLimitsRepository} so that all rate limit data is ready
 * when Twitter API requests take place at a later stage.
 */

public class MainActivityViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final TweetsRepository tweetsRepository;
    private final MutableLiveData<String> trendNameLiveData;

    @Inject
    public MainActivityViewModel(LocationRepository locationRepository,
                                 TweetsRepository tweetsRepository,
                                 RateLimitsRepository rateLimitsRepository) {
        this.locationRepository = locationRepository;
        this.tweetsRepository = tweetsRepository;

        // init rateLimitRepo here
        // so that are ready for data request taking place after user selection
        rateLimitsRepository.initRateLimits();

        this.trendNameLiveData = new MutableLiveData<>();
    }

    public LiveData<LocationViewData> getLocationViewData() {
        return locationRepository.getLocationLiveData();
    }

    public void initLocationUpdates() {
        locationRepository.getCurrentFusedLocation();
    }

    public LiveData<Resource<List<MapItem>>> getNeighbourhoodResource(String trendName) {
        trendNameLiveData.setValue(trendName);
        return Transformations.switchMap(trendNameLiveData, name -> {
            LocationQueryData loc = locationRepository.getLocationQueryDataFromPreferences();

            return tweetsRepository.getMapsItems(name,
                                                 loc.lat,
                                                 loc.lng,
                                                 loc.time,
                                                 loc.radiusSize,
                                                 loc.radiusUnit);
        });
    }


    public void retry(String trendName) {
        trendNameLiveData.setValue(trendName);
    }
}