package uk.me.desiderio.shiftt.data.database;

import android.util.Log;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.UrlEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import uk.me.desiderio.shiftt.data.database.model.CoordinatesEnt;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;
import uk.me.desiderio.shiftt.data.database.model.QueryTweetEnt;
import uk.me.desiderio.shiftt.data.database.model.QueryTweetEntitiesHashtagEntityJoin;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEntitiesEnt;
import uk.me.desiderio.shiftt.utils.LiveDataTestUtil;

import static com.google.common.truth.Truth.assertThat;
import static uk.me.desiderio.shiftt.data.TweetMockDataProvider.*;


public class ShifttDaoTest {

    private static final String TAG = ShifttDaoTest.class.getSimpleName();


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ShifttDatabase database;
    private ShifttDao dao;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ShifttDatabase.class)
                .allowMainThreadQueries()
                .build();

        dao = database.shifttDao();
    }

    // helper to populate database

    private void populateDatabase(boolean hasNullFieldValues) {
        List<TweetEnt> tweetEntities = getTweetEntities(hasNullFieldValues);
        dao.insertTweetEntities(tweetEntities);
    }

    private List<TweetEnt> getTweetEntities(boolean hasNullFieldValues) {
        List<TweetEnt> tweetList = new ArrayList<>();

        for (long i = 1; i < 11; i++) {
            tweetList.add(getTweetEnt(i, hasNullFieldValues));
        }

        return tweetList;
    }

    // Retrieve all

    @Test
    public void whenGetAllFeaturedPopTweetsEntQuery_thenRightCountRetrieved() throws
            InterruptedException {
        populateDatabase(false);

        List<QueryTweetEnt> queryTweetEntityList = LiveDataTestUtil.getValue(
                dao.getAllFeaturedPopTweetsEntQuery());

        assertThat(queryTweetEntityList).isNotNull();
        assertThat(queryTweetEntityList).hasSize(10);
        assertThat(queryTweetEntityList.get(0)).isInstanceOf(QueryTweetEnt.class);
    }


    @Test
    public void whenGetAllFeaturedPopTweetsEnt_thenRightCountRetrieved() throws
            InterruptedException {
        populateDatabase(false);

        List<Tweet> queryTweetEntityList = LiveDataTestUtil.getValue(
                dao.getAllFeaturedPopTweets());

        assertThat(queryTweetEntityList).isNotNull();
        assertThat(queryTweetEntityList).hasSize(10);
        assertThat(queryTweetEntityList.get(0)).isInstanceOf(Tweet.class);
    }

    @Test
    public void whenATweetEntIsRetrieved_thenRightTweetEntIsRetrieved() throws
            InterruptedException {
        populateDatabase(false);

        long firstItemId = 1;
        TweetEnt tweetEntity = getTweetEntityById(firstItemId);

        assertThat(tweetEntity).isNotNull();
        assertThat(tweetEntity).isInstanceOf(TweetEnt.class);
        assertThat(tweetEntity.id).isEqualTo(firstItemId);
    }

    @Test
    public void whenATweetIsRetrieved_thenRightTweetIsRetrieved() throws InterruptedException {
        populateDatabase(false);

        long firstItemId = 1;
        Tweet tweet = LiveDataTestUtil.getValue(dao.getFeaturedPopTweetById(firstItemId));

        assertThat(tweet).isNotNull();
        assertThat(tweet).isInstanceOf(Tweet.class);
        assertThat(tweet.id).isEqualTo(firstItemId);
    }


    @Test
    public void whenRetrievingAllQueryTweetEntities_hasBeenFullyPopulateWithChildObjects() throws
            InterruptedException {
        populateDatabase(false);

        List<QueryTweetEnt> tweetEntities = LiveDataTestUtil.getValue(
                dao.getAllFeaturedPopTweetsEntQuery());

        QueryTweetEnt tweetEnt = tweetEntities.get(1);

        if (tweetEnt.tweetEntity.coordinatesId > -1) {

            assertThat(tweetEnt.coordinatesList).isNotNull();
            assertThat(tweetEnt.coordinatesList).hasSize(1);
            assertThat(tweetEnt.coordinatesList.get(0).id).
                    isEqualTo(tweetEnt.tweetEntity.coordinatesId);
        }

        if (tweetEnt.tweetEntity.placeId != null) {

            assertThat(tweetEnt.placeList).isNotNull();
            assertThat(tweetEnt.placeList).hasSize(1);
            assertThat(tweetEnt.placeList.get(0).id).
                    isEqualTo(tweetEnt.tweetEntity.placeId);
        }

        if (tweetEnt.tweetEntity.entitiesId > -1) {

            assertThat(tweetEnt.entitiesList).isNotNull();
            assertThat(tweetEnt.entitiesList).hasSize(1);
            assertThat(tweetEnt.entitiesList.get(0).id).
                    isEqualTo(tweetEnt.tweetEntity.entitiesId);

            // see below for hashtag tests
        }

        if (tweetEnt.tweetEntity.extendedEntitiesId > -1) {

            assertThat(tweetEnt.extendedEntitiesList).isNotNull();
            assertThat(tweetEnt.extendedEntitiesList).hasSize(1);
            assertThat(tweetEnt.extendedEntitiesList.get(0).id).
                    isEqualTo(tweetEnt.tweetEntity.extendedEntitiesId);

            // see below for hashtag test
        }

        if (tweetEnt.tweetEntity.quotedStatusId > -1) {

            assertThat(tweetEnt.quotedStatusList).isNotNull();
            assertThat(tweetEnt.quotedStatusList).hasSize(1);
            assertThat(tweetEnt.quotedStatusList.get(0).id).
                    isEqualTo(tweetEnt.tweetEntity.quotedStatusId);
        }

        if (tweetEnt.tweetEntity.retweetedStatusId > 0) {

            assertThat(tweetEnt.retweetedStatusList).isNotNull();
            assertThat(tweetEnt.retweetedStatusList).hasSize(1);
            assertThat(tweetEnt.retweetedStatusList.get(0).id).
                    isEqualTo(tweetEnt.tweetEntity.retweetedStatusId);
        }

        if (tweetEnt.tweetEntity.userId > 0) {

            assertThat(tweetEnt).isNotNull();
            assertThat(tweetEnt.userList).hasSize(1);
            assertThat(tweetEnt.userList.get(0).userEnt.id).
                    isEqualTo(tweetEnt.tweetEntity.userId);


            assertThat(tweetEnt.userList.get(0).tweetEntList.get(0)).isNotNull();
        }
    }

    // test for primites properties
    @Test
    public void whenATweetIsRetrieved_thenAValidTweetEntityIsReturned() throws InterruptedException {
        populateDatabase(false);

        long firstItemId = 1;
        TweetEnt tweetEntity = getTweetEntityById(firstItemId);

        assertThat(tweetEntity.coordinates).isNotNull();
        // see below for further coordinates tests

        assertThat(tweetEntity.createdAt).isEqualTo(CREATED_AT_VALUE);

        // see below for currentUserRetweet tests

        // see below for entities tests

        assertThat(tweetEntity.entitiesId).isNotNull();

        // see below for extendedEntities tests

        assertThat(tweetEntity.extendedEntitiesId).isNotNull();

        assertThat(tweetEntity.favoriteCount).isEqualTo(FAVORITE_COUNT_VALUE);
        assertThat(tweetEntity.favorited).isEqualTo(FAVORITED_VALUE);
        assertThat(tweetEntity.filterLevel).isEqualTo(FILTER_LEVEL_VALUE);
        assertThat(tweetEntity.id).isEqualTo(firstItemId);
        assertThat(tweetEntity.idStr).isEqualTo(ID_STRING_VALUE);
        assertThat(tweetEntity.inReplyToScreenName).isEqualTo(IN_REPLY_TO_SCREEN_NAME_VALUE);
        assertThat(tweetEntity.inReplyToStatusId).isEqualTo(IN_REPLY_TO_STATUS_ID_VALUE);
        assertThat(tweetEntity.inReplyToStatusIdStr).isEqualTo(IN_REPLY_TO_STATUS_ID_STR_VALUE);
        assertThat(tweetEntity.inReplyToUserId).isEqualTo(IN_REPLY_TO_USER_ID_VALUE);
        assertThat(tweetEntity.inReplyToUserIdStr).isEqualTo(IN_REPLY_TO_USER_ID_STR_VALUE);
        assertThat(tweetEntity.lang).isEqualTo(LANG_VALUE);


        assertThat(tweetEntity.place).isNotNull();
        // see below for place tests

        assertThat(tweetEntity.possiblySensitive).isEqualTo(POSSIBLY_SENSITIVE_VALUE);

        // scopes haven't been implemented : no test available

        assertThat(tweetEntity.quotedStatusId).isEqualTo(QUOTED_STATUS_ID_VALUE);
        assertThat(tweetEntity.quotedStatusIdStr).isEqualTo(QUOTED_STATUS_ID_STR_VALUE);

        // see below for quotedStatus tests

        assertThat(tweetEntity.retweetCount).isEqualTo(RETWEET_COUNT);
        assertThat(tweetEntity.retweeted).isEqualTo(RETWEEDED_VALUE);

        // see below for retweetStatus test

        assertThat(tweetEntity.source).isEqualTo(SOURCE_VALUE);
        assertThat(tweetEntity.text).isEqualTo(TEXT_VALUE);
        assertThat(tweetEntity.displayTextRange).containsAllIn(DISPLAY_TEXT_RANGE);
        assertThat(tweetEntity.truncated).isEqualTo(TRUNCATED_VALUE);
        assertThat(tweetEntity.user).isNotNull();
        // see below for further user tests

        assertThat(tweetEntity.withheldCopyright).isEqualTo(WITHHELD_COPYRIGHT_VALUE);

        // see below for withheldInCountries tests

        assertThat(tweetEntity.withheldScope).isEqualTo(WITHHELD_SCOPE_VALUE);

        // see below for card test
    }


    // Coordinates test

    @Test
    public void whenATweetIsRetrieve_thenAValidCoordinatesIsReturned() throws InterruptedException {
        populateDatabase(false);

        long firstItemId = 1;
        TweetEnt tweetEntity = getTweetEntityById(firstItemId);

        long coordinatesId = tweetEntity.coordinatesId;

        CoordinatesEnt coordinatesEntity = dao.getCoordinates(coordinatesId);

        assertThat(coordinatesEntity).isNotNull();

        assertThat(coordinatesEntity.id).isEqualTo(coordinatesId);
        assertThat(coordinatesEntity.coordinates).containsAllIn(Arrays.asList
                (COORDINATES_LAT_VALUE,
                 COORDINATES_LNG_VALUE));
        assertThat(coordinatesEntity.type).isEqualTo(COORDINATES_TYPE_VALUE);
    }

    @Test
    public void whenATweetWithoutCoordinatesIsRetrieve_thenNullCoordinatesIsReturned() throws InterruptedException {
        populateDatabase(true);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.coordinatesId).isEqualTo(-1);
        assertThat(tweetEntity.coordinates).isNull();
    }

    // currentUserRetweet tests

    @Test
    public void whenATweetIsRetrieve_thenAValidCurrentUserRetweetIsReturned() throws InterruptedException {
        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.currentUserRetweet).isNotNull();

        assertThat(tweetEntity.currentUserRetweet.id).isEqualTo(CURRENT_USER_RETWEET_ID_VALUE);
        assertThat(tweetEntity.currentUserRetweet.id_str).isEqualTo(CURRENT_USER_RETWEET_ID_STR_VALUE);
    }


    @Test
    public void whenATweetIsRetrieve_thenANullCurrentUserRetweetIsReturned() throws InterruptedException {
        populateDatabase(true);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.currentUserRetweet).isNull();
    }

    // Place tests

    @Test
    public void whenATweetIsRetrieve_thenAValidPlaceIsReturned() throws InterruptedException {
        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        String placeId = tweetEntity.placeId;
        PlaceEnt placeEntity = dao.getPlace(placeId);

        assertThat(placeEntity).isNotNull();

        assertThat(placeEntity.attributes).containsExactlyEntriesIn(PLACE_ATTRIBUTES_VALUE);

        assertThat(placeEntity.boundingBox).isNotNull();
        assertThat(placeEntity.boundingBox.coordinates).containsAllIn(BOUNDING_BOX_COORDINATES);
        assertThat(placeEntity.boundingBox.type).isEqualTo(BOUNDING_BOX_TYPE_VALUE);

        assertThat(placeEntity.country).isEqualTo(PLACE_COUNTRY_VALUE);
        assertThat(placeEntity.countryCode).isEqualTo(PLACE_COUNTRY_CODE_VALUE);
        assertThat(placeEntity.fullName).isEqualTo(PLACE_FULL_NAME_VALUE);
        assertThat(placeEntity.id).isEqualTo(placeId);
        assertThat(placeEntity.name).isEqualTo(PLACE_NAME_VALUE);
        assertThat(placeEntity.placeType).isEqualTo(PLACE_TYPE_VALUE);
        assertThat(placeEntity.url).isEqualTo(PLACE_URL_VALUE);
    }

    @Test
    public void whenATweetWithoutPlaceIsRetrieve_thenNullPlaceIsReturned() throws InterruptedException {
        populateDatabase(true);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.placeId).isNull();
        assertThat(tweetEntity.place).isNull();
    }

    // Entities tests

    @Test
    public void whenATweetIsRetrieve_thenAValidEntitiesIsReturned() throws InterruptedException {

        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);

        assertThat(tweetEntity).isNotNull();
        assertThat(tweetEntity.entities).isNotNull();

        testEntitiesObject(tweetEntity.entities);
    }

    @Test
    public void
    whenTweetEntitiesHasId_thenTheRightEntitiesObjecExistInEntityTable() throws InterruptedException {
        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);

        long entitiesid = tweetEntity.entitiesId;

        assertThat(entitiesid).isNotEqualTo(0);

        testEntityInsertion(entitiesid);
        testEntitiesHashtagJoinInserction(entitiesid);
    }

    @Test
    public void whenATweetIsRetrieve_thenAValidExtendedEntitiesIsReturned() throws InterruptedException {

        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);

        assertThat(tweetEntity).isNotNull();
        assertThat(tweetEntity.extendedEntities).isNotNull();

        testEntitiesObject(tweetEntity.extendedEntities);
    }

    @Test
    public void whenTweetExtendedEntitiesHasId_thenTheRightEntitiesObjecExistInEntityTable() throws InterruptedException {
        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);

        long entitiesid = tweetEntity.extendedEntitiesId;
        Log.d(TAG, ":  ======= insertTweetData: tweet id: " + tweetEntity.id +
                "extended " + "entities id " + entitiesid);

        assertThat(entitiesid).isNotEqualTo(0);

        testEntityInsertion(entitiesid);
        testEntitiesHashtagJoinInserction(entitiesid);
    }

    private void testEntitiesHashtagJoinInserction(long entitiesId) {

        List<QueryTweetEntitiesHashtagEntityJoin> hashtagJointList =
                dao.getHashtagEntityListByEntitiesId(entitiesId);

        Log.d(TAG, entitiesId + ":  ======= insertTweetData: id: " + hashtagJointList.get(0).tweetEntitiesId + "" +
                " : hashtag: "
                + hashtagJointList.get(0).hashtagText);
        assertThat(hashtagJointList).isNotNull();
        assertThat(hashtagJointList).hasSize(HASHTAG_COUNT_VALUE);
        assertThat(hashtagJointList.get(0).tweetEntitiesId).isEqualTo(entitiesId);
        assertThat(hashtagJointList.get(0).hashtagText).contains(HASHTAG_ENTITY_TEXT_VALUE);
    }

    private void testEntityInsertion(long entitiesid) {
        if (entitiesid != 0) {
            // New row has been created
            TweetEntitiesEnt rawTweetEntities = dao.getRawTweetEntities(entitiesid);
            assertThat(rawTweetEntities).isNotNull();
            assertThat(rawTweetEntities.symbols).isNotNull();
            assertThat(rawTweetEntities.symbols).hasSize(1);
            assertThat(rawTweetEntities.symbols.get(0).text).isEqualTo(SYMBOL_ENTITY_TEXT_VALUE);
            assertThat(rawTweetEntities.symbols.get(0).indices)
                    .containsAllIn(Arrays.asList(ENTITY_START_VALUE,
                                                 ENTITY_END_VALUE));

            // see below for hashtag join table insertion tests
        }
    }

    private void testEntitiesObject(TweetEntitiesEnt tweetEntity) {

        //has expected url
        assertThat(tweetEntity.urls).isNotNull();
        assertThat(tweetEntity.urls).hasSize(1);
        assertThat(tweetEntity.urls.get(0).url).isEqualTo(URL_ENTITY_URL_VALUE);
        assertThat(tweetEntity.urls.get(0).expandedUrl).isEqualTo(URL_ENTITY_EXPANDED_URL_VALUE);
        assertThat(tweetEntity.urls.get(0).displayUrl).isEqualTo(URL_ENTITY_DISPLAY_URL_VALUE);
        assertThat(tweetEntity.urls.get(0).indices)
                .containsAllIn(Arrays.asList(ENTITY_START_VALUE,
                                             ENTITY_END_VALUE));

        // has expected mentions
        assertThat(tweetEntity.userMentions).isNotNull();
        assertThat(tweetEntity.userMentions).hasSize(1);
        assertThat(tweetEntity.userMentions.get(0).id).isEqualTo(MENTION_ENTITY_ID_VALUE);
        assertThat(tweetEntity.userMentions.get(0).idStr)
                .isEqualTo(MENTION_ENTITY_ID_STR_VALUE);
        assertThat(tweetEntity.userMentions.get(0).name)
                .isEqualTo(MENTION_ENTITY_NAME_VALUE);
        assertThat(tweetEntity.userMentions.get(0).screenName)
                .isEqualTo(MENTION_ENTITY_SCREEN_NAME_VALUE);
        assertThat(tweetEntity.userMentions.get(0).indices)
                .containsAllIn(Arrays.asList(ENTITY_START_VALUE,
                                             ENTITY_END_VALUE));

        // has expected media
        assertThat(tweetEntity.media).isNotNull();
        assertThat(tweetEntity.media).hasSize(1);
        assertThat(tweetEntity.media.get(0).url).isEqualTo(MEDIA_ENTITY_URL_VALUE);
        assertThat(tweetEntity.media.get(0).expandedUrl).isEqualTo(MEDIA_ENTITY_EXPANDED_URL_VALUE);
        assertThat(tweetEntity.media.get(0).displayUrl).isEqualTo(MEDIA_ENTITY_DISPLAY_URL_VALUE);
        assertThat(tweetEntity.media.get(0).indices)
                .containsAllIn(Arrays.asList(ENTITY_START_VALUE,
                                             ENTITY_END_VALUE));
        assertThat(tweetEntity.media.get(0).id).isEqualTo(MEDIA_ENTITY_ID_VALUE);
        assertThat(tweetEntity.media.get(0).idStr).isEqualTo(MEDIA_ENTITY_ID_STR_VALUE);
        assertThat(tweetEntity.media.get(0).mediaUrl).isEqualTo(MEDIA_ENTITY_MEDIA_URL_VALUE);
        assertThat(tweetEntity.media.get(0).mediaUrlHttps).isEqualTo
                (MEDIA_ENTITY_MEDIA_URL_HTTPS_VALUE);

        assertThat(tweetEntity.media.get(0).sizes).isNotNull();
        assertThat(tweetEntity.media.get(0).sizes.thumb).isNotNull();
        assertThat(tweetEntity.media.get(0).sizes.small).isNotNull();
        assertThat(tweetEntity.media.get(0).sizes.medium).isNotNull();
        assertThat(tweetEntity.media.get(0).sizes.large).isNotNull();
        assertThat(tweetEntity.media.get(0).sizes.large.w)
                .isEqualTo(MEDIA_ENTITY_SIZE_WIDTH_VALUE);
        assertThat(tweetEntity.media.get(0).sizes.large.h)
                .isEqualTo(MEDIA_ENTITY_SIZE_HEIGHT_VALUE);
        assertThat(tweetEntity.media.get(0).sizes.large.resize)
                .isEqualTo(MEDIA_ENTITY_SIZE_RESIZE_VALUE);

        assertThat(tweetEntity.media.get(0).sourceStatusId).isEqualTo
                (MEDIA_ENTITY_SOURCE_STATUS_ID_VALUE);
        assertThat(tweetEntity.media.get(0).sourceStatusIdStr).isEqualTo
                (MEDIA_ENTITY_SOURCE_STATUS_ID_STR_VALUE);
        assertThat(tweetEntity.media.get(0).type).isEqualTo(MEDIA_ENTITY_TYPE_VALUE);

        assertThat(tweetEntity.media.get(0).videoInfo).isNotNull();
        assertThat(tweetEntity.media.get(0).videoInfo.durationMillis)
                .isEqualTo(MEDIA_ENTITY_VIDEO_INFO_DURATION_MILLS_VALUE);
        assertThat(tweetEntity.media.get(0).videoInfo.aspectRatio)
                .containsAllIn(Collections.singletonList
                        (MEDIA_ENTITY_VIDEO_INFO_ASPECT_RATIO_VALUE));

        assertThat(tweetEntity.media.get(0).altText).isEqualTo(MEDIA_ENTITY_ALT_TEXT_VALUE);

        // has expected symbols
        assertThat(tweetEntity.symbols).isNotNull();
        assertThat(tweetEntity.symbols).hasSize(1);
        assertThat(tweetEntity.symbols.get(0).text).isEqualTo(SYMBOL_ENTITY_TEXT_VALUE);
        assertThat(tweetEntity.symbols.get(0).indices)
                .containsAllIn(Arrays.asList(ENTITY_START_VALUE,
                                             ENTITY_END_VALUE));
    }

    // Status tests

    @Test
    public void whenATweetIsRetrieve_thenAValidQuotedStatusIsReturned() throws InterruptedException {
        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.quotedStatus).isNotNull();
        assertThat(tweetEntity.quotedStatus.id).isEqualTo(QUOTED_STATUS_ID_VALUE);
    }

    @Test
    public void whenATweetWithoutQuotedRequestIsRetrieve_thenANullQuotedStatusIsReturned() throws InterruptedException {
        populateDatabase(true);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.quotedStatus).isNull();
    }

    @Test
    public void whenATweetIsRetrieve_thenAValidRetweetStatusIsReturned() throws InterruptedException {
        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.retweetedStatus).isNotNull();
        assertThat(tweetEntity.retweetedStatus.id).isEqualTo(RETWEET_STATUS_ID);
    }

    @Test
    public void whenATweetWithoutRetweetStatusIsRetrieve_thenANullRetweetStatusIsReturned() throws InterruptedException {
        populateDatabase(true);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.retweetedStatus).isNull();
    }

    // User tests

    @Test
    public void whenATweetIsRetrieve_thenAValidUserIsReturned() throws InterruptedException {
        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.user).isNotNull();

        assertThat(tweetEntity.user.contributorsEnabled).isEqualTo(USER_CONTRIBUTORS_ENABLED_VALUE);
        assertThat(tweetEntity.user.createdAt).isEqualTo(USER_CREATED_AT_VALUE);
        assertThat(tweetEntity.user.defaultProfile).isEqualTo(USER_DEFAUL_PROFILE_VALUE);
        assertThat(tweetEntity.user.defaultProfileImage).isEqualTo(USER_DEFAULT_PROFILE_IMAGE_VALUE);
        assertThat(tweetEntity.user.description).isEqualTo(USER_DESCRIPTION_VALUE);
        assertThat(tweetEntity.user.email).isEqualTo(USER_EMAIL_VALUE);
        assertThat(tweetEntity.user.favouritesCount).isEqualTo(USER_FAVORITES_COUNT_VALUE);
        assertThat(tweetEntity.user.followRequestSent).isEqualTo(USER_FOLLOW_REQUEST_SENT_VALUE);
        assertThat(tweetEntity.user.followersCount).isEqualTo(USER_FOLLOWERS_COUNT_VALUE);
        assertThat(tweetEntity.user.friendsCount).isEqualTo(USER_FRIENDS_COUNT_VALUE);
        assertThat(tweetEntity.user.geoEnabled).isEqualTo(USER_GEO_ENABLED_VALUE);
        assertThat(tweetEntity.user.id).isEqualTo(USER_ID_VALUE);
        assertThat(tweetEntity.user.idStr).isEqualTo(USER_ID_STR_VALUE);
        assertThat(tweetEntity.user.isTranslator).isEqualTo(USER_IS_TRANSLATOR_VALUE);
        assertThat(tweetEntity.user.lang).isEqualTo(USER_LANG_VALUE);
        assertThat(tweetEntity.user.listedCount).isEqualTo(USER_LISTED_COUNT_VALUE);
        assertThat(tweetEntity.user.location).isEqualTo(USER_LOCATION_VALUE);
        assertThat(tweetEntity.user.name).isEqualTo(USER_NAME_VALUE);
        assertThat(tweetEntity.user.profileBackgroundColor).isEqualTo(USER_PROFILE_BG_COLOR_VALUE);
        assertThat(tweetEntity.user.profileBackgroundImageUrl).isEqualTo(USER_PROFILE_BG_IMAGE_URL_VALUE);
        assertThat(tweetEntity.user.profileBackgroundImageUrlHttps).isEqualTo(USER_PROFILE_BG_IMAGE_URL_HTTPS_VALUE);
        assertThat(tweetEntity.user.profileBackgroundTile).isEqualTo(USER_PROFILE_BG_TILE_VALUE);
        assertThat(tweetEntity.user.profileBannerUrl).isEqualTo(USER_PROFILE_BANNER_URL_VALUE);
        assertThat(tweetEntity.user.profileImageUrl).isEqualTo(USER_PROFILE_IMAGE_URL_VALUE);
        assertThat(tweetEntity.user.profileImageUrlHttps).isEqualTo(USER_PROFILE_IMAGE_URL_HTTPS_VALUE);
        assertThat(tweetEntity.user.profileLinkColor).isEqualTo(USER_PROFILE_LINK_COLOR_VALUE);
        assertThat(tweetEntity.user.profileSidebarBorderColor).isEqualTo(USER_PROFILE_SIDEBAR_BORDER_COLOR_VALUE);
        assertThat(tweetEntity.user.profileSidebarFillColor).isEqualTo(USER_PROFILE_SIDEBAR_FILL_COLOR_VALUE);
        assertThat(tweetEntity.user.profileTextColor).isEqualTo(USER_PROFILE_TEXT_COLOR_VALUE);
        assertThat(tweetEntity.user.profileUseBackgroundImage).isEqualTo(USER_PROFILE_USE_BACKGROUND_IMAGE_VALUE);
        assertThat(tweetEntity.user.protectedUser).isEqualTo(USER_PROTECTED_USER_VALUE);
        assertThat(tweetEntity.user.screenName).isEqualTo(USER_SCREEN_NAME_VALUE);
        assertThat(tweetEntity.user.showAllInlineMedia).isEqualTo(USER_SHOW_ALL_INLINE_MEDIA_VALUE);
        assertThat(tweetEntity.user.statusesCount).isEqualTo(USER_STATUSES_COUNT_VALUE);
        assertThat(tweetEntity.user.timeZone).isEqualTo(USER_TIME_ZONE_VALUE);
        assertThat(tweetEntity.user.url).isEqualTo(USER_URL_VALUE);
        assertThat(tweetEntity.user.utcOffset).isEqualTo(USER_UTC_OFFSET_VALUE);
        assertThat(tweetEntity.user.verified).isEqualTo(USER_VERIFIED_VALUE);
        assertThat(tweetEntity.user.withheldInCountries).containsAllIn(USER_WITHHELD_IN_COUNTRIES_VALUE);
        assertThat(tweetEntity.user.withheldScope).isEqualTo(USER_WITHELD_SCOPE_VALUE);

        // see below for user entities tests

        assertThat(tweetEntity.user.status).isNotNull();
        assertThat(tweetEntity.user.statusId).
                isEqualTo(tweetEntity.user.status.id);

    }

    @Test
    public void whenATweetWithoutUserIsRetrieve_thenNullUserIsReturned() throws InterruptedException {
        populateDatabase(true);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.userId).isEqualTo(0);
        assertThat(tweetEntity.user).isNull();
    }

    @Test
    public void whenATweetIsRetrieve_thenAValidUserEntitiesIsReturned() throws InterruptedException {
        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);

        assertThat(tweetEntity.user.entities).isNotNull();
        assertThat(tweetEntity.user.entities.url).isNotNull();
        assertThat(tweetEntity.user.entities.url.urls).isNotNull();
        assertThat(tweetEntity.user.entities.url.urls).hasSize(1);
        assertThat(tweetEntity.user.entities.description).isNotNull();
        assertThat(tweetEntity.user.entities.description.urls).isNotNull();
        assertThat(tweetEntity.user.entities.description.urls).hasSize(1);

        testEntityUrl(tweetEntity.user.entities.description.urls.get(0));
        testEntityUrl(tweetEntity.user.entities.url.urls.get(0));
    }

    private void testEntityUrl(UrlEntity urlEntity) {
        assertThat(urlEntity.url).
                isEqualTo(URL_ENTITY_URL_VALUE);
        assertThat(urlEntity.expandedUrl).
                isEqualTo(URL_ENTITY_EXPANDED_URL_VALUE);
        assertThat(urlEntity.displayUrl).
                isEqualTo(URL_ENTITY_DISPLAY_URL_VALUE);
        assertThat(urlEntity.indices).
                containsExactly(ENTITY_START_VALUE,
                                ENTITY_END_VALUE);
    }

    // withheldInCountries tests

    @Test
    public void withheldInCountriesTest() throws InterruptedException {
        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.withheldInCountries).isNotNull();

        assertThat(tweetEntity.withheldInCountries).hasSize(2);
        assertThat(tweetEntity.withheldInCountries).containsAnyIn(WITHHELD_IN_COUNTRIES_VALUE);
    }


    // Card tests

    @Test
    public void whenATweetIsRetrieve_thenAValidTCardIsReturned() throws InterruptedException {
        populateDatabase(false);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.card).isNotNull();

        assertThat(tweetEntity.card.name).isEqualTo(CARD_NAME_VALUE);
        assertThat(tweetEntity.card.bindingValues.containsKey(CARD_BOUND_VALUES_KEY_VALUE));
        assertThat((Object) tweetEntity.card.bindingValues.get(CARD_BOUND_VALUES_KEY_VALUE))
                .isNotEqualTo(CARD_BOUND_VALUES_OBJECT_VALUE);

    }

    @Test
    public void whenATweetWithoutCardIsRetrieve_thenNullCardIsReturned() throws InterruptedException {
        populateDatabase(true);

        long secondItemId = 2;
        TweetEnt tweetEntity = getTweetEntityById(secondItemId);
        assertThat(tweetEntity.card).isNull();

    }

    @After
    public void tearDown() {
        database.close();
    }

    private TweetEnt getTweetEntityById(long id) throws InterruptedException {
        return LiveDataTestUtil
                .getValue(dao.getFeaturedPopTweetEntQuery(id))
                .getPopulatedTweetEnt();
    }
}