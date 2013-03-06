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
import java.util.Set;

/**
 * FilterOptions for portlets
 *
 */
public interface OptionFilter extends Serializable {

    /**
     * set the default options, you should call this in your
     * constructor
     */
    public void setDefaultOptions();

    /**
     * Get a filter option by name
     * @param key
     * @return
     */
    public Object get(final String key);

    /**
     * if the option is not set, alternativeValue will be returned
     * @param key
     * @param alternativeValue
     * @return
     */
    public Object getOr(final String key, final Object alternativeValue);

    /**
     * set a filter option
     *
     * @param key name of the option
     * @param value value of the option
     * @return
     */
    public void set(final String key, final Object value);

    /**
     * Get all available keys configured in the filter
     * @return
     */
    public Set<String> keySet();
}
