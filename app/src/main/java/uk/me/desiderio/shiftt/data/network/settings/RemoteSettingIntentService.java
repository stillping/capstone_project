package uk.me.desiderio.shiftt.data.network.settings;

import android.app.IntentService;
import android.content.Intent;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import dagger.android.AndroidInjection;
import uk.me.desiderio.shiftt.data.repository.RemoteSettingsRepository;

/**
 * Service to download app settings from server
 */
public class RemoteSettingIntentService extends IntentService {

    public static final String SETTINGS_SERVICE_NAME = "remote_settings_intent_service";

    @Inject
    RemoteSettingsRepository remoteSettingsRepository;

    public RemoteSettingIntentService() {
        super(SETTINGS_SERVICE_NAME);
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        remoteSettingsRepository.initSettings();
    }
}
