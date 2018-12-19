package uk.me.desiderio.shiftt.data;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.VideoInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.lang.reflect.Field;

import uk.me.desiderio.shiftt.data.database.model.TweetEnt;

import static com.google.common.truth.Truth.assertThat;
import static uk.me.desiderio.shiftt.data.TweetMockDataProvider.CARD_BOUND_VALUES_KEY_VALUE;
import static uk.me.desiderio.shiftt.data.TweetMockDataProvider.CARD_BOUND_VALUES_OBJECT_VALUE;

/**
 * tests serialization betweet {@link Tweet} network data object and {@link TweetEnt}
 * room database data objects.
 */
@RunWith(Parameterized.class)
public class ParseTweetEntitiesTest {

    private static final Long TWEET_ID_VALUE = 33L;
    // extected tweet
    private final Tweet initialTweet;
    // actual tweet
    private Tweet returnedTweet;

    public ParseTweetEntitiesTest(Tweet initialTweet) {
        this.initialTweet = initialTweet;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Parameters
    public static Tweet[] getParameters() {
        Tweet fulltweet = TweetMockDataProvider.getTweet(TWEET_ID_VALUE);
        Tweet tweetWithNulls = TweetMockDataProvider.getTweetWithNullValues(TWEET_ID_VALUE);
        Tweet[] tweets = {fulltweet, tweetWithNulls};
        return tweets;
    }

    @Before
    public void setUp() {
        //initialTweet = TweetMockDataProvider.getTweet(TWEET_ID_VALUE);
        TweetEnt tweetEntity = new TweetEnt(initialTweet);
        returnedTweet = tweetEntity.getSeed();
    }

    @Test
    public void givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsWithSameFieldsValues() {
        // see below for coordinates tests

        assertThat(returnedTweet.createdAt).isEqualTo(initialTweet.createdAt);

        // see below for currentUserRetweet tests

        // see below for entities tests

        // see below for extendedEntities tests

        assertThat(returnedTweet.favoriteCount).isEqualTo(initialTweet.favoriteCount);
        assertThat(returnedTweet.favorited).isEqualTo(initialTweet.favorited);
        assertThat(returnedTweet.filterLevel).isEqualTo(initialTweet.filterLevel);
        assertThat(returnedTweet.idStr).isEqualTo(initialTweet.idStr);
        assertThat(returnedTweet.inReplyToScreenName).isEqualTo(initialTweet.inReplyToScreenName);
        assertThat(returnedTweet.inReplyToStatusId).isEqualTo(initialTweet.inReplyToStatusId);
        assertThat(returnedTweet.inReplyToStatusIdStr).isEqualTo(initialTweet.inReplyToStatusIdStr);
        assertThat(returnedTweet.inReplyToUserId).isEqualTo(initialTweet.inReplyToUserId);
        assertThat(returnedTweet.inReplyToUserIdStr).isEqualTo(initialTweet.inReplyToUserIdStr);
        assertThat(returnedTweet.lang).isEqualTo(initialTweet.lang);

        // see below for place tests

        assertThat(returnedTweet.possiblySensitive).isEqualTo(initialTweet.possiblySensitive);

        // scopes haven't been implemented : no test available

        assertThat(returnedTweet.quotedStatusId).isEqualTo(initialTweet.quotedStatusId);
        assertThat(returnedTweet.quotedStatusIdStr).isEqualTo(initialTweet.quotedStatusIdStr);

        // see below for quotedStatus tests

        assertThat(returnedTweet.retweetCount).isEqualTo(initialTweet.retweetCount);
        assertThat(returnedTweet.retweeted).isEqualTo(initialTweet.retweeted);

        // see below for retweetStatus test

        assertThat(returnedTweet.source).isEqualTo(initialTweet.source);
        assertThat(returnedTweet.text).isEqualTo(initialTweet.text);

        if (returnedTweet.displayTextRange != null) {
            assertThat(returnedTweet.displayTextRange).isNotNull();
            assertThat(returnedTweet.displayTextRange).containsAllIn(initialTweet.displayTextRange);
        }
        assertThat(returnedTweet.truncated).isEqualTo(initialTweet.truncated);

        // see below for further user tests

        assertThat(returnedTweet.withheldCopyright).isEqualTo(initialTweet.withheldCopyright);

        // see below for withheldInCountries tests

        assertThat(returnedTweet.withheldScope).isEqualTo(initialTweet.withheldScope);

        // see below for card test
    }

    // Coordinates tests

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameCoordinatesObjectOtherwiseNull
            () {
        if (initialTweet.coordinates != null) {
            assertThat(returnedTweet.coordinates).isNotNull();
            if (initialTweet.coordinates.coordinates != null) {
                assertThat(returnedTweet.coordinates.coordinates).isNotNull();
                assertThat(returnedTweet.coordinates.coordinates).containsExactlyElementsIn(
                        initialTweet.coordinates.coordinates);
                assertThat(returnedTweet.coordinates.getLatitude()).
                        isEqualTo(initialTweet.coordinates.getLatitude());
                assertThat(returnedTweet.coordinates.getLongitude()).
                        isEqualTo(initialTweet.coordinates.getLongitude());
            }
            assertThat(returnedTweet.coordinates.type).
                    isEqualTo(initialTweet.coordinates.type);
        } else {
            assertThat(returnedTweet.coordinates).isNull();
        }
    }

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameCurrentUserRetweetOtherwiseNull
            () {
        if (initialTweet.currentUserRetweet != null) {
            assertThat(returnedTweet.currentUserRetweet).isNotNull();

            Long returnedId = (Long) getFieldValue(returnedTweet.currentUserRetweet, "id");
            Long expectedId = (Long) getFieldValue(initialTweet.currentUserRetweet, "id");
            String returnedIdStr = (String) getFieldValue(returnedTweet.currentUserRetweet, "id_str");
            String expectedIdStr = (String) getFieldValue(initialTweet.currentUserRetweet, "id_str");

            assertThat(returnedId).isEqualTo(expectedId);
            assertThat(returnedIdStr).isEqualTo(expectedIdStr);

        } else {
            assertThat(returnedTweet.currentUserRetweet).isNull();
        }
    }


    private Object getFieldValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(object);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Entities test

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameEntitiesObjectOtherwiseNull
            () {
        testTweetEntitiesObject(returnedTweet.entities, initialTweet.entities);
    }

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameExtendedEntitiesObjectOtherwiseNull
            () {
        testTweetEntitiesObject(returnedTweet.extendedEntities, initialTweet.extendedEntities);
    }

    private void testTweetEntitiesObject(TweetEntities actualTweetEntities,
                                         TweetEntities expectedEntities) {
        if (expectedEntities != null) {
            assertThat(actualTweetEntities).isNotNull();
            assertThat(actualTweetEntities.hashtags.size())
                    .isEqualTo(expectedEntities.hashtags.size());
            for (int i = 0; i < actualTweetEntities.hashtags.size(); i++) {
                assertThat(actualTweetEntities.hashtags.get(i).indices)
                        .containsExactlyElementsIn(expectedEntities.hashtags.get(i).indices);
                assertThat(actualTweetEntities.hashtags.get(i).text)
                        .isEqualTo(expectedEntities.hashtags.get(i).text);
            }

            for (int i = 0; i < actualTweetEntities.media.size(); i++) {
                assertThat(actualTweetEntities.media.get(i).indices)
                        .containsExactlyElementsIn(expectedEntities.media.get(i).indices);

                assertThat(actualTweetEntities.media.get(i).id)
                        .isEqualTo(expectedEntities.media.get(i).id);

                assertThat(actualTweetEntities.media.get(i).idStr)
                        .isEqualTo(expectedEntities.media.get(i).idStr);

                assertThat(actualTweetEntities.media.get(i).mediaUrl)
                        .isEqualTo(expectedEntities.media.get(i).mediaUrl);

                assertThat(actualTweetEntities.media.get(i).mediaUrlHttps)
                        .isEqualTo(expectedEntities.media.get(i).mediaUrlHttps);

                testTweetEntitiesSizesObject(
                        actualTweetEntities.media.get(i).sizes,
                        expectedEntities.media.get(i).sizes);

                assertThat(actualTweetEntities.media.get(i).sourceStatusId)
                        .isEqualTo(expectedEntities.media.get(i).sourceStatusId);

                assertThat(actualTweetEntities.media.get(i).sourceStatusIdStr)
                        .isEqualTo(expectedEntities.media.get(i).sourceStatusIdStr);

                assertThat(actualTweetEntities.media.get(i).type)
                        .isEqualTo(expectedEntities.media.get(i).type);

                testTweetEntitiesMediaVideoInfoObject(
                        actualTweetEntities.media.get(i).videoInfo,
                        expectedEntities.media.get(i).videoInfo);

                assertThat(actualTweetEntities.media.get(i).altText)
                        .isEqualTo(expectedEntities.media.get(i).altText);
            }

            for (int i = 0; i < actualTweetEntities.symbols.size(); i++) {
                assertThat(actualTweetEntities.symbols.get(i).indices)
                        .containsExactlyElementsIn(expectedEntities.symbols.get(i).indices);

                assertThat(actualTweetEntities.symbols.get(i).text)
                        .isEqualTo(expectedEntities.symbols.get(i).text);
            }

            for (int i = 0; i < actualTweetEntities.urls.size(); i++) {
                assertThat(actualTweetEntities.urls.get(i).indices)
                        .containsExactlyElementsIn(expectedEntities.urls.get(i).indices);

                assertThat(actualTweetEntities.urls.get(i).url)
                        .isEqualTo(expectedEntities.urls.get(i).url);

                assertThat(actualTweetEntities.urls.get(i).expandedUrl)
                        .isEqualTo(expectedEntities.urls.get(i).expandedUrl);

                assertThat(actualTweetEntities.urls.get(i).displayUrl)
                        .isEqualTo(expectedEntities.urls.get(i).displayUrl);
            }

            for (int i = 0; i < actualTweetEntities.userMentions.size(); i++) {
                assertThat(actualTweetEntities.userMentions.get(i).indices)
                        .containsExactlyElementsIn(expectedEntities.userMentions.get(i).indices);

                assertThat(actualTweetEntities.userMentions.get(i).id)
                        .isEqualTo(expectedEntities.userMentions.get(i).id);

                assertThat(actualTweetEntities.userMentions.get(i).idStr)
                        .isEqualTo(expectedEntities.userMentions.get(i).idStr);

                assertThat(actualTweetEntities.userMentions.get(i).name)
                        .isEqualTo(expectedEntities.userMentions.get(i).name);

                assertThat(actualTweetEntities.userMentions.get(i).screenName)
                        .isEqualTo(expectedEntities.userMentions.get(i).screenName);
            }
        } else {
            assertThat(actualTweetEntities).isNull();
        }
    }

    private void testTweetEntitiesMediaVideoInfoObject(VideoInfo actualVideoInfo,
                                                       VideoInfo expectedVideoInfo) {
        if (expectedVideoInfo != null) {
            assertThat(actualVideoInfo).isNotNull();
            assertThat(actualVideoInfo.aspectRatio).
                    containsExactlyElementsIn(expectedVideoInfo.aspectRatio);
            assertThat(actualVideoInfo.durationMillis).
                    isEqualTo(expectedVideoInfo.durationMillis);
            assertThat(actualVideoInfo.variants).
                    containsExactlyElementsIn(expectedVideoInfo.variants);
        }
    }

    private void testTweetEntitiesSizesSizeObject(MediaEntity.Size actualSize,
                                                  MediaEntity.Size expectedSize) {
        if (expectedSize != null) {
            assertThat(actualSize).isNotNull();
            assertThat(actualSize.h).isEqualTo(expectedSize.h);
            assertThat(actualSize.w).isEqualTo(expectedSize.w);
            assertThat(actualSize.resize).isEqualTo(expectedSize.resize);
        }
    }

    private void testTweetEntitiesSizesObject(MediaEntity.Sizes actualSizes,
                                              MediaEntity.Sizes expectedSizes) {
        if (expectedSizes != null) {
            assertThat(actualSizes).isNotNull();
            testTweetEntitiesSizesSizeObject(actualSizes.large, expectedSizes.large);
            testTweetEntitiesSizesSizeObject(actualSizes.medium, expectedSizes.medium);
            testTweetEntitiesSizesSizeObject(actualSizes.small, expectedSizes.small);
            testTweetEntitiesSizesSizeObject(actualSizes.thumb, expectedSizes.thumb);
        }
    }

    // place tests

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSamePlaceObjectOtherwiseNull
            () {
        if (initialTweet.place != null) {
            assertThat(returnedTweet.place).isNotNull();
            assertThat(returnedTweet.place.attributes)
                    .containsExactlyEntriesIn(initialTweet.place.attributes);

            // see below for bounding box test

            assertThat(returnedTweet.place.country)
                    .isEqualTo(initialTweet.place.country);
            assertThat(returnedTweet.place.countryCode)
                    .isEqualTo(initialTweet.place.countryCode);
            assertThat(returnedTweet.place.fullName)
                    .isEqualTo(initialTweet.place.fullName);
            assertThat(returnedTweet.place.id)
                    .isEqualTo(initialTweet.place.id);
            assertThat(returnedTweet.place.name)
                    .isEqualTo(initialTweet.place.name);
            assertThat(returnedTweet.place.placeType)
                    .isEqualTo(initialTweet.place.placeType);
            assertThat(returnedTweet.place.url)
                    .isEqualTo(initialTweet.place.url);


        } else {
            assertThat(returnedTweet.place).isNull();
        }
    }

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSamePlaceBoundingBoxObjectOtherwiseNull
            () {
        if (initialTweet.place != null && initialTweet.place.boundingBox != null) {
            assertThat(returnedTweet.place.boundingBox).isNotNull();
            assertThat(returnedTweet.place.boundingBox.type).
                    isEqualTo(initialTweet.place.boundingBox.type);

            assertThat(returnedTweet.place.boundingBox.coordinates).
                    containsExactlyElementsIn(initialTweet.place.boundingBox.coordinates);
        }
    }

    // quotedStatus test

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameQuotedStatusObjectOtherwiseNull
            () {
        if (initialTweet.quotedStatus != null) {
            assertThat(returnedTweet.quotedStatus).isNotNull();
            assertThat(returnedTweet.quotedStatus.id).
                    isEqualTo(initialTweet.quotedStatus.id);

        } else {
            assertThat(returnedTweet.quotedStatus).isNull();
        }
    }

    // retweetStatus test

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameRetweetStatusObjectOtherwiseNull
            () {
        if (initialTweet.retweetedStatus != null) {
            assertThat(returnedTweet.retweetedStatus).isNotNull();
            assertThat(returnedTweet.retweetedStatus.id).
                    isEqualTo(initialTweet.retweetedStatus.id);

        } else {
            assertThat(returnedTweet.retweetedStatus).isNull();
        }
    }

    // user test


    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameUserObjectOtherwiseNull
            () {
        if (initialTweet.user != null) {
            assertThat(returnedTweet.user).isNotNull();
            assertThat(returnedTweet.user.contributorsEnabled).
                    isEqualTo(initialTweet.user.contributorsEnabled);
            assertThat(returnedTweet.user.createdAt).
                    isEqualTo(initialTweet.user.createdAt);
            assertThat(returnedTweet.user.defaultProfile).
                    isEqualTo(initialTweet.user.defaultProfile);
            assertThat(returnedTweet.user.defaultProfileImage).
                    isEqualTo(initialTweet.user.defaultProfileImage);
            assertThat(returnedTweet.user.description).
                    isEqualTo(initialTweet.user.description);
            assertThat(returnedTweet.user.email).
                    isEqualTo(initialTweet.user.email);
            // see below for entities test
            assertThat(returnedTweet.user.favouritesCount).
                    isEqualTo(initialTweet.user.favouritesCount);
            assertThat(returnedTweet.user.followRequestSent).
                    isEqualTo(initialTweet.user.followRequestSent);
            assertThat(returnedTweet.user.friendsCount).
                    isEqualTo(initialTweet.user.friendsCount);
            assertThat(returnedTweet.user.geoEnabled).
                    isEqualTo(initialTweet.user.geoEnabled);
            assertThat(returnedTweet.user.id).
                    isEqualTo(initialTweet.user.id);
            assertThat(returnedTweet.user.idStr).
                    isEqualTo(initialTweet.user.idStr);
            assertThat(returnedTweet.user.isTranslator).
                    isEqualTo(initialTweet.user.isTranslator);
            assertThat(returnedTweet.user.lang).
                    isEqualTo(initialTweet.user.lang);
            assertThat(returnedTweet.user.listedCount).
                    isEqualTo(initialTweet.user.listedCount);
            assertThat(returnedTweet.user.location).
                    isEqualTo(initialTweet.user.location);
            assertThat(returnedTweet.user.name).
                    isEqualTo(initialTweet.user.name);
            assertThat(returnedTweet.user.profileBackgroundColor).
                    isEqualTo(initialTweet.user.profileBackgroundColor);
            assertThat(returnedTweet.user.profileBackgroundImageUrl).
                    isEqualTo(initialTweet.user.profileBackgroundImageUrl);
            assertThat(returnedTweet.user.profileBackgroundImageUrlHttps).
                    isEqualTo(initialTweet.user.profileBackgroundImageUrlHttps);
            assertThat(returnedTweet.user.profileBackgroundTile).
                    isEqualTo(initialTweet.user.profileBackgroundTile);
            assertThat(returnedTweet.user.profileBannerUrl).
                    isEqualTo(initialTweet.user.profileBannerUrl);
            assertThat(returnedTweet.user.profileImageUrl).
                    isEqualTo(initialTweet.user.profileImageUrl);
            assertThat(returnedTweet.user.profileImageUrlHttps).
                    isEqualTo(initialTweet.user.profileImageUrlHttps);
            assertThat(returnedTweet.user.profileLinkColor).
                    isEqualTo(initialTweet.user.profileLinkColor);
            assertThat(returnedTweet.user.profileSidebarBorderColor).
                    isEqualTo(initialTweet.user.profileSidebarBorderColor);
            assertThat(returnedTweet.user.profileSidebarFillColor).
                    isEqualTo(initialTweet.user.profileSidebarFillColor);
            assertThat(returnedTweet.user.profileTextColor).
                    isEqualTo(initialTweet.user.profileTextColor);
            assertThat(returnedTweet.user.profileUseBackgroundImage).
                    isEqualTo(initialTweet.user.profileUseBackgroundImage);
            assertThat(returnedTweet.user.protectedUser).
                    isEqualTo(initialTweet.user.protectedUser);
            assertThat(returnedTweet.user.screenName).
                    isEqualTo(initialTweet.user.screenName);
            assertThat(returnedTweet.user.showAllInlineMedia).
                    isEqualTo(initialTweet.user.showAllInlineMedia);
            // see below for status test
            assertThat(returnedTweet.user.statusesCount).
                    isEqualTo(initialTweet.user.statusesCount);
            assertThat(returnedTweet.user.timeZone).
                    isEqualTo(initialTweet.user.timeZone);
            assertThat(returnedTweet.user.url).
                    isEqualTo(initialTweet.user.url);
            assertThat(returnedTweet.user.utcOffset).
                    isEqualTo(initialTweet.user.utcOffset);
            assertThat(returnedTweet.user.verified).
                    isEqualTo(initialTweet.user.verified);
            assertThat(returnedTweet.user.withheldInCountries).
                    containsExactlyElementsIn(initialTweet.user.withheldInCountries);
            assertThat(returnedTweet.user.withheldScope).
                    isEqualTo(initialTweet.user.withheldScope);
        } else {
            assertThat(returnedTweet.user).isNull();
        }
    }

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameUserEntitiesObjectOtherwiseNull
            () {
        if (initialTweet.user != null && initialTweet.user.entities != null) {
            assertThat(returnedTweet.user.entities).isNotNull();
            if (initialTweet.user.entities.description != null) {
                assertThat(returnedTweet.user.entities.description).isNotNull();
                assertThat(returnedTweet.user.entities.description.urls).
                        containsExactlyElementsIn(initialTweet.user.entities.description.urls);

            }
            if (initialTweet.user.entities.url != null) {
                assertThat(returnedTweet.user.entities.url).isNotNull();
                assertThat(returnedTweet.user.entities.url.urls).
                        containsExactlyElementsIn(initialTweet.user.entities.url.urls);
            }
        }
    }

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameUserStatusObjectOtherwiseNull
            () {
        if (initialTweet.user != null && initialTweet.user.status != null) {
            assertThat(returnedTweet.user.status).isNotNull();
            assertThat(returnedTweet.user.status.id).
                    isEqualTo(initialTweet.user.status.id);

        }
    }

    // withheldInCountries test

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSamewithheldInCountriesOtherwiseNull
            () {
        if (initialTweet.withheldInCountries != null) {
            assertThat(returnedTweet.withheldInCountries).isNotNull();
            assertThat(returnedTweet.withheldInCountries)
                    .containsExactlyElementsIn(initialTweet.withheldInCountries);
        } else {
            assertThat(returnedTweet.withheldInCountries).isNull();
        }
    }

    // Card test

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameCardObjectOtherwiseNull
            () {
        if (initialTweet.card != null) {
            assertThat(returnedTweet.card).isNotNull();
            assertThat(returnedTweet.card.name).
                    isEqualTo(initialTweet.card.name);

            assertThat(returnedTweet.card.bindingValues.containsKey(CARD_BOUND_VALUES_KEY_VALUE)).isTrue();
            assertThat((Object) returnedTweet.card.bindingValues.get(CARD_BOUND_VALUES_KEY_VALUE)).
                    isEqualTo(CARD_BOUND_VALUES_OBJECT_VALUE);
        } else {
            assertThat(returnedTweet.card).isNull();
        }
    }

    @Test
    public void
    givenATweet_whenCreatingTweetEntity_thenTweetEntityReturnsSameOOOObjectOtherwiseNull
            () {
        if (initialTweet != null) {
            assertThat(returnedTweet).isNotNull();
        } else {
            assertThat(returnedTweet).isNull();
        }
    }
}
