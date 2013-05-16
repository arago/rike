<%@page import="de.arago.rike.zombie.OverdueMilestone"%>
<%@page import="de.arago.rike.data.Milestone"%>
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
        List<OverdueMilestone> milestones = (List) portletSession.getAttribute("overdue-milestones");
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>

        <span>Exceeded date</span>
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.zombies');" title="Help"><span class="icon">S</span></a> 
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
            <li class="selected"><a href="#">Graph</a></li>
            <li><a href="<portlet:actionURL portletMode="view"/>&action=showMilestones">Milestones</a></li>
            <li><a href="<portlet:actionURL portletMode="view"/>&action=showTasks">Tasks</a></li>
          </ul>
        </div>
      </div>


    </div>
    <div class="content">

      <svg version="1.1" id="Ebene_1xdasdfasdsdf" xmlns="http://www.w3.org/2000/svg" >
        <g>
          <% int y = 10; %>     
          <% for (OverdueMilestone o : milestones) {
            Milestone stone = o.getMilestone();
          %>
          <text x="0" y="<%= y+10 %>"><%= stone.getTitle() %></text>
          <rect x="<%= 150 %>" y="<%= y %>" fill="#009736" width="<%= stone.getDays() %>" height="20"/>
          <% y += 25; %>     
          <% }%>
        </g>
      </svg>

    </div>
    <div class="footer">
      <div class="inner">

      </div>

    </div>
  </div>
</div>
<% } catch (Throwable t) {

        out.write("Please Reload");
        t.printStackTrace(System.err);
        throw (t);
    }%>