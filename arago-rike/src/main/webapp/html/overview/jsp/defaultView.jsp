<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.data.Artifact"%>
<%@page import="de.arago.rike.data.Milestone"%>
<%@page import="de.arago.rike.data.TaskUser"%>
<%@page import="de.arago.rike.util.TaskListFilter"%>
<%@page import="de.arago.rike.util.ViewHelper"%>
<%@page import="de.arago.rike.data.Task"%>
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
    List<Task> tasks = (List<Task>) portletSession.getAttribute("taskList");
    TaskListFilter filter = (TaskListFilter) portletSession.getAttribute("taskListFilter");
    Task currentTask = (Task) portletSession.getAttribute("task");
%>


<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        Overview: Tasks
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.overview');" title="Help"><span class="icon">S</span></a> 
          <% if(renderRequest.getWindowState().equals(WindowState.MAXIMIZED)){ %>
            <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize"><span class="icon">%</span></a>
          <% } else { %>
            <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize"><span class="icon">%</span></a>
          <% } %>
        </span>
      </h1>
      <div class="inner">
        <div id="<portlet:namespace />Filter" class="dropDown" style="display:none;">
          <form method="post" action="<portlet:actionURL portletMode="view"/>">
            <input type="hidden" name="action" value="filterTasks" />
            <table style="width:100%">
              <tbody>
                <tr>
                  <td>User:</td>
                  <td>
                    <select name="user"  class="rike-select">
                      <option <%= filter.getUser().length() == 0 ? "selected='selected'" : ""%> value="">Any</option>
                      <% for (TaskUser user: ViewHelper.getAvailableUsers()) {%>

                      <option <%= filter.getUser().equals(user.getEmail()) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(user.getEmail())%>"><%= StringEscapeUtils.escapeHtml(user.getEmail())%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Creator:</td>
                  <td>
                    <select name="creator"  class="rike-select">
                      <option <%= filter.getCreator().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (TaskUser user: ViewHelper.getAvailableUsers()) {%>

                      <option <%= filter.getCreator().equals(user.getEmail()) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(user.getEmail())%>"><%= StringEscapeUtils.escapeHtml(user.getEmail())%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Status:</td>
                  <td>
                    <select name="status"  class="rike-select">
                      <option <%= filter.getStatus().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (String status: ViewHelper.getStatus()) {%>

                      <option <%= filter.getStatus().equalsIgnoreCase(status) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(status)%>"><%= StringEscapeUtils.escapeHtml(ViewHelper.getStatus(status))%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Priority:</td>
                  <td>
                    <select name="priority"  class="rike-select">
                      <option <%= filter.getStatus().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (String priority: ViewHelper.getPriorities()) {%>

                      <option <%= filter.getPriority().equalsIgnoreCase(priority) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(priority)%>"><%= StringEscapeUtils.escapeHtml(ViewHelper.getPriority(priority))%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Milestone:</td>
                  <td>
                    <select name="milestone"  class="rike-select">
                      <option <%= filter.getMilestone().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (String[] data: ViewHelper.getAvailableMilestones(service)) {%>

                      <option <%= filter.getMilestone().equals(data[0]) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(data[0])%>"><%= StringEscapeUtils.escapeHtml(data[1])%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Artifact:</td>
                  <td>
                    <select name="artifact" class="rike-select">
                      <option <%= filter.getMilestone().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (Artifact artifact: ViewHelper.getAvailableArtifacts()) {%>

                      <option <%= filter.getArtifact().equals(artifact.getId().toString()) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(artifact.getId().toString())%>"><%= StringEscapeUtils.escapeHtml(artifact.getName())%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>


                <tr>
                  <td style="text-align:left"><input type="reset" value="Close" onclick="$('#<portlet:namespace />Filter').hide();"/></td>
                  <td style="text-align:right"><input type="submit" value="Ok" /></td>
                </tr>
              </tbody>
            </table>

          </form>

        </div>
        <div class="left">
          <ul class="tabbar">
            <li class="selected"><a href="#">Tasks</a></li>
            <li ><a href="<portlet:actionURL portletMode="view"/>&action=showMilestones">Milestones</a></li>
            <li><a href="<portlet:actionURL portletMode="view"/>&action=showArtifacts">Artifacts</a></li>
          </ul>
        </div>
      </div>
    </div>
    <!-- content -->
    <div class="content">
        <div id="<portlet:namespace />TableScroll">
        <table>
          <thead>
            <tr>
              <th class="shrink center"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.ID.toString()%>">#</a></th>
              <th class="shrink center"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.STATUS.toString()%>" title="Status">?</a></th>
              <th class="shrink center"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.PRIORITY.toString()%>" title="Priority">!</a></th>
              <th class="shrink center"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.TITLE.toString()%>">Title</a></th>
            </tr>
          </thead>
          <tbody>
            <%

              for (Task task: tasks) {

            %>
            <tr<%= currentTask != null && currentTask.getId().equals(task.getId()) ? " class=\"selected\"" : ""%>>
              <td class="shrink"><%= StringEscapeUtils.escapeHtml(task.getId().toString())%></td>
              <td class="shrink <%= ViewHelper.getTaskStatusColorClass(task)%>"></td>
              <td class="shrink"><%= task.getPriority()%></td>
              <td class="last shrink"><a href="<portlet:actionURL portletMode="view" />&action=selectTask&id=<%= URLEncoder.encode(task.getId().toString(), "UTF-8")%>"><%= StringEscapeUtils.escapeHtml(task.getTitle())%></a>
                <% if (task.getOwner() != null && !task.getOwner().isEmpty()) {%>
                <br />
                <%= ViewHelper.formatUser(task)%>
                <% }%>
              </td>

            </tr>
            <%
              }
            %>
          </tbody>
        </table>

      </div>

      <script type="text/javascript">
        <% if (currentTask != null) {%>
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
    <div class="footer">
      <div class="inner">
        <a href="javascript:void(0);" onclick="$('#<portlet:namespace />Filter').toggle();"><span class="icon">S</span> Filter <%= filter.isActive() ? "(active)" : ""%></a> <br />
      </div>
    </div>
  </div>
</div>  
<%
  } catch (Throwable t) {
      out.write("Please Reload");
      t.printStackTrace(System.err);
  }
%>
