/**
 * Copyright (C) 2009-2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.fusesource.restygwt.client.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class FilterawareRetryingCallback implements FilterawareRequestCallback {

    /**
     * sets default number of retries
     */
    public static int DEFAULT_NUMBER_OF_RETRIES = 5;

    /**
     * Used by RetryingCallback
     */
    protected int numberOfRetries;

    /**
     * time to wait for reconnect upon failure
     */
    protected int gracePeriod = 1000;

    protected int currentRetryCounter = 0;

    protected final Method method;

    protected RequestCallback requestCallback;

    final protected List<CallbackFilter> callbackFilters = new ArrayList<CallbackFilter>();

    public FilterawareRetryingCallback(Method method) {
        // default number of retries is 5
        this(method, DEFAULT_NUMBER_OF_RETRIES);
    }

    public FilterawareRetryingCallback(Method method, int numberOfRetries) {
        this.method = method;
        // need to keep requestcallback here, as ``method.builder.getCallback()`` does not
        // give the same callback later on
        this.requestCallback = method.builder.getCallback();
        this.numberOfRetries = numberOfRetries;
    }

    @Override
    public final void onResponseReceived(Request request, Response response) {
        for (CallbackFilter f : callbackFilters) {
            if (f.canHandle(method.builder.getHTTPMethod(), response.getStatusCode())) {
                if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(FilterawareRetryingCallback.class.getName()).finest(
                            "apply filter " + f.getClass() + " to " + method);
                }
                requestCallback = f.filter(method, response, requestCallback);
            }
        }

        // TODO retrying, should be moved to filter as well...
        if (method.builder.getHTTPMethod().equals(RequestBuilder.GET.toString())
                && (response.getStatusCode() < 200 || response.getStatusCode() > 302)
                && (response.getStatusCode() < 400 || response.getStatusCode() > 404)) {
            handleErrorGracefully(request, response, requestCallback);
            return;
        }

        requestCallback.onResponseReceived(request, response);
    }

    /**
     * TODO when is this used ?
     */
    @Override
    public void onError(Request request, Throwable exception) {
        if (LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(FilterawareRetryingCallback.class.getName()).severe(
                    "call onError in " + this.getClass() + ". this should not happen...");
        }
        handleErrorGracefully(request, null, requestCallback);
    }

    public void handleErrorGracefully(Request request, Response response,
            RequestCallback requestCallback) {
        // error handling...:
        if (currentRetryCounter < numberOfRetries) {
            if (LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(FilterawareRetryingCallback.class.getName()).severe(
                        "error handling in progress for: " + method.builder.getHTTPMethod() + " "
                                + method.builder.getUrl());
            }

            currentRetryCounter++;

            Timer t = new Timer() {
                public void run() {
                    try {
                        method.builder.send();
                    } catch (RequestException ex) {
                        if (LogConfiguration.loggingIsEnabled()) {
                            Logger.getLogger(FilterawareRetryingCallback.class.getName()).severe(
                                    ex.getMessage());
                        }
                    }
                }
            };

            t.schedule(gracePeriod);
            gracePeriod = gracePeriod * 2;
        } else {
            if (LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(FilterawareRetryingCallback.class.getName()).severe(
                        "Request failed: " + method.builder.getHTTPMethod() + " "
                                + method.builder.getUrl() + " after " + currentRetryCounter
                                + " tries.");
            }

            if (null != request && null != response && null != requestCallback) {

                if (LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(FilterawareRetryingCallback.class.getName()).severe(
                            "Response " + response.getStatusCode() + " for "
                                    + method.builder.getHTTPMethod() + " "
                                    + method.builder.getUrl() + " after " + numberOfRetries
                                    + " retries.");
                }

                // got the original callback, call error here
                requestCallback.onResponseReceived(request, response);
            } else {
                // got no callback - well, goodbye
                if (Window.confirm("error")) {
                    // Super severe error.
                    // reload app or redirect.
                    // ===> this breaks the app but that's by intention.
                    Window.Location.reload();
                }
            }
        }
    }

    /**
     * put a filter in the "chain of responsibility" of all callbackfilters that will be
     * performed on callback passing.
     */
    public void addFilter(CallbackFilter filter) {
        callbackFilters.add(filter);
    }
}
