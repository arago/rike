<%@page import="de.arago.rike.commons.data.Artifact"%>
<%@page import="de.arago.rike.commons.data.Milestone"%>
<%@page import="de.arago.rike.commons.data.TaskUser"%>
<%@page import="de.arago.rike.commons.util.TaskListFilter"%>
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
    try {
        List<Task> tasks = (List<Task>) portletSession.getAttribute("taskList");
        TaskListFilter filter = (TaskListFilter) portletSession.getAttribute("taskListFilter");
        Task currentTask = (Task) portletSession.getAttribute("task");
%>


<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        Overview: Tasks
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.overview');" title="Help" class="icon-question"></a>
          <% if (renderRequest.getWindowState().equals(WindowState.MAXIMIZED)) {%>
          <a href="<portlet:actionURL portletMode="view" windowState="normal"/>" title="Minimize" class="icon-resize-small"></a>
          <% } else {%>
          <a href="<portlet:actionURL portletMode="view" windowState="maximized"/>" title="Maximize" class="icon-resize-full"></a>
          <% }%>
        </span>
      </h1>
      <div class="inner">
        <div id="<portlet:namespace />Filter" class="dropDown" style="display:none;">
          <form method="post" action="<portlet:actionURL portletMode="view"/>">
            <input type="hidden" name="action" value="filterTasks" />
            <table style="width:100%">
              <tbody>
                <tr>
                  <td>User:</td>
                  <td>
                    <select name="user"  class="rike-select">
                      <option <%= filter.getUser().length() == 0 ? "selected='selected'" : ""%> value="">Any</option>
                      <% for (TaskUser user : ViewHelper.getAvailableUsers()) {%>

                      <option <%= filter.getUser().equals(user.getEmail()) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(user.getEmail())%>"><%= StringEscapeUtils.escapeHtml(user.getEmail())%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Creator:</td>
                  <td>
                    <select name="creator"  class="rike-select">
                      <option <%= filter.getCreator().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (TaskUser user : ViewHelper.getAvailableUsers()) {%>

                      <option <%= filter.getCreator().equals(user.getEmail()) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(user.getEmail())%>"><%= StringEscapeUtils.escapeHtml(user.getEmail())%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Status:</td>
                  <td>
                    <select name="status"  class="rike-select">
                      <option <%= filter.getStatus().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (String status : ViewHelper.getStatus()) {%>

                      <option <%= filter.getStatus().equalsIgnoreCase(status) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(status)%>"><%= StringEscapeUtils.escapeHtml(ViewHelper.getStatus(status))%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Priority:</td>
                  <td>
                    <select name="priority"  class="rike-select">
                      <option <%= filter.getStatus().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (String priority : ViewHelper.getPriorities()) {%>

                      <option <%= filter.getPriority().equalsIgnoreCase(priority) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(priority)%>"><%= StringEscapeUtils.escapeHtml(ViewHelper.getPriority(priority))%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Milestone:</td>
                  <td>
                    <select name="milestone"  class="rike-select">
                      <option <%= filter.getMilestone().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (String[] data : ViewHelper.getAvailableMilestones()) {%>

                      <option <%= filter.getMilestone().equals(data[0]) ? "selected='selected'" : ""%> value="<%= StringEscapeUtils.escapeHtml(data[0])%>"><%= StringEscapeUtils.escapeHtml(data[1])%></option>

                      <% }%>

                    </select>

                  </td>
                </tr>

                <tr>
                  <td>Artifact:</td>
                  <td>
                    <select name="artifact" class="rike-select">
                      <option <%= filter.getMilestone().length() == 0 ? "selected='selected'" : ""%> value="">All</option>
                      <% for (Artifact artifact : ViewHelper.getAvailableArtifacts()) {%>

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

        <a class="submit rike-addbutton icon-plus-sign" onclick="$('#<portlet:namespace/>New').toggle()"></a>
        <ul class="rike-add-dropdown" id="<portlet:namespace/>New">
          <li><a href="<portlet:actionURL portletMode="view" />&action=createTask">Task</a></li>
          <li><a href="<portlet:actionURL portletMode="view" />&action=createMilestone">Milestone</a></li>
          <li><a href="<portlet:actionURL portletMode="view" />&action=createArtifact">Artifact</a></li>
        </ul>

        <input onclick="if (this.value === 'Search...') this.value = '';" onblur="" 
               id="<portlet:namespace/>search" class="rike-input rike-search" placeholder="Search..." type="text"/>
        
          <script type="text/javascript">
            $(function()
            {
              var statusColors =
                      {
                        OPEN: "status-critical",
                        IN_PROGRESS: "status-warning",
                        DONE: "status-ok",
                        UNKNOWN: "status-unknown"
                      };

              $("#<portlet:namespace/>search").autocomplete("<portlet:resourceURL />&as=json&action=findTask",
                      {
                        max: 10,
                        minChars: 1,
                        dataType: 'json',
                        delay: 50,
                        width: '250px',
                        selectFirst: false,
                        widthNode: document.getElementById('<portlet:namespace/>Searchbox'),
                        offsetRight: 250,
                        onstart: function()
                        {
                          //$('#<portlet:namespace/>SearchboxButton').get(0).style.backgroundImage = 'url(/wisdome-theme/pix/ajax-loader.gif)';
                        },
                        onend: function()
                        {
                          //$('#<portlet:namespace/>SearchboxButton').get(0).style.backgroundImage = '';
                        },
                        onResult: function(item)
                        {
                          window.location = '<portlet:actionURL portletMode="view"/>&action=selectTask&id=' + encodeURIComponent(item.value);

                          return false;
                        },
                        parse: function(text)
                        {
                          var parsed = [];

                          for (var i = 0; i < text.items.length; ++i)
                          {
                            var item = text.items[i];
                            parsed[parsed.length] = {
                              data: '<span class="' + statusColors[item.status] + '">&nbsp;</span> <span class="priority-' + item.priority.toLowerCase() + '">&nbsp;</span> <span class="name">#' + item.id + ' ' + item.name + '</span><span class="status">' + (item.owner ? 'completed by ' + item.owner : 'not completed') + '</span>',
                              value: text.items[i].id,
                              result: text.items[i].id
                            };
                          }
                          ;

                          return parsed;
                        },
                        formatItem: function(row)
                        {
                          return row;
                        }

                      });
            });

          </script>
        
        <a href="javascript:void(0);" onclick="$('#<portlet:namespace />Filter').toggle();" class="right"> 
          <span class="icon-filter"></span> Filter <%= filter.isActive() ? "(active)" : ""%>
        </a> 
        <br/>
      </div>
    </div>
    <!-- content -->
    <div class="content nofooter">
      <div id="<portlet:namespace />TableScroll">
        <table class="list">
          <thead>
            <tr>
              <th class="id shrink center" title="ID"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.ID.toString()%>">#</a></th>
              <th class="status shrink center" title="Status"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.STATUS.toString()%>" title="Status">?</a></th>
              <th class="prio shrink center" title="Priority"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.PRIORITY.toString()%>" title="Priority">Prio</a></th>
              <th class="name" title="Name"><a href="<portlet:actionURL portletMode="view" />&action=orderBy&field=<%= TaskListFilter.SortField.TITLE.toString()%>">Name</a></th>
              <th class="shrink center"></th>
            </tr>
          </thead>
          <tbody>
            <%

                for (Task task : tasks) {

            %>
            <tr<%= currentTask != null && currentTask.getId().equals(task.getId()) ? " class=\"selected\"" : ""%>>
              <td class="id shrink"><%= StringEscapeUtils.escapeHtml(task.getId().toString())%></td>
              <td class="status shrink"><span class="<%= ViewHelper.getTaskStatusColorClass(task)%>"></span></td>
              <td class="prio shrink"><%= task.getPriority()%></td>
              <td class="name"><a href="<portlet:actionURL portletMode="view" />&action=selectTask&id=<%= URLEncoder.encode(task.getId().toString(), "UTF-8")%>"><%= StringEscapeUtils.escapeHtml(task.getTitle())%></a>
                <% if (task.getOwner() != null && !task.getOwner().isEmpty()) {%>
                <br />
                <%= ViewHelper.formatUser(task)%>
                <% }%>
              </td>
			 <td class="arrow shrink"><span class="icon-chevron-right"></span></td>
            </tr>
            <%
                }
            %>
          </tbody>
        </table>

      </div>

      <script type="text/javascript">
        <% if (currentTask != null) {%>
            $(function()
            {
              var el = $('#<portlet:namespace />TableScroll .selected').get(0);

              try
              {
                if (el)
                  el.scrollIntoView();
              } catch (e) {
                ;
              }
              ;
            });
        <% }%>
      </script>
    </div>
  </div>
</div>  
<%
    } catch (Throwable t) {
        out.write("Please Reload");
        t.printStackTrace(System.err);
    }
%>
