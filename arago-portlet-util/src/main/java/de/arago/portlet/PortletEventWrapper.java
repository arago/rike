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

import de.arago.data.IEventWrapper;
import java.util.Map;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;

public class PortletEventWrapper implements IEventWrapper {

    private EventRequest request;
    private EventResponse response;

    public PortletEventWrapper(EventRequest request, EventResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public String getName() {
        return request.getEvent().getQName().toString();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getEventAttribute(String key) {
        return ((Map) request.getEvent().getValue()).get(key);
    }

    @Override
    public void setEventAttribute(String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getSessionAttribute(String key) {
        return request.getPortletSession().getAttribute(key);
    }

    @Override
    public void setSessionAttribute(String key, Object value) {
        request.getPortletSession().setAttribute(key, value);
    }

    @Override
    public void removeSessionAttribute(String key) {
        request.getPortletSession().removeAttribute(key);
    }

    @Override
    public String getUser() {
        return request.getRemoteUser();
    }
}
