package uk.me.desiderio.shiftt.data.repository;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.collection.ArrayMap;
import uk.me.desiderio.shiftt.data.network.model.RateLimit;

/**
 * Helper class that determines whether a new request to the Twitter API should go ahead.
 * <p>
 * It holds a map of {@link RateLimit} for each of the endpoint used by the application
 * <p>
 * It takes into account several factors in order to make its decision:
 *
 * - Location data age : it determines whether the location is older than 30min. If so, the
 * location data is considered stale and it won't allow further request
 *
 * - Location data: it makes sure that the current resquest refers to the data stored. if a
 * different coordinates are provided in the query to the ones stores, it determines it is a new
 * request and allows it
 *
 * - Tweet data age: it makes sure than only one request is done in the every 3mins window.
 *
 * - Twitter API conditions: Only a certain number of resquest are allowed by the Twitter API in
 * each 15mins window. The server provides remaining request allowance count and future time UTC
 * epoch when  the new window starts and allowance will be reset.
 */
public class RateLimiter {

    // the rate limit ceiling for that given endpoint
    public static final String RATE_LIMIT_CEILING_HEADER_KEY = "x-rate-limit-limit";
    // the number of requests left for the 15 minute window
    public static final String RATE_LIMIT_REMAINING_HEADER_KEY = "x-rate-limit-remaining";
    // the remaining window before the rate limit resets, in UTC epoch seconds
    public static final String RATE_LIMIT_RESET_TIME_HEADER_KEY = "x-rate-limit-reset";

    public static final String TWEETS_KEY_NAME = "/search/tweets";
    public static final String TREND_PLACE_KEY_NAME = "trends/place";

    public static final long MAX_LOCATION_AGE_IN_MIN = 30;
    public static final long MIN_DATA_AGE_IN_MIN = 3;

    private final Map<String, RateLimit> limits;

    @Inject
    public RateLimiter() {
        this.limits = new ArrayMap<>();
    }

    public void addLimit(String name,
                         List<Double> coordinates,
                         long time,
                         int limit,
                         int remaining,
                         long resetTime) {
        RateLimit rateLimit = new RateLimit(name, coordinates, time, limit, remaining, resetTime);
        limits.put(name, rateLimit);
    }

    public void addLimit(RateLimit rateLimit) {
        limits.put(rateLimit.name, rateLimit);
    }

    public boolean shouldFetch(String name, double lat, double lng, long time) {
        // if no limit, it is first request
        // wip : ST-201
        if (!limits.containsKey(name)) {
            return true;
        }

        RateLimit rateLimit = limits.get(name);
        return isDataStale(lat, lng, rateLimit)
                && wouldServerAllow(rateLimit);
    }

    /**
     * checks if the location is not stale and is not older than 30min defined by the
     * MAX_LOCATION_AGE_IN_MIN const.
     *
     * it would determine whether the app should request a new location
     */
    public static boolean isAFreshLocation(long lastKnownLocTime) {
        Instant locationMaxAge = getMaxLocationAge(lastKnownLocTime);
        return now().isBefore(locationMaxAge);
    }

    /**
     * given a location makes sure that data is not older than 3 min as
     * defined by MIN_DATA_AGE_IN_MIN constant
     *
     * if data stored doesn't relate to new location, data will be considered stale
     */
    private boolean isDataStale(double newLat, double newLng, RateLimit rateLimit) {
        if (isSameLocation(newLat, newLng, rateLimit)) {
            return now().isAfter(getMaxDataAge(rateLimit.time));
        }
        return true;
    }

    /**
     * determines if the new call will be allowed by server
     * checks if there is enough remaining request available in the current window.
     * Twitter window are normally 15min in length. In each window there is a request allowance
     * which is determined by the endpoint.
     * If window time has passed, remaining request are reset to the max for that endpoint
     */
    private boolean wouldServerAllow(RateLimit rateLimit) {
        return  hasResetTimePassed(rateLimit.reset)
                || rateLimit.remaining > 0;
    }

    // time helpers

    private static Instant now() {
        return Instant.now();
    }

    private boolean hasResetTimePassed(long resetTime) {
        return Instant.ofEpochSecond(resetTime).isBefore(now());
    }

    private static Instant getMaxLocationAge(long lastKnownLocTime) {
        Duration halfAnHour = Duration.ofMinutes(MAX_LOCATION_AGE_IN_MIN);
        return Instant.ofEpochSecond(lastKnownLocTime).plus(halfAnHour);
    }

    private Instant getMaxDataAge(long dataTime) {
        Duration dataMaxAge = Duration.ofMinutes(MIN_DATA_AGE_IN_MIN);
        return Instant.ofEpochSecond(dataTime).plus(dataMaxAge);
    }

    // coordinates helpers

    private boolean isSameLocation(double newLat, double newLng, RateLimit rateLimit) {
        return compareCoorValue(newLat, rateLimit.coors.get(0))
                && compareCoorValue(newLng, rateLimit.coors.get(1));
    }

    private boolean compareCoorValue(double oldCoor, double newCoor) {
        BigDecimal oldBigCoor = convertDoubleToRoundedBigDecimal(oldCoor);
        BigDecimal newBigCoor = convertDoubleToRoundedBigDecimal(newCoor);
        return oldBigCoor.compareTo(newBigCoor) == 0;
    }

    private BigDecimal convertDoubleToRoundedBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(3, BigDecimal.ROUND_HALF_UP);
    }
}
