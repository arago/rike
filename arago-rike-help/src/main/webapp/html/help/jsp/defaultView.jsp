<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
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


<%
      PortletPreferences prefs = renderRequest.getPreferences();

      if ("false".equals(prefs.getValue("de.arago.rike.help.shown", "false")))
      {
        //prefs.setValue("de.arago.rike.help.shown", "true");


%>

<script>
(function()
{
  // TODO hier das overlay einpflegen
  var div = document.createElement('div');
  
  div.innerHTML = '<div style="position:absolute; top:0px; left:0px; right:0px; bottom:0px; z-index:10000000000; background-color:#ccc; opacity:0.7"><img style="position:absolute; top:0px; left:0px; right:0px; bottom:0px; width:100%; height:100%;" src="<%= renderRequest.getContextPath()%>/images/helpoverlay.svg" alt="" /></div>';
  
  document.body.insertBefore(div, null);
})(); 
</script>
  
<%
      }
%>

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


