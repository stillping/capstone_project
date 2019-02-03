package uk.me.desiderio.shiftt.data.network;

import okhttp3.Headers;

/**
 * Provides API response with response's body and header when request returns as success
 */
public class ApiSuccessResponse<T> implements ApiResponse<T> {

    public T body;
    public final Headers headers;

    public ApiSuccessResponse(T body, Headers headers) {
        this.body = body;
        this.headers = headers;
    }
}
