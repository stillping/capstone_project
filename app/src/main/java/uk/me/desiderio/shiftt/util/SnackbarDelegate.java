package uk.me.desiderio.shiftt.util;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import uk.me.desiderio.shiftt.R;

/**
 * creates and show {@link Snackbar} based on parameters provided
 * <p>
 * There are 3 main types: Connected, No connected and Error. Snackbar will show related massage
 * and behaviour depending on type.
 */
public class SnackbarDelegate {

    public static final int CONNECTED = 3;
    public static final int NO_CONNECTED = 2;
    public static final int ERROR = 1;

    private Snackbar snackbar;
    private final View anchorView;
    @StringRes
    private final int messageSuffixResId;

    public SnackbarDelegate(@StringRes int messageSuffixResId,
                            View anchorView) {
        this.anchorView = anchorView;
        this.messageSuffixResId = messageSuffixResId;
    }

    /**
     * returns instance of {@link Snackbar}
     * the bar is customised depending on the connectivity state provided as parameter
     */
    @NonNull
    private Snackbar getSnackbar(@SnackBarState int snackBarState,
                                 @NonNull View view,
                                 @Nullable View.OnClickListener listener) {
        // wip inject info message for errors (?)
        Context context = view.getContext();
        String message;
        int duration;
        String buttonLabel = null;

        switch (snackBarState) {
            case CONNECTED:
                String suffix = context.getString(messageSuffixResId);
                message = context.getString(R.string.snackbar_connected_message, suffix);
                duration = Snackbar.LENGTH_INDEFINITE;
                buttonLabel = context.getString(R.string.snackbar_connect_button_label);
                break;
            case NO_CONNECTED:
                message = context.getString(R.string.snackbar_no_connection_message);
                duration = Snackbar.LENGTH_INDEFINITE;
                break;
            case ERROR:
                buttonLabel = context.getString(R.string.snackbar_error_button_label);
                message = context.getString(R.string.snackbar_error_message);
                duration = Snackbar.LENGTH_INDEFINITE;
                break;
            default:
                message = "";
                duration = Snackbar.LENGTH_INDEFINITE;
        }

        final Snackbar snackbar = Snackbar
                .make(view, message, duration);


        if (buttonLabel != null && listener != null) {
            snackbar.setAction(buttonLabel, listener);
        }

        return snackbar;
    }

    public void showSnackbar(@SnackBarState int snackBarState, View.OnClickListener listener) {
        snackbar = getSnackbar(snackBarState, anchorView, listener);
        snackbar.show();
    }

    public void hideSnackbar() {
        if (snackbar != null && snackbar.isShown()) {
            // branch for Resource.LOADING || Resource.SUCCESS
            snackbar.dismiss();
        }
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONNECTED, NO_CONNECTED, ERROR})
    public @interface SnackBarState {
    }
}
