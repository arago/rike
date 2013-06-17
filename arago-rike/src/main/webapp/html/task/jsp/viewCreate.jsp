<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.commons.data.Artifact"%>
<%@page import="de.arago.rike.commons.data.Milestone"%>
<%@page import="de.arago.rike.commons.util.ViewHelper"%>
<%@page import="de.arago.rike.commons.data.Task.Status"%>
<%@page import="de.arago.rike.commons.data.Task"%>
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

    List<Artifact> artifacts = (List<Artifact>) portletSession.getAttribute("artifacts");
    List<Milestone> milestones = (List<Milestone>) portletSession.getAttribute("milestones");
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        <div class="ellipsis">
          New Task
        </div>
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.task');" title="Help" class="icon-question"></a> 
          <% if(renderRequest.getWindowState().equals(WindowState.MAXIMIZED)){ %>
            <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize" class="icon-resize-small"></a>
          <% } else { %>
            <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize" class="icon-resize-full"></a>
          <% } %>
        </span>
      </h1>
    </div>
    <div class="content nohead nofooter">
      <form method="post" class="<portlet:namespace />form" id="<portlet:namespace/>Form" action="<portlet:actionURL portletMode="view" />">
        <div>
          <input type="hidden" name="action" value="saveTask" />
        </div>
        <table>
          <tbody>

            <tr>
              <th class="shrink">Title:</th>
              <td><input placeholder="title of the task" class="rike-input" type="text" name="title" /></td>
            </tr>

            <tr>
              <th class="shrink">URL:</th>
              <td><input placeholder="URL of the task" class="rike-input" type="text" name="url" /></td>
            </tr>
            
            <tr>
              <th class="shrink">Time:</th>
              <td>
                <input type="number" min="1" placeholder="Estimated hours to finish" class="rike-input" name="size_estimated" value="" />
              </td>
            </tr>
            
            <tr>
              <th class="shrink">Artifact:</th>
              <td>
                <select name="artifact" class="rike-select">
                  <% for (Artifact artifact: artifacts) {%>
                  <option value="<%= artifact.getId()%>"><%= StringEscapeUtils.escapeHtml(artifact.getName())%></option>
                  <% }%>
                </select>
              </td>
            </tr>

            <tr>
              <th class="shrink">Milestone:</th>
              <td>
                <% boolean haveMilestone = false;%>
                <select name="milestone" class="rike-select">
                  <% for (Milestone milestone: milestones) {%>
                  <option <% if (milestone.getDueDate() != null && !haveMilestone) {
                      out.print("selected='selected'");
                      haveMilestone = true;
                  }%>value="<%= milestone.getId()%>">[<%= milestone.getDueDate() == null ? "?" : service.formatDate(milestone.getDueDate())%>] <%= StringEscapeUtils.escapeHtml(milestone.getTitle())%></option>
                  <% }%>
                </select>
              </td>
            </tr>

            <tr>
              <th class="shrink">Description:</th>
              <td>
                <textarea placeholder="Optional description of the task" class="rike-textarea" name="description"></textarea>
              </td>
            </tr>

            <tr>
              <td class="shrink"><input type="reset" value="Close" onclick="document.location= '<portlet:actionURL portletMode="view" />&action=abortCreate';"/></td>
              <td class="right"><input type="submit" value="Create" /></td>
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