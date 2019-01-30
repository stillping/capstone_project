package uk.me.desiderio.shiftt.data.network;

/**
 * Provides API response with error message when request returns as error.
 */
public class ApiErrorResponse<T> implements ApiResponse<T> {
    public final String message;

    public ApiErrorResponse(String message) {
        this.message = message;
    }
}
