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
package de.arago.data;

import java.util.Enumeration;
import java.util.HashMap;

public interface IDataWrapper {
    public void setSessionAttribute(String key,Object value);
    public Enumeration<java.lang.String> getSessionAttributeNames();
    public Object getSessionAttribute(String key);
    public void removeSessionAttribute(String key);

    public Enumeration<java.lang.String> getRequestAttributeNames();
    public String getRequestAttribute(String key);
    public void setRequestAttribute(String key,Object value);

    public String getPersistentPreference(String key, String def);
    public void setPersistentPreference(String key,String value);
    
    public void setEvent(String key, HashMap<String, Object> event);
    public String getUser();
    public Object getRequestData(String name);
}
