package uk.me.desiderio.shiftt.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.network.TrendMockDataProvider;
import uk.me.desiderio.shiftt.data.network.model.Trend;

import static com.google.common.truth.Truth.assertThat;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_NAME_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_PLACE_COUNTRY_CODE_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_PLACE_COUNTRY_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_PLACE_NAME_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_PLACE_PARENT_ID_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_PLACE_TYPE_CODE_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_PLACE_TYPE_NAME_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_PLACE_URL_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_PLACE_WOE_ID_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_QUERY_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_TWEET_VOLUME_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.TREND_URL_VALUE;
import static uk.me.desiderio.shiftt.data.network.TrendMockDataProvider.getExtepectedName;

/**
 * tests serialization betweet {@link Trend} network dataSource object and {@link TrendEnt}
 * room database dataSource objects.
 */
@RunWith(JUnit4.class)
public class ParseTrendEntitiesTest {

    private static final int TREND_NAME_POSTSCRIPT = 22;
    // extected tweet
    private Trend initialTrend;
    // actual tweet
    private Trend returnedTrend;

    @Before
    public void setUp() {
        initialTrend = TrendMockDataProvider.getTrend(TREND_NAME_POSTSCRIPT);
        TrendEnt trendEntity = new TrendEnt(initialTrend);
        returnedTrend = trendEntity.getSeed();
    }

    @Test
    public void givenATrend_whenCreatingTrendEntity_thenTrendReturnedWithSameFieldsValues() {
        assertThat(returnedTrend).isNotNull();
        assertThat(returnedTrend.name).isEqualTo(getExtepectedName(TREND_NAME_VALUE,
                                                                   TREND_NAME_POSTSCRIPT));
        assertThat(returnedTrend.url).isEqualTo(TREND_URL_VALUE);
        assertThat(returnedTrend.query).isEqualTo(TREND_QUERY_VALUE);

        // Not implemented:
        //  assertThat(returnedTrend.promoted_content).isEqualTo(TREND_PROMOTED_CONTENT_VALUE);

        assertThat(returnedTrend.tweetVolume).isEqualTo(TREND_TWEET_VOLUME_VALUE);

        assertThat(returnedTrend.place).isNotNull();
        assertThat(returnedTrend.place.country).isEqualTo(TREND_PLACE_COUNTRY_VALUE);
        assertThat(returnedTrend.place.countryCode).isEqualTo(TREND_PLACE_COUNTRY_CODE_VALUE);
        assertThat(returnedTrend.place.name).isEqualTo(getExtepectedName
                                                               (TREND_PLACE_NAME_VALUE,
                                                                TREND_NAME_POSTSCRIPT));
        assertThat(returnedTrend.place.parentId).isEqualTo(TREND_PLACE_PARENT_ID_VALUE);
        assertThat(returnedTrend.place.url).isEqualTo(TREND_PLACE_URL_VALUE);
        assertThat(returnedTrend.place.woeid).isEqualTo(TREND_PLACE_WOE_ID_VALUE);

        assertThat(returnedTrend.place.placeType).isNotNull();
        assertThat(returnedTrend.place.placeType.code).isEqualTo(TREND_PLACE_TYPE_CODE_VALUE);
        assertThat(returnedTrend.place.placeType.name).isEqualTo(TREND_PLACE_TYPE_NAME_VALUE);
    }
}
