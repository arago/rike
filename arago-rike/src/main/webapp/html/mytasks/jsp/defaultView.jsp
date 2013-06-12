<%@page import="de.arago.rike.commons.util.TaskListFilter"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
<%@page import="de.arago.rike.commons.data.TaskUser"%>
<%@page import="de.arago.rike.commons.util.ViewHelper"%>
<%@page import="de.arago.rike.commons.data.Task"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html; charset=UTF-8" %> 
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects />

<%
  try {
    List<Task> tasks = (List<Task>) portletSession.getAttribute("list");
    User user = SecurityHelper.getUser(renderRequest.getRemoteUser());
    String currentUser = (String) portletSession.getAttribute("currentUser");
%>


<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        My Tasks
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.mytasks');" title="Help" class="icon-question"></a>
          <% if(renderRequest.getWindowState().equals(WindowState.MAXIMIZED)){ %>
            <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize" class="icon-resize-small"></a>
          <% } else { %>
            <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize" class="icon-resize-full"></a>
          <% } %>
        </span>
      </h1>
      <div class="inner">
        <div class="right">
          <form method="post" action="<portlet:actionURL portletMode="view" />">
            <input type="hidden" name="action" value="filterTasks" />
            <select name="user" onchange="this.form.submit();" style="width:60px;">
              <% for (TaskUser taskUser: ViewHelper.getAvailableUsers()) {%>
              <option <%= taskUser.getEmail().equals(currentUser) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(taskUser.getEmail())%>"><%= user.getEmailAddress().equals(taskUser.getEmail()) ? "Me" : StringEscapeUtils.escapeHtml(taskUser.getEmail())%></option>
              <% }%>
            </select>
          </form>
        </div>
        <div class="left">
          <ul class="tabbar">
            <li class="selected"><a href="#">In Progress</a></li>
            <li><a href="<portlet:actionURL portletMode="view"/>&action=showEvaluated">Rated</a></li>
          </ul>
        </div>
      </div>

    </div>
    <div class="content">
        <table>
          <thead>
            <tr>
              <th class="shrink center">#</th>
              <th class="shrink center" title="Status">?</th>
              <th class="shrink center" title="Priority">!</th>
              <th class="shrink center">Title</th>
            </tr>
          </thead>
          <tbody>
            <%

              for (Task task: tasks) {

            %>
            <tr>
              <td class="shrink center"><%= StringEscapeUtils.escapeHtml(task.getId().toString())%></td>
              <td class="shrink <%= ViewHelper.getTaskStatusColorClass(task)%>"></td>
              <td class="shrink <%= ViewHelper.getTaskPriorityColorClass(task)%>"></td>
              <td class="last shrink"><a href="<portlet:actionURL portletMode="view" />&action=selectTask&id=<%= URLEncoder.encode(task.getId().toString(), "UTF-8")%>"><%= StringEscapeUtils.escapeHtml(task.getTitle())%></a></td>
            </tr>
            <%
              }
            %>
          </tbody>
        </table>
    </div>     

    <div class="footer">
      <div class="inner">
        <%
          int size = 0;
          for (Task task: tasks) {
            size += task.getSizeEstimated();
          }
        %>

        Time of my tasks: <%= size%>
      </div>  
    </div>
  </div>
</div>
<% } catch (Throwable t) {
    out.write("Please Reload");
    t.printStackTrace(System.err);
  }%>
