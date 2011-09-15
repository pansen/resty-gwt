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

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

/**
 * @author <a href="mailto:andi.balke@gmail.com">Andi</a>
 */
public interface ServiceWithResponseInterceptorDto extends RestService {
    @GET
    @Path("/responseInterceptorDto/{id}")
    public void getDtoIntercepted(@HeaderParam("X-Echo-Body") String response,
            @PathParam("id") String id, MethodCallback<SimpleResponseInterceptedDto> callback);

    @GET
    @Path("/responseInterceptorDto/{id}")
    @JsonDecoderRawInterceptor(JsonDecoderRawInterceptorTestCallback.class)
    public void getServiceIntercepted(@HeaderParam("X-Echo-Body") String response,
            @PathParam("id") String id, MethodCallback<SimpleDto> callback);
}