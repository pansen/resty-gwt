/**
 * Copyright (C) 2011 the original author or authors.
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

import org.fusesource.restygwt.client.Method;

public class JsonDecoderRawInterceptorTestCallback implements JsonDecoderRawInterceptorCallback {

    public static final JsonDecoderRawInterceptorCallback INSTANCE =
            new JsonDecoderRawInterceptorTestCallback();

    /**
     * property for testing purpose
     */
    private String lastInput;

    /**
     * property for testing purpose
     */
    private Class lastType;

    /**
     * property for testing purpose
     */
    private String lastUrl;

    @Override
    public void intercept(final String input, final Method m, Class expectedType) {
        lastInput = input;
        lastType = expectedType;
        lastUrl = m.builder.getUrl();
    }

    public String getLastInput() {
        return lastInput;
    }

    public Class getLastType() {
        return lastType;
    }

    public String getLastUrl() {
        return lastUrl;
    }

    public void clear() {
        lastInput = null;
        lastType = null;
        lastUrl = null;
    }

}
