package uk.me.desiderio.shiftt.data.network;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import uk.me.desiderio.shiftt.data.network.model.RateLimit;

import static com.google.common.truth.Truth.assertThat;
import static uk.me.desiderio.shiftt.data.network.RateLimiter.MAX_LOCATION_AGE;
import static uk.me.desiderio.shiftt.data.network.RateLimiter.MIN_DATA_AGE;

/** tests for the {@link RateLimiter} class
 */
@RunWith(JUnit4.class)
public class RateLimiterTest {

    private static final String DEFAULT_LIMIT_NAME_VALUE = "expected_limit_name_value";
    private static final double DEFAULT_LOC_LAT_VALUE = 10;
    private static final double DEFAULT_LOC_LNG_VALUE = 11;
    private static final int DEFAULT_LIMIT_LIMIT_VALUE = 12;
    private static final int DEFAULT_LIMIT_REMAINING_VALUE = 14;


    private RateLimiter rateLimiter;
    private RateLimit limit;


    @Before
    public void setUp() {
        rateLimiter = new RateLimiter();
    }

    /**
     * happy path test where :
     * - location data is not stale,
     * - tweet data is stale for same coordinates
     * - server allows for futher request as remaining request count is greater than zero
     */
    @Test
    public void shouldFetch_happyPathTest() {
        limit = new RateLimitBuilder().build();
        rateLimiter.addLimit(limit);
        boolean shouldFetch = rateLimiter.shouldFetch(DEFAULT_LIMIT_NAME_VALUE,
                                                      DEFAULT_LOC_LAT_VALUE,
                                                      DEFAULT_LOC_LNG_VALUE,
                                                      getEpochPassed(MAX_LOCATION_AGE - 1));

        assertThat(shouldFetch).isTrue();
    }

    @Test
    public void addLimit_happyPathTest() {
        limit = new RateLimitBuilder().build();
        rateLimiter.addLimit(DEFAULT_LIMIT_NAME_VALUE,
                             Arrays.asList(DEFAULT_LOC_LAT_VALUE,
                                           DEFAULT_LOC_LNG_VALUE),
                             getEpochPassed(RateLimiter.MIN_DATA_AGE + 1),
                             DEFAULT_LIMIT_LIMIT_VALUE,
                             DEFAULT_LIMIT_REMAINING_VALUE,
                             getEpochInFuture(15));

        boolean shouldFetch = rateLimiter.shouldFetch(DEFAULT_LIMIT_NAME_VALUE,
                                                      DEFAULT_LOC_LAT_VALUE,
                                                      DEFAULT_LOC_LNG_VALUE,
                                                      getEpochPassed(MAX_LOCATION_AGE - 1));

        assertThat(shouldFetch).isTrue();
    }

    @Test
    public void
    givenNewEndPoint_whenCheckIfRequestShouldGoAhead_thenReturnTrue() {
        limit = new RateLimitBuilder().build();
        rateLimiter.addLimit(limit);
        boolean shouldFetch = rateLimiter.shouldFetch("Unknow Limit Name",
                                                      DEFAULT_LOC_LAT_VALUE,
                                                      DEFAULT_LOC_LNG_VALUE,
                                                      getEpochPassed(MAX_LOCATION_AGE - 1));

        assertThat(shouldFetch).isTrue();
    }

    @Test
    public void
    givenKnownEndPoint_whenRequestingWithDifferentCoordinates_thenReturnTrue() {
        limit = new RateLimitBuilder().build();
        rateLimiter.addLimit(limit);
        boolean shouldFetch = rateLimiter.shouldFetch(DEFAULT_LIMIT_NAME_VALUE,
                                                      99,
                                                      99,
                                                      getEpochPassed(MAX_LOCATION_AGE - 1));

        assertThat(shouldFetch).isTrue();
    }

    @Test
    public void
    givenKnownEndPoint_whenRequestingWithStaleLocation_thenReturnFalse() {
        limit = new RateLimitBuilder().build();
        rateLimiter.addLimit(limit);
        boolean shouldFetch = rateLimiter.shouldFetch(DEFAULT_LIMIT_NAME_VALUE,
                                                      DEFAULT_LOC_LAT_VALUE,
                                                      DEFAULT_LOC_LNG_VALUE,
                                                      getEpochPassed(MAX_LOCATION_AGE + 1));

        assertThat(shouldFetch).isFalse();
    }

    @Test
    public void
    givenKnownEndPoint_whenRequestingWithSameLocationAndNoLaterThan3Min_thenReturnFalse() {
        limit = new RateLimitBuilder()
                .setTime(MIN_DATA_AGE - 1)
                .build();

        rateLimiter.addLimit(limit);
        boolean shouldFetch = rateLimiter.shouldFetch(DEFAULT_LIMIT_NAME_VALUE,
                                                      DEFAULT_LOC_LAT_VALUE,
                                                      DEFAULT_LOC_LNG_VALUE,
                                                      getEpochPassed(MAX_LOCATION_AGE - 1));

        assertThat(shouldFetch).isFalse();
    }

    @Test
    public void
    givenKnownEndPoint_whenRequestingWithNoRemainingRequestInSameWindow_thenReturnFalse() {
        limit = new RateLimitBuilder()
                .setRemaining(0)
                .build();

        rateLimiter.addLimit(limit);
        boolean shouldFetch = rateLimiter.shouldFetch(DEFAULT_LIMIT_NAME_VALUE,
                                                      DEFAULT_LOC_LAT_VALUE,
                                                      DEFAULT_LOC_LNG_VALUE,
                                                      getEpochPassed(MAX_LOCATION_AGE - 1));

        assertThat(shouldFetch).isFalse();
    }

    @Test
    public void
    givenKnownEndPoint_whenRequestingWithNoRemainingRequestInNewWindow_thenReturnTrue() {
        limit = new RateLimitBuilder()
                .setRemaining(0)
                .setReset(getEpochPassed(1))
                .build();

        rateLimiter.addLimit(limit);
        boolean shouldFetch = rateLimiter.shouldFetch(DEFAULT_LIMIT_NAME_VALUE,
                                                      DEFAULT_LOC_LAT_VALUE,
                                                      DEFAULT_LOC_LNG_VALUE,
                                                      getEpochPassed(MAX_LOCATION_AGE - 1));

        assertThat(shouldFetch).isTrue();
    }

    private long getEpochInFuture(long timeLeftMinutes) {
        Instant timeInFuture = Instant.now().plus(Duration.ofMinutes(timeLeftMinutes));
        return timeInFuture.getEpochSecond();
    }

    private long getEpochPassed(long timePassedInMinutes) {
        Instant timeInPass = Instant.now().minus(Duration.ofMinutes(timePassedInMinutes));
        return timeInPass.getEpochSecond();
    }

    private class RateLimitBuilder {

        private String name = DEFAULT_LIMIT_NAME_VALUE;
        private double lat = DEFAULT_LOC_LAT_VALUE;
        private double lng = DEFAULT_LOC_LNG_VALUE;
        private long time = getEpochPassed(RateLimiter.MIN_DATA_AGE + 1);
        private int limit = DEFAULT_LIMIT_LIMIT_VALUE;
        private int remaining = DEFAULT_LIMIT_REMAINING_VALUE;
        private long reset = getEpochInFuture(1);

        RateLimitBuilder setTime(long time) {
            this.time = getEpochPassed(time);
            return this;
        }

        RateLimitBuilder setRemaining(int remaining) {
            this.remaining = remaining;
            return this;
        }

        RateLimitBuilder setReset(long reset) {
            this.reset = reset;
            return this;
        }

        RateLimit build() {
            return new RateLimit(name,
                                 Arrays.asList(lat, lng),
                                 time,
                                 limit,
                                 remaining,
                                 reset);
        }
    }
}