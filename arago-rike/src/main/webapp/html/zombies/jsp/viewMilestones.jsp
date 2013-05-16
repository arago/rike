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
          %>
          
          <tr>
            <td>  
              <%=
                service.formatDate(o.getEstimatedDoneDate(), "yyyy-MM-dd")
              %>
            </td>
            <td>
              work left <%= o.getWorkLeftInHours()%>h<br />
              time left <%= o.getDaysLeft() %>d<br />
              work done in <%= o.getWorkDoneInDays() %>d<br />
               
              <% if (o.getDaysLeft() > 0 && o.getDaysLeft() >= o.getWorkDoneInDays()) { %>
                <span style="color:green">in time</span>
              <% } else { %>
                <span style="color:red"><%= o.getWorkDoneInDays() - o.getDaysLeft() %> days late</span>
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