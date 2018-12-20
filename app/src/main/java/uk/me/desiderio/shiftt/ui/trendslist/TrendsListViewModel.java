package uk.me.desiderio.shiftt.ui.trendslist;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.data.ShifttRepository;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.ui.model.TrendsViewData;

/**
 * ViewModel for the {@link TrendsListFragment}
 */

public class TrendsListViewModel extends ViewModel {

    private ShifttRepository repository;

    @Inject
    public TrendsListViewModel(ShifttRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<TrendsViewData>> getTrends() {
        LiveData<List<TrendEnt>> trendsEntLiveData = repository.getTrendsListLiveData();

        return Transformations.map(trendsEntLiveData,
                                   trendEntList -> trendEntList.stream()
                                           .map(trenEnt -> new TrendsViewData(trenEnt.name))
                                           .collect(Collectors.toList()));
    }
}
