package org.fusesource.restygwt.client.intercept;

public class JsonDecoderRawInterceptorServiceTestCallback implements
        JsonDecoderRawInterceptorCallback<SimpleDto> {

    public static final JsonDecoderRawInterceptorCallback<SimpleDto> INSTANCE =
            new JsonDecoderRawInterceptorServiceTestCallback();

    /**
     * property for testing purpose
     */
    private String lastInput;

    /**
     * property for testing purpose
     */
    private Class<SimpleDto> lastType;

    @Override
    public void intercept(final String input, Class<SimpleDto> expectedType) {
        lastInput = input;
        lastType = expectedType;
    }

    public String getLastInput() {
        return lastInput;
    }

    public Class<SimpleDto> getLastType() {
        return lastType;
    }

    public void clear() {
        lastInput = null;
        lastType = null;
    }

}
