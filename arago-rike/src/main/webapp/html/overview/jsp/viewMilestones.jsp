<%@page import="de.arago.rike.commons.util.TaskListFilter"%>
<%@page import="de.arago.rike.commons.data.Milestone"%>
<%@page import="de.arago.rike.commons.util.TaskHelper"%>
<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.commons.util.ViewHelper"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
<%@page import="de.arago.rike.commons.data.Task.Status"%>
<%@page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@page import="de.arago.rike.commons.data.Task"%>
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
    TaskListFilter filter = (TaskListFilter) portletSession.getAttribute("taskListFilter");
    List<Milestone> milestones = (List) portletSession.getAttribute("milestones");
    Milestone currentMilestone = (Milestone) portletSession.getAttribute("milestone");
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        Overview: Milestones
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.overview');" title="Help" class="icon-question"></a>
          <% if (renderRequest.getWindowState().equals(WindowState.MAXIMIZED)) {%>
          <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize"><span class="icon">%</span></a>
          <% } else {%>
          <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize"><span class="icon">%</span></a>
          <% }%>
        </span>
      </h1>
      <div class="inner">
        <div class="right">
          <ul class="tabbar">
            <li><a href="<portlet:actionURL portletMode="view"/>&action=showTasks">Tasks</a></li>
            <li class="selected"><a href="#">Milestones</a></li>
            <li><a href="<portlet:actionURL portletMode="view"/>&action=showArtifacts">Artifacts</a></li>
          </ul>
        </div>
      </div>
    </div>
    <div class="content nofooter">
      <div id="<portlet:namespace />TableScroll">
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>Title</th>
            <th>URL</th>
          </tr>
        </thead>
        <tbody>
          <% for (Milestone stone : milestones) {%>

          <tr<%= currentMilestone != null && currentMilestone.getId().equals(stone.getId()) ? " class=\"selected\"" : ""%>>
            <td><%=stone.getId()%></td>
            <td><a href="<portlet:actionURL portletMode="view"/>&action=showMilestone&id=<%= stone.getId()%>"><%=StringEscapeUtils.escapeHtml(stone.getTitle())%></a></td>
            <td><%= ViewHelper.formatURL(stone.getUrl())%></td>
          </tr>

          <% }%>

        </tbody>
      </table>
      </div>
          
      <script type="text/javascript">
        <% if (currentMilestone != null) {%>
          $(function()
          {
            var el = $('#<portlet:namespace />TableScroll .selected').get(0);

            try
            {
              if (el) el.scrollIntoView();
            } catch(e) { ; };
          });
        <% }%>
      </script>

    </div>
  </div>
</div>
<% } catch (Throwable t) {

    out.write("Please Reload");
    t.printStackTrace(System.err);
    throw (t);
  }%>