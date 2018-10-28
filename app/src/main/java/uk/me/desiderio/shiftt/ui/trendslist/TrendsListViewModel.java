package uk.me.desiderio.shiftt.ui.trendslist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.TrendsListActivity;

/**
 * ViewModel for the {@link TrendsListActivity}
 */

public class TrendsListViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> message;

    public TrendsListViewModel() {
        this.message = new MutableLiveData<>();
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
}
