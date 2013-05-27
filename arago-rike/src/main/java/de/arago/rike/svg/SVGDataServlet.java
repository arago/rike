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
package de.arago.rike.svg;

import de.arago.rike.data.DataHelperRike;
import de.arago.rike.data.Dependency;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * this servlet serves svg graphs and status for mars nodes
 */
public class SVGDataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*  if (SecurityHelper.getUserFromRequest(request) == null)
          {
            response.setStatus(403);
            return;
          } */

        String action = request.getParameter("action");
        action = action == null || action.length() == 0 ? "graph" : action;

        if (action.equals("graph")) {
            serveGraph(request, response);
        } else if (action.equals("connect")) {
            connectTasks(request, response);
        } else if (action.equals("disconnect")) {
            disconnectTasks(request, response);
        } else {
            response.sendError(400);
        }
    }

    private static void serveGraph(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        response.setContentType("image/svg+xml; charset=utf-8");

        try {
            //addExpires(response);
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


            // TODO
            //filter.setArtifact(request.getParameter("artifact"));
            response.getWriter().append(SVGGraphCreator.getGraph(filter));
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private static void removeConnections(long from, long to) {
        DataHelperRike<Dependency> helper = new DataHelperRike<Dependency>(Dependency.class);
        Criteria crit = helper.filter();

        crit.add(Restrictions.eq("premise", from));
        crit.add(Restrictions.eq("sequel", to));

        for (Dependency dependency : helper.list(crit)) {
            helper.kill(dependency);
        }
    }

    private void connectTasks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject ret = new JSONObject();
        ret.put("error", null);

        try {
            long from = Long.valueOf(request.getParameter("from"), 10);
            long to = Long.valueOf(request.getParameter("to"), 10);

            removeConnections(from, to);

            DataHelperRike<Dependency> helper = new DataHelperRike<Dependency>(Dependency.class);

            Dependency dependency = new Dependency();
            dependency.setPremise(from);
            dependency.setSequel(to);

            helper.save(dependency);
        } catch (Throwable t) {
            log("error", t);
            ret.put("error", true);
        }

        response.setContentType("application/json;charset=utf-8");

        ret.writeJSONString(response.getWriter());
    }

    private void disconnectTasks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject ret = new JSONObject();
        ret.put("error", null);

        try {
            long from = Long.valueOf(request.getParameter("from"), 10);
            long to = Long.valueOf(request.getParameter("to"), 10);

            removeConnections(from, to);

        } catch (Throwable t) {
            log("error", t);
            ret.put("error", true);
        }

        response.setContentType("application/json;charset=utf-8");

        ret.writeJSONString(response.getWriter());
    }
}
