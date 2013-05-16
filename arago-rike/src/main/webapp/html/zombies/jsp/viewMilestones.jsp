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
          <% if(renderRequest.getWindowState().equals(WindowState.MAXIMIZED)){ %>
            <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize"><span class="icon">%</span></a>
          <% } else { %>
            <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize"><span class="icon">%</span></a>
          <% } %>
        </span>
      </h1>
      <div class="inner">
        <div class="left">
        <ul class="tabbar">
          <li><a href="<portlet:actionURL portletMode="view"/>&action=showGraph">Graph</a></li>
          <li class="selected"><a href="#">Milestones</a></li>
          <li><a href="<portlet:actionURL portletMode="view"/>&action=showTasks">Tasks</a></li>
        </ul>
        </div>
      </div>
        
      
    </div>
    <div class="content">

      <table>
        <thead>
          <tr>
            <th>ETA</th>
            <th>Status</th>
            <th>Name</th>
          </tr>
          
        </thead>
        
        <tbody>
          
          <% for (OverdueMilestone o: milestones) { 
            Milestone stone = o.getMilestone();
            int workPerWeek = (int) Math.ceil(stone.getPerformance() / 7.);
          
            int dueDaysLeft = ViewHelper.getDayDifference(stone.getDueDate());
            int workLeftInDays = (int) Math.ceil(o.getHoursLeft() / 7.);
          %>
          
          <tr>
            <td>  
              <%=
                service.formatDate(new Date(new Date().getTime() + ((workLeftInDays + 1) * 24 * 60 * 60 * 1000)), "yyyy-MM-dd")
              %>
            </td>
            <td>
              done in <%= workLeftInDays %> day(s)<br />
              due in <%= dueDaysLeft %> day(s)<br /> 
              <% if (dueDaysLeft > 0 && dueDaysLeft >= workLeftInDays) { %>
                <span style="color:green">in time</span>
              <% } else if (dueDaysLeft <= 0) { %>
                <span style="color:red">past due date</span>
              <% } else if (dueDaysLeft < workLeftInDays) { %>
                <span style="color:orange"><%= workLeftInDays - dueDaysLeft %> days late</span>
              <% } %>
              </td>
            <td><%= StringEscapeUtils.escapeHtml(stone.getTitle()) %></td>
          </tr>
          
          
          <% } %>
          
        </tbody>
      </table>


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
    throw(t);
  }%>