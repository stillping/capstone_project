package uk.me.desiderio.shiftt.data.network.model;

import java.util.List;

/**
 * Retrofit network data object for the 'TrendsByPlaceIdService' response
 */
public class TrendsQueryResult {
    public List<Trend> trends;
    public String as_of;
    public String created_at;
    public List<Place> places;
}
