package uk.me.desiderio.shiftt.data.network;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.VisibleForTesting;
import uk.me.desiderio.shiftt.data.database.TrendsDao;
import uk.me.desiderio.shiftt.data.network.model.Place;
import uk.me.desiderio.shiftt.data.network.model.PlaceType;
import uk.me.desiderio.shiftt.data.network.model.Trend;


/**
 * Provides mocked serielised network data to carry out tests on {@link TrendsDao}
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
public class TrendMockDataProvider {

    public static final int TREND_LIST_COUNT = 10;

    public static final String TREND_NAME_VALUE = "trend_name_expected_value_";
    public static final String TREND_URL_VALUE = "trend_url_expected_value";
    public static final String TREND_QUERY_VALUE = "trend_query_expected_value";
    public static final int TREND_PROMOTED_CONTENT_VALUE = 22;
    public static final long TREND_TWEET_VOLUME_VALUE = 33L;

    public static final String TREND_PLACE_NAME_VALUE = "trend_place_name_expected_value_";
    public static final String TREND_PLACE_COUNTRY_VALUE = "trend_place_country_expected_value";
    public static final String TREND_PLACE_COUNTRY_CODE_VALUE = "trend_place_country_code_expected_value";
    public static final String TREND_PLACE_URL_VALUE = "trend_place_url_expected_value";

    public static final long TREND_PLACE_PARENT_ID_VALUE = 44L;
    public static final long TREND_PLACE_WOE_ID_VALUE = 55L;

    public static final int TREND_PLACE_TYPE_CODE_VALUE = 66;
    public static final String TREND_PLACE_TYPE_NAME_VALUE = "trend_place_type_name_expected_value";


    public static String getExtepectedName(String baseName, int index) {
        return baseName + index;
    }

    public static List<Trend> getTrendList() {
        List<Trend> trendList = new ArrayList<>();
        for (int i = 0; i < TREND_LIST_COUNT; i++) {
            trendList.add(getTrend(i));
        }
        return trendList;
    }

    public static Trend getTrend(int index) {
        return new Trend(TREND_NAME_VALUE + index,
                         TREND_URL_VALUE,
                         TREND_QUERY_VALUE,
                         TREND_TWEET_VOLUME_VALUE,
                         getMockPlace(index));
    }


    private static Place getMockPlace(int index) {
        return new Place(TREND_PLACE_COUNTRY_VALUE,
                         TREND_PLACE_COUNTRY_CODE_VALUE,
                         TREND_PLACE_NAME_VALUE + index,
                         TREND_PLACE_PARENT_ID_VALUE,
                         getMockPlaceType(),
                         TREND_PLACE_URL_VALUE,
                         TREND_PLACE_WOE_ID_VALUE);
    }

    private static PlaceType getMockPlaceType() {
        return new PlaceType(TREND_PLACE_TYPE_CODE_VALUE,
                             TREND_PLACE_TYPE_NAME_VALUE);
    }
}
