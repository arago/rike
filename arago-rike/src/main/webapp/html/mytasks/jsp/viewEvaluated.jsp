<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.commons.data.TaskUser"%>
<%@page import="de.arago.rike.commons.util.TaskListFilter"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
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
    UserService service = new JspUserService(renderRequest, portletSession);
    List<Task> tasks = (List<Task>) portletSession.getAttribute("list");
    String currentUser = (String) portletSession.getAttribute("currentUser");
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        My Tasks
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.mytasks');" title="Help"><span class="icon">S</span></a> 
          <% if(renderRequest.getWindowState().equals(WindowState.MAXIMIZED)){ %>
            <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize"><span class="icon">%</span></a>
          <% } else { %>
            <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize"><span class="icon">%</span></a>
          <% } %>
        </span>
      </h1>
      <div class="inner">
        <div class="right">
          <form method="post" action="<portlet:actionURL portletMode="view" />">
            <input type="hidden" name="action" value="filterTasks" />
            <select name="user" onchange="this.form.submit();" style="width:60px">
              <% for (TaskUser taskUser: ViewHelper.getAvailableUsers()) {%>
              <option <%= taskUser.getEmail().equals(currentUser) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(taskUser.getEmail())%>"><%= service.getEmail().equals(taskUser.getEmail()) ? "Me" : StringEscapeUtils.escapeHtml(taskUser.getEmail())%></option>
              <% }%>
            </select>
          </form>
        </div>
        <div class="left">
          <ul class="tabbar">
            <li><a href="<portlet:actionURL portletMode="view"/>&action=showInProgress">In Progress</a></li>
            <li class="selected"><a href="#">Rated</a></li>
          </ul>
        </div>
      </div>

    </div>
    <div class="content nofooter">
        <table>
          <thead>
            <tr>
              <th class="shrink center">#</th>
              <th class="shrink center">Status</th>
              <th class="shrink center">Title</th>
            </tr>
          </thead>
          <tbody>
            <%

              for (Task task: tasks) {

            %>
            <tr>
              <td class="shrink center"><%= StringEscapeUtils.escapeHtml(task.getId().toString())%></td>
              <td class="<%= ViewHelper.getTaskStatusColorClass(task)%>"></td>
              <td class="last shrink"><a href="<portlet:actionURL portletMode="view" />&action=selectTask&id=<%= URLEncoder.encode(task.getId().toString(), "UTF-8")%>"><%= StringEscapeUtils.escapeHtml(task.getTitle())%></a></td>
            </tr>
            <%
              }
            %>
          </tbody>
        </table>
    </div>     

  </div>
</div>

<% } catch (Throwable t) {
    out.write("Please Reload");
    t.printStackTrace(System.err);
  }%>
