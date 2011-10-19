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

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.client.event.ModelChangeAnnotatedService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * @author <a href="mailto:andi.balke@gmail.com">andi</a>
 */
public class DontBeEvilTestGwt extends GWTTestCase {

    /**
     * fake response for the GET request (service.getItems)
     */
    private static final String responseGetBody = "  <{([{id:1},{id:2},{id:3}]";

    @Override
    public String getModuleName() {
        // load Event.gwt.xml with EchoServlet configured
        return "org.fusesource.restygwt.Event";
    }

    public void testFilterWorks() {
        /*
         * setup the service, usually done in gin
         */
        Resource resource = new Resource(GWT.getModuleBaseURL());
        final ModelChangeAnnotatedService service = GWT.create(ModelChangeAnnotatedService.class);
        ((RestServiceProxy) service).setResource(resource);

        service.getItems(responseGetBody, new MethodCallback<JSONValue>() {

            @Override
            public void onSuccess(Method method, JSONValue response) {
                JSONArray jsonArray = response.isArray();

                assertNotNull(jsonArray);

                assertEquals(3, jsonArray.size());

                // as we are here, although the response is invalid, all works well
                finishTest();
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                fail("method failed: " + exception.getMessage());
            }
        });
        // wait... we are in async testing...
        delayTestFinish(10000);
    }
}