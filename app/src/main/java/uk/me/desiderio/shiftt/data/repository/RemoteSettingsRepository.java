package uk.me.desiderio.shiftt.data.repository;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.MutableLiveData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import uk.me.desiderio.shiftt.data.network.settings.MapSettingsApiClient;
import uk.me.desiderio.shiftt.data.network.settings.RemoteSettingIntentService;

/**
 * loads application settings from the server
 */
@Singleton
public class RemoteSettingsRepository {

    private final Context context;
    private MutableLiveData<String> mapSettingsLiveData;

    @Inject
    public RemoteSettingsRepository(Context context) {
        this.context = context;
        mapSettingsLiveData = new MutableLiveData<>();
        startService();
    }

    public MutableLiveData<String> getMapSettings() {
        return mapSettingsLiveData;
    }

    private void startService() {
        Intent intent = new Intent(context, RemoteSettingIntentService.class);
        context.startService(intent);
    }


    public void initSettings() {
        MapSettingsApiClient client = new MapSettingsApiClient();
        Call<ResponseBody> call = client.getMapStyleService().mapStyle();

        try {
            Response<ResponseBody> response = call.execute();
            if (response.body() != null) {
                mapSettingsLiveData.postValue(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
