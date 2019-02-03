package uk.me.desiderio.shiftt.data.network.model;

import java.util.List;

import uk.me.desiderio.shiftt.data.network.TrendsByPlaceIdService;

/**
 * Retrofit network data object for the {@link TrendsByPlaceIdService} response
 */

public class TrendsQueryResult {
    public List<Trend> trends;
    @SuppressWarnings("unused")
    public String as_of;
    @SuppressWarnings("unused")
    public String created_at;
    @SuppressWarnings("unused")
    public List<Place> places;
}
