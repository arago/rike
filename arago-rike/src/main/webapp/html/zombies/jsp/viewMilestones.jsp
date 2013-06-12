<%@page import="de.arago.rike.zombie.OverdueMilestone"%>
<%@page import="de.arago.rike.commons.data.Milestone"%>
<%@page import="de.arago.rike.commons.util.TaskHelper"%>
<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.commons.util.ViewHelper"%>
<%@page import="com.liferay.portal.model.User"%>
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
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.zombies');" title="Help" class="icon-question"></a>  
          <% if(renderRequest.getWindowState().equals(WindowState.MAXIMIZED)){ %>
            <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize" class="icon-resize-small"></a>
          <% } else { %>
            <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize" class="icon-resize-full"></a>
          <% } %>
        </span>
      </h1>
      <div class="inner">
        <div class="left">
        <ul class="tabbar">
          <li><a href="<portlet:actionURL portletMode="view"/>&action=showGraph">Graph</a></li>
          <li class="selected"><a href="#">Milestones  (<span style="color:<%= milestones.isEmpty()?"#000":"#cc0000" %>"><%= milestones.size() %></span>)</a></li>
          <li><a href="<portlet:actionURL portletMode="view"/>&action=showTasks">Tasks (<span style="color:<%= tasks.isEmpty()?"#000":"#cc0000" %>"><%= tasks.size() %></span>)</a></li>
        </ul>
        </div>
      </div>
        
      
    </div>
    <div class="content nofooter">

      <table>
        <thead>
          <tr>  
            <th class="id shrink" title="ID">#</th>
            <th class="name" title="Name">Name</th>
            <th class="shrink nowrap" title="ETA">ETA</th>
            <th class="shrink" title="delay">Delay</th>
          </tr>
          
        </thead>
        
        <tbody>
          
          <% for (OverdueMilestone o: milestones) { 
            Milestone stone = o.getMilestone();
          %>
          
          <tr>
            <td><%= stone.getId() %> </td>
            <td><a href="/web/guest/rike/-/show/milestone/<%= stone.getId() %>"><%= StringEscapeUtils.escapeHtml(stone.getTitle()) %></a>
            <div>work left <span class="bold"> <%= o.getWorkLeftInHours()%>h </span>, time left <span class="bold"><%= o.getDaysLeft() %>d</span>, work done in <span class="bold"><%= o.getWorkDoneInDays() %>d</span></div>
            </td>
            <td class="nowrap">  
              <%=
                service.formatDate(o.getEstimatedDoneDate(), "yyyy-MM-dd")
              %>
              </td>
            <td class="nowrap">
              
               
              <% if (o.getLate()<=0) { %>
                <span class="green">in time</span>
              <% } else { %>
                <span class="red bold" title="<%= o.getLate() %> days late"><%= o.getLate() %>d</span>
              <% } %>
              </td>
              
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