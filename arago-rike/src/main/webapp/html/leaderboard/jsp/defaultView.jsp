<%@page import="de.arago.rike.util.PortraitHelper"%>
<%@page import="javax.portlet.RenderResponse"%>
<%@page import="de.arago.rike.data.ActivityLog"%>
<%@page import="de.arago.rike.util.TaskListFilter"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
<%@page import="de.arago.rike.data.TaskUser"%>
<%@page import="de.arago.rike.util.ViewHelper"%>
<%@page import="de.arago.rike.data.Task"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.List"%>
<%@ page import="java.util.Date" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<portlet:defineObjects />

<%
    try {
        List<TaskUser> list = (List<TaskUser>) portletSession.getAttribute("list");
        //User user = SecurityHelper.getUser(renderRequest.getRemoteUser());
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        <div class="ellipsis">
          Leaderboard
        </div>
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.leaderboard');" title="Help"><span class="icon">S</span></a> 
          <% if (renderRequest.getWindowState().equals(WindowState.MAXIMIZED)) {%>
          <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize"><span class="icon">%</span></a>
          <% } else {%>
          <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize"><span class="icon">%</span></a>
          <% }%>
        </span>
      </h1>
    </div>
    <div class="content nohead nofooter">

      <div class="tablescroll max" style="height:200px" id="<portlet:namespace />TableScroll">
        <table>
          <tbody>
            <tr>
              <% for (int k = 0; k < 2; k++) {%>
              <td style="border-bottom: 0px;">
                <table>
                  <tbody>
                    <%
                        if (k == 1) {
                    %>
                    <tr style="border-bottom: 1px solid black; height: 43px;"><td/><td/></tr>
                    <%                }
                        int i = 1;
                        for (TaskUser user : list) {
                            if (i % 2 != k) {

                                String klass = "";
                                long sum = user.getAccount() - user.getYesterday();

                                if (sum > 0) {
                                    klass = "place-down";
                                } else if (sum < 0) {
                                    klass = "place-up";
                                } else {
                                    klass = "place-unchanged";
                                }

                                int[] points = user.getEnded_tasks();
                                String str = "" + points[0];
                                for (int j = 1; j < points.length; j++) {
                                    str += "/" + points[j];
                                }
                    %>
                    <tr style="border-bottom: 1px solid black;">
                      <td style="width:70px">
                        <img style="width:70px; display:block" onerror="this.src = '<%= StringEscapeUtils.escapeHtml(PortraitHelper.getUserPortraitByEmail(user.getEmail()))%>';" src="/arago-rike/avatar/<%= StringEscapeUtils.escapeHtml(user.getEmail().replaceFirst("@.+$", "") + "-" + klass + ".png")%>" alt="" />
                      </td>
                      <td style="white-space:nowrap; width:50px; text-align:right" title="Current points">
                        <div style="font-weight:bold; text-shadow:1px 1px white"> <%= i%>. <%= ViewHelper.formatUser(user.getEmail())%></div><br />

                        <%=str%> <br />
                        <span class="info" title="Place last week: ">(<%=user.getYesterday()%>)</span>
                      </td>
                    </tr>
                    <%
                            }
                            ++i;
                        }
                    %>
                  </tbody>
                </table>
              </td>
              <% }%>
            </tr>
          </tbody>
        </table>

      </div>

      <script type="text/javascript">
            $(function()
            {
              $('#<portlet:namespace/>TableScroll').height($('#<portlet:namespace />Portlet').height() - 36).show();
            });
      </script>
    </div>
  </div>
</div>
<% 
    } catch (Throwable t) {
        out.write("Please Reload");
        t.printStackTrace(System.err);
    }
%>
