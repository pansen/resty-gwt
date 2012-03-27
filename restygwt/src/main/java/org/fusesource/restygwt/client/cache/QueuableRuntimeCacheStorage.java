/**
 * Copyright (C) 2009-2010 the original author or authors. See the notice.md file distributed with
 * this work for additional information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.fusesource.restygwt.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;

public class QueuableRuntimeCacheStorage implements ScopableQueueableCacheStorage {

    /**
     * how long will a cachekey be allowed to exist
     */
    public int DEFAULT_LIFETIME_MS = 90 * 1000;

    /**
     * key <> value hashmap for holding cache values. nothing special here.
     *
     * invalidated values will be dropped by timer
     */
    protected final Map<CacheKey, Response> cache = new HashMap<CacheKey, Response>();

    /**
     * keep all callbacks to a cachekey that need to be performed when a response comes from the
     * server.
     */
    private final Map<CacheKey, List<RequestCallback>> pendingCallbacks =
            new HashMap<CacheKey, List<RequestCallback>>();

    /**
     * store timers to invalidate data after predefined timeout. each cachekey has one invalidation
     * timer.
     */
    private final List<Timer> timers = new ArrayList<Timer>();

    /**
     * reference from scope to cachekey
     */
    protected final Map<String, List<CacheKey>> scopeRef = new HashMap<String, List<CacheKey>>();

    public Response getResultOrReturnNull(CacheKey key) {
        return cache.get(key);
    }

    @Override
    public void putResult(final CacheKey key, final Response response) {
        putResult(key, response, "");
    }

    @Override
    public void putResult(final CacheKey key, final Response response, final int lifetime) {
        putResult(key, response, lifetime, "");
    }

    public void putResult(final CacheKey key, final Response response, final String scope) {
        putResult(key, response, DEFAULT_LIFETIME_MS, scope);
    }

    public void putResult(final CacheKey key, final Response response, final int lifetime,
            final String scope) {
        GWT.log("put result: " + key + " with lifetime: " + lifetime);
        final Timer t = new Timer() {
            public void run() {
                GWT.log("remove result: " + key);
                removeResult(key, scope);
            }
        };

        cache.put(key, response);

        getScope(scope).add(key);

        t.schedule(lifetime);
        timers.add(t);
    }

    /**
     * put a result to one or many scopes
     *
     * e.g. <code>
     *      putResult("foo_key", "bar_result", {"UserDto", "ProfileDto"}) {
     * </code>
     */
    @Override
    public void putResult(final CacheKey key, final Response response, final String[] scopes) {
        putResult(key, response, DEFAULT_LIFETIME_MS, scopes);
    }

    @Override
    public void putResult(CacheKey key, Response response, int lifetime, String[] scopes) {
        if (null == scopes) {
            putResult(key, response, lifetime);
            return;
        }

        int count = 0;

        for (String s : scopes) {
            if (0 == count) {
                putResult(key, response, lifetime, s);
            } else {
                getScope(s).add(key);
            }
            ++count;
        }
    }

    public void removeResult(CacheKey key) {
        removeResult(key, null);
    }

    public void removeResult(CacheKey key, final String scope) {
        try {
            if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                String s = scope;

                if (null == scope) {
                    s = "";
                }

                if (hasCallback(key)) {
                    Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).warning(
                            "cache-key " + key + " still has " + pendingCallbacks.get(key).size()
                                    + " callbacks, but will be removed now.");
                }
                Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).finer(
                        "removing cache-key " + key + " from scope \"" + s + "\"");
            }

            try {
                timers.remove(this);
            } catch (Exception ignored) {
            }

            if (null != scope) {
                List<CacheKey> currentScope = scopeRef.get(scope);

                currentScope.remove(key);
            }

            cache.remove(key);
        } catch (Exception ex) {
            if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).severe(
                        ex.getMessage());
            }
        }
    }

    @Override
    public boolean hasCallback(final CacheKey k) {
        return pendingCallbacks.containsKey(k);
    }

    @Override
    public void addCallback(final CacheKey k, final RequestCallback rc) {
        // init value of key if not there...
        if (!pendingCallbacks.containsKey(k)) {
            pendingCallbacks.put(k, new ArrayList<RequestCallback>());
        }

        pendingCallbacks.get(k).add(rc);
    }

    @Override
    public List<RequestCallback> removeCallbacks(final CacheKey k) {
        return pendingCallbacks.remove(k);
    }

    /**
     * purge all
     */
    @Override
    public void purge() {
        if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).finer(
                    "will remove " + cache.size() + " elements from cache.");
        }
        cache.clear();
        if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).finer(
                    "remove " + timers.size() + " timers from list.");
        }
        for (Timer t : timers) {
            t.cancel();
        }
        timers.clear();
    }

    /**
     * purge only a scope
     */
    @Override
    public void purge(final String scope) {
        List<CacheKey> currentScope = scopeRef.get(scope);

        if (null != currentScope) {
            List<CacheKey> tmpScope = new ArrayList<CacheKey>();

            if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).fine(
                        "will remove " + currentScope.size() + " elements from cache, scope: "
                                + scope);
            }

            for (CacheKey k : currentScope) {
                if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
                    Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).finer(
                            "remove " + k + " from cache.");
                }
                tmpScope.add(k);
            }
            for (CacheKey k : tmpScope) {
                removeResult(k, scope);
            }
            return;
        }

        if (GWT.isClient() && LogConfiguration.loggingIsEnabled()) {
            Logger.getLogger(QueuableRuntimeCacheStorage.class.getName()).fine(
                    "nothing to be removed for scope " + scope);
        }
    }

    /**
     * helper method to access a scope
     *
     * @param scope
     * @return
     */
    protected List<CacheKey> getScope(final String scope) {
        List<CacheKey> currentScope = scopeRef.get(scope);

        if (null == currentScope) {
            scopeRef.put(scope, new ArrayList<CacheKey>());
            currentScope = scopeRef.get(scope);
        }

        return currentScope;
    }
}
