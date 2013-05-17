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
    <div class="content">

      <div class="inner" id="<portlet:namespace />PortletContent">
        <div id="<portlet:namespace />chart"></div>
      </div>



      <%
        // http://jumflot.jumware.com/examples/Experimental/Gantt.html
        // MIT License
        final List data = new ArrayList();
        // [[1,"Bowl"],[2,"Cup"],[3,"Can"],[4,"Desk"]]
        final List ticks = new ArrayList();
        final Map open = new HashMap();
        final Map done = new HashMap();
        final Map overdue = new HashMap();
        final boolean isMaximized = renderRequest.getWindowState().equals(WindowState.MAXIMIZED);
        
        data.add(open);
        data.add(done);
        data.add(overdue);

        open.put("Label", "open");
        done.put("Label", "done");
        overdue.put("Label", "overdue");

        open.put("data", new ArrayList());
        done.put("data", new ArrayList());
        overdue.put("data", new ArrayList());
        long now = new Date().getTime();

        int i = 1;
        long farInTheFuture = System.currentTimeMillis();
        long farInThePast   = System.currentTimeMillis();

        for (OverdueMilestone o : milestones) {
          Milestone stone = o.getMilestone();

          final List tick = new ArrayList();
          tick.add(i);
          tick.add(isMaximized?StringEscapeUtils.escapeHtml(stone.getTitle()):"<span title='"+StringEscapeUtils.escapeHtml(stone.getTitle())+"'>"+StringEscapeUtils.escapeHtml(stone.getTitle().substring(0, 5))+"..</span>");
          ticks.add(tick);

          List doneData = (List) done.get("data");

          List item = new ArrayList();
          item.add(stone.getCreated().getTime());
          item.add(i);
          item.add(stone.getDueDate().getTime());
          item.add("");
          doneData.add(item);

          List openData = (List) open.get("data");

          item = new ArrayList();
          item.add(stone.getDueDate().getTime());
          item.add(i);
          item.add(o.getEstimatedDoneDate().getTime());
          item.add("");
          openData.add(item);

          if (farInTheFuture < o.getEstimatedDoneDate().getTime()) {
            farInTheFuture = o.getEstimatedDoneDate().getTime();
          }
          
          if (farInThePast > stone.getCreated().getTime()) {
            farInThePast = stone.getCreated().getTime();
          }  

          ++i;
        }

        farInThePast   -= (14 * 24 * 60 * 60 * 1000l);
        farInTheFuture += (14 * 24 * 60 * 60 * 1000l);
      %>
      <script type="text/javascript">


            $(function()
            {

              try
              {
                $("#<portlet:namespace />chart").width($('#<portlet:namespace />PortletContent').width() - 20).height($("div.portletbox").height() - 100).show();
                var placeholder = $("#<portlet:namespace />chart");
                var result = <%= JSONArray.toJSONString(data)%>;



                var markings = [
                  {color: "#cc0000", lineWidth: 1, xaxis: {from: <%= now%>, to: <%= now%>}}
                ];

                var options = {
                  legend: {show: false},
                  series: {
                    gantt: {active: true, show: true, barHeight: .5}                                    
                  },
                  grid: {hoverable: true, clickable: true, markings: markings},
                  xaxis: {mode: "time", min:<%= farInThePast %>, max:<%= farInTheFuture %>},
                  yaxis: {min: 0.5, max:<%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? ticks.size() + .5 : 5.5%>, ticks:<%= JSONArray.toJSONString(ticks)%>},
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