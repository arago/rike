<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.data.Artifact"%>
<%@page import="de.arago.rike.data.Milestone"%>
<%@page import="de.arago.rike.util.TaskListFilter"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
<%@page import="de.arago.rike.data.TaskUser"%>
<%@page import="de.arago.rike.util.ViewHelper"%>
<%@page import="de.arago.rike.data.Task"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html; charset=UTF-8" %> 

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects />

<%
  UserService service = new JspUserService(renderRequest, portletSession);
  TaskListFilter filter = (TaskListFilter) portletSession.getAttribute("taskListFilter");
%>


<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        Dependencies
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.dependencies');" title="Help"><span class="icon">S</span></a> 
          <% if(renderRequest.getWindowState().equals(WindowState.MAXIMIZED)){ %>
            <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize"><span class="icon">%</span></a>
          <% } else { %>
            <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize"><span class="icon">%</span></a>
          <% } %>
        </span>
      </h1>
      <div class="inner">
        <!-- Filterfunktion-->
        <a href="javascript:void(0);" onclick="$('#<portlet:namespace />Filter').toggle();"><span class="icon">S</span> Filter <%= filter.isActive() ? "(active)" : ""%></a> <br />
        <div id="<portlet:namespace />Filter" class="dropDown" style="display:none; ">
          <form method="post" action="<portlet:actionURL portletMode="view"/>">
            <input type="hidden" name="action" value="filterTasks" />
            <table style="width:100%">
              <tbody>
                <tr>
                  <td>User:</td>
                  <td>
                    <select name="user"  class="rike-select">
                      <option <%= filter.getUser().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (TaskUser user: ViewHelper.getAvailableUsers()) {%>

                      <option <%= filter.getUser().equals(user.getEmail()) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(user.getEmail())%>"><%= StringEscapeUtils.escapeHtml(user.getEmail())%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Milestone:</td>
                  <td>
                    <select name="milestone"  class="rike-select">
                      <% for (String[] data: ViewHelper.getAvailableMilestones(service)) {%>

                      <option <%= filter.getMilestone().equals(data[0]) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(data[0])%>"><%= StringEscapeUtils.escapeHtml(data[1])%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Artifact:</td>
                  <td>
                    <select name="artifact"  class="rike-select">
                      <option <%= filter.getMilestone().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (Artifact artifact: ViewHelper.getAvailableArtifacts()) {%>

                      <option <%= filter.getArtifact().equals(artifact.getId().toString()) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(artifact.getId().toString())%>"><%= StringEscapeUtils.escapeHtml(artifact.getName())%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>


                <tr>
                  <td style="text-align:left"><input type="reset" value="Close" onclick="$('#<portlet:namespace />Filter').hide();"/></td>
                  <td style="text-align:right"><input type="submit" value="Ok" /></td>
                </tr>
              </tbody>
            </table>

          </form>

        </div>

      </div>
    </div>
    <script type="text/javascript">

    top.openRikeTask = function(id)
    {
      window.location = '/web/guest/rike/-/show/task/' + (id * 1);
      return false;
    };


      window.onload = <portlet:namespace />enrichSVG;

      var mySVG;

      <% if (renderRequest.getWindowState().equals(WindowState.MAXIMIZED)) {%>
        var data =
          {
          doc: null,
          elementNode: null,
          currentFromNode: null,
          currentToNode: null,
          currentPath: null,
          currentEdge: null,
          dragging: false,
          rendering: false,
          translate: {x: 0, y: 0},
          scale: {x: 1, y: 1},
          nodeIndicator: 'polygon',

          init: function(node, onconnect, onedgeselect)
          {
            var local = this;
            var doc = this.doc = node.getSVGDocument();
            this.elementNode = $('#graph1', doc).get(0);

            this.onconnect = onconnect;
            this.onedgeselect  = onedgeselect;

          },

          onEdgeClick: function(node, ev)
          {
            this.markEdge(node);
          },

          markEdge: function(node)
          {
            this.unmarkFromNode();
            this.unmarkToNode();
            if (this.currentEdge) this.unmarkEdge();
            this.currentEdge = node;

            $('path', node).get(0).setAttribute('stroke', 'red');
            $('polygon', node).get(0).setAttribute('stroke', 'red');

            this.onedgeselect(node, this);
          },

          unmarkEdge: function()
          {
            if (!this.currentEdge) return;

            $('path', this.currentEdge).get(0).setAttribute('stroke', 'black');
            $('polygon', this.currentEdge).get(0).setAttribute('stroke', 'black');

            this.currentEdge = null;
          },

          unmarkFromNode: function()
          {
            if (!this.currentFromNode) return;
            $(this.nodeIndicator, this.currentFromNode).get(0).removeAttribute('stroke-width');

            this.currentFromNode = null;
          },

          markFromNode: function(node)
          {
            this.unmarkEdge();
            if (this.currentFromNode) this.unmarkFromNode();

            this.currentFromNode = node;

            $(this.nodeIndicator, this.currentFromNode).get(0).setAttribute('stroke-width', '10');
          },

          unmarkToNode: function()
          {
            if (!this.currentToNode) return;
            $(this.nodeIndicator, this.currentFromNode).get(0).removeAttribute('stroke-width');

            this.currentToNode = null;
          },

          markToNode: function(node)
          {
            this.unmarkEdge();
            if (this.currentToNode) this.unmarkToNode();

            this.currentToNode = node;

            $(this.nodeIndicator, this.currentToNode).get(0).setAttribute('stroke-width', '10');

            this.onconnect(this.currentFromNode, this.currentToNode, this);
          },

          onNodeClick: function(node, ev)
          {
            if (this.currentFromNode && node !== this.currentFromNode)
            {
              this.markToNode(node);
            } else if (this.currentFromNode) {
              this.unmarkFromNode();
              this.unmarkToNode();
            } else {
              this.markFromNode(node);
            };

          }
        };

      <% }%>

        function <portlet:namespace />enrichSVG(evt)
        {
          var theSVG = window.document.getElementById('<portlet:namespace />SVG');
          var doc       = theSVG.getSVGDocument();
					
          var svgNode = $('svg', doc).get(0);

          var outer_width = $('#<portlet:namespace/>Portlet').width() - 12;
          var outer_height = $('#<portlet:namespace/>Portlet').height() - 60;

          $("#<portlet:namespace />SVG").width(outer_width).height(outer_height);
          $('#<portlet:namespace/>PortletContent').show();
	                
          mySVG = new de.arago.svg.SVGInteractive(doc,
          {
            scaleNode: doc,
            width: outer_width,
            height: outer_height
          });

          // remove the background generated by dot
          $('polygon', doc).get(0).setAttributeNS(null, 'fill', 'transparent');

      <% if (renderRequest.getWindowState().equals(WindowState.MAXIMIZED)) {%>

          $('g[class=node]', doc).each(function()
          {
            if (!$('polygon', this).get(0)) return;

            this.addEventListener('click', function(ev)
            {
              ev.preventDefault();
              ev.stopPropagation();
              data.onNodeClick(this, ev);
              return false;
            }, false);
          });

          $('g[class=edge]', doc).each(function()
          {
            this.addEventListener('click', function(ev)
            {
              ev.preventDefault();
              ev.stopPropagation();
              data.onEdgeClick(this, ev);
            }, false);
          });

          data.init(theSVG, function(from, to)
          {
            var fromId = $('title', from).get(0).textContent;
            var toId   = $('title', to).get(0).textContent;

            var url = '<%=renderRequest.getContextPath()%>/svg?action=connect';

            url += '&from=' + encodeURIComponent(fromId);
            url += '&to=' + encodeURIComponent(toId);

            $.ajax
            ({
              type: "GET",
              url: url,
              dataType: "json",
              success: function(ret) {
                if (ret.error)
                {
                  alert('error: ' + ret.error);
                } else {
                  document.location = '<portlet:actionURL portletMode="view"/>';
                };
              }
            });
          }, function(edge, data)
          {
            if (confirm('Remove Connection?'))
            {
              var parts = $('title', edge).get(0).textContent.split(/[^\d]+/);

              var url = '<%=renderRequest.getContextPath()%>/svg?action=disconnect';

              url += '&from=' + encodeURIComponent(parts[0]);
              url += '&to=' + encodeURIComponent(parts[1]);

              $.ajax
              ({
                type: "GET",
                url: url,
                dataType: "json",
                success: function(ret) {
                  if (ret.error)
                  {
                    alert('error: ' + ret.error);
                  } else {
                    edge.parentNode.removeChild(edge);
                  };
                }
              });
            };

            data.unmarkEdge();
          });
      <% }%>
	            
        };
			
    </script>
    <div class="content nofooter">
      <div style="position:relative; height:100%" id="<portlet:namespace/>PortletContent">
        <div id="<portlet:namespace />controlNode" style="<%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "" : "display:none;"%> position:absolute; top:0px; left:0px; width:170px; height:150px"></div>
        <embed src="<%=renderRequest.getContextPath()%>/svg?action=graph&user=<%= URLEncoder.encode(filter.getUser(), "UTF-8")%>&artifact=<%= URLEncoder.encode(filter.getArtifact(), "UTF-8")%>&milestone=<%= URLEncoder.encode(filter.getMilestone(), "UTF-8")%>" id="<portlet:namespace />SVG"  type="image/svg+xml" style="width:300px; height:200px;" />
      </div>
    </div>
  </div>
</div>
