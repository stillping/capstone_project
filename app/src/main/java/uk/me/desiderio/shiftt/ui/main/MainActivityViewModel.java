package uk.me.desiderio.shiftt.ui.main;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.MainActivity;
import uk.me.desiderio.shiftt.data.repository.LocationRepository;
import uk.me.desiderio.shiftt.data.repository.RateLimitsRepository;
import uk.me.desiderio.shiftt.data.repository.TweetsRepository;
import uk.me.desiderio.shiftt.ui.model.LocationViewData;
import uk.me.desiderio.shiftt.ui.model.MapItem;

/**
 * {@link ViewModel} for the {@link MainActivity}
 * <p>
 * The model gathers the neccessary location parameters from the {@link LocationRepository}
 * and makes the request for new {@link MapItem}s to the {@link TweetsRepository}
 * <p>
 * Using {@link Transformations}, it provides the availability to refresh data request
 * <p>
 * The model also initiates the {@link RateLimitsRepository} so that all rate limit data is ready
 * when Twitter API requests take place at a later stage.
 */

public class MainActivityViewModel extends ViewModel {

    private final LocationRepository locationRepository;

    @Inject
    public MainActivityViewModel(LocationRepository locationRepository,
                                 RateLimitsRepository rateLimitsRepository) {
        this.locationRepository = locationRepository;

        // init rateLimitRepo here
        // so that are ready for data request taking place after user selection
        rateLimitsRepository.initRateLimits();
    }

    public LiveData<LocationViewData> getLocationViewData() {
        return locationRepository.getLocationLiveData();
    }

    public void initLocationUpdates() {
        locationRepository.getCurrentFusedLocation();
    }
}