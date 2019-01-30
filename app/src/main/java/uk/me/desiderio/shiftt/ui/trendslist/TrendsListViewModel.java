package uk.me.desiderio.shiftt.ui.trendslist;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.location.LocationQueryData;
import uk.me.desiderio.shiftt.data.repository.LocationRepository;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.data.repository.TrendsRepository;

/**
 * {@link ViewModel} for the {@link TrendsListFragment}
 * <p>
 * The model gathers the neccessary location parameters from the {@link LocationRepository}
 * and makes the request for new {@link TrendEnt}s to the {@link TrendsRepository}
 * <p>
 * Using {@link Transformations}, it provides the availability to refresh data request
 */

public class TrendsListViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final TrendsRepository trendsRepository;
    private final MutableLiveData<Void> liveData;

    @Inject
    public TrendsListViewModel(LocationRepository locationRepository,
                               TrendsRepository trendsRepository) {
        this.locationRepository = locationRepository;
        this.trendsRepository = trendsRepository;
        liveData = new MutableLiveData<>();

    }

    public LiveData<Resource<List<TrendEnt>>> getTrendsResource() {
        liveData.setValue(null);
        return Transformations.switchMap(liveData, none -> {
            LocationQueryData loc = locationRepository.getLocationQueryDataFromPreferences();

            return trendsRepository.getAllTrendsByLocation(loc.lat, loc.lng, loc.time);
        });
    }

    public void retry() {
        liveData.setValue(null);

    }
}
