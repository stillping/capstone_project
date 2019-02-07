package uk.me.desiderio.shiftt.data.network.settings;

import retrofit2.Retrofit;

/**
 * Retrofit client providing the {@link MapStyleService}
 */
public class MapSettingsApiClient {

    public static final String SETTINGS_BASE_URL = "https://s3.eu-west-2.amazonaws.com/shiftt.settings/";

    private Retrofit retrofit;

    public MapSettingsApiClient() {
        this.retrofit = getRetrofit();
    }

    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(SETTINGS_BASE_URL)
                .build();

    }

    public MapStyleService getMapStyleService() {
        return retrofit.create(MapStyleService.class);
    }
}
