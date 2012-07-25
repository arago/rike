<div class="portlet big <%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "maximized" : ""%>" style="" id="<portlet:namespace />Portlet">
  <div class="portletbox">
    <!-- head -->
    <div class="head">
      <h1>
        All Tasks
        <span class="right">
          <a href="javascript:void(0);" onclick="return de.arago.help.Provider.show('rike.overview');" title="Help"><span class="icon">S</span></a> 
          <a href="<portlet:actionURL portletMode="view" windowState="<%= renderRequest.getWindowState().equals(WindowState.MAXIMIZED) ? "normal" : "maximized"%>"/>" title="Maximize"><span class="icon">%</span></a>
        </span>
      </h1>
      <div class="inner">
        
      </div>
    </div>
    <!-- content -->
    <div class="content">
      <div class="inner">
        
      </div>
    </div>
    <!-- footer -->
    <div class="footer">
      <div class="inner">
        
      </div>  
    </div>
  </div>
</div> 