<%@page import="de.arago.rike.commons.util.TaskListFilter"%>
<%@page import="de.arago.rike.commons.data.Artifact"%>
<%@page import="de.arago.rike.commons.data.Milestone"%>
<%@page import="de.arago.rike.commons.util.TaskHelper"%>
<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.commons.util.ViewHelper"%>
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
        TaskListFilter filter = (TaskListFilter) portletSession.getAttribute("taskListFilter");
        List<Artifact> artifacts = (List) portletSession.getAttribute("artifacts");
        Artifact currentArtifact = (Artifact) portletSession.getAttribute("artifact");
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        Overview: Artifacts
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.overview');" title="Help" class="icon-question"></a>
          <% if (renderRequest.getWindowState().equals(WindowState.MAXIMIZED)) {%>
          <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize"><span class="icon">%</span></a>
          <% } else {%>
          <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize"><span class="icon">%</span></a>
          <% }%>
        </span>
      </h1>
      <div class="inner">
        <div class="left">
          <ul class="tabbar">
            <li><a href="<portlet:actionURL portletMode="view"/>&action=showTasks">Tasks</a></li>
            <li ><a href="<portlet:actionURL portletMode="view"/>&action=showMilestones">Milestones</a></li>
            <li class="selected"><a href="#">Artifacts</a></li>
          </ul>
        </div>
      </div>
    </div>
    <div class="content nofooter">
      <div id="<portlet:namespace />TableScroll">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Title</th>
              <th>URL</th>
            </tr>         
          </thead>
          <tbody>
            <% for (Artifact a : artifacts) {%>

            <tr<%= currentArtifact != null && currentArtifact.getId().equals(a.getId()) ? " class=\"selected\"" : ""%>>
              <td><%=a.getId()%></td>
              <td><a href="<portlet:actionURL portletMode="view" />&action=showArtifact&id=<%= a.getId()%>"><%=StringEscapeUtils.escapeHtml(a.getName())%></a></td>
              <td><%= ViewHelper.formatURL(a.getUrl())%></td>
            </tr>

            <% }%>


          </tbody>
        </table>
      </div>

      <script type="text/javascript">
        <% if (currentArtifact != null) {%>
            $(function()
            {
              var el = $('#<portlet:namespace />TableScroll .selected').get(0);

              try
              {
                if (el)
                  el.scrollIntoView();
              } catch (e) {
                ;
              }
              ;
            });
        <% }%>
      </script>

    </div>
  </div>
</div>
<% } catch (Throwable t) {

        out.write("Please Reload");
        t.printStackTrace(System.err);
        throw (t);
    }%>