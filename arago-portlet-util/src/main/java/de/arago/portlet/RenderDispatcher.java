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

import de.arago.debug.performance.Performance;
import java.io.IOException;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * The RenderDispatcher renders jsp Views
 *
 * based on the class simplename the dispatcher will look for action classes
 * in /prefix/[simplename]/suffix/[viewname].fileSuffix
 *
 * e.g.
 * class:  de.arago.portlet.some.ThePortlet
 * view: testView
 *
 * will lead to including the jsp: /prefix/theportlet/testView.jsp
 *
 */

public class RenderDispatcher {
    /**
     * the namespace where all the views are located
     */
    private final String viewNamespace;
    private final String className;


    private static String dirPrefix 	= "/html/";
    private static String dirSuffix 	= "/jsp/";
    private static String fileSuffix 	= ".jsp";

    /**
     * @param forWho The class on which to base the location of the jsp
     */
    public RenderDispatcher(Class<?> forWho) {
        viewNamespace = dirPrefix
                        .concat(forWho.getSimpleName().toLowerCase())
                        .concat(dirSuffix);

        className = forWho.getName() + ".";
    }

    /**
     * Dispatch a rendering request
     *
     * @param viewName the name of the view
     * @param context the context of the current portlet
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    public void dispatch(String viewName, PortletContext context, RenderRequest request, RenderResponse response) throws PortletException, IOException {
        long then = System.currentTimeMillis();

        String jsp = viewNamespace.concat(viewName.replaceAll("[^a-zA-Z0-9]", "")).concat(fileSuffix);

        context.getRequestDispatcher(jsp).include(request, response);

        Performance.timing("arago.portlet.dispatch.render", System.currentTimeMillis() - then);
        Performance.timing("arago.portlet.dispatch.render." + className + viewName, System.currentTimeMillis() - then);
    }

    /**
     * Dispatch a rendering request, if viewName == null then defaultName is rendered
     *
     * @param viewName the name of the view
     * @param defaultName the name of the defaultView
     * @param context the context of the current portlet
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    public void dispatchWithDefault(Object viewName, String defaultName, PortletContext context, RenderRequest request, RenderResponse response) throws PortletException, IOException {
        if (viewName == null || viewName.toString().length() == 0) {
            dispatch(defaultName, context, request, response);
        } else {
            dispatch(viewName.toString(), context, request, response);
        }
    }
}
