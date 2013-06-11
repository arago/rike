/**
 * Copyright (c) 2010 arago AG, http://www.arago.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.arago.portlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The PortletDispatcher dispatches classbased work
 *
 * based on the class namespace the dispatcher will look for classes in
 * [namespace].[other].,
 *
 * e.g. class: de.arago.portlet.some.ThePortlet dispatchable: testEvent
 *
 * will lead to loading the class de.arago.portlet.some.other.TestEvent and
 * execute it
 */

public class PortletDispatcher<T> {
    private static final Logger logger = Logger.getLogger(PortletDispatcher.class.getName());

    /**
     * the namespace where all the classes are located
     */
    private String namespace;

    /**
     * cache
     */
    private Map<String, T> cache = new ConcurrentHashMap<String, T>();

    /**
     *
     * @param forWho
     *            the class for which to dispatch, e.g. the portlet
     * @param type
     *            the type of the dispatchable e.g. event or action, ...
     */
    protected PortletDispatcher(Class<?> forWho, String type) {
        namespace = forWho.getPackage().getName().concat(".").concat(type).concat(".");
    }

    protected T getDispatchable(final String name) {
        if (name == null || name.length() == 0)
            return null;

        String url = constructURL(name);

        if (cache.containsKey(url))
            return cache.get(url);

        try {
            return loadAndCache(url);
        } catch (ClassNotFoundException ignored) {
            return null;
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "failed to instantiate dispatchable", t);
        }

        return null;
    }

    private String constructURL(final String name) {
        String sanitized = name.replaceAll("[^a-zA-Z0-9]", "");
        sanitized = sanitized.substring(0, 1).toUpperCase().concat(sanitized.substring(1));

        return namespace.concat(sanitized);
    }

    /**
     * load a class and store in cache
     *
     * @param url
     *            the url as de.arago.[..]
     * @return the loaded instance
     * @throws Throwable
     *             if class could not be loaded or instantiated
     */
    private T loadAndCache(String url) throws Throwable {
        Class<? extends T> klass = (Class<? extends T>) Class.forName(url);

        T instance = klass.newInstance();
        cache.put(url, instance);

        return instance;
    }

    /**
     * Get the namespace from which classes are loaded
     *
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }
}
