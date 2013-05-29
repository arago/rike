<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="java.util.Locale"%>
<%@page import="javax.portlet.RenderResponse"%>
<%@page import="de.arago.rike.data.ActivityLog"%>
<%@page import="de.arago.rike.util.TaskListFilter"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
<%@page import="de.arago.rike.data.TaskUser"%>
<%@page import="de.arago.rike.util.ViewHelper"%>
<%@page import="com.liferay.portal.service.UserLocalServiceUtil"%>
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
    UserService service = new JspUserService(renderRequest, portletSession);
    List<ActivityLog> logs = (List<ActivityLog>) portletSession.getAttribute("list");
    User user = SecurityHelper.getUser(renderRequest.getRemoteUser());

    ActivityLog first = logs.isEmpty() ? null : logs.get(0);
%>


<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- Headline -->
    <div class="head">
      <h1>
        Activity Log <a id="<portlet:namespace />NewUpdates" href="<portlet:actionURL />&action=reload" style="display:none; color:#cc0000"></a>
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.activitylog');" title="Help"><span class="icon">S</span></a>
          <% if(renderRequest.getWindowState().equals(WindowState.MAXIMIZED)){ %>
            <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize"><span class="icon">%</span></a>
          <% } else { %>
            <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize"><span class="icon">%</span></a>
          <% } %>
        </span>
      </h1>
    </div>
    <!-- content -->
    <div class="content nohead nofooter">

        <div id="<portlet:namespace />TableScroll">
          <table>
            <tbody>
              <%

                for (ActivityLog log: logs) {

              %>
              <tr>
                <td class="rike-activity-<%= log.getIcon() %>" style="width:10px"></td>
                <td>
                  <%= ViewHelper.formatUser(log.getUser())%>
                  <%-- the content has been escaped before putting it into the model --%>
                  <%= log.getContent() %> <br />
                  <span style="color:#999; font-size:0.9em"><%= service.formatHumanDate(log.getCreated())%></span>
                </td>
              </tr>
              <%
                }
              %>
            </tbody>
          </table>


        </div>

        <script type="text/javascript">
          $(function()
          {
            $('#<portlet:namespace/>TableScroll').height($('#<portlet:namespace />Portlet').height() - 36).show();
          });

          <% if (first != null) {%>
            $(function()
            {
              var check = function()
              {
            $.ajax
                ({
                  url: '<portlet:resourceURL />&as=json&action=pollUpdates&id=<%= URLEncoder.encode(first.getId().toString(), "UTF-8")%>',
                  dataType: 'json',
                  success: function(data)
                  {
                    if (data.error) return alert(data.error);

                if     (data.count)
                    {
                      $('#<portlet:namespace />NewUpdates').text('('+data.count + ' update'+(data.count == 1?'':'s')+')').show();
                    };
                  }
                });
              };

              window.setInterval(check, 60 * 1e3);
            });
          <% }%>
        </script>
    </div>
  </div>
</div>
    <% } catch (Throwable t) {
        out.write("Please Reload");
        t.printStackTrace(System.err);
  }%>