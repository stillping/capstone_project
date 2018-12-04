package uk.me.desiderio.shiftt.data.database.model;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserEntities;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import uk.me.desiderio.shiftt.data.database.converter.StringListTypeConverter;
import uk.me.desiderio.shiftt.data.database.converter.UserEntitiesTypeConverter;

/**
 * Room entity class to store data from the {@link User} Twitter data object
 */
@Entity(tableName = "user")
public class UserEnt implements SeedProvider {

    @ColumnInfo(name = "contributors_enabled")
    public boolean contributorsEnabled;

    @ColumnInfo(name = "created_at")
    public String createdAt;

    @ColumnInfo(name = "default_profile")
    public boolean defaultProfile;

    @ColumnInfo(name = "default_profile_image")
    public boolean defaultProfileImage;

    public String description;

    public String email;

    @TypeConverters(UserEntitiesTypeConverter.class)
    public UserEntities entities;

    @ColumnInfo(name = "favourites_count")
    public int favouritesCount;

    @ColumnInfo(name = "follow_request_sent")
    public boolean followRequestSent;

    @ColumnInfo(name = "followers_count")
    public int followersCount;

    @ColumnInfo(name = "friends_count")
    public int friendsCount;

    @ColumnInfo(name = "geo_enabled")
    public boolean geoEnabled;

    @PrimaryKey
    public long id;

    @ColumnInfo(name = "id_str")
    public String idStr;

    @ColumnInfo(name = "is_translator")
    public boolean isTranslator;

    public String lang;

    @ColumnInfo(name = "listed_count")
    public int listedCount;

    public String location;

    public String name;

    @ColumnInfo(name = "profile_background_color")
    public String profileBackgroundColor;

    @ColumnInfo(name = "profile_background_image_url")
    public String profileBackgroundImageUrl;

    @ColumnInfo(name = "profile_background_image_url_https")
    public String profileBackgroundImageUrlHttps;

    @ColumnInfo(name = "profile_background_tile")
    public boolean profileBackgroundTile;

    @ColumnInfo(name = "profile_banner_url")
    public String profileBannerUrl;

    @ColumnInfo(name = "profile_image_url")
    public String profileImageUrl;

    @ColumnInfo(name = "profile_image_url_https")
    public String profileImageUrlHttps;

    @ColumnInfo(name = "profile_link_color")
    public String profileLinkColor;

    @ColumnInfo(name = "profile_sidebar_border_color")
    public String profileSidebarBorderColor;

    @ColumnInfo(name = "profile_sidebar_fill_color")
    public String profileSidebarFillColor;

    @ColumnInfo(name = "profile_text_color")
    public String profileTextColor;

    @ColumnInfo(name = "profile_use_background_image")
    public boolean profileUseBackgroundImage;

    @ColumnInfo(name = "protected")
    public boolean protectedUser;

    @ColumnInfo(name = "screen_name")
    public String screenName;

    @ColumnInfo(name = "show_all_inline_media")
    public boolean showAllInlineMedia;

    @Ignore
    public Tweet status;
    public long statusId;

    @ColumnInfo(name = "statuses_count")
    public int statusesCount;

    @ColumnInfo(name = "time_zone")
    public String timeZone;

    public String url;

    @ColumnInfo(name = "utc_offset")
    public int utcOffset;

    public boolean verified;

    @ColumnInfo(name = "withheld_in_countries")
    @TypeConverters(StringListTypeConverter.class)
    public List<String> withheldInCountries;

    @ColumnInfo(name = "withheld_scope")
    public String withheldScope;

    public UserEnt(boolean contributorsEnabled, String createdAt, boolean defaultProfile,
                   boolean defaultProfileImage, String description, String email,
                   UserEntities entities, int favouritesCount, boolean followRequestSent,
                   int followersCount, int friendsCount, boolean geoEnabled, long id, String idStr,
                   boolean isTranslator, String lang, int listedCount, String location, String name,
                   String profileBackgroundColor, String profileBackgroundImageUrl,
                   String profileBackgroundImageUrlHttps, boolean profileBackgroundTile,
                   String profileBannerUrl, String profileImageUrl, String profileImageUrlHttps,
                   String profileLinkColor, String profileSidebarBorderColor,
                   String profileSidebarFillColor, String profileTextColor,
                   boolean profileUseBackgroundImage, boolean protectedUser, String screenName,
                   boolean showAllInlineMedia, long statusId, int statusesCount, String timeZone,
                   String url, int utcOffset, boolean verified, List<String> withheldInCountries,
                   String withheldScope) {
        this.contributorsEnabled = contributorsEnabled;
        this.createdAt = createdAt;
        this.defaultProfile = defaultProfile;
        this.defaultProfileImage = defaultProfileImage;
        this.description = description;
        this.email = email;
        this.entities = entities;
        this.favouritesCount = favouritesCount;
        this.followRequestSent = followRequestSent;
        this.followersCount = followersCount;
        this.friendsCount = friendsCount;
        this.geoEnabled = geoEnabled;
        this.id = id;
        this.idStr = idStr;
        this.isTranslator = isTranslator;
        this.lang = lang;
        this.listedCount = listedCount;
        this.location = location;
        this.name = name;
        this.profileBackgroundColor = profileBackgroundColor;
        this.profileBackgroundImageUrl = profileBackgroundImageUrl;
        this.profileBackgroundImageUrlHttps = profileBackgroundImageUrlHttps;
        this.profileBackgroundTile = profileBackgroundTile;
        this.profileBannerUrl = profileBannerUrl;
        this.profileImageUrl = profileImageUrl;
        this.profileImageUrlHttps = profileImageUrlHttps;
        this.profileLinkColor = profileLinkColor;
        this.profileSidebarBorderColor = profileSidebarBorderColor;
        this.profileSidebarFillColor = profileSidebarFillColor;
        this.profileTextColor = profileTextColor;
        this.profileUseBackgroundImage = profileUseBackgroundImage;
        this.protectedUser = protectedUser;
        this.screenName = screenName;
        this.showAllInlineMedia = showAllInlineMedia;
        this.statusId = statusId;
        this.statusesCount = statusesCount;
        this.timeZone = timeZone;
        this.url = url;
        this.utcOffset = utcOffset;
        this.verified = verified;
        this.withheldInCountries = withheldInCountries;
        this.withheldScope = withheldScope;
    }

    /**
     * Creates an user entity from a {@link User} Twitter data object
     * It is ignored by the Room framework
     */
    @Ignore
    public UserEnt(@NonNull User user) {
        this.contributorsEnabled = user.contributorsEnabled;
        this.createdAt= user.createdAt;
        this.defaultProfile= user.defaultProfile;
        this.defaultProfileImage= user.defaultProfileImage;
        this.description= user.description;
        this.email= user.email;
        this.entities= user.entities;
        this.favouritesCount= user.favouritesCount;
        this.followRequestSent= user.followRequestSent;
        this.followersCount= user.followersCount;
        this.friendsCount= user.friendsCount;
        this.geoEnabled= user.geoEnabled;
        this.id= user.id;
        this.idStr= user.idStr;
        this.isTranslator= user.isTranslator;
        this.lang= user.lang;
        this.listedCount= user.listedCount;
        this.location= user.location;
        this.name= user.name;
        this.profileBackgroundColor= user.profileBackgroundColor;
        this.profileBackgroundImageUrl= user.profileBackgroundImageUrl;
        this.profileBackgroundImageUrlHttps= user.profileBackgroundImageUrlHttps;
        this.profileBackgroundTile= user.profileBackgroundTile;
        this.profileBannerUrl= user.profileBannerUrl;
        this.profileImageUrl= user.profileImageUrl;
        this.profileImageUrlHttps= user.profileImageUrlHttps;
        this.profileLinkColor= user.profileLinkColor;
        this.profileSidebarBorderColor= user.profileSidebarBorderColor;
        this.profileSidebarFillColor= user.profileSidebarFillColor;
        this.profileTextColor= user.profileTextColor;
        this.profileUseBackgroundImage= user.profileUseBackgroundImage;
        this.protectedUser= user.protectedUser;
        this.screenName= user.screenName;
        this.showAllInlineMedia= user.showAllInlineMedia;
        this.status= user.status;
        this.statusesCount= user.statusesCount;
        this.timeZone= user.timeZone;
        this.url= user.url;
        this.utcOffset= user.utcOffset;
        this.verified= user.verified;
        this.withheldInCountries= user.withheldInCountries;
        this.withheldScope= user.withheldScope;
    }

    /**
     * Returns a {@link User} Twitter data object
     */
    public User getSeed() {
        return new User(contributorsEnabled,
                        createdAt,
                        defaultProfile,
                        defaultProfileImage,
                        description,
                        email,
                        entities,
                        favouritesCount,
                        followRequestSent,
                        followersCount,
                        friendsCount,
                        geoEnabled,
                        id,
                        idStr,
                        isTranslator,
                        lang,
                        listedCount,
                        location,
                        name,
                        profileBackgroundColor,
                        profileBackgroundImageUrl,
                        profileBackgroundImageUrlHttps,
                        profileBackgroundTile,
                        profileBannerUrl,
                        profileImageUrl,
                        profileImageUrlHttps,
                        profileLinkColor,
                        profileSidebarBorderColor,
                        profileSidebarFillColor,
                        profileTextColor,
                        profileUseBackgroundImage,
                        protectedUser,
                        screenName,
                        showAllInlineMedia,
                        status,
                        statusesCount,
                        timeZone,
                        url,
                        utcOffset,
                        verified,
                        withheldInCountries,
                        withheldScope);
    }
}
