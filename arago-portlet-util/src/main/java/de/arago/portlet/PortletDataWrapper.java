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

import de.arago.data.IDataWrapper;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ValidatorException;
import javax.portlet.WindowState;

public class PortletDataWrapper implements IDataWrapper {
    private PortletRequest request;
    private PortletResponse response;
    private ActionResponse actionResponse = null;

    public PortletDataWrapper(ResourceRequest request, ResourceResponse response) {
        this.request = request;
        this.response = response;
    }

    public PortletDataWrapper(ActionRequest request, ActionResponse response) {
        this.request = request;
        this.response = response;
        actionResponse = response;
    }

    public PortletDataWrapper(RenderRequest request, RenderResponse response) {
        this.request = request;
        this.response = response;
    }

    public WindowState getWindowState() {
        if (request != null) return request.getWindowState();
        if (actionResponse != null) return actionResponse.getWindowState();

        return WindowState.NORMAL;
    }

    @Override
    public void setSessionAttribute(String key, Object value) {
        request.getPortletSession().setAttribute(key, value);
    }

    @Override
    public Enumeration<String> getSessionAttributeNames() {
        return request.getPortletSession().getAttributeNames();
    }

    @Override
    public Object getSessionAttribute(String key) {
        return request.getPortletSession().getAttribute(key);
    }

    @Override
    public void removeSessionAttribute(String key) {
        request.getPortletSession().removeAttribute(key);
    }

    @Override
    public Enumeration<String> getRequestAttributeNames() {
        return request.getParameterNames();
    }

    @Override
    public String getRequestAttribute(String key) {
        return request.getParameter(key);
    }

    @Override
    public void setRequestAttribute(String key, Object value) {
        request.setAttribute(key, value);
    }

    @Override
    public String getPersistentPreference(String key, String def){
        return request.getPreferences().getValue(key, def);
    }
    
    @Override
    public void setPersistentPreference(String key,String value){
        try{
            request.getPreferences().setValue(key, value);
        } catch (    ReadOnlyException e) {
            Logger.getLogger(PortletDataWrapper.class.getName()).log(Level.SEVERE, null, e);
        }
        try {
            request.getPreferences().store();
        } catch (IOException ex) {
            Logger.getLogger(PortletDataWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ValidatorException ex) {
            Logger.getLogger(PortletDataWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void setEvent(String key, HashMap<String, Object> event) {
        actionResponse.setEvent(key, event);
    }

    @Override
    public String getUser() {
        return request.getRemoteUser();
    }

  @Override
  public Object getRequestData(String name)
  {
    return null;
  }

}
