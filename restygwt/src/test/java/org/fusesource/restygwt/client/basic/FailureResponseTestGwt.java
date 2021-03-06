/**
 * Copyright (C) 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.fusesource.restygwt.client.cache.ScopableQueueableCacheStorage;
import org.fusesource.restygwt.client.callback.CachingCallbackFilter;
import org.fusesource.restygwt.client.callback.CallbackFactory;
import org.fusesource.restygwt.client.callback.FilterawareRequestCallback;
import org.fusesource.restygwt.client.callback.FilterawareRetryingCallback;
import org.fusesource.restygwt.client.callback.ModelChangeCallbackFilter;
import org.fusesource.restygwt.client.dispatcher.CachingDispatcherFilter;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.FilterawareRetryingDispatcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * used to check if with conventional 4xx response errors (non 401)
 * the right error handling is done.
 * So that the onFailure contains of the {@link MethodCallback} is filled
 *  with the right {@link Method} -> should contain method.response
 *
 * @author <a href="mailto:tim@elbart.com">elbart</<a>
 */
public class FailureResponseTestGwt extends GWTTestCase {

    private ExampleService service;

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.FailureResponseTestGwt";
    }

    public void testFailureResponseOnPostTest() {
        /*
         *  setup the service, usually done in gin
         */
        Resource resource = new Resource(GWT.getModuleBaseURL() + "api/getendpoint");
        service = GWT.create(ExampleService.class);
        ((RestServiceProxy) service).setResource(resource);

        service.postExample(new MethodCallback<ExampleDto>() {

            @Override
            public void onSuccess(Method method, ExampleDto response) {
                fail("got onSuccess even if there should be an error response");
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                assertNotNull(method.getResponse());
                assertEquals(410, method.getResponse().getStatusCode());
                finishTest();

            }
        });

        delayTestFinish(10000);
    }

    public void testFailureResponseOnGetTest() {
        /*
         *  setup the service, usually done in gin
         */
        Resource resource = new Resource(GWT.getModuleBaseURL() + "api/postendpoint");
        service = GWT.create(ExampleService.class);
        ((RestServiceProxy) service).setResource(resource);

        service.getExampleDto(new MethodCallback<ExampleDto>() {

            @Override
            public void onSuccess(Method method, ExampleDto response) {
                fail("got onSuccess even if there should be an error response");
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                assertNotNull(method.getResponse());
                assertEquals(410, method.getResponse().getStatusCode());
                finishTest();

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

        dispatcher.addFilter(new CachingDispatcherFilter(
                cacheStorage,
                new CallbackFactory() {
                    public FilterawareRequestCallback createCallback(Method method) {
                        final FilterawareRequestCallback retryingCallback = new FilterawareRetryingCallback(
                                method, 2);

                        retryingCallback.addFilter(new CachingCallbackFilter(cacheStorage));
                        retryingCallback.addFilter(new ModelChangeCallbackFilter(eventBus));
                        return retryingCallback;
                    }
                }));

        Defaults.setDispatcher(dispatcher);
    }
}