<%@ page session="false"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.portlet.*"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="org.apache.commons.lang.*" %>
<%@ page contentType="text/html; charset=UTF-8" %> 

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:defineObjects/>
<%
  try {
    String type = (String) portletSession.getAttribute("type");
    String typeName = (String) portletSession.getAttribute("typeName");
    String milestone = (String) portletSession.getAttribute("milestone");

    milestone = milestone == null ? "" : milestone;
%>

<script type="text/javascript">
  $.globalPortletJS
  ([
    '/arago-rike/js/flot/jquery.flot.js',
    '/arago-rike/js/flot/jquery.flot.navigate.min.js',
    '/arago-rike/js/flot/jquery.flot.fillbetween.min.js',
    '/arago-rike/js/flot/jquery.flot.selection.js'
  ], false);
</script>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        <%= typeName%>
        <span class="right">
          <% if (type.equals("taskstatus")) {%>
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.taskstatus');" title="Help"><span class="icon">S</span></a>
          <% } else {%>
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.burndown');" title="Help"><span class="icon">S</span></a>
          <% }%>
          <a href="<portlet:actionURL portletMode="view" windowState="<%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "normal" : "maximized"%>"/>" title="Maximize"><span class="icon">%</span></a>
        </span>
      </h1>
    </div>

    <div class="content nohead nofooter">
      <div class="inner" id="<portlet:namespace />PortletContent">
        <div id="<portlet:namespace />chart"></div>
            <p><input id="whole" type="button" value="Whole period" />
             <label><input id="zoom" type="checkbox" />Zoom to selection.</label></p>
	 </div>
      
      <script type="text/javascript">

          $(function()
          {
            window.setTimeout(function()
            {

              var poll;

              // add leading zero
              var z = function(what)
              {
                return what < 10?'0'+what:what;
              };

              var format = function(date)
              {
                return z(date.getDate()) + '.' + z(date.getMonth() + 1) + '.' +  date.getFullYear();
              };

              var url = '<%=renderRequest.getContextPath()%>/report/json?type=<%= type%>&milestone=<%= URLEncoder.encode(milestone, "UTF-8")%>';

              poll = function(noRepoll)
              {
                $("#<portlet:namespace />chart").width($('#<portlet:namespace />PortletContent').width() - 40).height($("div.portletbox").height()-80).show();
		var placeholder = $("#<portlet:namespace />chart");            
                try
                {
									

                  $.ajax
                  ({
                    url: url,
                    type: 'get',
                    dataType: 'json',
                    success: function(result)
                    {
                  var theId = 0,
                      tmp   = [],
                      enablePlane = <%= type.equals("taskstatus") ? "true" : "false"%>;
                  
                      var placeholder = $("#<portlet:namespace />chart");
                      $(result).each(function()
                      {
                        if (enablePlane)
                        {  
                          ++theId;
                          this.id = "plot" + theId;
                          this.fillBetween = "plot" + (theId - 1);
                        };
                    
                        tmp.push(this);
                      });
                       result = tmp;
                        var options = {
                                legend: { show:false },
                                series: {
                                stack:1,
                                lines: { show: true, fill:enablePlane},                                         
                                        //bars:{show: false},
                                        points: { show: true }
                                        },
                                 grid:{hoverable:true,clickable:true},
                                 xaxis: { mode:"time"},
                                 yaxis: { min:null,tickFormatter:function(m,n){return(m/1000)+"k LOC"}},
                                 selection: { mode: "x" }
                                 };                     


                                                                    placeholder.bind("plotselected", function (event, ranges) {
                                                                   // $("#selection").text(ranges.xaxis.from.toFixed(1) + " to " + ranges.xaxis.to.toFixed(1));
                                                                      
                                                                    var zoom = $("#zoom").attr("checked");
                                                                    if (zoom)
                                                                        //plot= $.plot(placeholder, e,
                                                                        $.plot(placeholder, result,
                                                                              $.extend(true, {}, options, {
                                                                                  xaxis: { min: ranges.xaxis.from, max: ranges.xaxis.to }
                                                                              }));
                                                                    }); 

                      $.plot(placeholder, result, options);

                                                                
                       $("#whole").click(function () {
                              $.plot(placeholder,result, options);
                              }); 
                                                                
                       placeholder.bind("plotunselected", function (event) {
                              $("#selection").text("");
                              });
                                                                    
                      function showTooltip(x, y, contents) {
                        $('<div id="<portlet:namespace />tooltip">' + contents + '</div>').css( {
                          position: 'absolute',
                          display: 'none',
                          top: y + 5,
                          left: x + 5,
                          border: '1px solid #fdd',
                          padding: '2px',
                          'background-color': '#fee',
                          opacity: 0.80
                        }).appendTo("body").fadeIn(200);
                      }


                      var previousPoint = null;
                  
                  
                      placeholder.bind("plotclick", function(event, pos, item) 
                      {
                        if (item)
                        {
                          if (enablePlane)
                          {  
                            document.location = '<portlet:actionURL />&action=filterTasks&status=' + encodeURIComponent(item.series.key);
                          } else {
                            document.location = '<portlet:actionURL />&action=selectMilestone&milestone=' + encodeURIComponent(item.series.key);
                          };
                        };  
                      });
                  
                      placeholder.bind("plothover", function (event, pos, item) {

                        if (item) {
                          if (previousPoint != item.datapoint) {
                            previousPoint = item.datapoint;

                            $("#<portlet:namespace />tooltip").remove();
                            var x = format(new Date(item.datapoint[0])),
                            y = item.datapoint[1];

                            showTooltip(item.pageX, item.pageY,
                            (item.series.label || '') + "  " + x + " = " + (y / 1000) + "k");
                          }
                        }
                        else {
                          $("#<portlet:namespace />tooltip").remove();
                          previousPoint = null;
                        }
										
                      });
                     
                    }
                  });

                } catch(e) { alert(e); };
              };

              poll();

            }, 150);
          });
        </script>
    </div>
  </div>
</div>
    <% } catch (Throwable t) {
        out.write("Please Reload");
        t.printStackTrace(System.err);
  }%>
