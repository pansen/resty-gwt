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

/**
 * just a dummy dto that shows us our interception will work
 * 
 * usually it might be useless to perform those pretty common interceptors at once,
 * but this is just for testing.
 * 
 * @author abalke
 */
@JsonDecoderRawInterceptor({JsonDecoderRawInterceptorTestCallback.class})
@JsonDecoderInterceptor({JsonDecoderInterceptorTestCallback.class})
public class SimpleResponseInterceptedDto {

    public String id;
    public String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
