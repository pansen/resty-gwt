package org.fusesource.restygwt.client.intercept;

import java.util.Map;

public class JsonDecoderRawInterceptorServiceTestCallbackGeneric implements
        JsonDecoderRawInterceptorCallback<Map> {

    public static final JsonDecoderRawInterceptorCallback<Map> INSTANCE =
            new JsonDecoderRawInterceptorServiceTestCallbackGeneric();

    /**
     * property for testing purpose
     */
    private String lastInput;

    /**
     * property for testing purpose
     */
    private Class<Map> lastType;

    @Override
    public void intercept(final String input, Class<Map> expectedType) {
        lastInput = input;
        lastType = expectedType;
    }

    public String getLastInput() {
        return lastInput;
    }

    public Class<Map> getLastType() {
        return lastType;
    }

    public void clear() {
        lastInput = null;
        lastType = null;
    }

}
