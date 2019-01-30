package uk.me.desiderio.shiftt.data.database;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import uk.me.desiderio.shiftt.data.database.model.QueryTrendEnt;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.network.TrendMockDataProvider;
import uk.me.desiderio.shiftt.util.LiveDataTestUtil;

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


/**
 * Tests for the {@link TrendsDao}
 */
public class TrendsDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TrendsDao dao;

    @Before
    public void setUp() {
        ShifttDatabase database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ShifttDatabase.class)
                .allowMainThreadQueries()
                .build();

        dao = database.trendsDao();
    }


    private void populateDatabase() {
        List<TrendEnt> trendEntList = getTrendEntList();
        dao.insertTrendEntList(trendEntList);
    }

    private List<TrendEnt> getTrendEntList() {
        return TrendMockDataProvider.getTrendList().stream()
                .map(TrendEnt::new)
                .collect(Collectors.toList());
    }


    // Retrieve All

    @Test
    public void whenGetAllQueryTrends_thenRightCountRetrieved() throws InterruptedException {
        populateDatabase();

        List<QueryTrendEnt> queryTrendEntityList = LiveDataTestUtil.getValue(
                dao.getAllQueryTrends());

        assertThat(queryTrendEntityList).isNotNull();
        assertThat(queryTrendEntityList).hasSize(10);
    }

    @Test
    public void whenGetAllQueryTrends_thenRightlyPopulatedEntitiesAreRetrieved() throws
            InterruptedException {
        populateDatabase();

        List<QueryTrendEnt> queryTrendEntityList = LiveDataTestUtil.getValue(
                dao.getAllQueryTrends());

        assertThat(queryTrendEntityList.get(0)).isInstanceOf(QueryTrendEnt.class);
        assertThat(queryTrendEntityList.get(0).trendEntity).isNotNull();
        assertThat(queryTrendEntityList.get(0).trendEntity.placeName).isNotEmpty();
        assertThat(queryTrendEntityList.get(0).placeList).isNotEmpty();
    }


    @Test
    public void whenGetAllTrends_thenRightCountRetrieved() throws InterruptedException {
        populateDatabase();

        List<TrendEnt> queryTrendEntityList = LiveDataTestUtil.getValue(
                dao.getAllTrends());

        assertThat(queryTrendEntityList).isNotNull();
        assertThat(queryTrendEntityList).hasSize(10);
    }

    @Test
    public void whenGetAllTrends_thenEntitiesHaveChildObjectsRightlyPopulated()
            throws
            InterruptedException {
        populateDatabase();

        List<TrendEnt> trendEntityList = LiveDataTestUtil.getValue(
                dao.getAllTrends());

        assertThat(trendEntityList.get(0)).isInstanceOf(TrendEnt.class);
        assertThat(trendEntityList.get(0)).isNotNull();
        assertThat(trendEntityList.get(0).placeName).isNotEmpty();
        assertThat(trendEntityList.get(0).place).isNotNull();
    }

    @Test
    public void whenGetAllTrends_thenEntitiesWithRightValuesAreRetrieved() throws
            InterruptedException {
        populateDatabase();

        List<TrendEnt> trendEntityList = LiveDataTestUtil.getValue(
                dao.getAllTrends());

        TrendEnt actualTrendEnt = trendEntityList.get(0);
        String expectedTrendName = TrendMockDataProvider.getExtepectedName(TREND_NAME_VALUE,
                                                                           0);
        String expectedPlaceName = TrendMockDataProvider.getExtepectedName(TREND_PLACE_NAME_VALUE,
                                                                           0);

        assertThat(actualTrendEnt.name).isEqualTo(expectedTrendName);
        assertThat(actualTrendEnt.url).isEqualTo(TREND_URL_VALUE);
        assertThat(actualTrendEnt.query).isEqualTo(TREND_QUERY_VALUE);
        // not implemented yet
        // assertThat(actualTrendEnt.promotedContent).isEqualTo(TREND_PROMOTED_CONTENT_VALUE);
        assertThat(actualTrendEnt.tweetVolume).isEqualTo(TREND_TWEET_VOLUME_VALUE);
        assertThat(actualTrendEnt.placeName).isEqualTo(expectedPlaceName);

        assertThat(actualTrendEnt.place).isNotNull();
    }


    @Test
    public void whenGetAllTrends_thenEntitiesWithRightPlaceEntityIsRetrieved() throws
            InterruptedException {
        populateDatabase();

        List<TrendEnt> trendEntityList = LiveDataTestUtil.getValue(
                dao.getAllTrends());

        TrendEnt actualTrendEnt = trendEntityList.get(0);
        String expectedPlaceName = TrendMockDataProvider.getExtepectedName(TREND_PLACE_NAME_VALUE,
                                                                           0);

        assertThat(actualTrendEnt.place.country).isEqualTo(TREND_PLACE_COUNTRY_VALUE);
        assertThat(actualTrendEnt.place.countryCode).isEqualTo(TREND_PLACE_COUNTRY_CODE_VALUE);
        assertThat(actualTrendEnt.place.placeName).isEqualTo(expectedPlaceName);
        assertThat(actualTrendEnt.place.parentId).isEqualTo(TREND_PLACE_PARENT_ID_VALUE);
        assertThat(actualTrendEnt.place.url).isEqualTo(TREND_PLACE_URL_VALUE);
        assertThat(actualTrendEnt.place.woeid).isEqualTo(TREND_PLACE_WOE_ID_VALUE);

        assertThat(actualTrendEnt.place.placeType).isNotNull();
        assertThat(actualTrendEnt.place.placeType.code).isEqualTo(TREND_PLACE_TYPE_CODE_VALUE);
        assertThat(actualTrendEnt.place.placeType.name).isEqualTo(TREND_PLACE_TYPE_NAME_VALUE);
    }
}