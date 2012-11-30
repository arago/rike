<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.data.Artifact"%>
<%@page import="de.arago.rike.data.Milestone"%>
<%@page import="de.arago.rike.util.ViewHelper"%>
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

    List<Artifact> artifacts = (List<Artifact>) portletSession.getAttribute("artifacts");
    List<Milestone> milestones = (List<Milestone>) portletSession.getAttribute("milestones");
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        New Task
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
    <div class="content nohead nofooter">
      <form method="post" class="<portlet:namespace />form" id="<portlet:namespace/>Form" action="<portlet:actionURL portletMode="view" />">
        <div>
          <input type="hidden" name="action" value="saveTask" />
        </div>
        <table>
          <tbody>

            <tr>
              <th class="shrink">Title:</th>
              <td class="shrink"><input type="text" name="title" /></td>
            </tr>

            <tr>
              <th class="shrink">URL:</th>
              <td class="shrink"><input type="text" name="url" /></td>
            </tr>

            <tr>
              <th class="shrink">Artifact:</th>
              <td class="shrink">
                <select name="artifact" style="width:150px">
                  <% for (Artifact artifact: artifacts) {%>
                  <option value="<%= artifact.getId()%>"><%= StringEscapeUtils.escapeHtml(artifact.getName())%></option>
                  <% }%>
                </select>
              </td>
            </tr>

            <tr>
              <th class="shrink">Milestone:</th>
              <td class="shrink">
                <% boolean haveMilestone = false;%>
                <select name="milestone" style="max-width:150px">
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
              <th class="shrink">Size:</th>
              <td class="shrink">
                <input type="text" class="positive-integer" name="size_estimated" value="" />
              </td>
            </tr>

            <tr>
              <td class="shrink"><input type="reset" value="Close" onclick="document.location= '<portlet:actionURL portletMode="view" />&action=abortCreate';"/></td>
              <td class="shrink" style="text-align:right"><input type="submit" value="Create" /></td>
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