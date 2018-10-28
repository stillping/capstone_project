package uk.me.desiderio.shiftt.ui.neighbourhood;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.NeighbourhoodActivity;

/**
 * ViewModel for the {@link NeighbourhoodActivity}
 */

public class NeighbourhoodViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> message;

    public NeighbourhoodViewModel() {
        this.message = new MutableLiveData<>();
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
}
