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
    Task task = (Task) portletSession.getAttribute("task");
    String user = (String) portletSession.getAttribute("userEmail");
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        <div class="ellipsis">
        <% if (task != null) {%>
          Task: #<%= StringEscapeUtils.escapeHtml(task.getId().toString())%> <%= StringEscapeUtils.escapeHtml(task.getTitle())%>
        <% } else {%>
          Task
        <% }%>
        </div>
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.task');" title="Help" class="icon-question"></a> 
          <% if (renderRequest.getWindowState().equals(WindowState.MAXIMIZED)) {%>
          <a href="<portlet:actionURL portletMode="view" windowState="normal"/>"  title="Minimize" class="icon-resize-small"></a>
          <% } else {%>
          <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize" class="icon-resize-full"></a>
          <% }%>
        </span>
      </h1>
    </div>
    <div class="content nohead">
          <% if (task != null && user.equals(task.getOwner())) {%>  
          <form id="<portlet:namespace/>Form1" class="dropDown" method="post" action="<portlet:actionURL portletMode="view"/>" style="display:none; ">
            <div>
              <input type="hidden" name="action" value="endTask" />
              <input type="hidden" name="id" value="<%= StringEscapeUtils.escapeHtml(task.getId().toString())%>" />
            </div>
            <table>
              <tbody>
                <tr>
                  <td>Hours spent:</td>
                  <td><input type="number" min="1" class="rike-input" name="hours_spent" value="<%= task.getHoursSpent()%>" /></td>
                </tr>
                <tr>
                  <td><input type="reset" value="Abort" onclick="$(this.form).hide();"/></td>
                  <td style="text-align:right"><input type="submit" value="Close Task" /></td>
                </tr>
              </tbody>
            </table>
          </form>

          <form id="<portlet:namespace/>Form2" class="dropDown" method="post" action="<portlet:actionURL portletMode="view"/>" style="display:none; ">
            <div>
              <input type="hidden" name="action" value="addHoursToTask" />
              <input type="hidden" name="id" value="<%= StringEscapeUtils.escapeHtml(task.getId().toString())%>" />
            </div>
            <table>
              <tbody>
                <tr>
                  <td>Hours spent:</td>
                  <td><input type="number" min="1" class="rike-input" name="hours_spent" value="<%= task.getHoursSpent()%>" /></td>
                </tr>
                <tr>
                  <td><input type="reset" value="Abort" onclick="$(this.form).hide();"/></td>
                  <td style="text-align:right"><input type="submit" value="Add" /></td>
                </tr>
              </tbody>
            </table>
          </form>
          <% }%>

      <% if (task != null) {%>

      <table>
        <tbody>
          
          <tr>
            <th class="shrink">Title:</th>
            <td ><%= StringEscapeUtils.escapeHtml(task.getTitle())%></td>
          </tr>

          <tr>
            <th class="shrink">Status:</th>
            <td>
              <%
                switch (task.getStatusEnum()) {
                  case DONE:
                    out.print("completed on " + service.formatDate(task.getEnd()) + " by " + ViewHelper.formatUser(task));
                    break;

                  case IN_PROGRESS:
                    out.print("in progress " + service.formatDate(task.getStart()) + " by " + ViewHelper.formatUser(task));
                    break;

                  case OPEN:
                    out.print("rated on " + service.formatDate(task.getRated()) + " by " + ViewHelper.formatUser(task.getRatedBy()));
                    break;

                  case UNKNOWN:
                    out.print("created on " + service.formatDate(task.getCreated()) + " by " + ViewHelper.formatUser(task.getCreator()));
                    break;
                }
              %>
            </td>
          </tr>

          <tr>
            <th class="shrink">URL:</th>
            <td><%= ViewHelper.formatURL(task.getUrl())%></td>
          </tr>
          
          <% if (task.getStatusEnum() != Task.Status.UNKNOWN) {%>
          <tr>
            <th class="shrink">Time:</th>
            <td><%= task.getSizeEstimated()%> hours estimated, <%= task.getHoursSpent()%> hours spent</td>
          </tr>

          <tr> 
            <th class="shrink">Priority:</th>
            <td><%= ViewHelper.getPriority(task.getPriority())%><%= task.getDueDate()==null ? "" : (", should be finished by " + service.formatDate(task.getDueDate())) %></td>
          </tr>
          <% }%>

          <tr>
            <th class="shrink">Artifact:</th>
            <td><a href="<portlet:actionURL portletMode="view"/>&action=selectArtifact&id=<%= task.getArtifact().getId()%>">
                <%= StringEscapeUtils.escapeHtml(task.getArtifact().getName())%></a></td>
          </tr>

          <% if (task.getMilestone() != null) {%>
          <tr>
            <th class="shrink">Milestone:</th>
            <td><a href="<portlet:actionURL portletMode="view"/>&action=selectMilestone&id=<%= task.getMilestone().getId()%>">
                <%= StringEscapeUtils.escapeHtml(task.getMilestone().getTitle())%></a></td>
          </tr>
          <%  }%>

          <% if (task.getDescription()!=null && !task.getDescription().isEmpty()) {%>
          <tr>
            <th class="shrink">Description:</th>
            <td><%= StringEscapeUtils.escapeHtml(task.getDescription()) %></td>
          </tr>
          <% }%>

        </tbody>
      </table>
      <% }%>


    </div>
    <div class="footer">
      <div class="inner">
        <% if (task != null) {%>
        <% if (task.getStatusEnum() == Status.UNKNOWN) {%>
        <a href="<portlet:actionURL portletMode="view"/>&action=viewEvaluateTask&id=<%= URLEncoder.encode(task.getId().toString(), "UTF-8")%>">rate</a>
        <% } else if (task.getStatusEnum() == Status.OPEN) {%>
        <a href="<portlet:actionURL portletMode="view"/>&action=viewEvaluateTask&id=<%= URLEncoder.encode(task.getId().toString(), "UTF-8")%>">rate</a>
        <% if (TaskHelper.canDoTask(user, task)) {%>
        or
        <a href="<portlet:actionURL portletMode="view"/>&action=startTask&id=<%= URLEncoder.encode(task.getId().toString(), "UTF-8")%>">start</a>
        <% } else {%>
        Task cannot be started!
        <% }%>
        <% } else if (task.getStatusEnum() == Status.IN_PROGRESS) {%>

        <% if (user.equals(task.getOwner())) {%>
        <a href="javascript:void(0)" onclick="$('#<portlet:namespace/>Form1').hide();
              $('#<portlet:namespace/>Form2').toggle();">add hours</a>
        or 
        <a href="javascript:void(0)" onclick="$('#<portlet:namespace/>Form2').hide();
              $('#<portlet:namespace/>Form1').toggle();">finish</a>


        <% } else {%>
        In progress (<%= ViewHelper.formatUser(task)%>)
        <% }%>

        <% } else if (task.getStatusEnum() == Status.DONE) {%>
        Done on <%= service.formatDate(task.getEnd())%> (<%= ViewHelper.formatUser(task.getOwner())%>)

        <% }%>
        | <a href="/web/guest/rike/-/show/task/<%= task.getId()%>">permalink</a>
        <% } else {%>
        <p>No Task selected</p>
        <% }%>
      </div>

    </div>
  </div>

</div>
<% } catch (Throwable t) {

    out.write("Please Reload");
    t.printStackTrace(System.err);
    throw (t);
  }%>