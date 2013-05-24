// Copyright 2006 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


// Known Issues:
//
// * Patterns are not implemented.
// * Radial gradient are not implemented. The VML version of these look very
//   different from the canvas one.
// * Clipping paths are not implemented.
// * Coordsize. The width and height attribute have higher priority than the
//   width and height style values which isn't correct.
// * Painting mode isn't implemented.
// * Canvas width/height should is using content-box by default. IE in
//   Quirks mode will draw the canvas using border-box. Either change your
//   doctype to HTML5
//   (http://www.whatwg.org/specs/web-apps/current-work/#the-doctype)
//   or use Box Sizing Behavior from WebFX
//   (http://webfx.eae.net/dhtml/boxsizing/boxsizing.html)
// * Non uniform scaling does not correctly scale strokes.
// * Optimize. There is always room for speed improvements.

// Only add this code if we do not already have a canvas implementation
if (!document.createElement('canvas').getContext) {

	(function() {

		// alias some functions to make (compiled) code shorter
		var m = Math;
		var mr = m.round;
		var ms = m.sin;
		var mc = m.cos;
		var abs = m.abs;
		var sqrt = m.sqrt;

		// this is used for sub pixel precision
		var Z = 10;
		var Z2 = Z / 2;

		/**
   * This funtion is assigned to the <canvas> elements as element.getContext().
   * @this {HTMLElement}
   * @return {CanvasRenderingContext2D_}
   */
		function getContext() {
			return this.context_ ||
			(this.context_ = new CanvasRenderingContext2D_(this));
		}

		var slice = Array.prototype.slice;

		/**
   * Binds a function to an object. The returned function will always use the
   * passed in {@code obj} as {@code this}.
   *
   * Example:
   *
   *   g = bind(f, obj, a, b)
   *   g(c, d) // will do f.call(obj, a, b, c, d)
   *
   * @param {Function} f The function to bind the object to
   * @param {Object} obj The object that should act as this when the function
   *     is called
   * @param {*} var_args Rest arguments that will be used as the initial
   *     arguments when the function is called
   * @return {Function} A new function that has bound this
   */
		function bind(f, obj, var_args) {
			var a = slice.call(arguments, 2);
			return function() {
				return f.apply(obj, a.concat(slice.call(arguments)));
			};
		}

		var G_vmlCanvasManager_ = {
			init: function(opt_doc) {
				if (/MSIE/.test(navigator.userAgent) && !window.opera) {
					var doc = opt_doc || document;
					// Create a dummy element so that IE will allow canvas elements to be
					// recognized.
					doc.createElement('canvas');
					doc.attachEvent('onreadystatechange', bind(this.init_, this, doc));
				}
			},

			init_: function(doc) {
				// create xmlns
				if (!doc.namespaces['g_vml_']) {
					doc.namespaces.add('g_vml_', 'urn:schemas-microsoft-com:vml',
						'#default#VML');

				}
				if (!doc.namespaces['g_o_']) {
					doc.namespaces.add('g_o_', 'urn:schemas-microsoft-com:office:office',
						'#default#VML');
				}

				// Setup default CSS.  Only add one style sheet per document
				if (!doc.styleSheets['ex_canvas_']) {
					var ss = doc.createStyleSheet();
					ss.owningElement.id = 'ex_canvas_';
					ss.cssText = 'canvas{display:inline-block;overflow:hidden;' +
				// default size is 300x150 in Gecko and Opera
				'text-align:left;width:300px;height:150px}' +
				'g_vml_\\:*{behavior:url(#default#VML)}' +
				'g_o_\\:*{behavior:url(#default#VML)}';

				}

				// find all canvas elements
				var els = doc.getElementsByTagName('canvas');
				for (var i = 0; i < els.length; i++) {
					this.initElement(els[i]);
				}
			},

			/**
     * Public initializes a canvas element so that it can be used as canvas
     * element from now on. This is called automatically before the page is
     * loaded but if you are creating elements using createElement you need to
     * make sure this is called on the element.
     * @param {HTMLElement} el The canvas element to initialize.
     * @return {HTMLElement} the element that was created.
     */
			initElement: function(el) {
				if (!el.getContext) {

					el.getContext = getContext;

					// Remove fallback content. There is no way to hide text nodes so we
					// just remove all childNodes. We could hide all elements and remove
					// text nodes but who really cares about the fallback content.
					el.innerHTML = '';

					// do not use inline function because that will leak memory
					el.attachEvent('onpropertychange', onPropertyChange);
					el.attachEvent('onresize', onResize);

					var attrs = el.attributes;
					if (attrs.width && attrs.width.specified) {
						// TODO: use runtimeStyle and coordsize
						// el.getContext().setWidth_(attrs.width.nodeValue);
						el.style.width = attrs.width.nodeValue + 'px';
					} else {
						el.width = el.clientWidth;
					}
					if (attrs.height && attrs.height.specified) {
						// TODO: use runtimeStyle and coordsize
						// el.getContext().setHeight_(attrs.height.nodeValue);
						el.style.height = attrs.height.nodeValue + 'px';
					} else {
						el.height = el.clientHeight;
					}
				//el.getContext().setCoordsize_()
				}
				return el;
			}
		};

		function onPropertyChange(e) {
			var el = e.srcElement;

			switch (e.propertyName) {
				case 'width':
					el.style.width = el.attributes.width.nodeValue + 'px';
					el.getContext().clearRect();
					break;
				case 'height':
					el.style.height = el.attributes.height.nodeValue + 'px';
					el.getContext().clearRect();
					break;
			}
		}

		function onResize(e) {
			var el = e.srcElement;
			if (el.firstChild) {
				el.firstChild.style.width =  el.clientWidth + 'px';
				el.firstChild.style.height = el.clientHeight + 'px';
			}
		}

		G_vmlCanvasManager_.init();

		// precompute "00" to "FF"
		var dec2hex = [];
		for (var i = 0; i < 16; i++) {
			for (var j = 0; j < 16; j++) {
				dec2hex[i * 16 + j] = i.toString(16) + j.toString(16);
			}
		}

		function createMatrixIdentity() {
			return [
			[1, 0, 0],
			[0, 1, 0],
			[0, 0, 1]
			];
		}

		function matrixMultiply(m1, m2) {
			var result = createMatrixIdentity();

			for (var x = 0; x < 3; x++) {
				for (var y = 0; y < 3; y++) {
					var sum = 0;

					for (var z = 0; z < 3; z++) {
						sum += m1[x][z] * m2[z][y];
					}

					result[x][y] = sum;
				}
			}
			return result;
		}

		function copyState(o1, o2) {
			o2.fillStyle     = o1.fillStyle;
			o2.lineCap       = o1.lineCap;
			o2.lineJoin      = o1.lineJoin;
			o2.lineWidth     = o1.lineWidth;
			o2.miterLimit    = o1.miterLimit;
			o2.shadowBlur    = o1.shadowBlur;
			o2.shadowColor   = o1.shadowColor;
			o2.shadowOffsetX = o1.shadowOffsetX;
			o2.shadowOffsetY = o1.shadowOffsetY;
			o2.strokeStyle   = o1.strokeStyle;
			o2.globalAlpha   = o1.globalAlpha;
			o2.arcScaleX_    = o1.arcScaleX_;
			o2.arcScaleY_    = o1.arcScaleY_;
			o2.lineScale_    = o1.lineScale_;
		}

		function processStyle(styleString) {
			var str, alpha = 1;

			styleString = String(styleString);
			if (styleString.substring(0, 3) == 'rgb') {
				var start = styleString.indexOf('(', 3);
				var end = styleString.indexOf(')', start + 1);
				var guts = styleString.substring(start + 1, end).split(',');

				str = '#';
				for (var i = 0; i < 3; i++) {
					str += dec2hex[Number(guts[i])];
				}

				if (guts.length == 4 && styleString.substr(3, 1) == 'a') {
					alpha = guts[3];
				}
			} else {
				str = styleString;
			}

			return {
				color: str,
				alpha: alpha
			};
		}

		function processLineCap(lineCap) {
			switch (lineCap) {
				case 'butt':
					return 'flat';
				case 'round':
					return 'round';
				case 'square':
				default:
					return 'square';
			}
		}

		/**
   * This class implements CanvasRenderingContext2D interface as described by
   * the WHATWG.
   * @param {HTMLElement} surfaceElement The element that the 2D context should
   * be associated with
   */
		function CanvasRenderingContext2D_(surfaceElement) {
			this.m_ = createMatrixIdentity();

			this.mStack_ = [];
			this.aStack_ = [];
			this.currentPath_ = [];

			// Canvas context properties
			this.strokeStyle = '#000';
			this.fillStyle = '#000';

			this.lineWidth = 1;
			this.lineJoin = 'miter';
			this.lineCap = 'butt';
			this.miterLimit = Z * 1;
			this.globalAlpha = 1;
			this.canvas = surfaceElement;

			var el = surfaceElement.ownerDocument.createElement('div');
			el.style.width =  surfaceElement.clientWidth + 'px';
			el.style.height = surfaceElement.clientHeight + 'px';
			el.style.overflow = 'hidden';
			el.style.position = 'absolute';
			surfaceElement.appendChild(el);

			this.element_ = el;
			this.arcScaleX_ = 1;
			this.arcScaleY_ = 1;
			this.lineScale_ = 1;
		}

		var contextPrototype = CanvasRenderingContext2D_.prototype;
		contextPrototype.clearRect = function() {
			this.element_.innerHTML = '';
		};

		contextPrototype.beginPath = function() {
			// TODO: Branch current matrix so that save/restore has no effect
			//       as per safari docs.
			this.currentPath_ = [];
		};

		contextPrototype.moveTo = function(aX, aY) {
			var p = this.getCoords_(aX, aY);
			this.currentPath_.push({
				type: 'moveTo',
				x: p.x,
				y: p.y
			});
			this.currentX_ = p.x;
			this.currentY_ = p.y;
		};

		contextPrototype.lineTo = function(aX, aY) {
			var p = this.getCoords_(aX, aY);
			this.currentPath_.push({
				type: 'lineTo',
				x: p.x,
				y: p.y
			});

			this.currentX_ = p.x;
			this.currentY_ = p.y;
		};

		contextPrototype.bezierCurveTo = function(aCP1x, aCP1y,
			aCP2x, aCP2y,
			aX, aY) {
			var p = this.getCoords_(aX, aY);
			var cp1 = this.getCoords_(aCP1x, aCP1y);
			var cp2 = this.getCoords_(aCP2x, aCP2y);
			bezierCurveTo(this, cp1, cp2, p);
		};

		// Helper function that takes the already fixed cordinates.
		function bezierCurveTo(self, cp1, cp2, p) {
			self.currentPath_.push({
				type: 'bezierCurveTo',
				cp1x: cp1.x,
				cp1y: cp1.y,
				cp2x: cp2.x,
				cp2y: cp2.y,
				x: p.x,
				y: p.y
			});
			self.currentX_ = p.x;
			self.currentY_ = p.y;
		}

		contextPrototype.quadraticCurveTo = function(aCPx, aCPy, aX, aY) {
			// the following is lifted almost directly from
			// http://developer.mozilla.org/en/docs/Canvas_tutorial:Drawing_shapes

			var cp = this.getCoords_(aCPx, aCPy);
			var p = this.getCoords_(aX, aY);

			var cp1 = {
				x: this.currentX_ + 2.0 / 3.0 * (cp.x - this.currentX_),
				y: this.currentY_ + 2.0 / 3.0 * (cp.y - this.currentY_)
			};
			var cp2 = {
				x: cp1.x + (p.x - this.currentX_) / 3.0,
				y: cp1.y + (p.y - this.currentY_) / 3.0
			};

			bezierCurveTo(this, cp1, cp2, p);
		};

		contextPrototype.arc = function(aX, aY, aRadius,
			aStartAngle, aEndAngle, aClockwise) {
			aRadius *= Z;
			var arcType = aClockwise ? 'at' : 'wa';

			var xStart = aX + mc(aStartAngle) * aRadius - Z2;
			var yStart = aY + ms(aStartAngle) * aRadius - Z2;

			var xEnd = aX + mc(aEndAngle) * aRadius - Z2;
			var yEnd = aY + ms(aEndAngle) * aRadius - Z2;

			// IE won't render arches drawn counter clockwise if xStart == xEnd.
			if (xStart == xEnd && !aClockwise) {
				xStart += 0.125; // Offset xStart by 1/80 of a pixel. Use something
			// that can be represented in binary
			}

			var p = this.getCoords_(aX, aY);
			var pStart = this.getCoords_(xStart, yStart);
			var pEnd = this.getCoords_(xEnd, yEnd);

			this.currentPath_.push({
				type: arcType,
				x: p.x,
				y: p.y,
				radius: aRadius,
				xStart: pStart.x,
				yStart: pStart.y,
				xEnd: pEnd.x,
				yEnd: pEnd.y
			});

		};

		contextPrototype.rect = function(aX, aY, aWidth, aHeight) {
			this.moveTo(aX, aY);
			this.lineTo(aX + aWidth, aY);
			this.lineTo(aX + aWidth, aY + aHeight);
			this.lineTo(aX, aY + aHeight);
			this.closePath();
		};

		contextPrototype.strokeRect = function(aX, aY, aWidth, aHeight) {
			var oldPath = this.currentPath_;
			this.beginPath();

			this.moveTo(aX, aY);
			this.lineTo(aX + aWidth, aY);
			this.lineTo(aX + aWidth, aY + aHeight);
			this.lineTo(aX, aY + aHeight);
			this.closePath();
			this.stroke();

			this.currentPath_ = oldPath;
		};

		contextPrototype.fillRect = function(aX, aY, aWidth, aHeight) {
			var oldPath = this.currentPath_;
			this.beginPath();

			this.moveTo(aX, aY);
			this.lineTo(aX + aWidth, aY);
			this.lineTo(aX + aWidth, aY + aHeight);
			this.lineTo(aX, aY + aHeight);
			this.closePath();
			this.fill();

			this.currentPath_ = oldPath;
		};

		contextPrototype.createLinearGradient = function(aX0, aY0, aX1, aY1) {
			var gradient = new CanvasGradient_('gradient');
			gradient.x0_ = aX0;
			gradient.y0_ = aY0;
			gradient.x1_ = aX1;
			gradient.y1_ = aY1;
			return gradient;
		};

		contextPrototype.createRadialGradient = function(aX0, aY0, aR0,
			aX1, aY1, aR1) {
			var gradient = new CanvasGradient_('gradientradial');
			gradient.x0_ = aX0;
			gradient.y0_ = aY0;
			gradient.r0_ = aR0;
			gradient.x1_ = aX1;
			gradient.y1_ = aY1;
			gradient.r1_ = aR1;
			return gradient;
		};

		contextPrototype.drawImage = function(image, var_args) {
			var dx, dy, dw, dh, sx, sy, sw, sh;

			// to find the original width we overide the width and height
			var oldRuntimeWidth = image.runtimeStyle.width;
			var oldRuntimeHeight = image.runtimeStyle.height;
			image.runtimeStyle.width = 'auto';
			image.runtimeStyle.height = 'auto';

			// get the original size
			var w = image.width;
			var h = image.height;

			// and remove overides
			image.runtimeStyle.width = oldRuntimeWidth;
			image.runtimeStyle.height = oldRuntimeHeight;

			if (arguments.length == 3) {
				dx = arguments[1];
				dy = arguments[2];
				sx = sy = 0;
				sw = dw = w;
				sh = dh = h;
			} else if (arguments.length == 5) {
				dx = arguments[1];
				dy = arguments[2];
				dw = arguments[3];
				dh = arguments[4];
				sx = sy = 0;
				sw = w;
				sh = h;
			} else if (arguments.length == 9) {
				sx = arguments[1];
				sy = arguments[2];
				sw = arguments[3];
				sh = arguments[4];
				dx = arguments[5];
				dy = arguments[6];
				dw = arguments[7];
				dh = arguments[8];
			} else {
				throw Error('Invalid number of arguments');
			}

			var d = this.getCoords_(dx, dy);

			var w2 = sw / 2;
			var h2 = sh / 2;

			var vmlStr = [];

			var W = 10;
			var H = 10;

			// For some reason that I've now forgotten, using divs didn't work
			vmlStr.push(' <g_vml_:group',
				' coordsize="', Z * W, ',', Z * H, '"',
				' coordorigin="0,0"' ,
				' style="width:', W, 'px;height:', H, 'px;position:absolute;');

			// If filters are necessary (rotation exists), create them
			// filters are bog-slow, so only create them if abbsolutely necessary
			// The following check doesn't account for skews (which don't exist
			// in the canvas spec (yet) anyway.

			if (this.m_[0][0] != 1 || this.m_[0][1]) {
				var filter = [];

				// Note the 12/21 reversal
				filter.push('M11=', this.m_[0][0], ',',
					'M12=', this.m_[1][0], ',',
					'M21=', this.m_[0][1], ',',
					'M22=', this.m_[1][1], ',',
					'Dx=', mr(d.x / Z), ',',
					'Dy=', mr(d.y / Z), '');

				// Bounding box calculation (need to minimize displayed area so that
				// filters don't waste time on unused pixels.
				var max = d;
				var c2 = this.getCoords_(dx + dw, dy);
				var c3 = this.getCoords_(dx, dy + dh);
				var c4 = this.getCoords_(dx + dw, dy + dh);

				max.x = m.max(max.x, c2.x, c3.x, c4.x);
				max.y = m.max(max.y, c2.y, c3.y, c4.y);

				vmlStr.push('padding:0 ', mr(max.x / Z), 'px ', mr(max.y / Z),
					'px 0;filter:progid:DXImageTransform.Microsoft.Matrix(',
					filter.join(''), ", sizingmethod='clip');")
			} else {
				vmlStr.push('top:', mr(d.y / Z), 'px;left:', mr(d.x / Z), 'px;');
			}

			vmlStr.push(' ">' ,
				'<g_vml_:image src="', image.src, '"',
				' style="width:', Z * dw, 'px;',
				' height:', Z * dh, 'px;"',
				' cropleft="', sx / w, '"',
				' croptop="', sy / h, '"',
				' cropright="', (w - sx - sw) / w, '"',
				' cropbottom="', (h - sy - sh) / h, '"',
				' />',
				'</g_vml_:group>');

			this.element_.insertAdjacentHTML('BeforeEnd',
				vmlStr.join(''));
		};

		contextPrototype.stroke = function(aFill) {
			var lineStr = [];
			var lineOpen = false;
			var a = processStyle(aFill ? this.fillStyle : this.strokeStyle);
			var color = a.color;
			var opacity = a.alpha * this.globalAlpha;

			var W = 10;
			var H = 10;

			lineStr.push('<g_vml_:shape',
				' filled="', !!aFill, '"',
				' style="position:absolute;width:', W, 'px;height:', H, 'px;"',
				' coordorigin="0 0" coordsize="', Z * W, ' ', Z * H, '"',
				' stroked="', !aFill, '"',
				' path="');

			var newSeq = false;
			var min = {
				x: null,
				y: null
			};
			var max = {
				x: null,
				y: null
			};

			for (var i = 0; i < this.currentPath_.length; i++) {
				var p = this.currentPath_[i];
				var c;

				switch (p.type) {
					case 'moveTo':
						c = p;
						lineStr.push(' m ', mr(p.x), ',', mr(p.y));
						break;
					case 'lineTo':
						lineStr.push(' l ', mr(p.x), ',', mr(p.y));
						break;
					case 'close':
						lineStr.push(' x ');
						p = null;
						break;
					case 'bezierCurveTo':
						lineStr.push(' c ',
							mr(p.cp1x), ',', mr(p.cp1y), ',',
							mr(p.cp2x), ',', mr(p.cp2y), ',',
							mr(p.x), ',', mr(p.y));
						break;
					case 'at':
					case 'wa':
						lineStr.push(' ', p.type, ' ',
							mr(p.x - this.arcScaleX_ * p.radius), ',',
							mr(p.y - this.arcScaleY_ * p.radius), ' ',
							mr(p.x + this.arcScaleX_ * p.radius), ',',
							mr(p.y + this.arcScaleY_ * p.radius), ' ',
							mr(p.xStart), ',', mr(p.yStart), ' ',
							mr(p.xEnd), ',', mr(p.yEnd));
						break;
				}


				// TODO: Following is broken for curves due to
				//       move to proper paths.

				// Figure out dimensions so we can do gradient fills
				// properly
				if (p) {
					if (min.x == null || p.x < min.x) {
						min.x = p.x;
					}
					if (max.x == null || p.x > max.x) {
						max.x = p.x;
					}
					if (min.y == null || p.y < min.y) {
						min.y = p.y;
					}
					if (max.y == null || p.y > max.y) {
						max.y = p.y;
					}
				}
			}
			lineStr.push(' ">');

			if (!aFill) {
				var lineWidth = this.lineScale_ * this.lineWidth;

				// VML cannot correctly render a line if the width is less than 1px.
				// In that case, we dilute the color to make the line look thinner.
				if (lineWidth < 1) {
					opacity *= lineWidth;
				}

				lineStr.push(
					'<g_vml_:stroke',
					' opacity="', opacity, '"',
					' joinstyle="', this.lineJoin, '"',
					' miterlimit="', this.miterLimit, '"',
					' endcap="', processLineCap(this.lineCap), '"',
					' weight="', lineWidth, 'px"',
					' color="', color, '" />'
					);
			} else if (typeof this.fillStyle == 'object') {
				var fillStyle = this.fillStyle;
				var angle = 0;
				var focus = {
					x: 0,
					y: 0
				};

				// additional offset
				var shift = 0;
				// scale factor for offset
				var expansion = 1;

				if (fillStyle.type_ == 'gradient') {
					var x0 = fillStyle.x0_ / this.arcScaleX_;
					var y0 = fillStyle.y0_ / this.arcScaleY_;
					var x1 = fillStyle.x1_ / this.arcScaleX_;
					var y1 = fillStyle.y1_ / this.arcScaleY_;
					var p0 = this.getCoords_(x0, y0);
					var p1 = this.getCoords_(x1, y1);
					var dx = p1.x - p0.x;
					var dy = p1.y - p0.y;
					angle = Math.atan2(dx, dy) * 180 / Math.PI;

					// The angle should be a non-negative number.
					if (angle < 0) {
						angle += 360;
					}

					// Very small angles produce an unexpected result because they are
					// converted to a scientific notation string.
					if (angle < 1e-6) {
						angle = 0;
					}
				} else {
					var p0 = this.getCoords_(fillStyle.x0_, fillStyle.y0_);
					var width  = max.x - min.x;
					var height = max.y - min.y;
					focus = {
						x: (p0.x - min.x) / width,
						y: (p0.y - min.y) / height
					};

					width  /= this.arcScaleX_ * Z;
					height /= this.arcScaleY_ * Z;
					var dimension = m.max(width, height);
					shift = 2 * fillStyle.r0_ / dimension;
					expansion = 2 * fillStyle.r1_ / dimension - shift;
				}

				// We need to sort the color stops in ascending order by offset,
				// otherwise IE won't interpret it correctly.
				var stops = fillStyle.colors_;
				stops.sort(function(cs1, cs2) {
					return cs1.offset - cs2.offset;
				});

				var length = stops.length;
				var color1 = stops[0].color;
				var color2 = stops[length - 1].color;
				var opacity1 = stops[0].alpha * this.globalAlpha;
				var opacity2 = stops[length - 1].alpha * this.globalAlpha;

				var colors = [];
				for (var i = 0; i < length; i++) {
					var stop = stops[i];
					colors.push(stop.offset * expansion + shift + ' ' + stop.color);
				}

				// When colors attribute is used, the meanings of opacity and o:opacity2
				// are reversed.
				lineStr.push('<g_vml_:fill type="', fillStyle.type_, '"',
					' method="none" focus="100%"',
					' color="', color1, '"',
					' color2="', color2, '"',
					' colors="', colors.join(','), '"',
					' opacity="', opacity2, '"',
					' g_o_:opacity2="', opacity1, '"',
					' angle="', angle, '"',
					' focusposition="', focus.x, ',', focus.y, '" />');
			} else {
				lineStr.push('<g_vml_:fill color="', color, '" opacity="', opacity,
					'" />');
			}

			lineStr.push('</g_vml_:shape>');

			this.element_.insertAdjacentHTML('beforeEnd', lineStr.join(''));
		};

		contextPrototype.fill = function() {
			this.stroke(true);
		}

		contextPrototype.closePath = function() {
			this.currentPath_.push({
				type: 'close'
			});
		};

		/**
   * @private
   */
		contextPrototype.getCoords_ = function(aX, aY) {
			var m = this.m_;
			return {
				x: Z * (aX * m[0][0] + aY * m[1][0] + m[2][0]) - Z2,
				y: Z * (aX * m[0][1] + aY * m[1][1] + m[2][1]) - Z2
			}
		};

		contextPrototype.save = function() {
			var o = {};
			copyState(this, o);
			this.aStack_.push(o);
			this.mStack_.push(this.m_);
			this.m_ = matrixMultiply(createMatrixIdentity(), this.m_);
		};

		contextPrototype.restore = function() {
			copyState(this.aStack_.pop(), this);
			this.m_ = this.mStack_.pop();
		};

		function matrixIsFinite(m) {
			for (var j = 0; j < 3; j++) {
				for (var k = 0; k < 2; k++) {
					if (!isFinite(m[j][k]) || isNaN(m[j][k])) {
						return false;
					}
				}
			}
			return true;
		}

		function setM(ctx, m, updateLineScale) {
			if (!matrixIsFinite(m)) {
				return;
			}
			ctx.m_ = m;

			if (updateLineScale) {
				// Get the line scale.
				// Determinant of this.m_ means how much the area is enlarged by the
				// transformation. So its square root can be used as a scale factor
				// for width.
				var det = m[0][0] * m[1][1] - m[0][1] * m[1][0];
				ctx.lineScale_ = sqrt(abs(det));
			}
		}

		contextPrototype.translate = function(aX, aY) {
			var m1 = [
			[1,  0,  0],
			[0,  1,  0],
			[aX, aY, 1]
			];

			setM(this, matrixMultiply(m1, this.m_), false);
		};

		contextPrototype.rotate = function(aRot) {
			var c = mc(aRot);
			var s = ms(aRot);

			var m1 = [
			[c,  s, 0],
			[-s, c, 0],
			[0,  0, 1]
			];

			setM(this, matrixMultiply(m1, this.m_), false);
		};

		contextPrototype.scale = function(aX, aY) {
			this.arcScaleX_ *= aX;
			this.arcScaleY_ *= aY;
			var m1 = [
			[aX, 0,  0],
			[0,  aY, 0],
			[0,  0,  1]
			];

			setM(this, matrixMultiply(m1, this.m_), true);
		};

		contextPrototype.transform = function(m11, m12, m21, m22, dx, dy) {
			var m1 = [
			[m11, m12, 0],
			[m21, m22, 0],
			[dx,  dy,  1]
			];

			setM(this, matrixMultiply(m1, this.m_), true);
		};

		contextPrototype.setTransform = function(m11, m12, m21, m22, dx, dy) {
			var m = [
			[m11, m12, 0],
			[m21, m22, 0],
			[dx,  dy,  1]
			];

			setM(this, m, true);
		};

		/******** STUBS ********/
		contextPrototype.clip = function() {
		// TODO: Implement
		};

		contextPrototype.arcTo = function() {
		// TODO: Implement
		};

		contextPrototype.createPattern = function() {
			return new CanvasPattern_;
		};

		// Gradient / Pattern Stubs
		function CanvasGradient_(aType) {
			this.type_ = aType;
			this.x0_ = 0;
			this.y0_ = 0;
			this.r0_ = 0;
			this.x1_ = 0;
			this.y1_ = 0;
			this.r1_ = 0;
			this.colors_ = [];
		}

		CanvasGradient_.prototype.addColorStop = function(aOffset, aColor) {
			aColor = processStyle(aColor);
			this.colors_.push({
				offset: aOffset,
				color: aColor.color,
				alpha: aColor.alpha
			});
		};

		function CanvasPattern_() {}

		// set up externs
		G_vmlCanvasManager = G_vmlCanvasManager_;
		CanvasRenderingContext2D = CanvasRenderingContext2D_;
		CanvasGradient = CanvasGradient_;
		CanvasPattern = CanvasPattern_;

	})();

} // if


/* Simple JavaScript Inheritance
 * By John Resig http://ejohn.org/
 * MIT Licensed.
 */
// Inspired by base2 and Prototype
(function(){
  var initializing = false, fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;

  // The base Class implementation (does nothing)
  this.Class = function(){};

  // Create a new Class that inherits from this class
  Class.extend = function(prop) {
    var _super = this.prototype;

    // Instantiate a base class (but only create the instance,
    // don't run the init constructor)
    initializing = true;
    var prototype = new this();
    initializing = false;

    // Copy the properties over onto the new prototype
    for (var name in prop) {
      // Check if we're overwriting an existing function
      prototype[name] = typeof prop[name] == "function" &&
        typeof _super[name] == "function" && fnTest.test(prop[name]) ?
        (function(name, fn){
          return function() {
            var tmp = this._super;

            // Add a new ._super() method that is the same method
            // but on the super-class
            this._super = _super[name];

            // The method only need to be bound temporarily, so we
            // remove it when we're done executing
            var ret = fn.apply(this, arguments);
            this._super = tmp;

            return ret;
          };
        })(name, prop[name]) :
        prop[name];
    }

    // The dummy class constructor
    function Class() {
      // All construction is actually done in the init method
      if ( !initializing && this.init )
        this.init.apply(this, arguments);
    }

    // Populate our constructed prototype object
    Class.prototype = prototype;

    // Enforce the constructor to be what we expect
    Class.constructor = Class;

    // And make this class extendable
    Class.extend = arguments.callee;

    return Class;
  };
})();


(function(jQuery)
{
	jQuery.extend
	({
		namespace: function(what, where)
		{
			where = where || window;

			var parts = what.split(/\./),
					len		= parts.length;

			for (var i = 0; i < len; ++i)
			{
				var part = parts[i];

				if (!(part in where)) where[part] = {};

				where = where[part];
			};

			return where;
		}
	});
})(jQuery);

(function(jQuery)
{
	var bigPortlets = function()
	{
		return true;
	};

	var isOnePorlet = false;

	jQuery.extend
	({
		bigPortlets: bigPortlets,
		getPortletWidth: function()
		{
			// we use the dockbar, because it scales automatically and is
			// not influenced by any content like #main-content
			return isOnePorlet?$('#dockbar').width() - 20:(bigPortlets()?400:325);
		},

		getPortletHeight: function()
		{
			// in fullscreen remove approximate height of dockbar and stuff
			return isOnePorlet?$(window).height() - 180:(bigPortlets()?300:240);
		},

		fixPortletSize: function()
		{
			// blank
		}
	});
	
	jQuery(function()
	{
		isOnePorlet = !!$('#main-content.columns-max').get(0);

		if (!!$('#main-content.columns-max').get(0))
		{
			var parts =
			[
				'<style type="text/css">',
				'div.portlet.maximized {height: '+(window.innerHeight * 1. - 140)+'px !important}',
				'</style>'
			];

			document.body.insertBefore($(parts.join("")).get(0), null);
		};

		jQuery.fixPortletSize();
  });

})(jQuery);

;(function(jQuery)
{
	/**
	 * block page leaving when user has manipulated form controls
	 */
	var isBlocked = null;
	jQuery.extend
	({
		blockControls: function(msg, context)
		{
			jQuery('form', context).bind('submit', function()
			{
				jQuery.unblockPageLeave();
			});

			jQuery('input,select', context).bind('change', function()
			{
				if ($(this).hasClass('no-page-leave-block')) return;

				jQuery.blockPageLeave(msg);
			});
		},
		
		blockAllControls: function(msg)
		{
			jQuery.blockControls(msg, document.body);
		},

		blockPageLeave: function(msg)
		{
			isBlocked = msg;
		},

		unblockPageLeave: function()
		{
//			jQuery(window).unbind("beforeunload");

			isBlocked = null;
		}
	});

  jQuery(window).bind('beforeunload', function(ev)
	{
		if (isBlocked != null) return isBlocked;
	});
})(jQuery);

;(function(ns)
{
	/**
	 * enable scrolling and panning of svg elements
	 * it requires the svg to have on root graph (<g />) which contains
	 * all other elements
	 **/
	ns.SVGInteractive = function(svg, arg)
	{
		arg         = arg || {};

		if (!arg.width || !arg.height) throw new Error("need with and height");

		this.controlNode    = arg.controlNode;

		this.viewportHeight = parseInt(arg.height, 10);
		this.viewportWidth  = parseInt(arg.width, 10);
		
		var svgNode = $('svg', svg).get(0);

		this.graphHeight    = parseInt(svgNode.getAttributeNS(null, 'height'), 10);
		this.graphWidth     = parseInt(svgNode.getAttributeNS(null, 'width'), 10);

		svgNode.setAttributeNS(null, 'height', this.viewportHeight);
		svgNode.setAttributeNS(null, 'width', this.viewportWidth);

		/**
		 * scale and offsets are needed to translate from
		 * viewport coordinate system to graph coordinates
		 **/
		this.graphScale	= 1;
		this.offsetX = 0;
		this.offsetY = 0;

		/**
		 * current zoom
		 **/
		this.userScale	= 1;

		this.dragging = false;

		/**
		 * offset x where the dragging started
		 **/
		this.startX   = 0;

		/**
		 * offset x where the dragging started
		 **/
		this.startY   = 0;

		/**
		 * svg document
		 **/
		this.svg		= svg;

		/**
		 * the first graph which contains all others
		 **/
		this.graph	= svg.getElementById('graph1');

		this.transformations = {};

		this.getTransform = function()
		{
			return this.graph.getAttribute('transform') + "";
		};

		this.setTransform = function(what)
		{
			this.graph.setAttribute('transform', what);
		};

		this.updateTransformations = function()
		{
			if (this.iAmTransforming) return;
			this.iAmTransforming = true;

			try
			{
				var str = [];

				for (var p in this.transformations) str.push(p  + this.transformations[p] );

				this.setTransform(str.join(" "));

			} finally {
				this.iAmTransforming = false;
			};
		};

		this.parseTransformations = function()
		{
			// e.g. "translate(1, 4) scale(1)"
			var m = this.getTransform().match(/\w+\([^\)]+\)/gi);

			if (m)
			{
				for (var i = 0; i < m.length; ++i)
				{
					// name(1, 2)
					var part = m[i].split(/\(/);
					if (!part[0]) continue;

					this.transformations[part[0]] = '(' + part[1];
				};
			};
		};

		this.getCurrentTransformation = function(name)
		{
			var parts = (this.transformations[name] || '').replace(/[\)\(]+/g, "").split(/\ *,?\ +/);

			if (parts && parts.length === 2)
			{
				// transform(4, 256)
				return {
					x: parseFloat(parts[0], 10),
					y: parseFloat(parts[1], 10)
				};
			} else if (parts && parts.length == 1) {
				// transform(0)
				return {
					x: parseFloat(parts[0], 10),
					y: parseFloat(parts[0], 10)
				};
			};

			// no transformation
			return {
				x: 0,
				y: 0
			};
		};

		this.getCurrentScale = function()
		{
			return this.getCurrentTransformation('scale');
		};

		/**
		 * scales the graph
		 *
		 * @param arg.x x scale
		 * @param arg.y y scale
		 */
		this.setScale = function(arg)
		{
			this.transformations.scale = ['(', arg.x, ', ', arg.y, ')'].join("");

			if (!arg.noUpdate) this.updateTransformations();
		};

		this.getCurrentPan = function()
		{
			return this.getCurrentTransformation('translate');
		};

		this.setup = function()
		{
			var local = this;

			this.initGraphScale(this.viewportWidth, this.viewportHeight);

			this.parseTransformations();
			this.setupScale();
			this.setupPan();
			this.setupControls();


			try
			{
				document.body.addEventListener("mouseup", function() 
        {
          local.dragging = false;
        }, false);

				$('*', this.svg).each(function()
				{
					this.addEventListener('dragstart', function(ev)
					{
						if (ev.stopPropagation) ev.stopPropagation();
						if (ev.preventDefault) ev.preventDefault();
					}, false);
				});
			} catch(e) {};
		};

		this.setupScale = function()
		{
			var local = this;

			if (navigator.userAgent.toLowerCase().indexOf('webkit') >= 0)
			{
				(this.graph).addEventListener('mousewheel', function(ev) {
					return local.handleScroll(ev);
				}, false); // Chrome/Safari
			} else {
				if (window.attachEvent)
				{
					(arg.scaleNode || this.graph).onmousewheel = function(ev) {
						return local.handleScroll(ev);
					}; // IE9
				}	else {
					(arg.scaleNode || this.graph).addEventListener('DOMMouseScroll', function(ev) {
						return local.handleScroll(ev);
					}, false); // Others
				}
			};
		};

		this.setupPan = function()
		{
			var local = this;

			this.svg.addEventListener('mousedown', function(ev)
			{
				local.startX   = ev.clientX;
				local.startY   = ev.clientY;
				local.dragging = true;
			}, false);

			this.svg.addEventListener('mouseup', function()
			{
				local.dragging = false;
			}, false);

			this.svg.addEventListener('mousemove', function(ev)
			{
				if (!local.dragging) return false;

				return local.handleDrag(ev);
			}, false);
		};

		this.handleDrag = function(ev)
		{
			if (ev.stopPropagation) ev.stopPropagation();
			if (ev.preventDefault) ev.preventDefault();

			this.translate({
				x: ev.clientX - this.startX,
				y: ev.clientY - this.startY
			});

			this.startX = ev.clientX;
			this.startY = ev.clientY;

			return false;
		};

		this.handleScroll = function(ev)
		{
			if (this.iAmScrolling) return;
			this.iAmScrolling = true;

			if (ev.preventDefault) ev.preventDefault();
			if (ev.stopPropagation) ev.stopPropagation();
			ev.ReturnValue = false;

			var delta;

			if (ev.wheelDelta)
			{
				delta = ev.wheelDelta / 40; // Chrome/Safari 3600
			} else {
				delta = -ev.detail; // Mozilla -90
			};

			this.zoom(Math.pow(1.1, delta), ev.clientX,ev.clientY);

			this.iAmScrolling = false;

			return false;
		};

		/**
		 * sets the panning
		 *
		 * @param arg.x x panning
		 * @param arg.y y panning
		 **/
		this.translate = function(arg)
		{
			var current = this.getCurrentPan();

			current.x += arg.x/(this.graphScale*this.userScale);
			current.y += arg.y/(this.graphScale*this.userScale);

			this.transformations.translate = ['(', current.x, ', ', current.y, ')'].join("");
			this.updateTransformations();
		};

		this.moveUp = function(step)
		{
			this.translate({x:0,y:-this.viewportHeight*step});
		};

		this.moveDown = function(step)
		{
			this.translate({x:0,y:this.viewportHeight*step});
		};

		this.moveLeft = function(step)
		{
			this.translate({x:-this.viewportWidth*step,y:0});
		};

		this.moveRight = function(step)
		{
			this.translate({x:this.viewportWidth*step,y:0});
		};

		this.zoom = function(delta,x,y)
		{
			this.userScale *= delta;

			this.setScale({
				x: this.userScale,
				y: this.userScale
			});

			x += this.offsetX;
			y += this.offsetY;

			this.translate({x:x*(1-delta),y:y*(1-delta)});
		};

		/**
		 * scale and offsets are needed to translate from
		 * viewport coordinate system to graph coordinates
		 **/
		this.initGraphScale = function(width, height)
		{
			var startWidth  = this.graphWidth;
			var startHeight = this.graphHeight;

			var scaleX = height/startHeight;
			var scaleY = width/startWidth;

			if(scaleX<scaleY)
			{
				this.graphScale = scaleX;
			} else {
				this.graphScale = scaleY;
			};

			this.offsetX = (startWidth*this.graphScale-width)/2;
			this.offsetY = (startHeight*this.graphScale-height)/2;
		};

		this.setupControls = function()
		{
			if (!this.controlNode) return;

			var local = this;

			var html =
			[
				'<table style="width:170px; opacity:0.8">',
				'<tr>',
				'<th/>',
				'<th style="text-align:center"><input class="svg-go-up" value="Up" type="button"/></th>',
				'<th/>',
				'</tr>',
				'<tr>',
				'<th style="text-align:center"><input class="svg-go-right" value="&lt;-" type="button" /></th>',
				'<th style="text-align:center"><input class="svg-zoom-in" value="ZoomIn" type="button" /><br/>',
				'<input class="svg-zoom-out" value="ZoomOut" type="button" />',
				'</th>',
				'<th style="text-align:center"><input class="svg-go-left" value="-&gt;" type="button"/></th>',
				'</tr>',
				'<tr>',
				'<th/>',
				'<th style="text-align:center"><input class="svg-go-down" value="Down" type="button" /></th>',
				'<th/>',
				'</tr>',
				'</table>'
			];

			var parts = $(html.join("")).get(0);

			$('.svg-go-up', parts).click(function() {local.moveDown(0.5);});
			$('.svg-go-down', parts).click(function() {local.moveUp(0.5);});
			$('.svg-go-left', parts).click(function() {local.moveLeft(0.5);});
			$('.svg-go-right', parts).click(function() {local.moveRight(0.5);});

			$('.svg-zoom-in', parts).click(function() {local.zoom(2,300,300);});
			$('.svg-zoom-out', parts).click(function() {local.zoom(0.5,300,300);});

			this.controlNode.appendChild(parts);
		};

		this.setup();
	};
})(jQuery.namespace('de.arago.svg'));

;(function(jQuery)
{
	var have = {};
	jQuery.extend
	({
		globalPortletJS: function(what, refresh)
		{
			$.each(what, function(k, mod)
			{
				if (have[mod]) return;

				have[mod] = 1;

				if (refresh) mod += '?'+Math.random();
				
				document.write
				([
					'<script type="text/javascript" src="',
					mod,
					'"></script>'
				].join(""));
			});
		}
	});


})(jQuery);

/*
 * Autocomplete - jQuery plugin 1.1pre
 *
 * Copyright (c) 2007 Dylan Verheul, Dan G. Switzer, Anjesh Tuladhar, JÃ¶rn Zaefferer
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * Revision: $Id: jquery.autocomplete.js 5785 2008-07-12 10:37:33Z joern.zaefferer $
 *
 * taken from: http://dev.jquery.com/view/trunk/plugins/autocomplete/jquery.autocomplete.js
 */

;(function($) {
	$.fn.extend({
		autocomplete: function(urlOrData, options) {
			var isUrl = typeof urlOrData == "string";
			options = $.extend({}, $.Autocompleter.defaults, {
				url: isUrl ? urlOrData : null,
				data: isUrl ? null : urlOrData,
				delay: isUrl ? $.Autocompleter.defaults.delay : 10,
				max: options && !options.scroll ? 10 : 150,
				onResult: options.onResult
			}, options);

			// if highlight is set to false, replace it with a do-nothing function
			options.highlight = options.highlight || function(value) {
				return value;
			};

			// if the formatMatch option is not specified, then use formatItem for backwards compatibility
			options.formatMatch = options.formatMatch || options.formatItem;

			return this.each(function() {
				new $.Autocompleter(this, options);
			});
		},
		result: function(handler) {
			return this.bind("result", handler);
		},
		search: function(handler) {
			return this.trigger("search", [handler]);
		},
		flushCache: function() {
			return this.trigger("flushCache");
		},
		setOptions: function(options){
			return this.trigger("setOptions", [options]);
		},
		unautocomplete: function() {
			return this.trigger("unautocomplete");
		}
	});

	$.Autocompleter = function(input, options) {

		var KEY = {
			UP: 38,
			DOWN: 40,
			DEL: 46,
			TAB: 9,
			RETURN: 13,
			ESC: 27,
			COMMA: 188,
			PAGEUP: 33,
			PAGEDOWN: 34,
			BACKSPACE: 8
		};

		// Create $ object for input element
		var $input = $(input).attr("autocomplete", "off").addClass(options.inputClass);

		var timeout;
		var previousValue = "";
		var cache = $.Autocompleter.Cache(options);
		var hasFocus = 0;
		var lastKeyPressCode;
		var config = {
			mouseDownOnSelect: false
		};
		var select = $.Autocompleter.Select(options, input, selectCurrent, config);

		var blockSubmit;

		// prevent form submit in opera when selecting with return key
		$.browser.opera && $(input.form).bind("submit.autocomplete", function() {
			if (blockSubmit) {
				blockSubmit = false;
				return false;
			}
		});

		// only opera doesn't trigger keydown multiple times while pressed, others don't work with keypress at all
		$input.bind(($.browser.opera ? "keypress" : "keydown") + ".autocomplete", function(event) {
			// track last key pressed
			lastKeyPressCode = event.keyCode;
			switch(event.keyCode) {

				case KEY.UP:
					event.preventDefault();
					if ( select.visible() ) {
						select.prev();
					} else {
						onChange(0, true);
					}
					break;

				case KEY.DOWN:
					event.preventDefault();
					if ( select.visible() ) {
						select.next();
					} else {
						onChange(0, true);
					}
					break;

				case KEY.PAGEUP:
					event.preventDefault();
					if ( select.visible() ) {
						select.pageUp();
					} else {
						onChange(0, true);
					}
					break;

				case KEY.PAGEDOWN:
					event.preventDefault();
					if ( select.visible() ) {
						select.pageDown();
					} else {
						onChange(0, true);
					}
					break;

				// matches also semicolon
				case options.multiple && $.trim(options.multipleSeparator) == "," && KEY.COMMA:
				case KEY.TAB:
				case KEY.RETURN:
					if( selectCurrent() ) {
						// stop default to prevent a form submit, Opera needs special handling
						event.preventDefault();
						blockSubmit = true;
						return false;
					}
					break;

				case KEY.ESC:
					select.hide();
					break;

				default:
					clearTimeout(timeout);
					timeout = setTimeout(onChange, options.delay);
					break;
			}
		}).focus(function(){
			// track whether the field has focus, we shouldn't process any
			// results if the field no longer has focus
			hasFocus++;
		}).blur(function() {
			hasFocus = 0;
			if (!config.mouseDownOnSelect) {
				hideResults();
			}
		}).click(function() {
			// show select when clicking in a focused field
			if ( hasFocus++ > 1 && !select.visible() ) {
				onChange(0, true);
			}
		}).bind("search", function() {
			// TODO why not just specifying both arguments?
			var fn = (arguments.length > 1) ? arguments[1] : null;
			function findValueCallback(q, data) {
				var result;
				if( data && data.length ) {
					for (var i=0; i < data.length; i++) {
						if( data[i].result.toLowerCase() == q.toLowerCase() ) {
							result = data[i];
							break;
						}
					}
				}
				if( typeof fn == "function" ) fn(result);
				else $input.trigger("result", result && [result.data, result.value]);
			}
			$.each(trimWords($input.val()), function(i, value) {
				request(value, findValueCallback, findValueCallback);
			});
		}).bind("flushCache", function() {
			cache.flush();
		}).bind("setOptions", function() {
			$.extend(options, arguments[1]);
			// if we've updated the data, repopulate
			if ( "data" in arguments[1] )
				cache.populate();
		}).bind("unautocomplete", function() {
			select.unbind();
			$input.unbind();
			$(input.form).unbind(".autocomplete");
		});


		function selectCurrent() {
			var selected = select.selected();
			if( !selected )
				return false;

			var v = selected.result;
			previousValue = v;

			if ( options.multiple ) {
				var words = trimWords($input.val());
				if ( words.length > 1 ) {
					v = words.slice(0, words.length - 1).join( options.multipleSeparator ) + options.multipleSeparator + v;
				}
				v += options.multipleSeparator;
			}

			$input.val(v);
			hideResultsNow();
			$input.trigger("result", [selected.data, selected.value]);
			if (options.onResult) return !!options.onResult({
				data: selected.data,
				value: selected.value
			});
			return true;
		}

		function onChange(crap, skipPrevCheck) {
			if( lastKeyPressCode == KEY.DEL ) {
				select.hide();
				return;
			}

			var currentValue = $input.val();

			if ( !skipPrevCheck && currentValue == previousValue )
				return;

			previousValue = currentValue;

			currentValue = lastWord(currentValue);
			if ( currentValue.length >= options.minChars) {
				$input.addClass(options.loadingClass);
				
				if (!options.matchCase)
					currentValue = currentValue.toLowerCase();
				request(currentValue, receiveData, hideResultsNow);
			} else {
				stopLoading();
				select.hide();
			}
		};

		function trimWords(value) {
			if ( !value ) {
				return [""];
			}
			var words = value.split( options.multipleSeparator );
			var result = [];
			$.each(words, function(i, value) {
				if ( $.trim(value) )
					result[i] = $.trim(value);
			});
			return result;
		}

		function lastWord(value) {
			if ( !options.multiple )
				return value;
			var words = trimWords(value);
			return words[words.length - 1];
		}

		// fills in the input box w/the first match (assumed to be the best match)
		// q: the term entered
		// sValue: the first matching result
		function autoFill(q, sValue){
			// autofill in the complete box w/the first match as long as the user hasn't entered in more data
			// if the last user key pressed was backspace, don't autofill
			if( options.autoFill && (lastWord($input.val()).toLowerCase() === q.toLowerCase()) && lastKeyPressCode != KEY.BACKSPACE ) {
				// fill in the value (keep the case the user has typed)
				$input.val($input.val() + sValue.substring(lastWord(previousValue).length));
				// select the portion of the value not typed by the user (so the next character will erase)
				$.Autocompleter.Selection(input, previousValue.length, previousValue.length + sValue.length);
			}
		};

		function hideResults() {
			clearTimeout(timeout);
			timeout = setTimeout(hideResultsNow, 200);
		};

		function hideResultsNow() {
			var wasVisible = select.visible();
			select.hide();
			clearTimeout(timeout);
			stopLoading();
			if (options.mustMatch) {
				// call search and run callback
				$input.search(
					function (result){
						// if no value found, clear the input box
						if( !result ) {
							if (options.multiple) {
								var words = trimWords($input.val()).slice(0, -1);
							//$input.val( words.join(options.multipleSeparator) + (words.length ? options.multipleSeparator : "") );
							}
							else {
						//$input.val( "" );
						};
						}
					}
					);
			}
			if (wasVisible)
				// position cursor at end of input field
				$.Autocompleter.Selection(input, input.value.length, input.value.length);
		};

		function receiveData(q, data) {
			if ( data && data.length && hasFocus ) {
				stopLoading();
				select.display(data, q);
				autoFill(q, data[0].value);
				select.show();
			} else {
				hideResultsNow();
			}
		};

		function request(term, success, failure) {
			if (!options.matchCase)
				term = term.toLowerCase();
			var data = cache.load(term);
			// recieve the cached data
			if (!options.noCache && data && data.length) {
				success(term, data);
			// if an AJAX url has been supplied, try loading the data now
			} else if( (typeof options.url == "string") && (options.url.length > 0) ){
				if (options.onstart) options.onstart();
				var extraParams = {
				//timestamp: +new Date()
				};
				$.each(options.extraParams, function(key, param) {
					extraParams[key] = typeof param == "function" ? param() : param;
				});

				$.ajax({
					// try to leverage ajaxQueue plugin to abort previous requests
					mode: "abort",
					// limit abortion to this input
					port: "autocomplete" + input.name,
					dataType: options.dataType,
					url: options.url,
					data: $.extend({
						q: lastWord(term),
						limit: options.max
					}, extraParams),
					success: function(data) {
						if (options.onend) options.onend();
						var parsed = options.parse && options.parse(data) || parse(data);
						cache.add(term, parsed);
						success(term, parsed);
					}
				});
			} else {
				// if we have a failure, we need to empty the list -- this prevents the the [TAB] key from selecting the last successful match
				select.emptyList();
				failure(term);
			}
		};

		function parse(data) {
			var parsed = [];
			var rows = data.split("\n");
			for (var i=0; i < rows.length; i++) {
				var row = $.trim(rows[i]);
				if (row) {
					row = row.split("|");
					parsed[parsed.length] = {
						data: row,
						value: row[0],
						result: options.formatResult && options.formatResult(row, row[0]) || row[0]
					};
				}
			}
			return parsed;
		};

		function stopLoading() {
			$input.removeClass(options.loadingClass);
		};

	};

	$.Autocompleter.defaults = {
		inputClass: "ac_input",
		resultsClass: "ac_results",
		loadingClass: "ac_loading",
		minChars: 1,
		delay: 400,
		matchCase: false,
		matchSubset: true,
		matchContains: false,
		cacheLength: 10,
		max: 100,
		mustMatch: false,
		extraParams: {},
		selectFirst: true,
		formatItem: function(row) {
			return row[0];
		},
		formatMatch: null,
		autoFill: false,
		width: 0,
		multiple: false,
		multipleSeparator: ", ",
		highlight: function(value, term) {
			return value.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + term.replace(/([\^\$\(\)\[\]\{\}\*\.\+\?\|\\])/gi, "\\$1") + ")(?![^<>]*>)(?![^&;]+;)", "gi"), "<strong>$1</strong>");
		},
		scroll: true,
		scrollHeight: 180
	};

	$.Autocompleter.Cache = function(options) {

		var data = {};
		var length = 0;

		function matchSubset(s, sub) {
			if (!options.matchCase)
				s = s.toLowerCase();
			var i = s.indexOf(sub);
			if (options.matchContains == "word"){
				i = s.toLowerCase().search("\\b" + sub.toLowerCase());
			}
			if (i == -1) return false;
			return i == 0 || options.matchContains;
		};

		function add(q, value) {
			if (length > options.cacheLength){
				flush();
			}
			if (!data[q]){
				length++;
			}
			data[q] = value;
		}

		function populate(){
			if( !options.data ) return false;
			// track the matches
			var stMatchSets = {},
			nullData = 0;

			// no url was specified, we need to adjust the cache length to make sure it fits the local data store
			if( !options.url ) options.cacheLength = 1;

			// track all options for minChars = 0
			stMatchSets[""] = [];

			// loop through the array and create a lookup structure
			for ( var i = 0, ol = options.data.length; i < ol; i++ ) {
				var rawValue = options.data[i];
				// if rawValue is a string, make an array otherwise just reference the array
				rawValue = (typeof rawValue == "string") ? [rawValue] : rawValue;

				var value = options.formatMatch(rawValue, i+1, options.data.length);
				if ( value === false )
					continue;

				var firstChar = value.charAt(0).toLowerCase();
				// if no lookup array for this character exists, look it up now
				if( !stMatchSets[firstChar] )
					stMatchSets[firstChar] = [];

				// if the match is a string
				var row = {
					value: value,
					data: rawValue,
					result: options.formatResult && options.formatResult(rawValue) || value
				};

				// push the current match into the set list
				stMatchSets[firstChar].push(row);

				// keep track of minChars zero items
				if ( nullData++ < options.max ) {
					stMatchSets[""].push(row);
				}
			};

			// add the data items to the cache
			$.each(stMatchSets, function(i, value) {
				// increase the cache size
				options.cacheLength++;
				// add to the cache
				add(i, value);
			});
		}

		// populate any existing data
		setTimeout(populate, 25);

		function flush(){
			data = {};
			length = 0;
		}

		return {
			flush: flush,
			add: add,
			populate: populate,
			load: function(q) {
				if (!options.cacheLength || !length)
					return null;
				/*
			 * if dealing w/local data and matchContains than we must make sure
			 * to loop through all the data collections looking for matches
			 */
				if( !options.url && options.matchContains ){
					// track all matches
					var csub = [];
					// loop through all the data grids for matches
					for( var k in data ){
						if (!data.hasOwnProperty(k)) continue;
						// don't search through the stMatchSets[""] (minChars: 0) cache
						// this prevents duplicates
						if( k.length > 0 ){
							var c = data[k];
							$.each(c, function(i, x) {
								// if we've got a match, add it to the array
								if (matchSubset(x.value, q)) {
									csub.push(x);
								}
							});
						}
					}
					return csub;
				} else
				// if the exact item exists, use it
				if (data[q] && data.hasOwnProperty(q)){
					return data[q];
				} else
				if (options.matchSubset) {
					for (var i = q.length - 1; i >= options.minChars; i--) {
						var c = data[q.substr(0, i)];
						if (c && data.hasOwnProperty(c)) {
							var csub = [];
							$.each(c, function(i, x) {
								if (matchSubset(x.value, q)) {
									csub[csub.length] = x;
								}
							});
							return csub;
						}
					}
				}
				return null;
			}
		};
	};

	$.Autocompleter.Select = function (options, input, select, config) {
		var CLASSES = {
			ACTIVE: "ac_over"
		};

		var listItems,
		active = -1,
		data,
		term = "",
		needsInit = true,
		element,
		list;

		// Create results
		function init() {
			if (!needsInit)
				return;
			element = $("<div/>")
			.hide()
			.addClass(options.resultsClass)
			.css("position", "absolute")
			.appendTo(document.body);

			list = $("<ul/>").appendTo(element).mouseover( function(event) {
				if(target(event).nodeName && target(event).nodeName.toUpperCase() == 'LI') {
					active = $("li", list).removeClass(CLASSES.ACTIVE).index(target(event));
					$(target(event)).addClass(CLASSES.ACTIVE);
				}
			}).click(function(event) {
				$(target(event)).addClass(CLASSES.ACTIVE);
				select();
				// TODO provide option to avoid setting focus again after selection? useful for cleanup-on-focus
				input.focus();
				return false;
			}).mousedown(function() {
				config.mouseDownOnSelect = true;
			}).mouseup(function() {
				config.mouseDownOnSelect = false;
			});

			if( options.width > 0 )
				element.css("width", options.width);

			needsInit = false;
		}

		function target(event) {
			var element = event.target;
			while(element && element.tagName != "LI")
				element = element.parentNode;
			// more fun with IE, sometimes event.target is empty, just ignore it then
			if(!element)
				return [];
			return element;
		}

		function moveSelect(step) {
			listItems.slice(active, active + 1).removeClass(CLASSES.ACTIVE);
			movePosition(step);
			var activeItem = listItems.slice(active, active + 1).addClass(CLASSES.ACTIVE);
			if(options.scroll) {
				var offset = 0;
				listItems.slice(0, active).each(function() {
					offset += this.offsetHeight;
				});
				if((offset + activeItem[0].offsetHeight - list.scrollTop()) > list[0].clientHeight) {
					list.scrollTop(offset + activeItem[0].offsetHeight - list.innerHeight());
				} else if(offset < list.scrollTop()) {
					list.scrollTop(offset);
				}
			}
		};

		function movePosition(step) {
			active += step;
			if (active < 0) {
				active = listItems.size() - 1;
			} else if (active >= listItems.size()) {
				active = 0;
			}
		}

		function limitNumberOfItems(available) {
			return options.max && options.max < available
			? options.max
			: available;
		}

		function fillList() {
			list.empty();
			var max = limitNumberOfItems(data.length);
			for (var i=0; i < max; i++) {
				if (!data[i])
					continue;
				var formatted = options.formatItem(data[i].data, i+1, max, data[i].value, term);
				if ( formatted === false )
					continue;
				var li = $("<li/>").html( options.highlight(formatted, term) ).addClass(i%2 == 0 ? "ac_even" : "ac_odd").appendTo(list)[0];
				$.data(li, "ac_data", data[i]);
			}
			listItems = list.find("li");
			if ( options.selectFirst ) {
				listItems.slice(0, 1).addClass(CLASSES.ACTIVE);
				active = 0;
			}
			// apply bgiframe if available
			if ( $.fn.bgiframe )
				list.bgiframe();
		}

		return {
			display: function(d, q) {
				init();
				data = d;
				term = q;
				fillList();
			},
			next: function() {
				moveSelect(1);
			},
			prev: function() {
				moveSelect(-1);
			},
			pageUp: function() {
				if (active != 0 && active - 8 < 0) {
					moveSelect( -active );
				} else {
					moveSelect(-8);
				}
			},
			pageDown: function() {
				if (active != listItems.size() - 1 && active + 8 > listItems.size()) {
					moveSelect( listItems.size() - 1 - active );
				} else {
					moveSelect(8);
				}
			},
			hide: function() {
				element && element.hide();
				listItems && listItems.removeClass(CLASSES.ACTIVE);
				active = -1;
			},
			visible : function() {
				return element && element.is(":visible");
			},
			current: function() {
				return this.visible() && (listItems.filter("." + CLASSES.ACTIVE)[0] || options.selectFirst && listItems[0]);
			},
			show: function() {
				var offset = $(input).offset();

				var elWidth = offset.left + (options.offsetRight || $(options.widthNode || input).width());

				element.css({
					width: typeof options.width == "string" || options.width > 0 ? options.width : $(input).width(),
					top: offset.top + input.offsetHeight
				}).show();

				element.css
				({
					left: elWidth - $(element).width()
				});

				if(options.scroll) {
					list.scrollTop(0);
					list.css({
						maxHeight: options.scrollHeight,
						overflow: 'auto'
					});

					if($.browser.msie && typeof document.body.style.maxHeight === "undefined") {
						var listHeight = 0;
						listItems.each(function() {
							listHeight += this.offsetHeight;
						});
						var scrollbarsVisible = listHeight > options.scrollHeight;
						list.css('height', scrollbarsVisible ? options.scrollHeight : listHeight );
						if (!scrollbarsVisible) {
							// IE doesn't recalculate width when scrollbar disappears
							listItems.width( list.width() - parseInt(listItems.css("padding-left"), 10) - parseInt(listItems.css("padding-right"), 10) );
						}
					}

				}
			},
			selected: function() {
				var selected = listItems && listItems.filter("." + CLASSES.ACTIVE).removeClass(CLASSES.ACTIVE);
				return selected && selected.length && $.data(selected[0], "ac_data");
			},
			emptyList: function (){
				list && list.empty();
			},
			unbind: function() {
				element && element.remove();
			}
		};
	};

	$.Autocompleter.Selection = function(field, start, end) {
		if( field.createTextRange ){
			var selRange = field.createTextRange();
			selRange.collapse(true);
			selRange.moveStart("character", start);
			selRange.moveEnd("character", end);
			selRange.select();
		} else if( field.setSelectionRange ){
			field.setSelectionRange(start, end);
		} else {
			if( field.selectionStart ){
				field.selectionStart = start;
				field.selectionEnd = end;
			}
		}
		field.focus();
	};

})(jQuery);