package org.fusesource.restygwt.client.intercept;

import org.fusesource.restygwt.client.Method;

public interface JsonDecoderRawInterceptorCallback {
    /**
     * the one and only interception method.
     * 
     * you will get the raw response body, no interaction so far
     * 
     * @param input
     * @param expectedType class type for the final response
     * @return
     */
    void intercept(final String input, final Method method, Class expectedType);
}
