Liferay.Util = {
	submitCountdown: 0,

	actsAsAspect: function(object) {
		object.yield = null;
		object.rv = {};

		object.before = function(method, f) {
			var original = eval('this.' + method);

			this[method] = function() {
				f.apply(this, arguments);

				return original.apply(this, arguments);
			};
		};

		object.after = function(method, f) {
			var original = eval('this.' + method);

			this[method] = function() {
				this.rv[method] = original.apply(this, arguments);

				return f.apply(this, arguments);
			};
		};

		object.around = function(method, f) {
			var original = eval('this.' + method);

			this[method] = function() {
				this.yield = original;

				return f.apply(this, arguments);
			};
		};
	},

	addInputFocus: function() {
		var instance = this;

		AUI().use(
			'aui-base',
			function(A) {
				var handleFocus = function(event) {
					var target = event.target;

					var tagName = target.get('tagName');

					if (tagName) {
						tagName = tagName.toLowerCase();
					}

					var nodeType = target.get('type');

					if (((tagName == 'input') && (/text|password/).test(nodeType)) ||
						(tagName == 'textarea')) {

						var action = 'addClass';

						if (/blur|focusout/.test(event.type)) {
							action = 'removeClass';
						}

						target[action]('focus');
					}
				};

				A.on('focus', handleFocus, document);
				A.on('blur', handleFocus, document);
			}
		);

		instance.addInputFocus = function(){};
	},

	addInputType: function(el) {
		var instance = this;

		var A = AUI();

		instance.addInputType = A.Lang.emptyFn;

		if (Liferay.Browser.isIe() && Liferay.Browser.getMajorVersion() < 7) {
			instance.addInputType = function(el) {
				var item;

				if (el) {
					el = A.one(el);
				}
				else {
					el = A.one(document.body);
				}

				var defaultType = 'text';

				el.all('input').each(
					function(item, index, collection) {
						var type = item.get('type') || defaultType;

						item.addClass(type);
					}
				);
			};
		}

		return instance.addInputType(el);
	},

	addParams: function(params, url) {
		var instance = this;

		var A = AUI().use('querystring-stringify-simple');

		if (A.Lang.isObject(params)) {
			params = A.QueryString.stringify(params);
		}
		else {
			params = A.Lang.trim(params);
		}

		if (params) {
			var loc = url || location.href;
			var anchorHash, finalUrl;

			if (loc.indexOf('#') > -1) {
				var locationPieces = loc.split('#');
				loc = locationPieces[0];
				anchorHash = locationPieces[1];
			}

			if (loc.indexOf('?') == -1) {
				params = '?' + params;
			}
			else {
				params = '&' + params;
			}

			if (loc.indexOf(params) == -1) {
				finalUrl = loc + params;

				if (anchorHash) {
					finalUrl += '#' + anchorHash;
				}
				if (!url) {
					location.href = finalUrl;
				}

				return finalUrl;
			}
		}
	},

	checkTab: function(box) {
		if ((document.all) && (event.keyCode == 9)) {
			box.selection = document.selection.createRange();

			setTimeout(
				function() {
					Liferay.Util.processTab(box.id);
				},
				0
			);
		}
	},

	disableEsc: function() {
		if ((document.all) && (event.keyCode == 27)) {
			event.returnValue = false;
		}
	},

	disableFormButtons: function(inputs, form) {
		var instance = this;

		inputs.set('disabled', true);
		inputs.setStyle('opacity', 0.5);
	},

	enableFormButtons: function(inputs, form) {
		var instance = this;

		instance._submitLocked = null;

		document.body.style.cursor = 'auto';

		inputs.set('disabled', false);
		inputs.setStyle('opacity', 1);
	},

	endsWith: function(str, x) {
		return (str.lastIndexOf(x) === (str.length - x.length));
	},

	escapeHTML: function(str) {
		return str.replace(
			/<|>|&/gi,
			function(match) {
				var str = '';

				if (match == '<') {
					str = '&lt;';
				}
				else if (match == '>') {
					str = '&gt;';
				}
				else if (match == '&') {
					str = '&amp;';
				}
				else if (match == '\"') {
					str = '&#034;';
				}
				else if (match == '\'') {
					str = '&#039;';
				}

				return str;
			}
		);
	},

	getColumnId: function(str) {
		var columnId = str.replace(/layout-column_/, '');

		return columnId;
	},

	getPortletId: function(portletId) {
		portletId = portletId.replace(/^p_p_id_/i, '');
		portletId = portletId.replace(/_$/, '');

		return portletId;
	},

	getURLWithSessionId: function(url) {
		if (document.cookie && (document.cookie.length > 0)) {
			return url;
		}

		// LEP-4787

		var x = url.indexOf(';');

		if (x > -1) {
			return url;
		}

		var sessionId = ';jsessionid=' + themeDisplay.getSessionId();

		x = url.indexOf('?');

		if (x > -1) {
			return url.substring(0, x) + sessionId + url.substring(x);
		}

		// In IE6, http://www.abc.com;jsessionid=XYZ does not work, but
		// http://www.abc.com/;jsessionid=XYZ does work.

		x = url.indexOf('//');

		if (x > -1) {
			var y = url.lastIndexOf('/');

			if (x + 1 == y) {
				return url + '/' + sessionId;
			}
		}

		return url + sessionId;
	},

	isArray: function(object) {
		return !!(window.Array && object.constructor == window.Array);
	},

	processTab: function(id) {
		document.all[id].selection.text = String.fromCharCode(9);
		document.all[id].focus();
	},

	randomInt: function() {
		return (Math.ceil(Math.random() * (new Date).getTime()));
	},

	randomMinMax: function(min, max) {
		return (Math.round(Math.random() * (max - min))) + min;
	},

	selectAndCopy: function(el) {
		el.focus();
		el.select();

		if (document.all) {
			var textRange = el.createTextRange();

			textRange.execCommand('copy');
		}
	},

	setBox: function(oldBox, newBox) {
		for (var i = oldBox.length - 1; i > -1; i--) {
			oldBox.options[i] = null;
		}

		for (var i = 0; i < newBox.length; i++) {
			oldBox.options[i] = new Option(newBox[i].value, i);
		}

		oldBox.options[0].selected = true;
	},

	showCapsLock: function(event, span) {
		var keyCode = event.keyCode ? event.keyCode : event.which;
		var shiftKey = event.shiftKey ? event.shiftKey : ((keyCode == 16) ? true : false);

		if (((keyCode >= 65 && keyCode <= 90) && !shiftKey) ||
			((keyCode >= 97 && keyCode <= 122) && shiftKey)) {

			document.getElementById(span).style.display = '';
		}
		else {
			document.getElementById(span).style.display = 'none';
		}
	},

	sortByAscending: function(a, b) {
		a = a[1].toLowerCase();
		b = b[1].toLowerCase();

		if (a > b) {
			return 1;
		}

		if (a < b) {
			return -1;
		}

		return 0;
	},

	startsWith: function(str, x) {
		return (str.indexOf(x) === 0);
	},

	textareaTabs: function(event) {
		var el = event.currentTarget.getDOM();
		var pressedKey = event.keyCode;

		if(pressedKey == 9) {
			event.halt();

			var oldscroll = el.scrollTop;

			if (el.setSelectionRange) {
				var caretPos = el.selectionStart + 1;
				var elValue = el.value;

				el.value = elValue.substring(0, el.selectionStart) + '\t' + elValue.substring(el.selectionEnd, elValue.length);

				setTimeout(
					function() {
						el.focus();
						el.setSelectionRange(caretPos, caretPos);
					}, 0);

			}
			else {
				document.selection.createRange().text='\t';
			}

	        el.scrollTop = oldscroll;

			return false;
	    }
	},

	uncamelize: function(value, separator) {
		separator = separator || ' ';

		value = value.replace(/([a-zA-Z][a-zA-Z])([A-Z])([a-z])/g, '$1' + separator + '$2$3');
		value = value.replace(/([a-z])([A-Z])/g, '$1' + separator + '$2');

		return value;
	},

	unescapeHTML: function(str) {
		return str.replace(
			/&lt;|&gt;|&amp;|&#034;|&#039;/gi,
			function(match) {
				var str = '';

				if (match == '&lt;') {
					str = '<';
				}
				else if (match == '&gt;') {
					str = '>';
				}
				else if (match == '&amp;') {
					str = '&';
				}
				else if (match == '&#034;') {
					str = '\"';
				}
				else if (match == '&#039;') {
					str = '\'';
				}

				return str;
			}
		);
	},

	_getEditableInstance: function(title) {
		var instance = this;

		var A = AUI();

		var editable = instance._EDITABLE;

		if (!editable) {
			editable = new A.Editable(
				{
					after: {
						contentTextChange: function(event) {
							var instance = this;

							if (!event.initial) {
								var title = instance.get('node');

								var portletTitleEditOptions = title.getData('portletTitleEditOptions');

								Liferay.Util.savePortletTitle(
									{
										doAsUserId: portletTitleEditOptions.doAsUserId,
										plid: portletTitleEditOptions.plid,
										portletId: portletTitleEditOptions.portletId,
										title: event.newVal
									}
								);
							}
						},
						startEditing: function(event) {
							var instance = this;

							if (Liferay.Layout) {
								instance._dragListener = Liferay.Layout.layoutHandler.on(
									'drag:start',
									function(event) {
										instance.fire('save');
									}
								);
							}
						},
						stopEditing: function(event) {
							var instance = this;

							if (instance._dragListener) {
								instance._dragListener.detach();
							}
						}
					},
					cssClass: 'lfr-portlet-title-editable',
					node: title
				}
			);

			instance._EDITABLE = editable;
		}

		return editable;
	}
};

Liferay.provide(
	Liferay.Util,
	'check',
	function(form, name, checked) {
		var A = AUI();

		var checkbox = A.one(form[name]);

		if (checkbox) {
			checkbox.set('checked', checked);
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'checkAll',
	function(form, name, allBox) {
		var A = AUI();

		var selector;

		if (Liferay.Util.isArray(name)) {
			selector = 'input[name='+ name.join('], input[name=') + ']';
		}
		else {
			selector = 'input[name=' + name + ']';
		}

		form = A.one(form);

		form.all(selector).set('checked', A.one(allBox).get('checked'));
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'checkAllBox',
	function(form, name, allBox) {
		var A = AUI();

		var totalBoxes = 0;
		var totalOn = 0;
		var inputs = A.one(form).all('input[type=checkbox]');

		allBox = A.one(allBox);

		if (!A.Lang.isArray(name)) {
			name = [name];
		}

		inputs.each(
			function(item, index, collection) {
				if (!item.compareTo(allBox)) {
					if (A.Array.indexOf(name, item.getAttribute('name')) > -1) {
						totalBoxes++;
					}

					if (item.get('checked')) {
						totalOn++;
					}
				}
			}
		);

		allBox.set('checked', (totalBoxes == totalOn));
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'createFlyouts',
	function(options) {
		var A = AUI();

		options = options || {};

		var flyout = A.one(options.container);
		var containers = [];

		if (flyout) {
			var lis = flyout.all('li');

			lis.each(
				function(item, index, collection) {
					var childUL = item.one('ul');

					if (childUL) {
						childUL.hide();

						item.addClass('lfr-flyout');
						item.addClass('has-children lfr-flyout-has-children');
					}
				}
			);

			var hideTask = new A.DelayedTask(
				function(event) {
					showTask.cancel();

					var li = event.currentTarget;

					if (li.hasClass('has-children')) {
						var childUL = event.currentTarget.one('> ul');

						if (childUL) {
							childUL.hide();

							if (options.mouseOut) {
								options.mouseOut.apply(event.currentTarget, [event]);
							}
						}
					}
				}
			);

			var showTask = new A.DelayedTask(
				function(event) {
					hideTask.cancel();

					var li = event.currentTarget;

					if (li.hasClass('has-children')) {
						var childUL = event.currentTarget.one('> ul');

						if (childUL) {
							childUL.show();

							if (options.mouseOver) {
								options.mouseOver.apply(event.currentTarget, [event]);
							}
						}
					}
				}
			);

			lis.on(
				'mouseenter',
				A.bind(showTask.delay, showTask, 0, null, null),
				'li'
			);

			lis.on(
				'mouseleave',
				A.bind(hideTask.delay, hideTask, 300, null, null),
				'li'
			);
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'disableElements',
	function(obj) {
		var A = AUI();

		var el = A.one(obj);

		if (el) {
			el = el.getDOM();

			var children = el.getElementsByTagName('*');

			var emptyFnFalse = A.Lang.emptyFnFalse;
			var Event = A.Event;

			for (var i = children.length - 1; i >= 0; i--) {
				var item = children[i];

				item.style.cursor = 'default';

				el.onclick = emptyFnFalse;
				el.onmouseover = emptyFnFalse;
				el.onmouseout = emptyFnFalse;
				el.onmouseenter = emptyFnFalse;
				el.onmouseleave = emptyFnFalse;

				Event.purgeElement(el, false);

				item.href = 'javascript:;';
				item.disabled = true;
				item.action = '';
				item.onsubmit = emptyFnFalse;
			}
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'disableTextareaTabs',
	function(textarea) {
		var A = AUI();

		textarea = A.one(textarea);

		if (textarea && textarea.attr('textareatabs') != 'enabled') {
			textarea.attr('textareatabs', 'disabled');

			textarea.detach('keydown', Liferay.Util.textareaTabs);
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'disableToggleBoxes',
	function(checkBoxId, toggleBoxId, checkDisabled) {
		var A = AUI();

		var checkBox = A.one('#' + checkBoxId);
		var toggleBox = A.one('#' + toggleBoxId);

		if (checkBox && toggleBox) {
			if (checkBox.get('checked') && checkDisabled) {
				toggleBox.set('disabled', true);
			}
			else {
				toggleBox.set('disabled', false);
			}

			checkBox.on(
				'click',
				function() {
					toggleBox.set('disabled', !toggleBox.get('disabled'));
				}
			);
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'enableTextareaTabs',
	function(textarea) {
		var A = AUI();

		textarea = A.one(textarea);

		if (textarea && textarea.attr('textareatabs') != 'enabled') {
			textarea.attr('textareatabs', 'disabled');

			textarea.on('keydown', Liferay.Util.textareaTabs);
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'focusFormField',
	function(el, caretPosition) {
		var instance = this;

		var A = AUI();

		instance.addInputFocus();

		var interacting = false;

		var clickHandle = A.getDoc().on(
			'click',
			function(event) {
				interacting = true;

				clickHandle.detach();
			}
		);

		if (!interacting) {
			el = A.one(el);

			try {
				el.focus();
			}
			catch (e) {
			}
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'forcePost',
	function(link) {
		var A = AUI();

		link = A.one(link);

		if (link) {
			var url = link.attr('href');

			submitForm(document.hrefFm, url);

			Liferay.Util._submitLocked = null;
		}
	},
	['aui-base']
);

/**
 * OPTIONS
 *
 * Required
 * button {string|object}: The button that opens the popup when clicked.
 * height {number}: The height to set the popup to.
 * textarea {string}: the name of the textarea to auto-resize.
 * url {string}: The url to open that sets the editor.
 * width {number}: The width to set the popup to.
 */

Liferay.provide(
	Liferay.Util,
	'inlineEditor',
	function(options) {
		var instance = this;

		var A = AUI();

		if (options.url && options.button) {
			var url = options.url;
			var button = options.button;
			var width = options.width || 680;
			var height = options.height || 640;
			var textarea = options.textarea;
			var clicked = false;

			var editorButton = A.one(button);
			var popup = null;

			if (editorButton) {
				editorButton.on(
					'click',
					function(event) {
						if (!clicked) {
							popup = new A.Dialog(
								{
									centered: true,
									height: 640,
									title: Liferay.Language.get('editor'),
									width: 680
								}
							).render();

							popup.plug(
								A.Plugin.IO,
								{
									uri: url + '&rt=' + Liferay.Util.randomInt()
								}
							);

							clicked = true;
						}
						else {
							popup.show();

							popup._setAlignCenter(true);

							popup.io.start();
						}
					}
				);
			}
		}
	},
	['aui-dialog', 'aui-io']
);

Liferay.provide(
	Liferay.Util,
	'moveItem',
	function(fromBox, toBox, sort) {
		var A = AUI();

		fromBox = A.one(fromBox);
		toBox = A.one(toBox);

		var selectedIndex = fromBox.get('selectedIndex');

		var selectedOption;

		if (selectedIndex >= 0) {
			var options = fromBox.all('option');

			selectedOption = options.item(selectedIndex);

			options.each(
				function(item, index, collection) {
					if (item.get('selected')) {
						toBox.append(item);
					}
				}
			);
		}

		if (selectedOption && selectedOption.text() != '' && sort == true) {
			Liferay.Util.sortBox(toBox);
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'portletTitleEdit',
	function(options) {
		var instance = this;

		var A = AUI();

		var obj = options.obj;
		var title = obj.one('.portlet-title-text');

		if (title && !title.hasClass('not-editable')) {
			title.setData('portletTitleEditOptions', options);

			title.on(
				'click',
				function(event) {
					var editable = instance._getEditableInstance(title);

					var rendered = editable.get('rendered');

					if (rendered) {
						editable.fire('stopEditing');
					}

					editable.set('node', event.currentTarget);

					if (rendered) {
						editable.syncUI();
					}

					editable._startEditing(event);
				}
			);
		}
	},
	['aui-editable']
);

Liferay.provide(
	Liferay.Util,
	'removeItem',
	function(box, value) {
		var A = AUI();

		box = A.one(box);

		var selectedIndex =  box.get('selectedIndex');

		if (!value) {
			box.all('option').item(selectedIndex).remove(true);
		}
		else {
			box.all('option[value=' + value + ']').item(selectedIndex).remove(true);
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'reorder',
	function(box, down) {
		var A = AUI();

		box = A.one(box);

		var selectedIndex = box.get('selectedIndex');

		if (selectedIndex == -1) {
			box.set('selectedIndex', 0);
		}
		else {
			var options = box.get('options');

			var selectedOption = options.item(selectedIndex);
			var lastIndex = box.get('length') - 1;

			var currentOption;
			var newOption;
			var newIndex;

			var text = selectedOption.get('text');
			var value = selectedOption.val();

			if (value && (selectedIndex > 0) && (down == 0)) {
				var previousOption = options.item(selectedIndex - 1);

				selectedOption.set('text', previousOption.get('text'));
				selectedOption.val(previousOption.val());

				newOption = previousOption;
				newIndex = selectedIndex - 1;
			}
			else if ((selectedIndex < lastIndex) && (options.item(selectedIndex + 1).val()) && (down == 1)) {
				var nextOption = options.item(selectedIndex + 1);

				selectedOption.set('text', nextOption.get('text'));
				selectedOption.val(nextOption.val());

				newOption = nextOption;
				newIndex = selectedIndex + 1;
			}
			else if (selectedIndex == 0) {
				var nextIndex;
				var nextOption;

				for (var i = 0; i < lastIndex; i++) {
					nextIndex = i + 1;
					currentOption = options.item(i);
					nextOption = options.item(nextIndex);

					currentOption.set('text', nextOption.get('text'));
					currentOption.val(nextOption.val());
				}

				newOption = options.item(lastIndex);
				newIndex = lastIndex;
			}
			else if (selectedIndex == lastIndex) {
				var previousIndex;
				var previousOption;

				for (var i = lastIndex; i > 0; i--) {
					previousIndex = i - 1;
					currentOption = options.item(i);
					previousOption = options.item(previousIndex);

					currentOption.set('text', previousOption.get('text'));
					currentOption.val(previousOption.val());
				}

				newOption = options.item(0);
				newIndex = 0;
			}

			if (newOption) {
				newOption.set('text', text);
				newOption.val(value);

				box.set('selectedIndex', newIndex);
			}
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'resizeTextarea',
	function(elString, usingRichEditor, resizeToInlinePopup) {
		var A = AUI();

		var el = A.one('#' + elString);

		if (!el) {
			el = A.one('textarea[name=' + elString + ']');
		}

		if (el) {
			var pageBody;

			if (resizeToInlinePopup) {
				if (el.get('nodeName').toLowerCase() != 'textarea') {
					el = A.one('#' + elString + '_cp');
				}

				pageBody = el.ancestor('.aui-dialog-bd');
			}
			else {
				pageBody = A.getBody();
			}

			var resize = function() {
				var pageBodyHeight = pageBody.get('offsetHeight');

				if (usingRichEditor) {
					try {
						if (el.get('nodeName').toLowerCase() != 'iframe') {
							el = window[elString];
						}
					}
					catch (e) {
					}
				}

				var diff = 170;

				if (!resizeToInlinePopup) {
					diff = 100;
				}

				el = A.one(el);

				var styles = {
					height: (pageBodyHeight - diff) + 'px',
					width: '98%'
				};

				if (usingRichEditor) {
					if (!el || !A.DOM.inDoc(el)) {
						A.on(
							'available',
							function(event) {
								el = A.one(window[elString]);

								if (el) {
									el.setStyles(styles);
								}
							},
							'#' + elString + '_cp'
						);

						return;
					}
				}

				if (el) {
					el.setStyles(styles);
				}
			};

			resize();

			if (resizeToInlinePopup) {
				A.on('popupResize', resize);
			}
			else {
				A.getWin().on('resize', resize);
			}
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'savePortletTitle',
	function(params) {
		var A = AUI();

		A.mix(
			params,
			{
				doAsUserId: 0,
				plid: 0,
				portletId: 0,
				title: '',
				url: themeDisplay.getPathMain() + '/portlet_configuration/update_title'
			}
		);

		A.io.request(
			params.url,
			{
				data: {
					doAsUserId: params.doAsUserId,
					p_l_id: params.plid,
					portletId: params.portletId,
					title: params.title
				}
			}
		);
	},
	['aui-io']
);

Liferay.provide(
	Liferay.Util,
	'setSelectedValue',
	function(col, value) {
		var A = AUI();

		var option = A.one(col).one('option[value=' + value + ']');

		if (option) {
			option.set('selected', true);
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'sortBox',
	function(box) {
		var A = AUI();

		var newBox = [];

		var options = box.all('option');

		for (var i = 0; i < options.size(); i++) {
			newBox[i] = [options.item(i).val(), options.item(i).text()];
		}

		newBox.sort(Liferay.Util.sortByAscending);

		var boxObj = A.one(box);

		boxObj.all('option').remove(true);

		A.each(
			newBox,
			function(item, index, collection) {
				boxObj.append('<option value="' + item[0] + '">' + item[1] + '</option>');
			}
		);

		if (Liferay.Browser.isIe()) {
			var currentWidth = boxObj.getStyle('width');

			if (currentWidth == 'auto') {
				boxObj.setStyle('width', 'auto');
			}
		}
	},
	['aui-base']
);

/**
 * OPTIONS
 *
 * Required
 * popup {string|object}: A selector or DOM element of the popup that contains the editor.
 * textarea {string}: the name of the textarea to auto-resize.
 * url {string}: The url to open that sets the editor.
 */

Liferay.provide(
	Liferay.Util,
	'switchEditor',
	function(options) {
		var A = AUI();

		var url = options.url;
		var popup = A.one(options.popup);
		var textarea = options.textarea;

		if (popup) {
			if (!popup.io) {
				popup.plug(
					A.Plugin.IO,
					{
						uri: url
					}
				);
			}
			else {
				popup.io.set('uri', url);

				popup.io.start();
			}
		}
	},
	['aui-io']
);

Liferay.provide(
	Liferay.Util,
	'toggleBoxes',
	function(checkBoxId, toggleBoxId, displayWhenUnchecked) {
		var A = AUI();

		var checkBox = A.one('#' + checkBoxId);
		var toggleBox = A.one('#' + toggleBoxId);

		if (checkBox && toggleBox) {
			var checked = checkBox.get('checked');

			if (checked) {
				toggleBox.show();
			}
			else {
				toggleBox.hide();
			}

			if (displayWhenUnchecked) {
				toggleBox.toggle();
			}

			checkBox.on(
				'click',
				function() {
					toggleBox.toggle();
				}
			);
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'toggleControls',
	function() {
		var A = AUI();

		var trigger = A.one('.toggle-controls');

		if (trigger) {
			var docBody = A.getBody();
			var hiddenClass = 'controls-hidden';
			var visibleClass = 'controls-visible';
			var currentClass = visibleClass;

			if (Liferay._editControlsState != 'visible') {
				currentClass = hiddenClass;
			}

			docBody.addClass(currentClass);

			trigger.on(
				'click',
				function(event) {
					docBody.toggleClass(visibleClass).toggleClass(hiddenClass);

					Liferay._editControlsState = (docBody.hasClass(visibleClass) ? 'visible' : 'hidden');

					A.io.request(
						themeDisplay.getPathMain() + '/portal/session_click',
						{
							data: {
								'liferay_toggle_controls': Liferay._editControlsState
							}
						}
					);
				}
			);
		} else {
		  A.getBody().addClass('controls-hidden');
		};
	},
	['aui-io']
);

Liferay.provide(
	Liferay.Util,
	'toggleSelectBox',
	function(selectBoxId, value, toggleBoxId) {
		var A = AUI();

		var selectBox = A.one('#' + selectBoxId);
		var toggleBox = A.one('#' + toggleBoxId);

		if (selectBox && toggleBox) {
			var toggle = function() {
				var action = 'show';

				if (selectBox.val() != value) {
					action = 'hide';
				}

				toggleBox[action]();
			};

			toggle();

			selectBox.on('change', toggle);
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.Util,
	'updateCheckboxValue',
	function(checkbox) {
		var A = AUI();

		A.one(checkbox).previous().val(checkbox.checked);
	},
	['aui-base']
);

Liferay.provide(
	window,
	'submitForm',
	function(form, action, singleSubmit) {
		var A = AUI();

		if (!Liferay.Util._submitLocked) {
			form = A.one(form);

			var inputs = form.all('input[type=button], input[type=reset], input[type=submit]');

			Liferay.Util.disableFormButtons(inputs, form);

			if (singleSubmit === false) {
				Liferay.Util._submitLocked = A.later(
					10000,
					Liferay.Util,
					Liferay.Util.enableFormButtons,
					[inputs, form]
				);
			}
			else {
				Liferay.Util._submitLocked = true;
			}

			if (action != null) {
				form.attr('action', action);
			}

			Liferay.fire(
				'submitForm',
				{
					form: form
				}
			);

			form.submit();
		}
	},
	['aui-base']
);

// 0-200: Theme Developer
// 200-400: Portlet Developer
// 400+: Liferay

Liferay.zIndex = {
	DOCK:			10,
	DOCK_PARENT:	20,
	ALERT:			430,
	DROP_AREA:		440,
	DROP_POSITION:	450,
	DRAG_ITEM:		460,
	TOOLTIP:		470
};