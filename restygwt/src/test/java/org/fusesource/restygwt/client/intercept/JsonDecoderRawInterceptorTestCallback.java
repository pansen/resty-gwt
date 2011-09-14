package org.fusesource.restygwt.client.intercept;

public class JsonDecoderRawInterceptorTestCallback implements
        JsonDecoderRawInterceptorCallback<ResponseInterceptedDto> {

    public static final JsonDecoderRawInterceptorCallback<ResponseInterceptedDto> INSTANCE =
            new JsonDecoderRawInterceptorTestCallback();

    /**
     * property for testing purpose
     */
    private String lastInput;

    /**
     * property for testing purpose
     */
    private Class<ResponseInterceptedDto> lastType;

    @Override
    public void intercept(final String input, Class<ResponseInterceptedDto> expectedType) {
        lastInput = input;
        lastType = expectedType;
    }

    public String getLastInput() {
        return lastInput;
    }

    public Class<ResponseInterceptedDto> getLastType() {
        return lastType;
    }
}
