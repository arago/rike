<%@page import="de.arago.rike.data.Artifact"%>
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
    List<Artifact> artifacts = (List<Artifact>) portletSession.getAttribute("artifacts");
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
        <form method="post" action="<portlet:actionURL portletMode="view" />" id="<portlet:namespace/>form">
          <div>
            <input type="hidden" name="id" value="<%= task.getId()%>" />
            <input type="hidden" name="action" value="evaluateTask" />
          </div>
          <table>
            <tbody>
              
              <tr>
                <th class="shrink">Title:</th>
                <td class="shrink"><input class="rike-input" placeholder="Title of the task" type="text" name="title" value="<%= StringEscapeUtils.escapeHtml(task.getTitle())%>"></td>
              </tr>

            <tr>
              <th class="shrink">URL:</th>
              <td class="shrink"><input class="rike-input" placeholder="URL of the task" type="text" name="url" value="<%= StringEscapeUtils.escapeHtml(task.getUrl())%>" /></td>
            </tr>
            
            <tr>
              <th class="shrink">Description:</th>
              <td class="shrink">
                <textarea placeholder="Optional description of the task" class="rike-textarea" ><%= StringEscapeUtils.escapeHtml(task.getDescription())%></textarea>
              </td>
            </tr>
            
            <tr>
                <th class="shrink">Time:</th>
                <td class="shrink">
                  <input type="number" min="1" class="rike-input" placeholder="Estimated hours to finish" name="size_estimated" value="<%= task.getSizeEstimated() == null ? 8 : task.getSizeEstimated()%>" />
                </td>
              </tr>
              
            <!-- <tr>
                <th class="shrink">Date:</th>
                <td class="shrink"><input class="rike-input" placeholder="optional due date of the task, YYYY-MM-DD" type="text" name="due_date" value="<%= StringEscapeUtils.escapeHtml(task == null || task.getDueDate() == null ? "" : service.formatDate(task.getDueDate(), "yyyy-MM-dd"))%>"/></td>
              </tr>-->
             
              
              <tr>
                <th class="shrink">Milestone:</th>
                <td class="shrink">
                  <select name="milestone"  class="rike-select">
                    <% for (Milestone milestone: milestones) {%>
                    <option <%= task.getMilestone() != null && task.getMilestone().getId().equals(milestone.getId()) ? "selected='selected'" : ""%> value="<%= milestone.getId()%>">[<%= milestone.getDueDate() == null ? "?" : service.formatDate(milestone.getDueDate(), "dd.MM.yyyy")%>] <%= StringEscapeUtils.escapeHtml(milestone.getTitle())%></option>
                    <% }%>
                  </select>
                </td>
              </tr>
              
              
              <tr>
              <th class="shrink">Artifact:</th>
              <td class="shrink">
                <select name="artifact"  class="rike-select">
                  <% for (Artifact artifact: artifacts) {%>
                  <option <%= task.getArtifact()!= null && task.getArtifact().getId().equals(artifact.getId()) ? "selected='selected'" : ""%> value="<%= artifact.getId()%>"><%= StringEscapeUtils.escapeHtml(artifact.getName())%></option>
                  <% }%>
                </select>
              </td>
            </tr>

              <tr>
                <th class="shrink">Priority:</th>
                <td class="shrink">
                  <select name="priority" class="rike-select">
                    <% for (String priority: ViewHelper.getPriorities()) {%>
                    <option <%= priority.equals(task.getPriority() + "") ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(priority)%>"><%= StringEscapeUtils.escapeHtml(ViewHelper.getPriority(priority))%></option>
                    <% }%>
                  </select>
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
          
          (function()
        {
          $('.<portlet:namespace />form').submit(function()
          {
            try
            {
              var ok = true;
						
              $([$("input[name=title]", this), $("input[name=url]", this), $("select[name=artifact]", this)]).each(function()
              {
                if (!this.val())
                {
                  this.get(0).style.borderColor = 'red';
                  ok = false;
                } else {
                  this.get(0).style.borderColor = '';
                };
              });


              return ok;
            } catch(e) {
              alert(e);
            };

            return false;
          });
        })();

        </script>
    </div>
  </div>
</div>

<% } catch (Throwable t) {
    out.write("Please Reload");
    t.printStackTrace(System.err);
  }%>