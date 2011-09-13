package org.fusesource.restygwt.client.intercept;

import org.fusesource.restygwt.client.basic.ResponseInterceptedDto;

public class TestingInterceptorCallback implements InterceptorCallback<ResponseInterceptedDto> {

    public static final InterceptorCallback<ResponseInterceptedDto> INSTANCE =
            new TestingInterceptorCallback();

    /**
     * property for testing purpose
     */
    private String lastInput;

    /**
     * property for testing purpose
     */
    private Class<ResponseInterceptedDto> lastType;

    @Override
    public void intercept(String input, Class<ResponseInterceptedDto> expectedType) {

    }

    public String getLastInput() {
        return lastInput;
    }

    public Class<ResponseInterceptedDto> getLastType() {
        return lastType;
    }
}
