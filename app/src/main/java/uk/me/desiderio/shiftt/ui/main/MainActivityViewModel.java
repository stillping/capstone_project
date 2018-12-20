package uk.me.desiderio.shiftt.ui.main;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.MainActivity;
import uk.me.desiderio.shiftt.data.ShifttRepository;
import uk.me.desiderio.shiftt.ui.model.LocationViewData;
import uk.me.desiderio.shiftt.ui.model.MapItem;

/**
 * {@link ViewModel} for the {@link MainActivity}
 */

public class MainActivityViewModel extends ViewModel {

    private ShifttRepository repository;

    @Inject
    public MainActivityViewModel(ShifttRepository repository) {
        this.repository = repository;
    }

    public LiveData<LocationViewData> getLocationViewData() {
        return repository.getLocationLiveData();
    }

    public void getLastKnownLocation() {
        repository.updateLastKnownLocation();
    }

    public void initLocationUpdates() {
        repository.getCurrentFusedLocation();
    }

    public LiveData<List<MapItem>> requestNeigbourhoodData() {
        return repository.requestNeigbourhoodData();
    }
}