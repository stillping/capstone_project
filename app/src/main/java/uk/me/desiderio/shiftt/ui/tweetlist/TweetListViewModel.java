package uk.me.desiderio.shiftt.ui.tweetlist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.TweetListActivity;

/**
 * ViewModel for the {@link TweetListActivity}
 */

public class TweetListViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> message;

    public TweetListViewModel() {
        this.message = new MutableLiveData<>();
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
}
