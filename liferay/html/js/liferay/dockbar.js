AUI().add(
	'liferay-dockbar',
	function(A) {
		Liferay.Dockbar = {
			init: function() {
				var instance = this;

				var body = A.getBody();

				var dockBar = A.one('#dockbar');

				if (dockBar) {
					dockBar.one('.pin-dockbar').on(
						'click',
						function(event) {
							body.toggleClass('lfr-dockbar-pinned');

							var pinned = body.hasClass('lfr-dockbar-pinned');

							A.io.request(
								themeDisplay.getPathMain() + '/portal/session_click',
								{
									data: {
										'liferay_dockbar_pinned': pinned
									}
								}
							);

							event.halt();

							Liferay.fire(
								'dockbar:pinned',
								{
									pinned: pinned
								}
							);
						}
					);

					instance.dockBar = dockBar;

					instance._namespace = dockBar.attr('data-namespace');

					instance.MenuManager = new A.OverlayManager(
						{
							zIndexBase: 100000
						}
					);

					instance.UnderlayManager = new A.OverlayManager(
						{
							zIndexBase: 300
						}
					);

					instance._toolbarItems = {};

					instance.addUnderlay(
						{
							align: {
								node: instance.dockBar,
								points: ['tc', 'bc']
							},
							bodyContent: '',
							boundingBox: '#' + instance._namespace + 'dockbarMessages',
							header: 'My messages',
							name: 'messages',
							visible: false
						}
					);

					instance.messages.on(
						'visibleChange',
						function(event) {
							if (event.newVal) {
								A.getBody().addClass('showing-messages');

								Liferay.Dockbar.MenuManager.hideAll();
							}
							else {
								A.getBody().removeClass('showing-messages');
							}
						}
					);

					instance.messages.closeTool.on('click', instance.clearMessages, instance);

					instance.dockBar.once(
						'mousemove',
						function() {
							instance.addMenu(
								{
									boundingBox: '#' + instance._namespace + 'addContentContainer',
									name: 'addContent',
									trigger: '#' + instance._namespace + 'addContent'
								}
							);

							if (instance.addContent) {
								instance.addContent.on(
									'show',
									function() {
										Liferay.fire('initLayout');
										Liferay.fire('initNavigation');
									}
								);

								instance.addContent.get('boundingBox').delegate(
									'click',
									function(event) {
										var item = event.currentTarget;

										var portletId = item.attr('data-portlet-id');

										if ((/^\d+$/).test(portletId)) {
											Liferay.Portlet.add(
												{
													portletId: portletId
												}
											);
										}

										if (!event.shiftKey) {
											Liferay.Dockbar.MenuManager.hideAll();
										}

										event.halt();
									},
									'.app-shortcut'
								);
							}

							/*instance.addMenu(
								{
									boundingBox: '#' + instance._namespace + 'manageContentContainer',
									name: 'manageContent',
									trigger: '#' + instance._namespace + 'manageContent'
								}
							);

							instance.addMenu(
								{
									boundingBox: '#' + instance._namespace + 'myPlacesContainer',
									name: 'myPlaces',
									trigger: '#' + instance._namespace + 'myPlaces'
								}
							);*/

							var userOptionsContainer = A.one('#' + instance._namespace + 'userOptionsContainer');

							if (userOptionsContainer) {
								instance.addMenu(
									{
										boundingBox: userOptionsContainer,
										name: 'userOptions',
										trigger: '#' + instance._namespace + 'userAvatar'
									}
								);
							}

							var isStaging = body.hasClass('staging') || body.hasClass('remote-staging');
							var isLiveView = body.hasClass('live-view');

							if (isStaging || isLiveView) {
								instance.addMenu(
									{
										boundingBox: '#' + instance._namespace + 'stagingContainer',
										name: 'staging',
										trigger: '#' + instance._namespace + 'staging'
									}
								);
							}
						}
					);

					var addApplication = A.one('#' + instance._namespace + 'addApplication');

					if (addApplication) {
						addApplication.on(
							'click',
							function(event) {
								Liferay.Dockbar.addContent.hide();

								if (!Liferay.Dockbar.addApplication) {
									var setAddApplicationUI = function(visible) {
										var body = A.getBody();

										body.toggleClass('lfr-has-sidebar', visible);
									};

									instance.addUnderlay(
										{
											after: {
												render: function(event) {
													setAddApplicationUI(true);
												}
											},
											className: 'add-application',
											io: {
												after: {
													success: Liferay.Dockbar._loadAddApplications
												},
												data: {
													doAsUserId: themeDisplay.getDoAsUserIdEncoded(),
													p_l_id: themeDisplay.getPlid(),
													p_p_id: 87,
													p_p_state: 'exclusive'
												},
												uri: themeDisplay.getPathMain() + '/portal/render_portlet'
											},
											name: 'addApplication',
											width: '255px'
										}
									);

									Liferay.Dockbar.addApplication.after(
										'visibleChange',
										function(event) {
											if (event.newVal) {
												Liferay.Util.focusFormField('#layout_configuration_content');
											}

											setAddApplicationUI(event.newVal);
										}
									);
								}
								else {
									Liferay.Dockbar.addApplication.show();
								}

								Liferay.Dockbar.addApplication.focus();
							}
						);
					}

					var pageTemplate = A.one('#pageTemplate');

					if (pageTemplate) {
						pageTemplate.on(
							'click',
							function(event) {
//								Liferay.Dockbar.manageContent.hide();

								if (!Liferay.Dockbar.manageLayouts) {
									instance.addUnderlay(
										{
											className: 'manage-layouts',
											io: {
												data: {
													doAsUserId: themeDisplay.getDoAsUserIdEncoded(),
													p_l_id: themeDisplay.getPlid(),
													redirect: Liferay.currentURL
												},
												uri: themeDisplay.getPathMain() + '/layout_configuration/templates'
											},
											name: 'manageLayouts',
											width: '670px'
										}
									);
								}
								else {
									Liferay.Dockbar.manageLayouts.show();
								}

								Liferay.Dockbar.manageLayouts.focus();
							}
						);
					}
				}
			},

			addItem: function(options) {
				var instance = this;

				if (options.url) {
					options.text = '<a href="' + options.url + '">' + options.text + '</a>';
				}

				var item = A.Node.create('<li class="' + (options.className || '') + '">' + options.text + '</li>');

				instance.dockBar.one('> ul').appendChild(item);

				instance._toolbarItems[options.name] = item;

				return item;
			},

			addMenu: function(options) {
				var instance = this;

				if (options.name && A.one(options.trigger)) {
					var name = options.name;

					delete options.name;

					options.zIndex = instance.menuZIndex++;

					A.mix(
						options,
						{
							hideDelay: 500,
							hideOn: 'mouseleave',
							showOn: 'mouseover'
						}
					);

					if (options.boundingBox && !('contentBox' in options)) {
						options.contentBox = options.boundingBox + '> .aui-menu-content';
					}

					var menu = new A.OverlayContext(options);

					var contentBox = menu.get('contentBox');

					contentBox.plug(
						A.Plugin.NodeFocusManager,
						{
							circular: true,
							descendants: 'a',
							focusClass: 'aui-focus',
							keys: {
								next: 'down:40',
								previous: 'down:38'
							}
						 }
					);

					var focusManager = contentBox.focusManager;

					contentBox.delegate(
						'mouseenter',
						function (event) {
							focusManager.focus(event.currentTarget.one('a'));
						},
						'li'
					);

					contentBox.delegate(
						'mouseleave',
						function (event) {
							focusManager.blur(event.currentTarget.one('a'));
						},
						'li'
					);

					Liferay.Dockbar.MenuManager.register(menu);

					menu.on(
						'show',
						function(event) {
							var instance = this;

							Liferay.Dockbar.MenuManager.hideAll();

							instance.get('trigger').addClass('menu-button-active');
						}
					);

					menu.on(
						'hide',
						function(event) {
							var instance = this;

							instance.get('trigger').removeClass('menu-button-active');
						}
					);

					menu.render(instance.dockBar);

					instance[name] = menu;
				}
			},

			addMessage: function(message, messageId) {
				var instance = this;

				var messages = instance.messages;

				if (!instance.messageList) {
					instance.messageList = [];
					instance.messageIdList = [];
				}

				messages.show();

				if (!messageId) {
					messageId = A.guid();
				}

				instance.messageList.push(message);
				instance.messageIdList.push(messageId);

				var currentBody = messages.get('bodyContent');

				message = instance._createMessage(message, messageId);

				messages.setStdModContent('body', message, 'after');

				var messagesContainer = messages.get('boundingBox');

				var action = 'removeClass';

				if (instance.messageList.length > 1) {
					action = 'addClass';
				}

				messagesContainer[action]('multiple-messages');

				return messageId;
			},

			addUnderlay: function(options) {
				var instance = this;

				var autoShow = true;

				if (options.name) {
					var name = options.name;

					autoShow = options.visible !== false;

					if (!instance[name]) {
						delete options.name;

						options.zIndex = instance.underlayZIndex++;

						options.align = options.align || {
							node: A.one('#container'),
							points: ['tl', 'tl']
						};

						var underlay = new instance.Underlay(options);

						//underlay.render(instance.dockBar);
						underlay.render(A.one('#container'));

						if (options.io) {
							options.io.loadingMask = {
								background: 'transparent'
							};

							underlay.plug(A.Plugin.IO, options.io);
						}

						instance[name] = underlay;
					}

					if (autoShow && instance[name] && instance[name] instanceof A.Overlay) {
						instance[name].show();
					}

					return instance[name];
				}
			},

			clearMessages: function(event) {
				var instance = this;

				instance.messages.set('bodyContent', ' ');

				instance.messageList = [];
				instance.messageIdList = [];
			},

			setMessage: function(message, messageId) {
				var instance = this;

				var messages = instance.messages;

				if (!messageId) {
					messageId = A.guid();
				}

				instance.messageList = [message];
				instance.messageIdList = [messageId];

				messages.show();

				message = instance._createMessage(message, messageId);

				messages.set('bodyContent', message);

				var messagesContainer = messages.get('boundingBox');

				messagesContainer.removeClass('multiple-messages');

				return messageId;
			},

			_createMessage: function(message, messageId) {
				var instance = this;

				var cssClass = '';

				if (instance.messageList.length == 1) {
					cssClass = 'first';
				}

				return '<div class="dockbar-message ' + cssClass + '" id="' + messageId + '">' + message + '</div>';
			}
		};

		var Underlay = A.Component.create(
			{
				ATTRS: {
					bodyContent: {
						value: A.Node.create('<div style="height: 100px"></div>')
					},
					className: {
						lazyAdd: false,
						setter: function(value) {
							var instance = this;

							instance.get('boundingBox').addClass(value);
						},
						value: null
					}
				},

				EXTENDS: A.OverlayBase,

				NAME: 'underlay',

				prototype: {
					initializer: function() {
						var instance = this;

						Liferay.Dockbar.UnderlayManager.register(instance);
					},

					renderUI: function() {
						var instance = this;

						Underlay.superclass.renderUI.apply(instance, arguments);

						var closeTool = new A.ButtonItem('close');

						closeTool.render(instance.get('boundingBox'));

						closeTool.get('contentBox').addClass('aui-underlay-close');

						instance.set('headerContent', closeTool.get('boundingBox'));

						instance.closeTool = closeTool;
					},

					bindUI: function() {
						var instance = this;

						Underlay.superclass.bindUI.apply(instance, arguments);

						instance.closeTool.on('click', instance.hide, instance);
					}
				}
			}
		);

		Liferay.Dockbar.Underlay = Underlay;

		Liferay.provide(
			Liferay.Dockbar,
			'_loadAddApplications',
			function(event, id, obj) {
				var contentBox = Liferay.Dockbar.addApplication.get('contentBox');

				Liferay.LayoutConfiguration._dialogBody = contentBox;

				Liferay.LayoutConfiguration._loadContent();
			},
			['liferay-layout-configuration']
		);
	},
	'',
	{
		requires: ['aui-button-item', 'aui-io-plugin', 'aui-io-request', 'aui-overlay-context', 'aui-overlay-manager', 'node-focusmanager']
	}
);