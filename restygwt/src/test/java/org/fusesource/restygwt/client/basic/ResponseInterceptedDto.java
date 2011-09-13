package org.fusesource.restygwt.client.basic;

import org.fusesource.restygwt.client.intercept.ResponseInterceptor;
import org.fusesource.restygwt.client.intercept.TestingInterceptorCallback;

/**
 * just a dummy dto that shows us our interception will work
 * 
 * @author abalke
 */
@ResponseInterceptor({TestingInterceptorCallback.class})
public class ResponseInterceptedDto {

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
