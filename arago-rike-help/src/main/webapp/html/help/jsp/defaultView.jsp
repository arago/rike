<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.List"%>
<%@ page import="java.util.Date" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:defineObjects />
<!--<script type="text/javascript">
$.globalPortletJS
	([
		'/arago-rike/js/help/help.js'
	]);
</script>-->
<style>
 .help-left-pane {padding:0px}
 .help-left-pane iframe {overflow:auto}
 .help-right-pane {padding:3px}
</style>
<script type="text/javascript">
  $(function()
  {
    de.arago.help.Provider.register('', function(topic)
    {
      AUI().ready('aui-dialog', 'aui-overlay-manager', 'dd-constrain', function(A) 
      {
        var content =
        [
          '<table><tbody><tr><td class="help-left-pane" valign="top">',
          '<iframe id="<portlet:namespace/>HelpFrame" name="contentwin" src="<%= renderRequest.getContextPath()%>/help/help.'+encodeURIComponent(topic)+'.html"></iframe>',
          '</td><td class="help-right-pane" valign="top"><input style="display:none" type="text" placeholder="Search ..."/> <br />',
          '<a target="contentwin" href="<%= renderRequest.getContextPath()%>/help/help.rike.index.html">Index</a></td></tr></tbody></table>'
        ];

        var instance = new A.Dialog({
          bodyContent: content.join(""),
          centered: true,
          constrain2view: true,
          destroyOnClose: true,
          draggable: false,
          modal:true,
          height: '75%',
          resizable: false,
          stack: true,
          title: 'Help',
          width: '75%'
        });

        instance.on('close', function()
        {
          
        });

        instance.render();

        var p	= $(instance.get('bodyContent').getDOM()[0].parentNode);

        var fixSize = function()
        {
          // this does not work, due to yui not resizing the content node correctly
          $('#<portlet:namespace/>HelpFrame').height(p.height() - 40).width(p.width() - 200);
        };

        fixSize();

        $(window).resize(fixSize);
      });
    });
  });
</script>


