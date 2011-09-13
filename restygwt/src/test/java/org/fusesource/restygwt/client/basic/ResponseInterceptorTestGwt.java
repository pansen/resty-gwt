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

package org.fusesource.restygwt.client.basic;

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
import org.fusesource.restygwt.client.callback.ModelChangeCallbackFilter;
import org.fusesource.restygwt.client.dispatcher.CachingDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.FilterawareRetryingDispatcher;
import org.fusesource.restygwt.client.intercept.TestingInterceptorCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * test to check if {@link CachingCallbackFilter} {@link QueueableCacheStorage} and caching stuff in
 * complete works as expected
 * 
 * @author <a href="mailto:andi.balke@gmail.com">andi</a>
 */
public class ResponseInterceptorTestGwt extends GWTTestCase {

    private ServiceWithResponseInterceptorDto service;

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.Event";
    }

    /**
     * check the interceptor is in position
     */
    public void testGetAndIntercept() {
        // defines our response dto via echoservlet
        final String JSON_RESPONSE = "{\"name:\"foo\", \"id\":\"U:ui\"}";

        // before the test, there is nothing
        String lastInput =
                ((TestingInterceptorCallback) TestingInterceptorCallback.INSTANCE).getLastInput();
        Class<ResponseInterceptedDto> lastType =
                ((TestingInterceptorCallback) TestingInterceptorCallback.INSTANCE).getLastType();

        service.get(JSON_RESPONSE, "U:ui", new MethodCallback<ResponseInterceptedDto>() {

            @Override
            public void onSuccess(Method method, ResponseInterceptedDto response) {
                // when the first interaction was done, we need to have valid input here
                String lastInput =
                        ((TestingInterceptorCallback) TestingInterceptorCallback.INSTANCE)
                                .getLastInput();
                Class<ResponseInterceptedDto> lastType =
                        ((TestingInterceptorCallback) TestingInterceptorCallback.INSTANCE)
                                .getLastType();

                assertEquals(JSON_RESPONSE, lastInput);
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
        final EventBus eventBus = new SimpleEventBus();
        final ScopableQueueableCacheStorage cacheStorage = new QueuableRuntimeCacheStorage();
        final FilterawareDispatcher dispatcher = new FilterawareRetryingDispatcher();

        dispatcher.addFilter(new CachingDispatcherFilter(cacheStorage, new CallbackFactory() {
            public FilterawareRequestCallback createCallback(Method method) {
                final FilterawareRequestCallback retryingCallback =
                        new FilterawareRetryingCallback(method);

                retryingCallback.addFilter(new CachingCallbackFilter(cacheStorage));
                retryingCallback.addFilter(new ModelChangeCallbackFilter(eventBus));
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