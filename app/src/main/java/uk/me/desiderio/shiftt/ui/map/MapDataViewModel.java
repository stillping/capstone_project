package uk.me.desiderio.shiftt.ui.map;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.data.location.LocationQueryData;
import uk.me.desiderio.shiftt.data.repository.LocationRepository;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.data.repository.TweetsRepository;
import uk.me.desiderio.shiftt.ui.model.MapItem;

/**
 * ViewModel for the {@link ShifttMapFragment}
 */
public class MapDataViewModel extends ViewModel {

    private final TweetsRepository tweetsRepository;
    private final LocationRepository locationRepository;

    // used to re-start data request
    private final MutableLiveData<String> mapDataLiveData;

    @Inject
    public MapDataViewModel(LocationRepository locationRepository,
                            TweetsRepository tweetsRepository) {
        this.locationRepository = locationRepository;
        this.tweetsRepository = tweetsRepository;

        this.mapDataLiveData = new MutableLiveData<>();
    }

    public LiveData<Resource<List<MapItem>>> getMapItemsResource(String trendName,
                                                                 boolean observerImmediately) {
        if (observerImmediately) {
            mapDataLiveData.setValue(trendName);
        }
        return Transformations.switchMap(mapDataLiveData, name -> {
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
        mapDataLiveData.setValue(trendName);
    }
}
