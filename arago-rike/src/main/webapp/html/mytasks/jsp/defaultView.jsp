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
          <ul class="aui-tabview-list">
            <li class="aui-tab aui-state-default first aui-tab-active">
               <span class="aui-tab-content"> 
                   <a class="aui-tab-label">
                    <strong> In Progress</strong>
                    </a> 
              </span>
            </li>
            <li class="aui-tab aui-state-default">
                <span class="aui-tab-content">
                    <a class="aui-tab-label" href="<portlet:actionURL portletMode="view"/>&action=showEvaluated">Rated</a>
                </span>
            </li>
          </ul>
        </div>
      </div>

    </div>
    <div class="content">
        <table class="list">
          <thead>
            <tr>
              <th class="id shrink center" title="ID"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.ID.toString()%>">#</a></th>
              <th class="status shrink center" title="Status"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.STATUS.toString()%>" title="Status">?</a></th>
              <th class="prio shrink center" title="Priority"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.PRIORITY.toString()%>" title="Priority">Prio</a></th>
              <th class="name" title="Name"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.TITLE.toString()%>">Name</a></th>
            </tr>
          </thead>
          <tbody>
            <%

              for (Task task: tasks) {

            %>
            <tr>
              <td class="id shrink"><%= StringEscapeUtils.escapeHtml(task.getId().toString())%></td>
              <td class="status shrink"><span class="<%= ViewHelper.getTaskStatusColorClass(task)%>"></span></td>
              <td class="prio shrink"><%= task.getPriority()%></td>
              <td class="name"><a href="<portlet:actionURL portletMode="view" />&action=selectTask&id=<%= URLEncoder.encode(task.getId().toString(), "UTF-8")%>"><%= StringEscapeUtils.escapeHtml(task.getTitle())%></a></td>
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

        Time of all tasks: <%= size%>
      </div>  
    </div>
  </div>
</div>
<% } catch (Throwable t) {
    out.write("Please Reload");
    t.printStackTrace(System.err);
  }%>
