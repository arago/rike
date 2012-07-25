<%@page import="de.arago.rike.data.Artifact"%>
<%@page import="de.arago.rike.data.Milestone"%>
<%@page import="de.arago.rike.util.ViewHelper"%>
<%@page import="de.arago.portlet.util.SecurityHelper"%>
<%@page import="de.arago.rike.data.Task.Status"%>
<%@page import="de.arago.rike.data.Task"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ page contentType="text/html; charset=UTF-8" %> 

<portlet:defineObjects />

<%
  try {
    Artifact artifact = (Artifact) portletSession.getAttribute("artifact");
%>

<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        New Artifact
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.task');" title="Help"><span class="icon">S</span></a> 
          <a href="<portlet:actionURL portletMode="view" windowState="<%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "normal" : "maximized"%>"/>" title="Maximize"><span class="icon">%</span></a>
        </span>
      </h1>
    </div>
    <div class="content nohead nofooter">

        <form method="post" class="<portlet:namespace />form" action="<portlet:actionURL portletMode="view" />">
          <div>
            <input type="hidden" name="action" value="saveArtifact" />
            <input type="hidden" name="id" value="<%= StringEscapeUtils.escapeHtml(artifact == null || artifact.getId() == null ? "" : artifact.getId().toString())%>" />
          </div>
          <table>
            <tbody>

              <tr>
                <th class="shrink">Name:</th>
                <td class="shrink"><input type="text" name="name" value="<%= StringEscapeUtils.escapeHtml(artifact == null ? "" : artifact.getName())%>" /></td>
              </tr>

              <tr>
                <th class="shrink">Short Name:</th>
                <td class="shrink"><input type="text" name="short_name" value="<%= StringEscapeUtils.escapeHtml(artifact == null ? "" : artifact.getShortName())%>" /></td>
              </tr>

              <tr>
                <th class="shrink">URL:</th>
                <td class="shrink"><input type="text" name="url" value="<%= StringEscapeUtils.escapeHtml(artifact == null ? "" : artifact.getUrl())%>"/></td>
              </tr>



              <tr>
                <td class="shrink"><input type="reset" value="Close" onclick="document.location= '<portlet:actionURL portletMode="view" />&action=abort';"/></td>
                <td class="shrink" style="text-align:right"><input type="submit" value="Create" /></td>
              </tr>
            </tbody>
          </table>
        </form>

        <script type="text/javascript">
          (function()
          {
            $('.<portlet:namespace />form').submit(function()
            {
              try
              {
                var ok = true;
						
                $([$("input[name=name]", this), $("input[name=short_name]", this), $("input[name=url]", this)]).each(function()
                {
                  if (!this.val())
                  {
                    this.get(0).style.borderColor = 'red';
                    ok = false;
                  } else {
                    this.get(0).style.borderColor = '';
                  };
                });


                return ok;
              } catch(e) {
                alert(e);
              };

              return false;
            });
          })();
        </script>

    </div>
  </div>
</div>

<% } catch (Throwable t) {
    out.write("Please Reload");
    t.printStackTrace(System.err);
  }%>