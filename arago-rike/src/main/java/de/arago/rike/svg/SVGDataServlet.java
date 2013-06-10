/**
 * Copyright (c) 2010 arago AG, http://www.arago.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.arago.rike.svg;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * this servlet serves svg graphs and status for mars nodes
 */
public class SVGDataServlet extends HttpServlet {
    private static final int CACHE_DURATION_IN_SECONDS = 3600;
    private static final long CACHE_DURATION_IN_MS = CACHE_DURATION_IN_SECONDS * 1000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("image/svg+xml; charset=utf-8");

        try {
            addExpires(response);
            SvgFilter filter = new SvgFilter();

            try {
                filter.setArtifact(Long.valueOf(request.getParameter("artifact")));
            } catch (NumberFormatException ignored) {
            }

            try {
                filter.setMilestone(request.getParameter("milestone"));
            } catch (NumberFormatException ignored) {
            }

            filter.setUser(request.getParameter("user"));

            response.getWriter().append(SVGGraphCreator.getGraph(filter));
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    public static void addExpires(HttpServletResponse response) {
        long now = System.currentTimeMillis();

        response.addHeader("Cache-Control", "max-age=" + CACHE_DURATION_IN_SECONDS);
        response.setDateHeader("Last-Modified", now);
        response.setDateHeader("Expires", now + CACHE_DURATION_IN_MS);
    }
}
