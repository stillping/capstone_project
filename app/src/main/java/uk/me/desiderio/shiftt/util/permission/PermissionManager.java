package uk.me.desiderio.shiftt.util.permission;

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

    public static final int PERMISSION_GRANTED = 11;
    public static final int PERMISSION_DENIED = 22;
    public static final int CAN_ASK_PERMISSION = 33;
    private final Activity activity;


    @Inject
    public PermissionManager(@ForActivity Activity activity) {
        this.activity = activity;
    }

    /**
     * Request application permissions define in string list provided as parameters
     */
    public void requestPermissions(String[] requiredPermisions, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                                          requiredPermisions,
                                          requestCode);
    }

    /**
     * returns permissions status of persmissions provided as parameter
     */
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

        for (String permission : permissions) {
            if (ActivityCompat
                    .shouldShowRequestPermissionRationale(activity, permission)) {
                shouldShowRationale = true;
                break;
            }
        }
        return shouldShowRationale;
    }

    private boolean hasSelfPermisionsGranted(String[] permissions) {
        boolean hasPersimisions = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                hasPersimisions = false;
                break;
            }
        }
        return hasPersimisions;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PERMISSION_GRANTED, CAN_ASK_PERMISSION, PERMISSION_DENIED})
    public @interface PermissionStatus {
    }
}
