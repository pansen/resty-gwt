package org.fusesource.restygwt.client.intercept;

import com.google.gwt.json.client.JSONValue;

public interface JsonDecoderInterceptorCallback<T> {
    /**
     * the one and only interception method.
     * 
     * you will get the raw response body, no interaction so far
     * 
     * @param input
     * @param expectedType class type for the final response
     * @return
     */
    void intercept(final JSONValue input, Class<T> expectedType);
}
