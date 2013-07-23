<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@page import="de.arago.portlet.jsp.UserService"%>
<%@page import="de.arago.portlet.jsp.JspUserService"%>
<%@page import="de.arago.rike.commons.data.Artifact"%>
<%@page import="de.arago.rike.commons.data.Milestone"%>
<%@page import="de.arago.rike.commons.util.TaskListFilter"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
<%@page import="de.arago.rike.commons.data.TaskUser"%>
<%@page import="de.arago.rike.commons.util.ViewHelper"%>
<%@page import="de.arago.rike.commons.data.Task"%>
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
  String lastActivity = "" + portletSession.getAttribute("lastActivity");
  String portletTitle = "" + portletSession.getAttribute("portletTitle");
%>


<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        <div class="ellipsis">
          <%= portletTitle%>
        </div>
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.dependencies');" title="Help" class="icon-question"></a> 
          <% if(renderRequest.getWindowState().equals(WindowState.MAXIMIZED)){ %>
            <a href="<portlet:actionURL portletMode="view" windowState="normal"/>"  title="Minimize" class="icon-resize-small"></a>
          <% } else { %>
            <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize" class="icon-resize-full"></a>
          <% } %>
        </span>
      </h1>
    </div>
    <script type="text/javascript">

    top.openRikeTask = function(id)
    {
      window.location = '<portlet:actionURL portletMode="view"/>&action=selectTask&id=' + (id * 1);
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

        function <portlet:namespace />enrichSVG(evt, counter)
        {
          var theSVG = window.document.getElementById('<portlet:namespace />SVG');
          var doc       = theSVG.getSVGDocument();
          
          counter = counter || 0;
          
          if (counter > 100) return;
          
          if (!doc)
          {
            window.setTimeout(function()
            {
              ++counter;
              <portlet:namespace />enrichSVG(evt, counter);
            }, 50);
            
            return;
          };  
          
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

            var url = '<portlet:actionURL portletMode="view"/>&action=connect';

            url += '&from=' + encodeURIComponent(fromId);
            url += '&to=' + encodeURIComponent(toId);

            document.location = url;
          }, function(edge, data)
          {
            if (confirm('Remove Connection?'))
            {
              var parts = $('title', edge).get(0).textContent.split(/[^\d]+/);

              var url = '<portlet:actionURL portletMode="view"/>&action=disconnect';

              url += '&from=' + encodeURIComponent(parts[0]);
              url += '&to=' + encodeURIComponent(parts[1]);

              document.location = url;
            };

            data.unmarkEdge();
          });
      <% }%>
	            
        };
			
    </script>
    <div class="content nofooter nohead">
      <div style="position:relative; height:100%" id="<portlet:namespace/>PortletContent">
        <div id="<portlet:namespace />controlNode" style="<%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "" : "display:none;"%> position:absolute; top:0px; left:0px; width:170px; height:150px"></div>
        <embed src="<%=renderRequest.getContextPath()%>/svg?action=graph&user=<%= URLEncoder.encode(filter.getUser(), "UTF-8")%>&lastActivity=<%=lastActivity %>&milestone=<%= URLEncoder.encode(filter.getMilestone(), "UTF-8")%>" id="<portlet:namespace />SVG"  type="image/svg+xml" />
      </div>
    </div>
  </div>
</div>
