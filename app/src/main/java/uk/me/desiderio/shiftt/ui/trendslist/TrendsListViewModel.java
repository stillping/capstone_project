package uk.me.desiderio.shiftt.ui.trendslist;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.TrendsListActivity;

/**
 * ViewModel for the {@link TrendsListActivity}
 */

public class TrendsListViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> message;

    @Inject
    public TrendsListViewModel() {
        this.message = new MutableLiveData<>();
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
}
