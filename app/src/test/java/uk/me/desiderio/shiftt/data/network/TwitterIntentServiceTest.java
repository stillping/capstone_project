package uk.me.desiderio.shiftt.data.network;

import android.content.Context;
import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;

import androidx.annotation.Nullable;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static uk.me.desiderio.shiftt.data.network.TwitterIntentService.EXTRA_LATITUDE;
import static uk.me.desiderio.shiftt.data.network.TwitterIntentService.EXTRA_LONGITUDE;
import static uk.me.desiderio.shiftt.data.network.TwitterIntentService.EXTRA_RADIUS_SIZE;
import static uk.me.desiderio.shiftt.data.network.TwitterIntentService.EXTRA_RADIUS_UNIT;
import static uk.me.desiderio.shiftt.data.network.TwitterIntentService.EXTRA_TREND_NAME;
import static uk.me.desiderio.shiftt.data.network.TwitterIntentService.TASK_SEARCH_TWEETS_ON_LOCATION;
import static uk.me.desiderio.shiftt.data.network.TwitterIntentService.TASK_TRENDS_BY_LOCATION;

@RunWith(RobolectricTestRunner.class)
public class TwitterIntentServiceTest {

    private static final double EXPECTED_LAT_VALUE = 44d;
    private static final double EXPECTED_LNG_VALUE = 55d;
    private static final String EXPECTED_TREND_NAME_VALUE = "expected_trend_name_value";
    private static final String EXPECTED_RADIUS_SIZE_VALUE = "expected_radius_size_value";
    private static final String EXPECTED_RADIUS_UNIT_VALUE = "expected_radius_unit_value";

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    TwitterNetworkDataSource dataSource;
    @Mock
    private Context context;

    private TwitterIntentServiceWrapper intentService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        intentService = new TwitterIntentServiceWrapper();
        intentService.dataSource = dataSource;
    }

    @Test
    public void getClosestPlacesByLocationIntentTest() {
        Intent actualIntent = TwitterIntentService
                .getClosestPlacesByLocationIntent(context,
                                                  EXPECTED_LAT_VALUE,
                                                  EXPECTED_LNG_VALUE);
        assertThat(actualIntent).isNotNull();
        assertThat(actualIntent.getAction()).isEqualTo(TASK_TRENDS_BY_LOCATION);
        assertThat(actualIntent.getExtras().keySet()).containsExactly(EXTRA_LATITUDE,
                                                                      EXTRA_LONGITUDE);
        assertThat(actualIntent.getDoubleExtra(EXTRA_LATITUDE, 0))
                .isEqualTo(EXPECTED_LAT_VALUE);
        assertThat(actualIntent.getDoubleExtra(EXTRA_LONGITUDE, 0))
                .isEqualTo(EXPECTED_LNG_VALUE);
    }

    @Test
    public void getSearchTweetsByLocationAndTrendIntentTest() {

        Intent actualIntent = TwitterIntentService
                .getSearchTweetsByLocationAndTrendIntent(context,
                                                         EXPECTED_LAT_VALUE,
                                                         EXPECTED_LNG_VALUE,
                                                         EXPECTED_TREND_NAME_VALUE,
                                                         EXPECTED_RADIUS_SIZE_VALUE,
                                                         EXPECTED_RADIUS_UNIT_VALUE);

        assertThat(actualIntent).isNotNull();
        assertThat(actualIntent.getAction()).isEqualTo(TASK_SEARCH_TWEETS_ON_LOCATION);
        assertThat(actualIntent.getExtras().keySet()).containsExactly(EXTRA_LATITUDE,
                                                                      EXTRA_LONGITUDE,
                                                                      EXTRA_TREND_NAME,
                                                                      EXTRA_RADIUS_SIZE,
                                                                      EXTRA_RADIUS_UNIT);
        assertThat(actualIntent.getDoubleExtra(EXTRA_LATITUDE, 0))
                .isEqualTo(EXPECTED_LAT_VALUE);
        assertThat(actualIntent.getDoubleExtra(EXTRA_LONGITUDE, 0))
                .isEqualTo(EXPECTED_LNG_VALUE);
        assertThat(actualIntent.getStringExtra(EXTRA_TREND_NAME))
                .isEqualTo(EXPECTED_TREND_NAME_VALUE);
        assertThat(actualIntent.getStringExtra(EXTRA_RADIUS_SIZE))
                .isEqualTo(EXPECTED_RADIUS_SIZE_VALUE);
        assertThat(actualIntent.getStringExtra(EXTRA_RADIUS_UNIT))
                .isEqualTo(EXPECTED_RADIUS_UNIT_VALUE);
    }

    @Test
    public void getSearchTweetsByLocationAndTrendIntentWithNullTrendValueTest() {
        Intent actualIntent = TwitterIntentService
                .getSearchTweetsByLocationAndTrendIntent(context,
                                                         EXPECTED_LAT_VALUE,
                                                         EXPECTED_LNG_VALUE,
                                                         null,
                                                         EXPECTED_RADIUS_SIZE_VALUE,
                                                         EXPECTED_RADIUS_UNIT_VALUE);

        assertThat(actualIntent).isNotNull();
        assertThat(actualIntent.getAction()).isEqualTo(TASK_SEARCH_TWEETS_ON_LOCATION);
        assertThat(actualIntent.getExtras().keySet()).containsExactly(EXTRA_LATITUDE,
                                                                      EXTRA_LONGITUDE,
                                                                      EXTRA_RADIUS_SIZE,
                                                                      EXTRA_RADIUS_UNIT);
        assertThat(actualIntent.getDoubleExtra(EXTRA_LATITUDE, 0))
                .isEqualTo(EXPECTED_LAT_VALUE);
        assertThat(actualIntent.getDoubleExtra(EXTRA_LONGITUDE, 0))
                .isEqualTo(EXPECTED_LNG_VALUE);
        assertThat(actualIntent.getStringExtra(EXTRA_TREND_NAME)).isNull();
        assertThat(actualIntent.getStringExtra(EXTRA_RADIUS_SIZE))
                .isEqualTo(EXPECTED_RADIUS_SIZE_VALUE);
        assertThat(actualIntent.getStringExtra(EXTRA_RADIUS_UNIT))
                .isEqualTo(EXPECTED_RADIUS_UNIT_VALUE);
    }

    @Test
    public void
    givenOnHandleIntentIsCalled_whenSearchTweetsByLocationAndTrendIntentIsProvide_thenRightMethodIsCalled() {
        Intent actualIntent = TwitterIntentService
                .getSearchTweetsByLocationAndTrendIntent(context,
                                                         EXPECTED_LAT_VALUE,
                                                         EXPECTED_LNG_VALUE,
                                                         EXPECTED_TREND_NAME_VALUE,
                                                         EXPECTED_RADIUS_SIZE_VALUE,
                                                         EXPECTED_RADIUS_UNIT_VALUE);

        intentService.onHandleIntent(actualIntent);

        ArgumentCaptor<String> trendArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> latArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> lngArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<String> radiusSizeArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> radiusUnitArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(dataSource, Mockito.times(1))
                .requestTweetsOnTrendNameAndGeocode(trendArgumentCaptor.capture(),
                                                    latArgumentCaptor.capture(),
                                                    lngArgumentCaptor.capture(),
                                                    radiusSizeArgumentCaptor.capture(),
                                                    radiusUnitArgumentCaptor.capture());


        assertThat(trendArgumentCaptor.getValue()).isEqualTo(EXPECTED_TREND_NAME_VALUE);
        assertThat(latArgumentCaptor.getValue()).isEqualTo(EXPECTED_LAT_VALUE);
        assertThat(lngArgumentCaptor.getValue()).isEqualTo(EXPECTED_LNG_VALUE);
        assertThat(radiusSizeArgumentCaptor.getValue()).isEqualTo(EXPECTED_RADIUS_SIZE_VALUE);
        assertThat(radiusUnitArgumentCaptor.getValue()).isEqualTo(EXPECTED_RADIUS_UNIT_VALUE);
    }

    @Test
    public void
    givenOnHandleIntentIsCalled_whenClosestPlacesByLocationIntentIsProvide_thenRightMethodIsCalled() {
        Intent actualIntent = TwitterIntentService
                .getClosestPlacesByLocationIntent(context,
                                                  EXPECTED_LAT_VALUE,
                                                  EXPECTED_LNG_VALUE);

        intentService.onHandleIntent(actualIntent);


        ArgumentCaptor<Double> latArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> lngArgumentCaptor = ArgumentCaptor.forClass(Double.class);


        verify(dataSource, Mockito.times(1))
                .requestClosestPlacesByLocation(latArgumentCaptor.capture(),
                                                lngArgumentCaptor.capture());


        assertThat(latArgumentCaptor.getValue()).isEqualTo(EXPECTED_LAT_VALUE);
        assertThat(lngArgumentCaptor.getValue()).isEqualTo(EXPECTED_LNG_VALUE);
    }

    @After
    public void tearDown() {
        Mockito.validateMockitoUsage();

    }

    private class TwitterIntentServiceWrapper extends TwitterIntentService {

        public TwitterIntentServiceWrapper() {
            super();
        }

        @Override
        public void onHandleIntent(@Nullable Intent intent) {
            super.onHandleIntent(intent);
        }
    }
}