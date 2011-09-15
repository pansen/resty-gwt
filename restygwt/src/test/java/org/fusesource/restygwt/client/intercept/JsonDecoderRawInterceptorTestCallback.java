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

public class JsonDecoderRawInterceptorTestCallback implements
        JsonDecoderRawInterceptorCallback<SimpleResponseInterceptedDto> {

    public static final JsonDecoderRawInterceptorCallback<SimpleResponseInterceptedDto> INSTANCE =
            new JsonDecoderRawInterceptorTestCallback();

    /**
     * property for testing purpose
     */
    private String lastInput;

    /**
     * property for testing purpose
     */
    private Class<SimpleResponseInterceptedDto> lastType;

    @Override
    public void intercept(final String input, Class<SimpleResponseInterceptedDto> expectedType) {
        lastInput = input;
        lastType = expectedType;
    }

    public String getLastInput() {
        return lastInput;
    }

    public Class<SimpleResponseInterceptedDto> getLastType() {
        return lastType;
    }

    public void clear() {
        lastInput = null;
        lastType = null;
    }


}
