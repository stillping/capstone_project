package uk.me.desiderio.shiftt.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import uk.me.desiderio.shiftt.di.ForActivity;

/**
 * Utility class to handle application permissions
 */

public class PermissionManager {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PERMISSION_GRANTED, CAN_ASK_PERMISSION, PERMISSION_DENIED})
    public @interface PermissionStatus { }
    public static final int PERMISSION_GRANTED = 11;
    public static final int PERMISSION_DENIED = 22;
    public static final int CAN_ASK_PERMISSION = 33;


    private Activity activity;

    @Inject
    public PermissionManager(@ForActivity Activity activity) {
        this.activity = activity;
    }

    public void requestPermissions(String[] requiredPermisions, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                                          requiredPermisions,
                                          requestCode);
    }

    public int getPermissionStatus(@NonNull String[] permissions) {

        if (hasSelfPermisionsGranted(permissions)) {
            return PERMISSION_GRANTED;
        } else if (shouldShowRequestPermissionRationale(permissions)) {
            return CAN_ASK_PERMISSION;
        } else {
            return PERMISSION_DENIED;
        }
    }

    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        boolean shouldShowRationale = false;

        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat
                    .shouldShowRequestPermissionRationale(activity, permissions[i])) {
                shouldShowRationale = true;
                break;
            }
        }
        return shouldShowRationale;
    }

    private boolean hasSelfPermisionsGranted(String[] permissions) {
        boolean hasPersimisions = true;

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) !=
                    PackageManager.PERMISSION_GRANTED) {
                hasPersimisions = false;
                break;
            }
        }
        return hasPersimisions;
    }
}
