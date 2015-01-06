/*
 * Tree Map JS
 *
 * Jan Engehausen, 2010-08-07
 *
 * Published under "Eclipse Public License - v 1.0"
 * http://www.eclipse.org/legal/epl-v10.html
 */

/*
 * Tree Map widget which renders a weighted tree model
 * into a canvas as a tree map. If the tree model is nested,
 * the widget supports zoom in (left click) and zoom out
 * (right click).
 *
 * The Tree Map widget needs to be connected to the canvas
 * element using the hook(canvas) method. It can be removed from
 * the canvas using unhook(canvas).
 *
 * A weighted tree model must be set for the tree map using
 * the setTreeModel(weightedTreeModel) method. For displaying
 * the rectangle layout needs to be built using the compute()
 * method, followed by the paint() method.
 *
 * It is recommended to use a non-default color provider, and
 * depending on your needs a non-default layout (e.g. with a
 * higher depth than default 2) and a non-default rectangle
 * renderer.
 *
 * Listeners can be added to the tree map which are notified
 * of selection changes using addSelectionChangeListener(listener).
 * The listener must have a method selectionChanged(treeMap, node).
 *
 * Finally: You might want to serve this file in a minified version.
 */
function TreeMap() {

  /** private members **/
  var layout = new SquarifiedLayout(2);

  var colorProvider = new ColorProvider('#000080');
  var renderer = new RectangleRenderer();
  var listeners = [];
  var rectangles;
  var currentRect;
  var currentNode;
  var treeModel;
  var canvas;
    
  /** privileged methods **/
  
  /*
   * sets the layout used by the treemap.
   * if not called, the treemap uses a squarified layout of depth 2
   */
  this.setLayout = function(aLayout) {
    layout = aLayout;
  };

  /*
   * sets the rectangle renderer used by the treemap.
   * if not called, the treemap uses a rectangle renderer
   */  
  this.setRenderer = function(aRenderer) {
    renderer = aRenderer;
  };

  /*
   * sets the color provider used by the treemap for rendering
   * individual rectangles.
   * if not called, the treemap uses a mono color provider (blue)
   */  
  this.setColorProvider = function(aColorProvider) {
    colorProvider = aColorProvider;
  };

  /*
   * sets the tree model
   */
  this.setTreeModel = function(aTreeModel) {
    treeModel = aTreeModel;
    if (treeModel) {
      currentNode = aTreeModel.getRoot();
    } else {
      currentNode = undefined;
    }
    currentRect = undefined;
    rectangles = undefined;
  };
  
  /*
   * returns the tree model currently used by the treemap
   */
  this.getTreeModel = function() {
    return treeModel;
  };
  
  /*
   * hooks up the canvas with this tree map
   */
  this.hook = function(aCanvas) {
    canvas = aCanvas;
    canvas.addEventListener("mousemove", this, false);
    canvas.addEventListener("mouseup", this, false);
  };
  
  /*
   * unhooks the tree map from the canvas
   */
  this.unhook = function() {
    canvas.removeEventListener("mouseup", this.zoom, false);
    canvas.removeEventListener("mousemove", this.highlight, false);
    canvas = undefined;  
  };
  
  /*
   * adds a listener for selection changes to the tree map
   * the listener will be called using ".selectionChanged(this)", i.e.
   * with the tree map where the selection changed. all required
   * information can then be retrieved from the tree map itself:
   * treemap.currentRect : currently selected rectangle 
   * treemap.canvas : canvas HTML object
   */
  this.addSelectionChangeListener = function(listener) {
    listeners.push(listener);
  };
  
  /*
   * removes a listener for selection changes to the tree map
   */
  this.removeSelectionChangeListener = function(listener) {
    var i;
    for (i = listeners.length-1; i>=0; i--) {
      if (listeners[i] == listener) {
        listeners.splice(i, 1);
        break;
      }
    }
  };

  /*
   * paints the canvas
   */
  this.paint = function() {
    var ctx = _getContext();
    if (ctx) {
      if (rectangles) {
        ctx.fillStyle = "#ffffff";
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        queue = [rectangles.getRoot()];
        while (queue.length > 0) {
          var rect = queue.shift();
          renderer.render(ctx, rectangles, rect, colorProvider);
          if (rectangles.hasChildren(rect)) {
            var children = rectangles.getChildren(rect);
            var i;
            for (i = children.length-1; i >= 0; i--) {
              queue.push(children[i]);
            }
          }
        }
      } else {
        this.noData(ctx);
      }
    }
  };

  /*
   * invoked when rendering takes place without
   * a tree model assigned, respectively when no rectangles
   * for the layout have been computed, or are not available.
   */
  this.noData = function(context) {
    context.fillStyle = "#ffffff";
    context.fillRect(0, 0, canvas.width, canvas.height);
    context.fillStyle = "#000000";
    context.strokeStyle = "#000000";
    var text = "no data";
    var metrics = context.measureText(text);
    context.fillText(text, (canvas.width-metrics.width)/2, canvas.height/2);
    context.strokeRect(0, 0, canvas.width, canvas.height);
  };

  /*
   * highlight; called when mouse moved over canvas
   */
  this.highlight = function(event) {  
    if (!rectangles) {
      return;
    }
    var runner = event.target;
    var lx = event.pageX;
    var ly = event.pageY;
    if (runner.offsetParent) {
      do {
        lx -= runner.offsetLeft;
        ly -= runner.offsetTop;
      } while (runner = runner.offsetParent);
    }
    if (currentRect) {
      if (currentRect.contains(lx, ly)) {
        // inside of highlighted rectangle already
        return;
      } else {
        var ctx = _getContext();
        renderer.render(ctx, rectangles, currentRect, colorProvider);
      }
    }
    currentRect = _findRectangle(lx, ly);
    if (currentRect) {
      var ctx = _getContext();
      renderer.highlight(ctx, rectangles, currentRect, colorProvider);
      var i;
      for (i = listeners.length-1; i>=0; i--) {
        listeners[i].selectionChanged(this, currentRect.node);
      }
    }
  };
  
  /*
   * zoom; called when a mouse button is clicked
   */
  this.zoom = function(event) {
    if (treeModel) {
      if (event.button == 0 && currentRect) {
        var runner = currentRect.node;
        var last;
        do {
          last = runner;
          runner = treeModel.getParent(runner);
        } while (runner != currentNode);
        currentNode = last;
        currentRect = undefined;
        this.compute();
        this.paint();
      } else if (event.button == 2) {
        var parent = treeModel.getParent(currentNode);
        if (parent) {
          currentNode = parent;
          currentRect = undefined;
          this.compute();
          this.paint();
        }
      }
    }
  };
    
  /*
   * computes the layout rectangles to be painted
   */
  this.compute = function() {
    if (canvas && treeModel && currentNode) {
      rectangles = layout.layout(treeModel, currentNode, canvas.width, canvas.height);
      currentRect = undefined;
    }
  };

  /*
   * event handling method; deals with mouse movement
   * for highlighting purposes and mouse clicks for
   * zooming purposes
   */  
  this.handleEvent = function(event) {
    if (event.type == "mousemove") {
      this.highlight(event);
    } else if (event.type == "mouseup") {
      this.zoom(event);
    }
  };

  /** private methods **/
    
  // returns the canvas 2d context for painting purposes
  function _getContext() {
    if (canvas && canvas.getContext) {
        return canvas.getContext('2d');
    }
  };
  
  // returns the best matching rectangle for the given coordinates, if any
  function _findRectangle(x, y) {
    if (rectangles) {
      var result = rectangles.getRoot();
      while (rectangles.hasChildren(result)) {
        var found = false;
        var children = rectangles.getChildren(result);
        var i;
        for (i = children.length-1; i>=0; i--) {
          var candidate = children[i];
          if (candidate.contains(x, y)) {
            result = candidate;
            found = true;
            break;
          }
        }
        if (found == false) {
          break;
        }
      }
      return result;
    }
    return undefined;
  };
  
}

/*
 * (Mono) color provider
 */
function ColorProvider(color) {

  /** private members **/
  
  var theColor = color;

  /** privileged methods **/

  this.getColor = function(node) {
    return theColor;
  };
  
}

/*
 * Rectangle renderer
 *
 * Renders single rectangles in two modes: normal and
 * highlighted.
 */
function RectangleRenderer() {

  /** privileged methods **/

  /*
   * Renders the given rectangle into the given context, using
   * the supplied color provider.
   */
  this.render = function(context, rectangleModel, rectangle, colorProvider) {
    context.fillStyle = colorProvider.getColor(rectangle.node);
    context.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  };
  
  /*
   * Renders the given rectangle in highlighted mode into the given context, using
   * the supplied color provider.
   */
  this.highlight = function(context, rectangleModel, rectangle, colorProvider) {
    this.render(context, rectangleModel, rectangle, colorProvider);
    context.strokeStyle = "#ff0000";
    context.strokeRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  };
  
}

/*
 * Cushion rectangle renderer
 *
 * Renders rectangle in a "cushion" style.
 */
function CushionRectangleRenderer() {

  /** privileged methods **/

  // normal mode
  this.render = function(context, rectangleModel, rectangle, colorProvider) {
    if (!rectangleModel.hasChildren(rectangle)) {
      var col1 = colorProvider.getColor(rectangle.node);
      var col2 = _convert(col1, 0.02);
      _render(context, rectangle, col2, col1);
    }
  };
  
  // highlighted mode
  this.highlight = function(context, rectangleModel, rectangle, colorProvider) {
    this.render(context, rectangleModel, rectangle, colorProvider);
    if (!rectangleModel.hasChildren(rectangle)) {
      context.fillStyle = "rgba(255, 255, 255, 0.33)";
      context.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
  };
  
  /** private methods **/

  // render a single rectangle using a radial gradient between
  // the given two colors
  function _render(context, rectangle, col1, col2) {
    var cx = rectangle.x+(rectangle.width/2);
    var cy = rectangle.y+(rectangle.height/2);
    var gradient = context.createRadialGradient(cx, cy, 1, cx, cy, Math.max(rectangle.width, rectangle.height));
    gradient.addColorStop(0, col1);
    gradient.addColorStop(1, col2);
    context.fillStyle = gradient;
    context.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }

  // multiply all colors of a "string color" by the given factor 
  function _convert(color, factor) {
    // color must be a string like #rrggbb
    var rgb = [parseInt(color.substring(1,2),16),
               parseInt(color.substring(3,4),16),
               parseInt(color.substring(5,6),16)];
    rgb[0] = Math.round(Math.max(255, rgb[0]*factor));
    rgb[1] = Math.round(Math.max(255, rgb[1]*factor));
    rgb[2] = Math.round(Math.max(255, rgb[2]*factor));
    return "#"+rgb[0].toString(16)+rgb[1].toString(16)+rgb[2].toString(16);
  }

}

/*
 * tree model, basing on a JSON "tree" representation (nested list)
 * the model also provides weights and labels for each node
 *
 * e.g. { "label": "root", "weight": 4, "children": [{ "label": "only-child", "weight": 4, "children": [] }] }
 */
function JSONTreeModel(treeRep) {

  /** private members **/
  var root = treeRep;

  /** privileged methods **/
  
  /*
   * returns the root node
   */
  this.getRoot = function() {
    return root;
  };

  /*
   * returns the children of the given node
   */ 
  this.getChildren = function(node) {  
    if (node.children && node.children.length) {
      return node.children;
    } else {
      return [];
    }
  };
  
  /*
   * indicates if the given node has children or not
   */ 
  this.hasChildren = function(node) {
    if (node.children && node.children.length && node.children.length>0) {
      return true;
    } else {
      return false;
    }
  };
  
  /*
   * returns the parent node for the given node
   */
  this.getParent = function(node) {
    return node._parent;  // public members access; ugly
  };

  /*
   * returns the weight of the given node
   */  
  this.getWeight = function(node) {
    if (node.weight) {
      return node.weight;
    } else {
      return 0;
    }
  };
  
  /*
   * returns the label of the given node
   */  
  this.getLabel = function(node) {
    if (node.label) {
      return node.label;
    } else {
      return "";
    }
  };
  
  /** private methods */
  
  // recursively builds the parent/child relationships
  function _build(_lst, _parent) {
    for (var i = 0; i < _lst.length; i++) {
      var node = _lst[i];
      node._parent = _parent;  // public assign of parent; ugly 
      if (node.children) {
        _build(node.children, node);
      }
    }
  }

  // initialization
  if (treeRep.children) {
    _build(treeRep.children, treeRep);
  }
  
}

/*
 * tree model, basing on a XML document
 * the model also provides weights and labels for each node
 */
function XMLTreeModel(xmlDocument) {

  /** private members **/
  
  var root = xmlDocument.documentElement;

  /** privileged methods **/

  /*
   * returns the root node
   */
  this.getRoot = function() {
    return root;
  };
  
  /*
   * returns the children of the given node
   */ 
  this.getChildren = function(node) {
    var result = [];
    var current = node.firstChild;
    while (current) {
      if (current.tagName == "node") {
        result.push(current);
      }
      current = current.nextSibling;
    }
    return result;
  };
  
  /*
   * indicates if the given node has children or not
   */ 
  this.hasChildren = function(node) {
    return node.hasChildNodes();
  };
  
  /*
   * returns the parent node for the given node
   */
  this.getParent = function(node) {
    return node.parentNode;
  };
  
  /*
   * returns the weight of the given node
   */  
  this.getWeight = function(node) {
    if (node.getAttribute) {
      return node.getAttribute("weight");
    } else {
      return 0;
    }
  };
  
  /*
   * returns the label of the given node
   */  
  this.getLabel = function(node) {
    if (node.getAttribute) {
      return node.getAttribute("label");
    } else {
      return "";
    }
  };
  
}

/*
 * rectangle representation, associated with a node of the
 * treemaps' tree model.
 */
function _Rectangle(node,x,y,width,height) {

  /** public members; making an exception here for easier use of the object **/
  this.node = node;
  this.x = x;
  this.y = y;
  this.width = width;
  this.height = height;
  
  /** privileged methods */

  /*
   * indicates if this rectangle contains
   * the given coordinates
   */  
  this.contains = function(x,y) {
    var t = x-this.x;
    if (t >= 0 && t < this.width) {
      t = y-this.y;
      return (t >= 0 && t < this.height);
    } else {
      return false;
    }
  };
  
  /*
   * splits the rectangle into two rectangles
   * with the given proportional fraction (must
   * be >0 and <1)
   */
  this.split = function(fraction) {
    if (fraction <= 0 || fraction >= 1) {
      throw "illegal fraction "+fraction;
    }
    var result = [];
    if (this.width < this.height) {
      var nh = this.height * fraction;
      result.push(new _Rectangle(this.node, this.x, this.y, this.width, nh));
      result.push(new _Rectangle(this.node, this.x, this.y+nh, this.width, this.height-nh));
    } else {
      var nw = this.width * fraction;
      result.push(new _Rectangle(this.node, this.x, this.y, nw, this.height));
      result.push(new _Rectangle(this.node, this.x+nw, this.y, this.width-nw, this.height))
    }
    return result;
  };
  
}

/*
 * weight comparator used for building the rectangles
 * used for rendering of a treemap.
 */
function _WeightComparator(treeModel) {

  /** private members **/
  var model = treeModel;
  
  /** privileged methods **/

  /*
   * sorts nodes based on their weight
   */
  this.sortFunction = function(a, b) {
    return model.getWeight(b) - model.getWeight(a);
  }

  /*
   * returns the associated tree model
   */  
  this.getTreeModel = function() {
    return model;
  }

}

/*
 * squarified layout, basing on the ideas of Wijk et al
 */
function SquarifiedLayout(maximumDepth) {

  /** private members **/
  
  // the maximum depth to recurse into a tree model
  var maxDepth = maximumDepth;
  
  /** privileged methods **/

  /*
   * computes the rectangle layout for the given tree model,
   * starting at the given starting node
   */
  this.layout = function(treeModel, startNode, width, height) {
    var root = new _Rectangle(startNode, 0, 0, width, height);
    var result = new JSONTreeModel(root);
    var comparator = new _WeightComparator(treeModel);
    _layout(result, root, comparator, 0);
    return result;
  };

  /** private methods **/
  
  // layout nodes for one particular depth
  function _layout(result, rectangle, comparator, depth) {
    if (depth < maxDepth) {
      var n = rectangle.node;
      var treeModel = comparator.getTreeModel();
      if (treeModel.hasChildren(n)) {
        var nodes = treeModel.getChildren(n);
        if (nodes.length > 2) {
          nodes = nodes.sort(comparator.sortFunction);
          _layoutNodes(result, rectangle, rectangle, comparator, nodes, 0, nodes.length, treeModel.getWeight(n), depth);
        } else {
          _slice(result, rectangle, rectangle, comparator, nodes, 0, nodes.length, treeModel.getWeight(n), depth);
        }
      }
    }
  }
  
  // layout a list of nodes
  function _layoutNodes(result, parent, rectangle, comparator, nodes, start, end, weight, depth) {
    if (end-start>2) {
      var aspectRatio = Number.MAX_VALUE;
      var last;
      var i = start;
      var sum = 0;
      var rect = [0,0];
      var treeModel = comparator.getTreeModel();
      do {
        var n = nodes[i++];
        var nodeWeight = treeModel.getWeight(n);
        sum += nodeWeight;
        rect[0] = rectangle.width;
        rect[1] = rectangle.height;
        _fit(rect, sum, weight);
        _fit(rect, nodeWeight, sum);
        last = aspectRatio;
        aspectRatio = rect[0]>rect[1]?rect[0]/rect[1]:rect[1]/rect[0];
        if (aspectRatio > last) {
          sum -= treeModel.getWeight(nodes[--i]);
          var frac = sum/weight;
          if (frac > 0 && frac < 1) {
            var r = rectangle.split(frac);
            _layoutNodes(result, parent, r[0], comparator, nodes, start, i, sum, depth);
            _layoutNodes(result, parent, r[1], comparator, nodes, i, end, weight-sum, depth);
            return;
          } else {
            break;
          }
        }
      } while (i<end);
    }
    // slice
    _slice(result, parent, rectangle, comparator, nodes, start, end, weight, depth);
  }
  
  // slice a list of nodes into a given rectangle
  function _slice(result, parent, rectangle, comparator, nodes, start, end, weight, depth) {
    var last = end-1;
    var treeModel = comparator.getTreeModel();
    if (rectangle.width < rectangle.height) {
      //split horizontally
	  var sx = rectangle.x;
      var sy = rectangle.y;
      var maxy = rectangle.y+rectangle.height;
      var i;
      for (i = start; i < end && sy < maxy; i++) {
        var c = nodes[i];
        var wc = treeModel.getWeight(c);
        var step = (i!=last)?Math.round(rectangle.height*wc/weight):(rectangle.height-(sy-rectangle.y));
        if (step > 0) {
          var child = new _Rectangle(c, sx, sy, rectangle.width, step);
          _addChild(result, parent, child);
          if (treeModel.hasChildren(c)) {
            _layout(result, child, comparator, depth+1);
          }
          sy += step;
        } else {
          // too small to actually display
          var rest = rectangle.height-(sy-rectangle.y);
          if (rest > 0) {
            var child = new _Rectangle(c, sx, sy, rectangle.width, 1);
            _addChild(result, parent, child);
            sy++;
          }
        }
      }
    } else {
      //split vertically
	  var sx = rectangle.x;
      var sy = rectangle.y;
      var maxx = rectangle.x+rectangle.width;
      var i;
      for (i = start; i < end && sx < maxx; i++) {
        var c = nodes[i];
        var wc = treeModel.getWeight(c);
        var step = (i!=last)?Math.round(rectangle.width*wc/weight):(rectangle.width-(sx-rectangle.x));
        if (step > 0) {
          var child = new _Rectangle(c, sx, sy, step, rectangle.height);
          _addChild(result, parent, child);
          if (treeModel.hasChildren(c)) {
            _layout(result, child, comparator, depth+1);
          }
          sx += step;
        } else {
          // too small to actually display
          var rest = rectangle.width-(sx-rectangle.x);
          if (rest > 0) {
            var child = new _Rectangle(c, sx, sy, 1, rectangle.height);
            _addChild(result, parent, child);
            sx++;
          }
        }
      }
    }
  }
  
  // compute fitting aspect ratio
  function _fit(rect, weight, total) {
    var s = rect[0]<rect[1]?rect[0]:rect[1];
	var l = rect[0]<rect[1]?rect[1]:rect[0];
	rect[0] = weight*l/total;
	rect[1] = s;
	if (rect[0] == 0) {
	  // sanitize to avoid bogus aspect
	  rect[0] = 1;
	}  
  }
    
  // add a child to the rectangle tree model
  function _addChild(treeModel, parent, child) {
    if (parent.children) {
      parent.children.push(child);
    } else {
      parent.children = [child];
    }
    child._parent = parent;
  }
  
}