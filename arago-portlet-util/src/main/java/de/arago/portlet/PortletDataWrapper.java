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

import java.util.Enumeration;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import de.arago.data.IDataWrapper;
import java.util.HashMap;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowState;

public class PortletDataWrapper implements IDataWrapper {
    private PortletRequest m_request;
    private PortletResponse m_response;
    private ActionResponse m_actionResponse = null;

    public PortletDataWrapper(ResourceRequest request, ResourceResponse response) {
        m_request = request;
        m_response = response;
    }

    public PortletDataWrapper(ActionRequest request, ActionResponse response) {
        m_request = request;
        m_response = response;
        m_actionResponse = response;
    }

    public PortletDataWrapper(RenderRequest request, RenderResponse response) {
        m_request = request;
        m_response = response;
    }

    public WindowState getWindowState() {
        if (m_request != null) return m_request.getWindowState();
        if (m_actionResponse != null) return m_actionResponse.getWindowState();

        return WindowState.NORMAL;
    }

    @Override
    public void setSessionAttribute(String key, Object value) {
        m_request.getPortletSession().setAttribute(key, value);
    }

    @Override
    public Enumeration<String> getSessionAttributeNames() {
        return m_request.getPortletSession().getAttributeNames();
    }

    @Override
    public Object getSessionAttribute(String key) {
        return m_request.getPortletSession().getAttribute(key);
    }

    @Override
    public void removeSessionAttribute(String key) {
        m_request.getPortletSession().removeAttribute(key);
    }

    @Override
    public Enumeration<String> getRequestAttributeNames() {
        return m_request.getParameterNames();
    }

    @Override
    public String getRequestAttribute(String key) {
        return m_request.getParameter(key);
    }

    @Override
    public void setRequestAttribute(String key, Object value) {
        m_request.setAttribute(key, value);
    }

    @Override
    public void setEvent(String key, HashMap<String, Object> event) {
        m_actionResponse.setEvent(key, event);
    }

    @Override
    public String getUser() {
        return m_request.getRemoteUser();
    }

}
