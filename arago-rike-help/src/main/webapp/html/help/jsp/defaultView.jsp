<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="java.util.Date" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:defineObjects />

<%
        if(!"true".equals(portletSession.getAttribute("help.shown")))
        {
%>

<script>
(function()
{
  // TODO hier das overlay einpflegen
  var div = document.createElement('div');
  div.setAttribute("id","welcome-overlay");
  
  div.innerHTML = 
      '<div class="overlay">' +
          '<img alt="Welcomeoverlay" src="<%= renderRequest.getContextPath()%>/pix/overlay.png" />' +
          '<a class="close icon-remove" title="close" href="javascript:void(0)" ' +
            'onclick="window.location=\'<portlet:actionURL portletMode="view"/>&action=closeOwerview&hide=\'+' +
            'document.<portlet:namespace/>IsHide.TheCheckBox.checked">' +
          '</a>' +
  	      '<div class="right">' +
              '<form name="<portlet:namespace/>IsHide">' +
                  '<input type="checkbox" name="TheCheckBox"/>' +
                  '<label for="hide">Hide overlay</label>' +
              '</form>' +
          '</div>'+
      '</div>';
      
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


