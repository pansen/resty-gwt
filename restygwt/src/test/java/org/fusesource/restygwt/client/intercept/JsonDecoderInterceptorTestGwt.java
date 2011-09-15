/**
 * Copyright (C) 2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.restygwt.client.intercept;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.client.cache.QueuableRuntimeCacheStorage;
import org.fusesource.restygwt.client.cache.QueueableCacheStorage;
import org.fusesource.restygwt.client.cache.ScopableQueueableCacheStorage;
import org.fusesource.restygwt.client.callback.CachingCallbackFilter;
import org.fusesource.restygwt.client.callback.CallbackFactory;
import org.fusesource.restygwt.client.callback.FilterawareRequestCallback;
import org.fusesource.restygwt.client.callback.FilterawareRetryingCallback;
import org.fusesource.restygwt.client.dispatcher.CachingDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.FilterawareRetryingDispatcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * test to check if {@link CachingCallbackFilter} {@link QueueableCacheStorage} and caching stuff in
 * complete works as expected
 * 
 * @author <a href="mailto:andi.balke@gmail.com">andi</a>
 */
public class JsonDecoderInterceptorTestGwt extends GWTTestCase {

    private ServiceWithResponseInterceptorDto service;

    // defines our response dto via echoservlet - this is unusual formatted with purpose to
    // to prove we're working on raw values
    private final String JSON_RESPONSE = "{     \"name\":\"foo\",  \"id\":\"U:ui\"          }";

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.Event";
    }

    /**
     * check the interceptor is working when a dto is annotated
     * 
     * @see SimpleResponseInterceptedDto
     */
    public void testGetAndInterceptRaw_OnDto() {

        // before the test, there is nothing (only working on the first test)
        String lastInput =
                ((JsonDecoderRawInterceptorTestCallback) JsonDecoderRawInterceptorTestCallback.INSTANCE)
                        .getLastInput();
        Class<SimpleResponseInterceptedDto> lastType =
                ((JsonDecoderRawInterceptorTestCallback) JsonDecoderRawInterceptorTestCallback.INSTANCE)
                        .getLastType();
        assertEquals(null, lastInput);
        assertEquals(null, lastType);

        service.getDtoIntercepted(JSON_RESPONSE, "U:ui",
                new MethodCallback<SimpleResponseInterceptedDto>() {

                    @Override
                    public void onSuccess(Method method, SimpleResponseInterceptedDto response) {
                        // when the first interaction was done, we need to have valid input here
                        String lastInput =
                                ((JsonDecoderRawInterceptorTestCallback) JsonDecoderRawInterceptorTestCallback.INSTANCE)
                                        .getLastInput();
                        Class<SimpleResponseInterceptedDto> lastType =
                                ((JsonDecoderRawInterceptorTestCallback) JsonDecoderRawInterceptorTestCallback.INSTANCE)
                                        .getLastType();

                        // the stringified version of this must be the same as above
                        assertEquals(JSON_RESPONSE, lastInput);
                        assertEquals(SimpleResponseInterceptedDto.class, lastType);
                        finishTest();
                    }

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        fail("on failure: " + exception.getMessage());
                    }
                });

        delayTestFinish(10000);
    }

    /**
     * check the interceptor is working when a dto is annotated
     * 
     * @see SimpleResponseInterceptedDto
     */
    public void testGetAndInterceptRaw_OnServiceMethod() {
        service.getServiceIntercepted(JSON_RESPONSE, "U:uu", new MethodCallback<SimpleDto>() {

            @Override
            public void onSuccess(Method method, SimpleDto response) {
                // when the first interaction was done, we need to have valid input here
                String lastInput =
                        ((JsonDecoderRawInterceptorTestCallback) JsonDecoderRawInterceptorTestCallback.INSTANCE)
                                .getLastInput();
                Class<SimpleResponseInterceptedDto> lastType =
                        ((JsonDecoderRawInterceptorTestCallback) JsonDecoderRawInterceptorTestCallback.INSTANCE)
                                .getLastType();

                // the stringified version of this must be the same as above
                assertEquals(JSON_RESPONSE, lastInput);
                assertEquals(SimpleResponseInterceptedDto.class, lastType);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("on failure: " + exception.getMessage());
            }
        });

        delayTestFinish(10000);
    }

    /**
     * check the interceptor is working when a dto is annotated
     * 
     * @see SimpleResponseInterceptedDto
     */
    public void testGetAndIntercept_OnDto() {
        service.getServiceIntercepted(JSON_RESPONSE, "U:ua", new MethodCallback<SimpleDto>() {

            @Override
            public void onSuccess(Method method, SimpleDto response) {
                // when the first interaction was done, we need to have valid input here
                JSONValue lastInput =
                        ((JsonDecoderInterceptorTestCallback) JsonDecoderInterceptorTestCallback.INSTANCE)
                                .getLastInput();
                Class<SimpleResponseInterceptedDto> lastType =
                        ((JsonDecoderInterceptorTestCallback) JsonDecoderInterceptorTestCallback.INSTANCE)
                                .getLastType();

                // the stringified version is not the same as its already preprocessed
                assertNotSame(JSON_RESPONSE, lastInput);
                // the interceptor gets a preparsed JSONObject
                assertNotNull(lastInput.isObject());
                // ...instead we have a version here that is just a ``toString`` result of
                // JSONObject
                assertEquals("{\"name\":\"foo\", \"id\":\"U:ui\"}", lastInput.toString());
                assertEquals(SimpleResponseInterceptedDto.class, lastType);
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("on failure: " + exception.getMessage());
            }
        });

        delayTestFinish(10000);
    }

    /**
     * usually this stuff is all done by gin in a real application. or at least there
     * would be a central place which is not the activity in the end.
     */
    @Override
    public void gwtSetUp() {
        /*
         * configure RESTY to use cache, usually done in gin
         */
        final ScopableQueueableCacheStorage cacheStorage = new QueuableRuntimeCacheStorage();
        final FilterawareDispatcher dispatcher = new FilterawareRetryingDispatcher();

        dispatcher.addFilter(new CachingDispatcherFilter(cacheStorage, new CallbackFactory() {
            public FilterawareRequestCallback createCallback(Method method) {
                final FilterawareRequestCallback retryingCallback =
                        new FilterawareRetryingCallback(method);

                return retryingCallback;
            }
        }));

        Defaults.setDispatcher(dispatcher);

        /*
         * setup the service, usually done in gin
         */
        Resource resource = new Resource(GWT.getModuleBaseURL());
        service = GWT.create(ServiceWithResponseInterceptorDto.class);
        ((RestServiceProxy) service).setResource(resource);
    }
}