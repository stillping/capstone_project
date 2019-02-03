package uk.me.desiderio.shiftt.data.network;

import com.google.android.gms.common.util.Strings;

/**
 * Util factory class to provide {@link ApiResponse}. The response type will be determined by
 * parameter provided
 */
@SuppressWarnings("unchecked")
public class ApiResponseFactory<T> {

    public static <T> ApiResponse<T> getApiResponse(retrofit2.Response response) {
        if (response.isSuccessful()) {
            Object body = response.body();
            if (body == null || response.code() == 202) {
                return new ApiEmptyResponse();
            } else {
                return new ApiSuccessResponse(body, response.headers());
            }
        } else {
            String msg = response.errorBody().toString();
            String errorMsg = (msg.isEmpty()) ? response.message() : msg;
            return new ApiErrorResponse(errorMsg);
        }
    }

    public static <T> ApiResponse<T> getApiResponse(Throwable error) {
        String msg = (!Strings.isEmptyOrWhitespace(error.getMessage()))
                ? error.getMessage()
                : "Unknown Error";

        return new ApiErrorResponse(msg);
    }
}
