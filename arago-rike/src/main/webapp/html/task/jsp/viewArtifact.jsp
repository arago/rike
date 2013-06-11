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
    Artifact artifact = (Artifact) portletSession.getAttribute("artifact");
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        <div class="ellipsis">
        <% if (artifact ==  null) { %>
          New Artifact
        <% } else { %>
          Artifact: #<%= artifact.getId() %> <%= StringEscapeUtils.escapeHtml(artifact.getName())%>
        <% } %>
        </div>
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.task');" title="Help" class="icon-question"></a> 
          <% if (renderRequest.getWindowState().equals(WindowState.MAXIMIZED)) {%>
          <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize" class="icon-resize-small"></a>
          <% } else {%>
          <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize" class="icon-resize-full"></a>
          <% }%>
        </span>
      </h1>
      
    </div>
    <div class="content nohead">

      <table>

        <tr>
          <th class="shrink">Title:</th>
          <td><%= StringEscapeUtils.escapeHtml(artifact.getName())%></td>
        </tr>

        <tr>
          <th class="shrink">URL:</th>
          <td><%= ViewHelper.formatURL(artifact.getUrl())%></td>
        </tr>

        <!--<tr>
          <td class="shrink" colspan="2"><a href="<%= renderRequest.getContextPath()%>?artifact=<%= artifact.getId()%>">link to this artifact</a></td>
        </tr>-->

      </table>
    </div>

    <div class="footer">
      <div class="inner">
        <a href="<portlet:actionURL portletMode="view"/>&action=editArtifact&id=<%= URLEncoder.encode(artifact.getId().toString(), "UTF-8")%>">edit</a>
      </div>

    </div>
  </div>
</div>

<% } catch (Throwable t) {
    out.write("Please Reload");
    t.printStackTrace(System.err);
  }%>