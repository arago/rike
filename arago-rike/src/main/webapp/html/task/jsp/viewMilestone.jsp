<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.data.Artifact"%>
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
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.task');" title="Help"><span class="icon">S</span></a> 
          <% if (renderRequest.getWindowState().equals(WindowState.MAXIMIZED)) {%>
          <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize"><span class="icon">%</span></a>
          <% } else {%>
          <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize"><span class="icon">%</span></a>
          <% }%>
        </span>
      </h1>
      
    </div>
    <div class="content nohead">

      <table>

        <tr>
          <th class="shrink">Title:</th>
          <td class="shrink"><%= StringEscapeUtils.escapeHtml(milestone.getTitle())%></td>
        </tr>

        <tr>
          <th class="shrink">URL:</th>
          <td class="shrink"><%= ViewHelper.formatURL(milestone.getUrl())%></td>
        </tr>
        
        <tr>
          <th class="shrink">Due Date:</th>
          <td class="shrink"><%= (milestone.getDueDate() != null)?service.formatDate(milestone.getDueDate(), "yyyy-MM-dd"):"[none]"%></td>
        </tr>
        
        
        <tr>
          <th class="shrink">Performance:</th>
          <td class="shrink"><%= milestone.getPerformance() == null?"[none]":(milestone.getPerformance() + "h per week") %></td>
        </tr>
        
        <tr>
          <th class="shrink">Release:</th>
          <td class="shrink"><%= milestone.getRelease() == null || milestone.getRelease().isEmpty()?"[none]":StringEscapeUtils.escapeHtml(milestone.getRelease()) %></td>
        </tr>
        
        <tr>
          <th class="shrink">Created:</th>
          <td class="shrink">on <%= StringEscapeUtils.escapeHtml(service.formatDate(milestone.getCreated()))%> by <%= ViewHelper.formatUser(milestone.getCreator())%></td>
        </tr>

      </table>
    </div>

    <div class="footer">
      <div class="inner">
        <a href="<portlet:actionURL portletMode="view"/>&action=editMilestone&id=<%= URLEncoder.encode(milestone.getId().toString(), "UTF-8")%>">edit</a>
        | <a href="/web/guest/rike/-/show/milestone/<%= milestone.getId()%>">permalink</a>
      </div>

    </div>
  </div>
</div>

<% } catch (Throwable t) {
    out.write("Please Reload");
    t.printStackTrace(System.err);
  }%>