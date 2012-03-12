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

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public class FilterawareRequestCallbackImpl implements FilterawareRequestCallback {

    protected final Method method;

    protected RequestCallback requestCallback;

    final protected List<CallbackFilter> callbackFilters = new ArrayList<CallbackFilter>();

    public FilterawareRequestCallbackImpl(Method method) {
        this.method = method;
        // need to keep requestcallback here, as ``method.builder.getCallback()`` does not
        // give the same callback later on
        this.requestCallback = method.builder.getCallback();
    }

    @Override
    public final void onResponseReceived(Request request, Response response) {
        for (CallbackFilter f : callbackFilters) {
            if (f.canHandle(method.builder.getHTTPMethod(), response.getStatusCode())) {
                if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(FilterawareRequestCallbackImpl.class.getName()).finest(
                            "apply filter " + f.getClass() + " to " + method);
                }
                requestCallback = f.filter(method, response, requestCallback);
            }
        }
        requestCallback.onResponseReceived(request, response);
    }

    /**
     * put a filter in the "chain of responsibility" of all callbackfilters that will be
     * performed on callback passing.
     */
    public void addFilter(CallbackFilter filter) {
        callbackFilters.add(filter);
    }

    @Override
    public void onError(Request request, Throwable exception) {
        requestCallback.onError(request, exception);
    }
}
