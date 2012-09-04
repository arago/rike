<%
/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
%>

<%@ include file="/html/portlet/dockbar/init.jsp" %>
<%
Group group = null;

if (layout != null) {
	group = layout.getGroup();
}

List<Portlet> portlets = new ArrayList<Portlet>();

for (String portletId : PropsValues.DOCKBAR_ADD_PORTLETS) {
	Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);

	if (portlet.isInclude() && portlet.isActive() && portlet.hasAddPortletPermission(user.getUserId())) {
		portlets.add(portlet);
	}
}
%>

<div style="position:fixed; left:0; bottom:0; width:100%; z-index:1000000000" idd="footer" class="" data-namespace="<portlet:namespace />" id="dockbar" >
<div class="pin-dockbar" style="display:none">
	<a href="javascript:;"><img alt='<liferay-ui:message key="pin-the-dockbar" />' src="<%= HtmlUtil.escape(themeDisplay.getPathThemeImages()) %>/spacer.png" /></a>
</div>
  <div class="toolbar clearfix">
  		<c:if test="<%= (group != null) && !group.isControlPanel() && (!group.hasStagingGroup() || group.isStagingGroup()) && LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.UPDATE) %>">  
  <div class="leftmenu">
  <ul class="dropshadow">
  <li > 
    
			<a href="#" id="<portlet:namespace />addContent">Add <span class="icon">+</span></a>  
  <div class="submenu">
      <ul>
      <li class="add-page"><a href="javascript:;" id="addPage">Page</a></li> 
      <li>
        <a href="javascript:;" id="<portlet:namespace />addApplication">
					Portlet
				</a>
        </li>
        
      </ul>
  </div>
  </li>
  </ul>
  <ul class="dropshadow" id="<portlet:namespace />manageContent">
  <li><a href="#"><span class="icon">:</span> Page Management </a>
  <div class="submenu" id="<portlet:namespace />manageContentContainer">
      <ul>
      <li><a href="<%= themeDisplay.getURLPageSettings().toString() %>">Edit</a></li> 
      <li class="page-layout"><a href="javascript:;" id="pageTemplate">Layout</a></li>
      <c:if test="<%= themeDisplay.isShowPageSettingsIcon() && !group.isLayoutPrototype() %>">
        <li><a href="<%= HttpUtil.setParameter(themeDisplay.getURLPageSettings().toString(), "selPlid", "-1") %>">Sitemap</a></li>
			</c:if>


      <%
			String pageSettingsURL = themeDisplay.getURLPageSettings().toString();

			pageSettingsURL = HttpUtil.removeParameter(pageSettingsURL, "tabs1");
			pageSettingsURL = HttpUtil.setParameter(pageSettingsURL, PortalUtil.getPortletNamespace(PortletKeys.LAYOUT_MANAGEMENT) + "tabs1", "settings");
			%>
      <li><a href="<%= pageSettingsURL %>">Settings</a></li> 
      <li><a href="<%= themeDisplay.getURLControlPanel() %>">Control Panel</a></li>
      </ul>
  </div>
  </li>
  </ul> 
  </div>
  </c:if>
  
  <div class="rightmenu">
  <ul class="dropshadow">  
  <li><a href="#"><span class="icon">:</span> 
    <c:if test="<%= themeDisplay.isSignedIn() %>">
      <%= HtmlUtil.escape(user.getEmailAddress()) %>
    </c:if>
    <c:if test="<%= !themeDisplay.isSignedIn() %>">
      Login
    </c:if>
    </a>
  <div class="submenu">
      <ul>
      <c:if test="<%= themeDisplay.isSignedIn()%>">  
      <c:if test="<%= LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.UPDATE) %>">
      <li class="toggle-controls" id="<portlet:namespace />toggleControls">
				<a href="javascript:;">
					<liferay-ui:message key="toggle-edit-controls" />
				</a>
			</li>
			</c:if>
        <li><a href="<%= themeDisplay.getURLMyAccount() %>"><span class="icon">S</span>Settings</a></li> 
      </c:if>
      <c:if test="<%= themeDisplay.isShowSignOutIcon() %>">
        <li><a href="<%= themeDisplay.getURLSignOut() %>"><span class="icon">X</span>Logout</a></li>
			</c:if>

      <c:if test="<%= !themeDisplay.isShowSignOutIcon() %>">      
       <li><a href="<%= themeDisplay.getURLSignIn() %>"><span class="icon">X</span>Login</a></li>
			</c:if>
      </ul>
  </div>
  </li>
  </ul>
  <ul class="dropshadow last">  
  <li><a href="#"><span class="icon">:</span> Rike</a>
  <div class="submenu">
   <ul>
      <li><a href="https://github.com/arago/rike/wiki">Help</a></li> 
      <li><a href="https://github.com/arago/rike/blob/master/README.md">About</a></li>
      </ul>
  </div>
  </li>
  </ul></div>
  </div>
  </div>
  
  
  <aui:script position="inline" use="liferay-dockbar">
  	Liferay.Dockbar.init();
  </aui:script>