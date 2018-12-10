package uk.me.desiderio.shiftt.data;

import com.twitter.sdk.android.core.models.BindingValues;
import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.Coordinates;
import com.twitter.sdk.android.core.models.HashtagEntity;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.MentionEntity;
import com.twitter.sdk.android.core.models.Place;
import com.twitter.sdk.android.core.models.SymbolEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.UrlEntity;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserEntities;
import com.twitter.sdk.android.core.models.VideoInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.VisibleForTesting;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
public class TweetMockDataProvider {


    public static final String CREATED_AT_VALUE = "tweet_property_created_at_value";
    public static final int CURRENT_USER_RETWEET_ID_VALUE = 67;
    public static final String CURRENT_USER_RETWEET_ID_STR_VALUE =
            "tweet_property_current_user_retweet_id_str_value";
    @SuppressWarnings("unused")
    public static final Object CURRENT_USER_RETWEET_VALUE = new Object() {
        public long id = CURRENT_USER_RETWEET_ID_VALUE;
        public String id_str = CURRENT_USER_RETWEET_ID_STR_VALUE;
    };
    // entity constants
    public static final int ENTITY_START_VALUE = 45;
    public static final int ENTITY_END_VALUE = 54;
    public static final String URL_ENTITY_URL_VALUE = "tweet_property_url_entity_url_value";
    public static final String URL_ENTITY_EXPANDED_URL_VALUE =
            "tweet_property_url_entity_expanded_url_value";
    public static final String URL_ENTITY_DISPLAY_URL_VALUE =
            "tweet_property_url_entity_display_url_value";
    public static final long MENTION_ENTITY_ID_VALUE = 89L;
    public static final String MENTION_ENTITY_ID_STR_VALUE =
            "tweet_property_mention_entity_id_str_value";
    public static final String MENTION_ENTITY_NAME_VALUE =
            "tweet_property_mention_entity_name_value";
    public static final String MENTION_ENTITY_SCREEN_NAME_VALUE =
            "tweet_property_mention_entity_screen_name_value";
    public static final int MEDIA_ENTITY_SIZE_WIDTH_VALUE = 189;
    public static final int MEDIA_ENTITY_SIZE_HEIGHT_VALUE = 981;
    public static final String MEDIA_ENTITY_SIZE_RESIZE_VALUE =
            "tweet_property_media_entity_size_resize_value";
    public static final String HASHTAG_ENTITY_TEXT_VALUE =
            "tweet_property_hashmap_entity_text_value";
    public static final int HASHTAG_COUNT_VALUE = 2;
    public static final Integer MEDIA_ENTITY_VIDEO_INFO_ASPECT_RATIO_VALUE = 433;
    public static final long MEDIA_ENTITY_VIDEO_INFO_DURATION_MILLS_VALUE = 43L;
    public static final Integer FAVORITE_COUNT_VALUE = 35;
    // end entity constants
    public static final boolean FAVORITED_VALUE = true;
    public static final String FILTER_LEVEL_VALUE =
            "tweet_property_filter_level_value";
    public static final String ID_STRING_VALUE =
            "tweet_property_id_str_value";
    public static final String IN_REPLY_TO_SCREEN_NAME_VALUE =
            "tweet_property_in_reply_to_screen_name_value";
    public static final long IN_REPLY_TO_STATUS_ID_VALUE = 99L;
    public static final String IN_REPLY_TO_STATUS_ID_STR_VALUE =
            "tweet_property_in_reply_to_status_id_str_value";
    public static final long IN_REPLY_TO_USER_ID_VALUE = 99L;
    public static final String IN_REPLY_TO_USER_ID_STR_VALUE =
            "tweet_property_in_reply_to_user_id_str_value";
    public static final String LANG_VALUE = "tweet_property_lang_value";
    public static final boolean POSSIBLY_SENSITIVE_VALUE = true;
    private static final Object SCOPES_VALUE = new Object();
    public static final long QUOTED_STATUS_ID_VALUE = 11L;
    public static final String QUOTED_STATUS_ID_STR_VALUE =
            "tweet_property_quoted_status_id_str_value";
    //TODO this.quotedStatus = tweet.quotedStatus;
    public static final boolean RETWEEDED_VALUE = true;
    public static final String SOURCE_VALUE = "tweet_property_source_value";
    //TODO this.retweetedStatus = tweet.retweetedStatus;
    public static final String TEXT_VALUE = "tweet_property_text_value";
    public static final List<Integer> DISPLAY_TEXT_RANGE =
            Arrays.asList(33, 44, 55);
    public static final boolean TRUNCATED_VALUE = true;
    public static final boolean WITHHELD_COPYRIGHT_VALUE = true;
    public static final List<String> WITHHELD_IN_COUNTRIES_VALUE =
            Arrays.asList("tweet_property_withheld_in_countries_one_value",
                          "tweet_property_withheld_in_countries_two_value");
    //TODO this.user = tweet.user;
    public static final String WITHHELD_SCOPE_VALUE = "tweet_property_withheld_scope_value";
    public static final int RETWEET_COUNT = 22;
    public static final int RETWEET_STATUS_ID = 27;
    public static final Double COORDINATES_LAT_VALUE = 5.8;
    public static final Double COORDINATES_LNG_VALUE = 5.6;
    public static final String COORDINATES_TYPE_VALUE = "tweet_property_withheld_scope_value";
    public static final Map<String, String> PLACE_ATTRIBUTES_VALUE = initPlaceAttributesValueMap();
    public static final String BOUNDING_BOX_TYPE_VALUE = "tweet_property_bounding_box_type_value";
    public static final List<List<List<Double>>> BOUNDING_BOX_COORDINATES =
            Collections.singletonList(Collections.singletonList(Arrays.asList(COORDINATES_LAT_VALUE, COORDINATES_LNG_VALUE)));
    public static final String PLACE_COUNTRY_VALUE = "tweet_property_place_country_value";
    public static final String PLACE_COUNTRY_CODE_VALUE = "tweet_property_place_country_code_value";
    public static final String PLACE_FULL_NAME_VALUE = "tweet_property_place_full_name_value";
    public static final String PLACE_NAME_VALUE = "tweet_property_place_name_value";
    public static final String PLACE_TYPE_VALUE = "tweet_property_place_type_value";
    public static final String PLACE_URL_VALUE = "tweet_property_place_url_value";
    public static final boolean USER_CONTRIBUTORS_ENABLED_VALUE = true;
    public static final String USER_CREATED_AT_VALUE = "tweet_property_user_created_at_value";
    public static final boolean USER_DEFAUL_PROFILE_VALUE = true;
    public static final boolean USER_DEFAULT_PROFILE_IMAGE_VALUE = true;
    public static final String USER_DESCRIPTION_VALUE = "tweet_property_user_description_value";
    public static final String USER_EMAIL_VALUE = "tweet_property_user_email_value";
    public static final int USER_FAVORITES_COUNT_VALUE = 701;
    public static final boolean USER_FOLLOW_REQUEST_SENT_VALUE = true;
    public static final int USER_FOLLOWERS_COUNT_VALUE = 702;
    public static final int USER_FRIENDS_COUNT_VALUE = 703;
    public static final boolean USER_GEO_ENABLED_VALUE = true;
    public static final long USER_ID_VALUE = 704L;
    public static final String USER_ID_STR_VALUE = "tweet_property_user_id_str_value";
    public static final boolean USER_IS_TRANSLATOR_VALUE = true;
    public static final String USER_LANG_VALUE = "tweet_property_user_lang_value";
    public static final int USER_LISTED_COUNT_VALUE = 704;
    public static final String USER_LOCATION_VALUE = "tweet_property_user_location_value";
    public static final String USER_NAME_VALUE = "tweet_property_user_name_value";
    public static final String USER_PROFILE_BG_COLOR_VALUE = "tweet_property_user_profile_bg_color_value";
    public static final String USER_PROFILE_BG_IMAGE_URL_VALUE =
            "tweet_property_user_profile_bg_image_url_value";
    public static final String USER_PROFILE_BG_IMAGE_URL_HTTPS_VALUE =
            "tweet_property_user_profile_bg_image_url_https_value";
    public static final boolean USER_PROFILE_BG_TILE_VALUE = true;
    public static final String USER_PROFILE_BANNER_URL_VALUE =
            "tweet_property_user_profile_banner_url_value";
    public static final String USER_PROFILE_IMAGE_URL_VALUE =
            "tweet_property_user_profile_image_url_value";
    public static final String USER_PROFILE_IMAGE_URL_HTTPS_VALUE =
            "tweet_property_user_image_url_https_value";
    public static final String USER_PROFILE_LINK_COLOR_VALUE =
            "tweet_property_user_profile link_color_value";
    public static final String USER_PROFILE_SIDEBAR_BORDER_COLOR_VALUE =
            "tweet_property_user_profile_sidebar_border_color_value";
    public static final String USER_PROFILE_SIDEBAR_FILL_COLOR_VALUE =
            "tweet_property_user_profile_sidebar_fill_color_value";
    public static final String USER_PROFILE_TEXT_COLOR_VALUE =
            "tweet_property_user_profile_text_color_value";
    public static final boolean USER_PROFILE_USE_BACKGROUND_IMAGE_VALUE = true;
    public static final boolean USER_PROTECTED_USER_VALUE = true;
    public static final String USER_SCREEN_NAME_VALUE = "tweet_property_user_user_name_value";
    public static final boolean USER_SHOW_ALL_INLINE_MEDIA_VALUE = true;
    public static final int USER_STATUSES_COUNT_VALUE = 705;
    public static final String USER_TIME_ZONE_VALUE = "tweet_property_user_time_zone_value";
    public static final String USER_URL_VALUE = "tweet_property_user_url_value";
    public static final int USER_UTC_OFFSET_VALUE = 706;
    public static final boolean USER_VERIFIED_VALUE = true;
    private static final String USER_WITHHELD_IN_COUNTRY_ONE_VALUE =
            "tweet_property_withheld_in_country_one_value";
    private static final String USER_WITHHELD_IN_COUNTRY_TWO_VALUE =
            "tweet_property_withheld_in_country_two_value";
    public static final List<String> USER_WITHHELD_IN_COUNTRIES_VALUE =
            Arrays.asList(USER_WITHHELD_IN_COUNTRY_ONE_VALUE,
                          USER_WITHHELD_IN_COUNTRY_TWO_VALUE);
    public static final String USER_WITHELD_SCOPE_VALUE =
            "tweet_property_withheld_scope_value_value";
    private static final long USER_SCOPE_ID_VALUE = 710L;
    public static final String CARD_NAME_VALUE =
            "card_bound_values_name_value";
    public static final String CARD_BOUND_VALUES_KEY_VALUE =
            "card_bound_values_key_value";
    public static final Object CARD_BOUND_VALUES_OBJECT_VALUE = new Object();
    public static final String SYMBOL_ENTITY_TEXT_VALUE =
            "tweet_property_symbol_entity_text_value";
    public static final String MEDIA_ENTITY_URL_VALUE = "tweet_property_entity_url_value";
    public static final String MEDIA_ENTITY_EXPANDED_URL_VALUE =
            "tweet_property_media_entity_expanded_url_value";
    public static final String MEDIA_ENTITY_DISPLAY_URL_VALUE =
            "tweet_property_media_entity_display_url_value";
    public static final long MEDIA_ENTITY_ID_VALUE = 58;
    public static final String MEDIA_ENTITY_ID_STR_VALUE =
            "tweet_property_media_entity_id_str_value";
    public static final String MEDIA_ENTITY_MEDIA_URL_VALUE =
            "tweet_property_media_entity_media_url_value";
    public static final String MEDIA_ENTITY_MEDIA_URL_HTTPS_VALUE =
            "tweet_property_media_entity_media_url_https_value";
    //sizes
    public static final long MEDIA_ENTITY_SOURCE_STATUS_ID_VALUE = 92;
    public static final String MEDIA_ENTITY_SOURCE_STATUS_ID_STR_VALUE =
            "tweet_property_media_entity_source_status_id_str_value";
    public static final String MEDIA_ENTITY_TYPE_VALUE = "tweet_property_media_entity_type_value";
    public static final String MEDIA_ENTITY_ALT_TEXT_VALUE =
            "tweet_property_media_entity_alt_text_value";
    private static final String PLACE_ID_VALUE = "tweet_property_place_id_value";

    /**
     * provides mock {@link TweetEnt} object
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public static TweetEnt getTweetEnt(long id, boolean hasNullFields) {
        Tweet tweet;
        if (hasNullFields) {
            tweet = getTweetWithNullValues(id);
        } else {
            tweet = getTweet(id);
        }
        return new TweetEnt(tweet);
    }

    /**
     * provides mocked Tweet with the nullable values as nulls
     **/
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public static Tweet getTweetWithNullValues(long id) {
        return initTweet(id,
                         null,
                         null,
                         null,
                         null,
                         null,
                         null,
                         null,
                         null,
                         null,
                         null);
    }

    /**
     * provides mocked Tweet with no null values
     **/
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public static Tweet getTweet(long id) {
        Coordinates coordinates = getMockedCoordinatesObject();
        Place place = getMockedPlaceObject();
        TweetEntities entities = getMockedEntitiesObject(id);
        TweetEntities extendedEntities = getMockedEntitiesObject(id);
        User user = getMockedUserObject();
        Card card = getMockedCardObject();
        return initTweet(id,
                         coordinates,
                         CURRENT_USER_RETWEET_VALUE,
                         entities,
                         extendedEntities,
                         place,
                         SCOPES_VALUE,
                         user,
                         card,
                         getMetadataTweet(QUOTED_STATUS_ID_VALUE),
                         getMetadataTweet(RETWEET_STATUS_ID));
    }

    /**
     * provides mocked Tweet for main tweet metadata
     **/
    private static Tweet getMetadataTweet(long id) {
        Coordinates coordinates = getMockedCoordinatesObject();
        Place place = getMockedPlaceObject();
        TweetEntities entities = getMockedEntitiesObject(id);
        TweetEntities extendedEntities = getMockedEntitiesObject(id);
        Card card = getMockedCardObject();
        return initTweet(id,
                         coordinates,
                         CURRENT_USER_RETWEET_VALUE,
                         entities,
                         extendedEntities,
                         place,
                         SCOPES_VALUE,
                         null,
                         card,
                         null,
                         null);

    }

    private static Tweet initTweet(long id,
                                   Coordinates coordinates,
                                   Object currentUserRetweet,
                                   TweetEntities entities,
                                   TweetEntities extendedEntities,
                                   Place place,
                                   Object scopes,
                                   User user,
                                   Card card,
                                   Tweet quotedStatus,
                                   Tweet retweetedStatus) {
        return new Tweet(coordinates,
                         CREATED_AT_VALUE,
                         currentUserRetweet,
                         entities,
                         extendedEntities,
                         FAVORITE_COUNT_VALUE,
                         FAVORITED_VALUE,
                         FILTER_LEVEL_VALUE,
                         id,
                         ID_STRING_VALUE,
                         IN_REPLY_TO_SCREEN_NAME_VALUE,
                         IN_REPLY_TO_STATUS_ID_VALUE,
                         IN_REPLY_TO_STATUS_ID_STR_VALUE,
                         IN_REPLY_TO_USER_ID_VALUE,
                         IN_REPLY_TO_USER_ID_STR_VALUE,
                         LANG_VALUE,
                         place,
                         POSSIBLY_SENSITIVE_VALUE,
                         scopes,
                         (quotedStatus != null) ? QUOTED_STATUS_ID_VALUE : 0L,
                         (quotedStatus != null) ? QUOTED_STATUS_ID_STR_VALUE : null,
                         quotedStatus,
                         RETWEET_COUNT,
                         RETWEEDED_VALUE,
                         retweetedStatus,
                         SOURCE_VALUE,
                         TEXT_VALUE,
                         DISPLAY_TEXT_RANGE,
                         TRUNCATED_VALUE,
                         user,
                         WITHHELD_COPYRIGHT_VALUE,
                         WITHHELD_IN_COUNTRIES_VALUE,
                         WITHHELD_SCOPE_VALUE,
                         card);

    }

    private static Coordinates getMockedCoordinatesObject() {
        return new Coordinates(COORDINATES_LAT_VALUE,
                               COORDINATES_LNG_VALUE,
                               COORDINATES_TYPE_VALUE);
    }

    // Place

    private static Place getMockedPlaceObject() {

        Place.BoundingBox boundingBox = new Place.BoundingBox(BOUNDING_BOX_COORDINATES,
                                                              BOUNDING_BOX_TYPE_VALUE);
        return new Place(PLACE_ATTRIBUTES_VALUE,
                         boundingBox,
                         PLACE_COUNTRY_VALUE,
                         PLACE_COUNTRY_CODE_VALUE,
                         PLACE_FULL_NAME_VALUE,
                         PLACE_ID_VALUE,
                         PLACE_NAME_VALUE,
                         PLACE_TYPE_VALUE,
                         PLACE_URL_VALUE);
    }

    private static Map initPlaceAttributesValueMap() {
        Map map = new HashMap();
        map.put("attributeKeyOne", "attribureValueOne");
        map.put("attributeKeyTwo", "attribureValueOneTwo");
        return Collections.unmodifiableMap(map);
    }

    // Entities

    private static UrlEntity getMockedUrlEntityObject() {
        return new UrlEntity(URL_ENTITY_URL_VALUE,
                             URL_ENTITY_EXPANDED_URL_VALUE,
                             URL_ENTITY_DISPLAY_URL_VALUE,
                             ENTITY_START_VALUE,
                             ENTITY_END_VALUE);
    }

    private static TweetEntities getMockedEntitiesObject(long tweetEntId) {
        UrlEntity urlEntity = getMockedUrlEntityObject();

        MentionEntity mentionEntity = new MentionEntity(MENTION_ENTITY_ID_VALUE,
                                                        MENTION_ENTITY_ID_STR_VALUE,
                                                        MENTION_ENTITY_NAME_VALUE,
                                                        MENTION_ENTITY_SCREEN_NAME_VALUE,
                                                        ENTITY_START_VALUE,
                                                        ENTITY_END_VALUE);

        MediaEntity.Size size = new MediaEntity.Size(MEDIA_ENTITY_SIZE_WIDTH_VALUE,
                                                     MEDIA_ENTITY_SIZE_HEIGHT_VALUE,
                                                     MEDIA_ENTITY_SIZE_RESIZE_VALUE);
        MediaEntity.Sizes sizes = new MediaEntity.Sizes(size, size, size, size);

        VideoInfo videoInfo =
                new VideoInfo(Collections.singletonList(MEDIA_ENTITY_VIDEO_INFO_ASPECT_RATIO_VALUE),
                              MEDIA_ENTITY_VIDEO_INFO_DURATION_MILLS_VALUE,
                              null);

        MediaEntity mediaEntity = new MediaEntity(MEDIA_ENTITY_URL_VALUE,
                                                  MEDIA_ENTITY_EXPANDED_URL_VALUE,
                                                  MEDIA_ENTITY_DISPLAY_URL_VALUE,
                                                  ENTITY_START_VALUE,
                                                  ENTITY_END_VALUE,
                                                  MEDIA_ENTITY_ID_VALUE,
                                                  MEDIA_ENTITY_ID_STR_VALUE,
                                                  MEDIA_ENTITY_MEDIA_URL_VALUE,
                                                  MEDIA_ENTITY_MEDIA_URL_HTTPS_VALUE,
                                                  sizes,
                                                  MEDIA_ENTITY_SOURCE_STATUS_ID_VALUE,
                                                  MEDIA_ENTITY_SOURCE_STATUS_ID_STR_VALUE,
                                                  MEDIA_ENTITY_TYPE_VALUE,
                                                  videoInfo,
                                                  MEDIA_ENTITY_ALT_TEXT_VALUE);

        HashtagEntity hashtagEntity = getHashtagEntity(1, tweetEntId);

        HashtagEntity hashtagEntityTwo = getHashtagEntity(2, tweetEntId);

        SymbolEntity symbolEntity = new SymbolEntity(SYMBOL_ENTITY_TEXT_VALUE,
                                                     ENTITY_START_VALUE,
                                                     ENTITY_END_VALUE);

        return new TweetEntities(Collections.singletonList(urlEntity),
                                 Collections.singletonList(mentionEntity),
                                 Collections.singletonList(mediaEntity),
                                 Arrays.asList(hashtagEntity,
                                               hashtagEntityTwo),
                                 Collections.singletonList(symbolEntity));
    }

    private static HashtagEntity getHashtagEntity(int index, long tweetEntId) {
        String text = tweetEntId + "_" + HASHTAG_ENTITY_TEXT_VALUE + "_" + index;
        return new HashtagEntity(text,
                                 ENTITY_START_VALUE,
                                 ENTITY_END_VALUE);
    }

    // User

    private static User getMockedUserObject() {
        Tweet status = getMetadataTweet(USER_SCOPE_ID_VALUE);
        return new User(USER_CONTRIBUTORS_ENABLED_VALUE,
                        USER_CREATED_AT_VALUE,
                        USER_DEFAUL_PROFILE_VALUE,
                        USER_DEFAULT_PROFILE_IMAGE_VALUE,
                        USER_DESCRIPTION_VALUE,
                        USER_EMAIL_VALUE,
                        getMockedUserEntitiesObject(),
                        USER_FAVORITES_COUNT_VALUE,
                        USER_FOLLOW_REQUEST_SENT_VALUE,
                        USER_FOLLOWERS_COUNT_VALUE,
                        USER_FRIENDS_COUNT_VALUE,
                        USER_GEO_ENABLED_VALUE,
                        USER_ID_VALUE,
                        USER_ID_STR_VALUE,
                        USER_IS_TRANSLATOR_VALUE,
                        USER_LANG_VALUE,
                        USER_LISTED_COUNT_VALUE,
                        USER_LOCATION_VALUE,
                        USER_NAME_VALUE,
                        USER_PROFILE_BG_COLOR_VALUE,
                        USER_PROFILE_BG_IMAGE_URL_VALUE,
                        USER_PROFILE_BG_IMAGE_URL_HTTPS_VALUE,
                        USER_PROFILE_BG_TILE_VALUE,
                        USER_PROFILE_BANNER_URL_VALUE,
                        USER_PROFILE_IMAGE_URL_VALUE,
                        USER_PROFILE_IMAGE_URL_HTTPS_VALUE,
                        USER_PROFILE_LINK_COLOR_VALUE,
                        USER_PROFILE_SIDEBAR_BORDER_COLOR_VALUE,
                        USER_PROFILE_SIDEBAR_FILL_COLOR_VALUE,
                        USER_PROFILE_TEXT_COLOR_VALUE,
                        USER_PROFILE_USE_BACKGROUND_IMAGE_VALUE,
                        USER_PROTECTED_USER_VALUE,
                        USER_SCREEN_NAME_VALUE,
                        USER_SHOW_ALL_INLINE_MEDIA_VALUE,
                        status,
                        USER_STATUSES_COUNT_VALUE,
                        USER_TIME_ZONE_VALUE,
                        USER_URL_VALUE,
                        USER_UTC_OFFSET_VALUE,
                        USER_VERIFIED_VALUE,
                        USER_WITHHELD_IN_COUNTRIES_VALUE,
                        USER_WITHELD_SCOPE_VALUE);
    }

    private static UserEntities getMockedUserEntitiesObject() {
        List<UrlEntity> entityList = Collections.singletonList(getMockedUrlEntityObject());
        UserEntities.UrlEntities urlEntities = new UserEntities.UrlEntities(entityList);
        return new UserEntities(urlEntities, urlEntities);
    }

    // Card

    private static Card getMockedCardObject() {

        Map<String, Object> bindingValuesMap = new HashMap<>();
        bindingValuesMap.put(CARD_BOUND_VALUES_KEY_VALUE,
                             CARD_BOUND_VALUES_OBJECT_VALUE);

        BindingValues boundingValues = new BindingValues(bindingValuesMap);
        return new Card(boundingValues,
                        CARD_NAME_VALUE);
    }
}
