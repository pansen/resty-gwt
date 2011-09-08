package org.fusesource.restygwt.client.basic;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.cache.CacheLifetime;
import org.fusesource.restygwt.client.cache.Domain;

/**
 * Supersimple example service for testing...
 * 
 * @author <a href="mailto:mail@raphaelbauer.com">rEyez</<a>
 * @author <a href="mailto:andi.balke@gmail.com">andi</<a>
 */
@Domain({ExampleDto.class})
public interface ExampleService extends RestService {
    @GET
    public void getExampleDto(MethodCallback<ExampleDto> callback);

    @POST
    public void postExample(MethodCallback<ExampleDto> callback);

    @GET
    @CacheLifetime(1 * 1500)
    public void getWithCachingTimeouConfigured(MethodCallback<ExampleDto> callback);
}
