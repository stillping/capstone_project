package uk.me.desiderio.shiftt.data.repository;

import java.time.Instant;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Headers;
import uk.me.desiderio.shiftt.data.database.RateLimitDao;
import uk.me.desiderio.shiftt.data.database.model.RateLimitEnt;
import uk.me.desiderio.shiftt.data.network.model.RateLimit;
import uk.me.desiderio.shiftt.util.AppExecutors;

/**
 * Repository to persist and retrieve rate limit data
 */

@Singleton
public class RateLimitsRepository {

    private final AppExecutors appExecutors;
    private final RateLimitDao rateLimitDao;

    private final RateLimiter rateLimiter;

    @Inject
    public RateLimitsRepository(
            RateLimitDao rateLimitDao,
            AppExecutors appExecutors,
            RateLimiter rateLimiter) {
        this.rateLimitDao = rateLimitDao;
        this.appExecutors = appExecutors;
        this.rateLimiter = rateLimiter;
    }

    /**
     * initilised {@link RateLimiter} with up-to-date data from the database
     */
    public void initRateLimits() {
        rateLimitDao.getAllRateLimits().observeForever(rateLimitList -> {
            rateLimitList.forEach(rateLimitEnt -> rateLimiter.addLimit(rateLimitEnt.name,
                                                                   rateLimitEnt.coors,
                                                                   rateLimitEnt.time,
                                                                   rateLimitEnt.limit,
                                                                   rateLimitEnt.remaining,
                                                                   rateLimitEnt.reset));
        });
    }


    /**
     * checks whether to fetch new data based on the info provided as parameters
     */
    public boolean shouldFetch(String limitName, double lat, double lng, long locTime) {
        return rateLimiter.shouldFetch(limitName, lat, lng, locTime);
    }

    /**
     * updates current {@link RateLimiter} and persist data provided as parameters
     */
    public void updateRateLimit(String key, double lat, double lng, Headers headers) {
        String limit = headers.get(RateLimiter.RATE_LIMIT_CEILING_HEADER_KEY);
        String remaining = headers.get(RateLimiter.RATE_LIMIT_REMAINING_HEADER_KEY);
        String reset = headers.get(RateLimiter.RATE_LIMIT_RESET_TIME_HEADER_KEY);

        long now = Instant.now().getEpochSecond();

        RateLimit rateLimit = new RateLimit(key, lat, lng, now, limit, remaining, reset);

        rateLimiter.addLimit(rateLimit);
        persistRateLimit(rateLimit);
    }


    private void persistRateLimit(RateLimit limit) {
        RateLimitEnt rateLimitEnt = new RateLimitEnt(limit.name,
                                                     limit.coors,
                                                     limit.time,
                                                     limit.limit,
                                                     limit.remaining,
                                                     limit.reset);

        appExecutors.getDiskIO().execute(() -> rateLimitDao.insertRateLimit(rateLimitEnt));
    }
}
