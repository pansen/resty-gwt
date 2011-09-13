package org.fusesource.restygwt.client.intercept;

import org.fusesource.restygwt.client.basic.ResponseInterceptedDto;

import com.google.gwt.json.client.JSONValue;

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
    public void intercept(JSONValue input, Class<ResponseInterceptedDto> expectedType) {
        lastInput = input.toString();
        lastType = expectedType;
    }

    public String getLastInput() {
        return lastInput;
    }

    public Class<ResponseInterceptedDto> getLastType() {
        return lastType;
    }
}
