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
package de.arago.rike.report;

import de.arago.portlet.util.SecurityHelper;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.arago.rike.commons.data.ChartTimeSeries;


/**
 * this servlet serves svg graphs and status for mars nodes
 */
public class ReportDataServlet extends HttpServlet {

    private final int CACHE_DURATION_IN_SECONDS = 600;
    private final long CACHE_DURATION_IN_MS = CACHE_DURATION_IN_SECONDS * 1000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*if (SecurityHelper.getUserFromRequest(request) == null)
        {
          response.setStatus(403);
          return;
        } */

        String action = request.getParameter("action");
        action = action == null || action.length() == 0 ? "graph" : action;

        String milestone = request.getParameter("milestone");
        String type			 = request.getParameter("type");

        if (action.equals("graph")) {
            response.setContentType("text/plain; charset=utf-8");

            String result = ChartTimeSeries.toPrettyJSON(type, milestone);

            if (result == null || result.isEmpty()) {
                response.sendError(400);
            } else {
                addExpires(response);
                ServletOutputStream out = response.getOutputStream();
                out.print(result);
                out.close();
            }
        } else {
            response.sendError(400);
        }
    }

    private void addExpires(HttpServletResponse response) {
        long now = System.currentTimeMillis();

        response.addHeader("Cache-Control", "max-age=" + CACHE_DURATION_IN_SECONDS);
        response.setDateHeader("Last-Modified", now);
        response.setDateHeader("Expires", now + CACHE_DURATION_IN_MS);
    }


}
