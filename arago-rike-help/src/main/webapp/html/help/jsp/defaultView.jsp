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
  var div = document.createElement('div');
  div.setAttribute("id","welcome-overlay");
  
  div.innerHTML = 
      '<div class="overlay">' +
          '<img alt="Welcomeoverlay" src="<%= renderRequest.getContextPath()%>/pix/overlay.png" />' +
          '<a class="close icon-remove" title="close" href="javascript:void(0)" ' +
            'onclick="window.location=\'<portlet:actionURL portletMode="view"/>&action=closeOverview&hide=\'+' +
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
    de.arago.help.Provider.register('', 
    {
      overlay: null,
      show:  function(topic)
      {
        var local = this;
        
        AUI().ready('aui-dialog', 'aui-overlay-manager', 'dd-constrain', function(A) 
        {
          $.ajax('<%= renderRequest.getContextPath()%>/help/help.'+encodeURIComponent(topic)+'.html', 
          {
            complete: function(ret)
            {
              if (ret.responseText)
              {
                var instance = new A.Dialog({
                  bodyContent: ret.responseText,
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
                  local.overlay = null;
                });
                
                local.overlay = instance;
                instance.render();
              };  
            }
          });
        });
      },
      hide: function()
      {
        if (this.overlay) 
        {
          this.overlay.close();
          this.overlay = null;
        };
      }
    });
    
  });
</script>


