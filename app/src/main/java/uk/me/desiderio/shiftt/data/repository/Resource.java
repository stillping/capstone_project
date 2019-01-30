package uk.me.desiderio.shiftt.data.repository;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

/**
 * Generic class that provides data with loading status
 *
 * Implemented following googlesamples/android-architecture-components
 *
 * @see <a href="https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/vo/Resource.kt">Resource.kt</a>
 */
public class Resource<T> {
    public static final int SUCCESS = 225;
    public static final int LOADING = 252;
    public static final int ERROR = 522;
    public static final int NO_CONNECTION = 552;

    @ResourceStatus
    public int status;
    public T data;
    public String message;

    private Resource(int status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    /**
     * returns data as {@link Resource} when no connection. It is assumed that data will be stale.
     */
    public static <T> Resource<T> noConnection(T data) {
        return new Resource<>(NO_CONNECTION, data, null);
    }

    /**
     * returns new data as {@link Resource} when success
     */
    public static <T> Resource<T> success(T data) {
        return new Resource<>(SUCCESS, data, null);
    }

    /**
     * returns message as {@link Resource} when request error. if data available, it is returned
     * assuming that this will be stale
     */
    public static <T> Resource<T> error(String msg, T data) {
        return new Resource<>(ERROR, data, msg);
    }

    /**
     * returns data as {@link Resource} while loading. It is assumed that data will be stale.
     */
    public static <T> Resource<T> loading(T data) {
        return new Resource<>(LOADING, data, null);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Resource) {
            Resource other = (Resource) obj;

            return status == other.status &&
                    data == other.data &&
                    isMessageEquals(message, other.message);
        }
        return false;
    }

    private boolean isMessageEquals(String message, String other) {
        if (message != null) {
            return message.equals(other);
        } else {
            return other == null;
        }
    }

    @IntDef({LOADING, SUCCESS, NO_CONNECTION, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResourceStatus {}
}
