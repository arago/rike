<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.commons.data.Artifact"%>
<%@page import="de.arago.rike.commons.data.Milestone"%>
<%@page import="de.arago.rike.commons.util.ViewHelper"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
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
    Milestone milestone = (Milestone) portletSession.getAttribute("milestone");


%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        <div class="ellipsis">
        <% if (milestone ==  null) { %>
          New Milestone
        <% } else { %>
          Milestone: #<%= milestone.getId() %> <%= StringEscapeUtils.escapeHtml(milestone.getTitle())%>
        <% } %>
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
        <form method="post" class="<portlet:namespace />form" action="<portlet:actionURL portletMode="view" />">
          <div>
            <input type="hidden" name="action" value="saveMilestone" />
            <input type="hidden" name="id" value="<%= StringEscapeUtils.escapeHtml(milestone == null || milestone.getId() == null ? "" : milestone.getId().toString())%>" />
          </div>
          <table>
            <tbody>

              <tr>
                <th class="shrink">Title:</th>
                <td><input class="rike-input" placeholder="title of the milestone" type="text" name="title" value="<%= StringEscapeUtils.escapeHtml(milestone == null ? "" : milestone.getTitle())%>" /></td>
              </tr>

              <tr>
                <th class="shrink">URL:</th>
                <td><input class="rike-input" placeholder="URL of the milestone, if any" type="text" name="url" value="<%= StringEscapeUtils.escapeHtml(milestone == null ? "" : milestone.getUrl())%>"/></td>
              </tr>

              <tr>
                <th class="shrink">Date:</th>
                <td><input class="rike-input" placeholder="due date of the milestone, YYYY-MM-DD" type="text" name="due_date" value="<%= StringEscapeUtils.escapeHtml(milestone == null || milestone.getDueDate() == null ? "" : service.formatDate(milestone.getDueDate(), "yyyy-MM-dd"))%>"/></td>
              </tr>
              
              <tr>
                <th class="shrink">Performance:</th>
                <td><input class="rike-input" placeholder="capacity per week in hours" type="text" name="performance" value="<%= milestone == null ? "" : milestone.getPerformance() %>"/></td>
              </tr>

              <tr>
                <th class="shrink">Release:</th>
                <td>
                  <select name="release" id="<portlet:namespace />Release" class="rike-select">
                    <option value="">[No Release]</option>
                    <% for (String r: ViewHelper.getAvailableReleases()) {%>
                    <option value="<%= StringEscapeUtils.escapeHtml(r)%>" <%= milestone != null && r.equals(milestone.getRelease())?"selected='selected'":"" %>><%= StringEscapeUtils.escapeHtml(r)%></option>
                    <% }%>
                    <option value="_new_">[New Release]</option>
                  </select>
                  <input placeholder="name of the release" type="text" name="new_release" style="display:none" id="<portlet:namespace />NewRelease" />

                  <script type="text/javascript">
                    $(function()
                    {
                      $('#<portlet:namespace />Release').change(function()
                      {
                        if (this.value === "_new_")
                        {
                          $('#<portlet:namespace />NewRelease').show();
                        } else {
                          $('#<portlet:namespace />NewRelease').hide();
                        };
                      });
                    })
                  </script>
                </td>
              </tr>

              <tr>
                <td class="shrink"><input type="reset" value="Close" onclick="document.location= '<portlet:actionURL portletMode="view" />&action=abortEditMilestone';"/></td>
                <td class="right"><input type="submit" value="Save" /></td>
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
						
                $([$("input[name=title]", this)]).each(function()
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