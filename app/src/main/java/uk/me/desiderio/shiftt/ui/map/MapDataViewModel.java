package uk.me.desiderio.shiftt.ui.map;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
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

    @Inject
    public MapDataViewModel(LocationRepository locationRepository,
                            TweetsRepository tweetsRepository) {
        this.locationRepository = locationRepository;
        this.tweetsRepository = tweetsRepository;
    }

    public LiveData<Resource<List<MapItem>>> getMapItemsResource(String trendName) {
        LocationQueryData loc = locationRepository.getLocationQueryDataFromPreferences();
        return tweetsRepository.getMapsItems(trendName,
                                             loc.lat,
                                             loc.lng,
                                             loc.time,
                                             loc.radiusSize,
                                             loc.radiusUnit);

    }

    // wip : ST-202

}
