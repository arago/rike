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

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;

import de.arago.data.IDataProcessor;
import de.arago.data.IDataWrapper;
import de.arago.data.IEventWrapper;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

abstract public class AragoPortlet extends GenericPortlet implements IDataProcessor {
    private final JsonDispatcher jsonDispatcher;
    private final ActionDispatcher actionDispatcher;
    private final RenderDispatcher renderDispatcher;
    private final EventDispatcher eventDispatcher;
    /**
     * the request attribute from which to take the targetView
     */
    private final String targetViewKey;

    /**
     *
     * @param targetViewKey the key from which to take the targetView for doView
     */
    public AragoPortlet(String targetViewKey) {
        jsonDispatcher = new JsonDispatcher(this.getClass());
        actionDispatcher = new ActionDispatcher(this.getClass());
        renderDispatcher = new RenderDispatcher(this.getClass());
        eventDispatcher = new EventDispatcher(this.getClass());
        this.targetViewKey = targetViewKey;
    }

    public AragoPortlet() {
        this("targetView");
    }

    @Override
    public final void processAction(ActionRequest request,
                                    ActionResponse response) throws PortletException, IOException {
        action(new PortletDataWrapper(request, response));
    }

    @Override
    public final void processEvent(EventRequest request, EventResponse response)
    throws PortletException, IOException {
        event(new PortletEventWrapper(request, response));
    }

    @Override
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException {
        PortletDataWrapper data = new PortletDataWrapper(request, response);

        if (!"true".equals(data.getSessionAttribute("isSessionInitialized"))) {
            initSession(data);
            data.setSessionAttribute("isSessionInitialized", "true");
        }

        if(checkViewData(data))
            renderDispatcher.dispatchWithDefault(
                request.getPortletSession().getAttribute(targetViewKey), "defaultView",
                getPortletContext(), request, response);
    }

    protected boolean checkViewData(IDataWrapper data) {
        return true;
    }

    @Override
    public void doEdit(RenderRequest request, RenderResponse response)
    throws PortletException, IOException {
        renderDispatcher.dispatchWithDefault(
            request.getPortletSession().getAttribute(targetViewKey), "defaultEdit",
            getPortletContext(), request, response);
    }

    @Override
    public void doHelp(RenderRequest request, RenderResponse response)
    throws PortletException, java.io.IOException {

        renderDispatcher.dispatchWithDefault(
            request.getPortletSession().getAttribute(targetViewKey), "defaultHelp",
            getPortletContext(), request, response);
    }

    @Override
    public void action(IDataWrapper data) throws PortletException, IOException {
        actionDispatcher.dispatch(data.getRequestAttribute("action"), data);
    }

    @Override
    public void event(IEventWrapper event) throws PortletException, IOException {
        eventDispatcher.dispatch(event);
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        String type = request.getParameter("as");

        if (type != null && type.equals("json")) {
            jsonDispatcher.dispatch(request.getParameter("action"), request, response);
        } else {
            super.serveResource(request, response);
        }
    }

    /**
     * initialize a session for a user/portlet
     *
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    @Override
    public void initSession(IDataWrapper data) throws PortletException, IOException {}
}
