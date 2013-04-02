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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract public class MapOptionFilter implements OptionFilter, Serializable {

    private final Map<String, Object> data = new HashMap<String, Object>();

    protected MapOptionFilter() {
        setDefaultOptions();
    }

    @Override
    public Object get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        return data.get(key);
    }

    @Override
    public Object getOr(final String key, final Object alternativeValue) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        Object value = data.get(key);

        return value == null ? alternativeValue : value;
    }

    @Override
    public void set(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        data.put(key, value);
    }

    @Override
    public Set<String> keySet() {
        return data.keySet();
    }
}
