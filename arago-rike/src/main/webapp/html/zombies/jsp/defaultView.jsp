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
    List<Milestone> milestones = (List) portletSession.getAttribute("overdue-milestones");
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
          <li class="selected"><a href="#">Graph</a></li>
          <li><a href="<portlet:actionURL portletMode="view"/>&action=showMilestones">Milestones</a></li>
          <li><a href="<portlet:actionURL portletMode="view"/>&action=showTasks">Tasks</a></li>
        </ul>
        </div>
      </div>
        
      
    </div>
    <div class="content">

      TODO svg
      
       <svg version="1.1" id="Ebene_1xdasdfasdsdf" xmlns="http://www.w3.org/2000/svg" >
<g transform="scale(1)">
  <line fill="none" stroke="#404041" stroke-linejoin="round" x1="114.667" y1="232.881" x2="407.823" y2="232.881"/>
  <line fill="none" stroke="#231F20" stroke-width="2" stroke-linejoin="round" stroke-miterlimit="10" x1="114" y1="233.376" x2="114" y2="95.495"/>
  <polygon fill="#404041" points="406.364,237.867 414.999,232.881 406.364,227.895 "/>
  <path d="M355,244.062c0,3.763-2.722,6.814-6.079,6.814h-15.842c-3.357,0-6.079-3.052-6.079-6.814l0,0
	c0-3.764,2.722-6.816,6.079-6.816h15.842C352.278,237.245,355,240.298,355,244.062L355,244.062z"/>
</g>
<g transform="scale(2)">
  <text transform="matrix(1 0 0 1 123.8267 126.877)">
    <tspan x="0" y="0" fill="#58595B" font-family="'ArialMT'" font-size="10">Gepi API</tspan>
    <tspan x="0" y="30" fill="#58595B" font-family="'ArialMT'" font-size="10">Deployment</tspan>
    <tspan x="0" y="60" fill="#58595B" font-family="'ArialMT'" font-size="10">Betrieb Migration</tspan>
    <tspan x="0" y="90" fill="#58595B" font-family="'ArialMT'" font-size="10">CloudOps</tspan>
  </text>
</g>
<g transform="scale(2)">
  <g>
    <rect x="262.194" y="203.459" fill="#009736" width="55" height="20"/>
    <rect x="317.112" y="203.5" fill="#F9C441" width="13.776" height="20"/>
    <rect x="330.806" y="203.5" fill="#E4342D" width="25.614" height="20"/>
  </g>
  <g >
    <rect x="256.847" y="173.459" fill="#009736" width="58.977" height="20"/>
    <rect x="315.55" y="173.459" fill="#E4342D" width="19.183" height="20"/>
  </g>
  <g >
    <rect x="218.75" y="143.459" fill="#009736" width="44.104" height="20"/>
    <rect x="262.64" y="143.459" fill="#F9C441" width="22.61" height="20"/>
  </g>
  <g>
    <rect x="206.097" y="113.459" fill="#009736" width="29.954" height="20"/>
  </g>
</g>
<g transform="scale(2)">
  <line fill="none" stroke="#BBBDBF" stroke-miterlimit="10" x1="206" y1="228.876" x2="206" y2="238.876"/>
  <line fill="none" stroke="#BBBDBF" stroke-miterlimit="10" x1="236" y1="228.876" x2="236" y2="238.876"/>
  <line fill="none" stroke="#BBBDBF" stroke-miterlimit="10" x1="266" y1="228.876" x2="266" y2="238.876"/>
  <line fill="none" stroke="#BBBDBF" stroke-miterlimit="10" x1="296" y1="228.876" x2="296" y2="238.876"/>
  <line fill="none" stroke="#BBBDBF" stroke-miterlimit="10" x1="326" y1="228.876" x2="326" y2="238.876"/>
  <line fill="none" stroke="#BBBDBF" stroke-miterlimit="10" x1="356" y1="228.876" x2="356" y2="238.876"/>
  <line fill="none" stroke="#BBBDBF" stroke-miterlimit="10" x1="386" y1="228.876" x2="386" y2="238.876"/>
</g>
<g transform="scale(2)">
  <text transform="matrix(1 0 0 1 209.3418 247.1533)">
    <tspan x="0" y="0" fill="#231F20" font-family="'ArialMT'" font-size="9">01</tspan>
    <tspan x="10.011" y="0" fill="#404041" font-family="'ArialMT'" font-size="9">/</tspan>
    <tspan x="12.511" y="0" fill="#808184" font-family="'ArialMT'" font-size="9">13</tspan>
  </text>
  <text transform="matrix(1 0 0 1 239.3887 247.1533)">
    <tspan x="0" y="0" fill="#231F20" font-family="'ArialMT'" font-size="9">02</tspan>
    <tspan x="10.011" y="0" fill="#404041" font-family="'ArialMT'" font-size="9">/</tspan>
    <tspan x="12.512" y="0" fill="#808184" font-family="'ArialMT'" font-size="9">13</tspan>
  </text>
  <text transform="matrix(1 0 0 1 268.9199 247.1533)">
    <tspan x="0" y="0" fill="#231F20" font-family="'ArialMT'" font-size="9">03</tspan>
    <tspan x="10.011" y="0" fill="#404041" font-family="'ArialMT'" font-size="9">/</tspan>
    <tspan x="12.512" y="0" fill="#808184" font-family="'ArialMT'" font-size="9">13</tspan>
  </text>
  <text transform="matrix(1 0 0 1 299.9199 247.1533)">
    <tspan x="0" y="0" fill="#231F20" font-family="'ArialMT'" font-size="9">04</tspan>
    <tspan x="10.011" y="0" fill="#404041" font-family="'ArialMT'" font-size="9">/</tspan>
    <tspan x="12.512" y="0" fill="#808184" font-family="'ArialMT'" font-size="9">13</tspan>
  </text>
  <text transform="matrix(1 0 0 1 329.2529 247.1533)" fill="#FFFFFF" font-family="'ArialMT'" font-size="9">05/13</text>
  <text transform="matrix(1 0 0 1 359.5859 247.1533)">
    <tspan x="0" y="0" fill="#231F20" font-family="'ArialMT'" font-size="9">06</tspan>
    <tspan x="10.011" y="0" fill="#404041" font-family="'ArialMT'" font-size="9">/</tspan>
    <tspan x="12.512" y="0" fill="#808184" font-family="'ArialMT'" font-size="9">13</tspan>
  </text>
</g>
<g transform="scale(2)">
  <text transform="matrix(-4.371139e-08 -1 1 -4.371139e-08 332.9268 83.2139)" fill="#231F20" font-family="'ArialMT'" font-size="9">today</text>
  <line fill="none" stroke="#231F20" stroke-width="1.5" stroke-linecap="round" stroke-miterlimit="10" x1="331" y1="237.291" x2="331" y2="235.291"/>
  <line fill="none" stroke="#231F20" stroke-width="1.5" stroke-linecap="round" stroke-miterlimit="10" stroke-dasharray="3.9403,3.9403" x1="331" y1="231.351" x2="331" y2="91.469"/>
  <line fill="none" stroke="#231F20" stroke-width="1.5" stroke-linecap="round" stroke-miterlimit="10" x1="331" y1="89.499" x2="331" y2="87.499"/>
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
    throw(t);
  }%>