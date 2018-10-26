package uk.me.desiderio.shiftt.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * {@link ViewModel} for the {@link MainActivity}
 */
public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<String> message;

    public MainActivityViewModel() {
        this.message = new MutableLiveData<>();
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
}
