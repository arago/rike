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
    List<Task> tasks = (List) portletSession.getAttribute("overdue-tasks");
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        
        <span>Exceeded date (<span style="color:<%= milestones.isEmpty() && tasks.isEmpty()?"#000":"#cc0000" %>"><%= milestones.size() + tasks.size() %></span>)</span>
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
          <li><a href="<portlet:actionURL portletMode="view"/>&action=showMilestones">Milestones  (<span style="color:<%= milestones.isEmpty()?"#000":"#cc0000" %>"><%= milestones.size() %></span>)</a></li>
          <li class="selected"><a href="#">Tasks  (<span style="color:<%= tasks.isEmpty()?"#000":"#cc0000" %>"><%= tasks.size() %></span>)</a></li>
        </ul>
        </div>
      </div>
        
      
    </div>
    <div class="content nofooter">

         <table>
        <thead>
          <tr>
            <th>#</th>
            <th>?</th>
            <th>Title</th>
            <th>Due Date</th>
            <th>Days overdue</th>
          </tr>
          
        </thead>
        
        <tbody>
          
          <% for (Task task: tasks) { %>
          
          <tr>
            <td><%= task.getId() %> </td>
            <td class="shrink <%= ViewHelper.getTaskStatusColorClass(task)%>"></td>
            <td><a href="<portlet:actionURL portletMode="view" />&action=selectTask&id=<%= URLEncoder.encode(task.getId().toString(), "UTF-8")%>"><%= StringEscapeUtils.escapeHtml(task.getTitle())%></td>
            <td><%= service.formatDate(task.getDueDate(), "yyyy-MM-dd") %></td>
            <td><%= Math.abs(ViewHelper.getDayDifference(task.getDueDate())) %></td>  
          </tr>
          
          <% } %>
          
        </tbody>
      </table>


    </div>    
  </div>
</div>
<% } catch (Throwable t) {
  
    out.write("Please Reload");
    t.printStackTrace(System.err);
    throw(t);
  }%>