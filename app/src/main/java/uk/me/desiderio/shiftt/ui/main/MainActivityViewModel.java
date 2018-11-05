package uk.me.desiderio.shiftt.ui.main;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.MainActivity;
import uk.me.desiderio.shiftt.data.ShifttRepository;
import uk.me.desiderio.shiftt.model.LocationViewData;

/**
 * {@link ViewModel} for the {@link MainActivity}
 */

public class MainActivityViewModel extends ViewModel {

    // TODO: Implement the ViewModel
    private ShifttRepository repository;

    @Inject
    public MainActivityViewModel(ShifttRepository repository) {
        this.repository = repository;
    }

    public MutableLiveData<LocationViewData> getLocationViewData() {
        return repository.getLocationViewData();
    }

    public void getLastKnownLocation() {
        repository.updateLastKnownLocation();
    }

    public void initLocationUpdates() {
        repository.initLocationUpdates();
    }

    public void requestNeigbourhoodData() {
        repository.requestNeigbourhoodData();
    }
}
