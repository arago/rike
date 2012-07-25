<%@page import="de.arago.rike.util.TaskHelper"%>
<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.util.ViewHelper"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
<%@page import="de.arago.rike.data.Task.Status"%>
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
    Task task = (Task) portletSession.getAttribute("task");
    String user = (String) portletSession.getAttribute("userEmail");
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
          <a href="<portlet:actionURL portletMode="view" windowState="<%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "normal" : "maximized"%>"/>" title="Maximize"><span class="icon">%</span></a>
        </span>
      </h1>
      <div class="inner">
        <div class="right">
          <a href="javascript:void(0);" onclick="$('#<portlet:namespace/>New').toggle()"><span class="icon">+</span> New</a></a>
          <div style="display:none;" class="dropDown" id="<portlet:namespace/>New" style="margin-left:-40px">
            <a href="<portlet:actionURL portletMode="view" />&action=createTask">Task</a> <br />
            <a href="<portlet:actionURL portletMode="view" />&action=editMilestone">Milestone</a><br />
            <a href="<portlet:actionURL portletMode="view" />&action=editArtifact">Artifact</a>
          </div>
        </div>
        <% if (task != null && user.equals(task.getOwner())) {%>  
        <form id="<portlet:namespace/>Form" class="dropDown" method="post" action="<portlet:actionURL portletMode="view"/>" style="display:none; ">
          <div>
            <input type="hidden" name="action" value="endTask" />
            <input type="hidden" name="id" value="<%= StringEscapeUtils.escapeHtml(task.getId().toString())%>" />
          </div>
          <table>
            <tbody>
              <tr>
                <td>Hours spent:</td>
                <td><input type="text" class="positive-integer" name="hours_spent" value="1" /></td>
              </tr>
              <tr>
                <td>Size:</td>
                <td><input type="text"  class="positive-integer" name="size" value="<%= StringEscapeUtils.escapeHtml(task.getSizeEstimated().toString())%>" /></td>
              </tr>
              <tr>
                <td><input type="reset" value="Abort" onclick="$(this.form).hide();"/></td>
                <td style="text-align:right"><input type="submit" value="Close Task" /></td>
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
        <% } %>
      </div>
    </div>
    <div class="content">

        <% if (task != null) {%>

        <table>
          <tbody>
            <tr>
              <th class="shrink">URL</th>
              <td class="shrink"><%= ViewHelper.formatURL(task.getUrl())%></td>
            </tr>

            <tr>
              <th class="shrink">Artifact</th>
              <td class="shrink"><%= StringEscapeUtils.escapeHtml(task.getArtifact().getName())%></td>
            </tr>

            <% if (task.getMilestone() != null) {%>

            <tr>
              <th class="shrink">Milestone</th>
              <td class="shrink"><%= StringEscapeUtils.escapeHtml(task.getMilestone().getTitle())%></td>
            </tr>

            <%  }%>

            <tr>
              <th class="shrink">Status</th>
              <td class="shrink">
                <%
                  switch (task.getStatusEnum()) {
                    case DONE:
                      out.print("completed on " + service.formatDate(task.getEnd()) + " by " + ViewHelper.formatUser(task));
                      break;

                    case IN_PROGRESS:
                      out.print("in progress " + service.formatDate(task.getStart()) + " by " + ViewHelper.formatUser(task));
                      break;

                    case OPEN:
                      out.print("open");
                      break;

                    case UNKNOWN:
                      out.print("not rated");
                      break;
                  }
                %>

              </td>
            </tr>

            <% if (task.getStatusEnum() != Task.Status.UNKNOWN) {%>


            <% if (task.getStatusEnum() == Status.DONE) {%>

            <tr>
              <th class="shrink">Hours spent:</th>
              <td class="shrink"><%= task.getHoursSpent()%></td>
            </tr>

            <tr>
              <th class="shrink">Size:</th>
              <td class="shrink"><%= task.getSize()%></td>
            </tr>

            <% }%>

            <tr>
              <th class="shrink">Estimated Size:</th>
              <td class="shrink"><%= task.getSizeEstimated()%></td>
            </tr>

            <tr> 
              <th class="shrink">Priority:</th>
              <td class="shrink"><%= ViewHelper.getPriority(task.getPriorityEnum())%></td>
            </tr>

            <tr>
              <th class="shrink">Challenge:</th>
              <td class="shrink"><%= ViewHelper.getChallenge(task.getChallengeEnum())%></td>
            </tr>

            <% }%>

            <tr>
              <th class="shrink">Created:</th>
              <td class="shrink">on <%= StringEscapeUtils.escapeHtml(service.formatDate(task.getCreated()))%> by <%= ViewHelper.formatUser(task.getCreator())%></td>
            </tr>
          </tbody>
        </table>
        <% } %>


    </div>
    <div class="footer">
      <div class="inner">
        <% if (task != null) { %>
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
        <a href="javascript:void(0)" onclick="$('#<portlet:namespace/>Form').toggle(); ">finish</a>
        

        <% } else {%>
        In progress (<%= ViewHelper.formatUser(task)%>)
        <% }%>

        <% } else if (task.getStatusEnum() == Status.DONE) {%>
        Done on <%= service.formatDate(task.getEnd())%> (<%= ViewHelper.formatUser(task.getOwner())%>)

        <% }%>
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
    throw(t);
  }%>