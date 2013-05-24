<%@page import="net.minidev.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
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
    String data = (String) portletSession.getAttribute("overdue-json");
    List ticks = (List) portletSession.getAttribute("overdue-ms-names");
    long now = new Date().getTime();
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>

        <span>Exceeded date (<span style="color:<%= milestones.isEmpty() && tasks.isEmpty()?"#000":"#cc0000" %>"><%= milestones.size() + tasks.size() %></span>)</span>
        <span class="right">
          <a id="<portlet:namespace />whole" href="javascript:;">whole period</a>
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
            <li><a href="<portlet:actionURL portletMode="view"/>&action=showMilestones">Milestones (<span style="color:<%= milestones.isEmpty()?"#000":"#cc0000" %>"><%= milestones.size() %></span>)</a></li>
            <li><a href="<portlet:actionURL portletMode="view"/>&action=showTasks">Tasks (<span style="color:<%= tasks.isEmpty()?"#000":"#cc0000" %>"><%= tasks.size() %></span>)</a></li>
          </ul>
        </div>
      </div>


    </div>
    <div class="content nofooter">

      <div class="inner" id="<portlet:namespace />PortletContent">
        <div id="<portlet:namespace />chart"></div>
      </div>

      <script type="text/javascript">


            $(function()
            {

              try
              {
                $("#<portlet:namespace />chart").width($('#<portlet:namespace />PortletContent').width() - 20).height($("div.portletbox").height() - 80).show();
                var placeholder = $("#<portlet:namespace />chart");
                var result = <%= data%>;

                var markings = [
                  {color: "#cc0000", lineWidth: 1, xaxis: {from: <%= now%>, to: <%= now%>}}
                ];

                var options = {
                  legend: {show: false},
                  series: {
                    gantt: {active: true, show: true, barHeight: .5}                                    
                  },
                  grid: {hoverable: true, clickable: true, markings: markings},
                  xaxis: {mode: "time"},
                  yaxis: {ticks:<%= JSONArray.toJSONString(ticks)%>},
                  selection: {mode: "x"}
                };

                placeholder.bind("plotselected", function(event, ranges) {
                  $.plot(placeholder, result,
                          $.extend(true, {}, options, {
                    xaxis: {min: ranges.xaxis.from, max: ranges.xaxis.to}
                  }));
                });

                $.plot(placeholder, result, options);


                $("#<portlet:namespace />whole").click(function() {
                  $.plot(placeholder, result, options);
                });


              } catch (e) {
                alert(e);
              }
              ;
            });
      </script>
    </div>


      </div>
</div>
<% } catch (Throwable t) {

    out.write("Please Reload");
    t.printStackTrace(System.err);
    throw (t);
  }%>