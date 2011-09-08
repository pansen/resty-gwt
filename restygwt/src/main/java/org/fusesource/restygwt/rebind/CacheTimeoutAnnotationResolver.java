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

package org.fusesource.restygwt.rebind;

import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.cache.CacheLifetime;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

/**
 * Implementation for an annotationparser which is responsible to put
 * annotation-data from {@link CacheLifetime} annotations to {@link Method} instances.
 * 
 * @author <a href="mailto:andi.balke@gmail.com">andi</<a>
 */
public class CacheTimeoutAnnotationResolver implements AnnotationResolver {

    @Override
    public Map<String, String[]> resolveAnnotation(TreeLogger logger, JClassType jClass,
            JMethod method, final String restMethod) throws UnableToCompleteException {
        CacheLifetime methodAnnot = method.getAnnotation(CacheLifetime.class);
        final Map<String, String[]> ret = new java.util.HashMap<String, String[]>();

        if (methodAnnot != null) {
            ret.put(CacheLifetime.CACHE_LIFETIME_KEY, new String[] {methodAnnot.value() + ""});
            return ret;
        }

        return ret;
    }
}
