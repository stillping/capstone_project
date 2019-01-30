package uk.me.desiderio.shiftt.data.mock;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.emory.mathcs.backport.java.util.Collections;
import uk.me.desiderio.shiftt.data.TweetMockDataProvider;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.network.model.Place;
import uk.me.desiderio.shiftt.data.network.model.Trend;
import uk.me.desiderio.shiftt.data.network.model.TrendsQueryResult;

import static org.mockito.Mockito.mock;

/**
 * Provides mock data for unit tests
 */
public class DataProvider {

    public static final String EXPECTED_MAP_ENT_NAME = "map_long_name";
    public static final String EXPECTED_TREND_ENT_NAME = "trend_ent_name";
    public static final int EXPECTED_TRENDS_COUNT = 2;
    public static final int EXPECTED_TRENDS_QUERY_COUNT = 13;
    public static int EXPECTED_PLACES_COUNT;


    // TWEETS

    public static List<Tweet> getMockedTweetList() {
        return IntStream.range(0, 10)
                .mapToObj(i -> TweetMockDataProvider.getTweet(i))
                .collect(Collectors.toList());
    }


    public static List<PlaceEnt> getMockedPlaceEntList() {
        PlaceEnt onlyPlace = new PlaceEnt(null,
                                          null,
                                          null,
                                          null,
                                          EXPECTED_MAP_ENT_NAME,
                                          null,
                                          "name",
                                          null,
                                          null);
        PlaceEnt place = mock(PlaceEnt.class);
        return Collections.singletonList(onlyPlace);
    }

    // TRENDS

    public static List<TrendEnt> getMockedTrendEnts() {
        TrendEnt trendEntOne = new TrendEnt(EXPECTED_TREND_ENT_NAME,
                                            null,
                                            null,
                                            0,
                                            null);
        TrendEnt trendEntTwo = mock(TrendEnt.class);
        TrendEnt trendEntThree = mock(TrendEnt.class);
        return Arrays.asList(trendEntOne, trendEntTwo, trendEntThree);
    }

    public static List<Place> getMockedClosestPlaceList() {
        Place firstPlace = new Place("UK",
                                     null,
                                     null,
                                     0,
                                     null,
                                     null,
                                     44);
        Place secondPlace = new Place("Spain",
                                      null,
                                      null,
                                      0,
                                      null,
                                      null,
                                      34);
        Place thirdPlace = new Place("Unknown",
                                     null,
                                     null,
                                     0,
                                     null,
                                     null,
                                     99);
        return Arrays.asList(firstPlace, secondPlace, thirdPlace);
    }

    public static List<TrendsQueryResult> getMockedTrendsQueryResults() {
        Trend trendOne = mock(Trend.class);
        Trend trendTwo = mock(Trend.class);
        return Stream.generate(() -> {
            TrendsQueryResult result = new TrendsQueryResult();
            result.trends = Arrays.asList(trendOne, trendTwo);
            return result;
        })
                .limit(EXPECTED_TRENDS_QUERY_COUNT)
                .collect(Collectors.toList
                        ());
    }
}
