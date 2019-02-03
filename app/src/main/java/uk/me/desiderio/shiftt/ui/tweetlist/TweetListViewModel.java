package uk.me.desiderio.shiftt.ui.tweetlist;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import uk.me.desiderio.shiftt.TweetListActivity;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;
import uk.me.desiderio.shiftt.data.location.LocationQueryData;
import uk.me.desiderio.shiftt.data.repository.LocationRepository;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.data.repository.TweetsRepository;

/**
 * ViewModel for the {@link TweetListActivity}
 */
public class TweetListViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final TweetsRepository tweetsRepository;

    @Inject
    public TweetListViewModel(LocationRepository locationRepository,
                              TweetsRepository tweetsRepository) {
        this.locationRepository = locationRepository;
        this.tweetsRepository = tweetsRepository;
    }

    public LiveData<Resource<List<Tweet>>> getTweetOnPlace(String placeFullName) {
        LocationQueryData loc = locationRepository.getLocationQueryDataFromPreferences();

        LiveData<Resource<List<TweetEnt>>> tweetEntListLive =
                tweetsRepository.getTweetsOnPlace(placeFullName,
                                                  loc.lat, loc.lng, loc.time,
                                                  loc.radiusSize, loc.radiusUnit);

        // converts db data to view data
        return Transformations.map(tweetEntListLive, tweetEntRes -> {
            List<Tweet> tweetList = null;
            if (tweetEntRes.data != null) {
                tweetList = tweetEntRes.data.stream()
                        .map(TweetEnt::getSeed)
                        .collect(Collectors.toList());
            }
            return Resource.clone(tweetEntRes.status, tweetList, tweetEntRes.message);
        });
    }
}
