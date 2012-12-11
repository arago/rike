<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.data.Milestone"%>
<%@page import="de.arago.rike.util.ViewHelper"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
<%@page import="de.arago.rike.data.Task.Status"%>
<%@page import="de.arago.rike.data.Task"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ page contentType="text/html; charset=UTF-8" %> 

<portlet:defineObjects />

<%
  try {
    UserService service = new JspUserService(renderRequest, portletSession);
    Task task = (Task) portletSession.getAttribute("task");
    List<Milestone> milestones = (List<Milestone>) portletSession.getAttribute("milestones");
%>


<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        <% if (task != null) {%>
        <span class="<%= ViewHelper.getTaskStatusColorClass(task)%>" style="overflow:hidden; max-width: 76%; padding-left:10px; background-position: center left">
          Task: #<%= StringEscapeUtils.escapeHtml(task.getId().toString())%> <%= StringEscapeUtils.escapeHtml(task.getTitle())%>
        </span>
        <% } else {%>
        <span>Task</span>
        <% }%>
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.task');" title="Help"><span class="icon">S</span></a> 
          <% if(renderRequest.getWindowState().equals(WindowState.MAXIMIZED)){ %>
            <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize"><span class="icon">%</span></a>
          <% } else { %>
            <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize"><span class="icon">%</span></a>
          <% } %>
        </span>
      </h1>
    </div>
    <div class="content nofooter nohead">
        <form method="post" action="<portlet:actionURL portletMode="view" />" id="<portlet:namespace/>Form">
          <div>
            <input type="hidden" name="id" value="<%= task.getId()%>" />
            <input type="hidden" name="action" value="evaluateTask" />
          </div>
          <table>
            <tbody>
              <tr>
                <th class="shrink">Milestone:</th>
                <td class="shrink">
                  <select name="milestone" style="max-width:150px">
                    <% for (Milestone milestone: milestones) {%>
                    <option <%= task.getMilestone() != null && task.getMilestone().getId().equals(milestone.getId()) ? "selected='selected'" : ""%> value="<%= milestone.getId()%>">[<%= milestone.getDueDate() == null ? "?" : service.formatDate(milestone.getDueDate(), "dd.MM.yyyy")%>] <%= StringEscapeUtils.escapeHtml(milestone.getTitle())%></option>
                    <% }%>
                  </select>
                </td>
              </tr>

              <tr>
                <th class="shrink">Challenge:</th>
                <td class="shrink">
                  <select name="challenge">
                    <% for (String challenge: ViewHelper.getChallenges()) {%>
                    <option <%= challenge.equalsIgnoreCase("average") ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(challenge)%>"><%= StringEscapeUtils.escapeHtml(ViewHelper.getChallenge(challenge))%></option>
                    <% }%>
                  </select>
                </td>
              </tr>

              <tr>
                <th class="shrink">Priority:</th>
                <td class="shrink">
                  <select name="priority">
                    <% for (String priority: ViewHelper.getPriorities()) {%>
                    <option <%= priority.equalsIgnoreCase("normal") ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(priority)%>"><%= StringEscapeUtils.escapeHtml(ViewHelper.getPriority(priority))%></option>
                    <% }%>
                  </select>
                </td>
              </tr>

              <tr>
                <th class="shrink">Size:</th>
                <td class="shrink">
                  <input type="text" class="positive-integer" name="size_estimated" value="<%= task.getSizeEstimated() == null ? 100 : task.getSizeEstimated()%>" />
                </td>
              </tr>

              <tr>
                <td class="shrink"><input type="reset" value="Close" onclick="document.location= '<portlet:actionURL portletMode="view" />&action=abortEvaluate';"/></td>
                <td class="shrink" style="text-align:right"><input type="submit" value="Rate" /></td>
              </tr>
            </tbody>
          </table>
        </form>
        <script type="text/javascript">
          $(function()
          {
            var normalize = function()
            {
              if (!this.value.length) return;

              this.value = this.value.replace(/\D+/g, "");
            };

            $('#<portlet:namespace/>Form .positive-integer').keydown(normalize)
            .keyup(normalize)
            .change(normalize)
            .blur(normalize)
            .focus(normalize);

          });

        </script>
    </div>
  </div>
</div>

<% } catch (Throwable t) {
    out.write("Please Reload");
    t.printStackTrace(System.err);
  }%>