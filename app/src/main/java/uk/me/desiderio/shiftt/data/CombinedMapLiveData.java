package uk.me.desiderio.shiftt.data;

import com.google.android.gms.maps.model.LatLng;
import com.twitter.sdk.android.core.models.Coordinates;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;
import uk.me.desiderio.shiftt.data.network.model.Place;
import uk.me.desiderio.shiftt.ui.model.MapItem;
import uk.me.desiderio.shiftt.ui.neighbourhood.ShifttMapFragment;

/**
 * {@link LiveData} to observe {@link MapItem} list to be shown in the {@link ShifttMapFragment}
 *
 * The class combines the different sources that will be shown on the map.
 *
 * {@link Tweet} object has two different location information items attached to them:
 *  - {@link Coordinates} Locates {@link Tweet} in a precised position
 *  - {@link Place} Locates {@link Tweet} in a area that normally coincides with a neighbourghood
 *
 *  These are not always present. {@link Place} will be prioritised. When missing the
 *  {@link Coordinates} will be used instead. In both cases a {@link MapItem} will be generated
 *  from their properties
 */
public class CombinedMapLiveData extends MediatorLiveData<List<MapItem>> {

    private List<MapItem> sources;

    private LiveData<List<PlaceEnt>> liveDataOne;
    // private LiveData<CoordinatesEnt> liveDataTwo;


    public CombinedMapLiveData(LiveData<List<PlaceEnt>> sourceOne) {

        sources = new ArrayList<>();

        super.addSource(sourceOne, placeEnts -> {
            liveDataOne = sourceOne;
            List<MapItem> items = placeEnts.stream()
                    .map(CombinedMapLiveData::getMapItemWithPlaceData)
                    .collect(Collectors.toList());

            sources.addAll(items);

            setCombinedValue();
        });
    }

    public static MapItem getMapItemWithPlaceData(PlaceEnt place) {
        List<LatLng> coordinates = new ArrayList<>();

        // TODO: 20/12/2018 : refractor to streams
        List<List<List<Double>>> groupCoorsList = place.boundingBox.coordinates;
        for (int i = 0; i < groupCoorsList.size(); i++) {
            List<List<Double>> coorList = groupCoorsList.get(i);
            for (int j = 0; j < coorList.size(); j++) {
                List<Double> coors = coorList.get(j);
                coordinates.add(new LatLng(coors.get(1),
                                           coors.get(0)));
            }
        }

        return new MapItem(place.fullName, coordinates);
    }

    private void setCombinedValue() {
        if (liveDataOne != null) {
            setValue(sources);
        }
    }

    // TODO: 20/12/2018 : Invalidate the addSource and removeSource local methods
}