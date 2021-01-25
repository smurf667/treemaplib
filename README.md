# treemaplib
TreeMap widget for Swing, SWT and JavaScript

This project provides a TreeMap widget in separate small modules for Swing, SWT (standalone and Eclipse feature) and [JavaScript](https://raw.githubusercontent.com/smurf667/treemaplib/master/TreeMapJS/sample.html) (at present more of a toy).

# Overview
Tree maps are used to visualize hierarchical structures. They become rather useful when the hierarchical structures are large. The data is represented with nested rectangles, whose proportions match the "size" or "weight" of a particular rectangle.
The idea of tree maps seems to go back to Prof. Ben Shneiderman (University of Maryland). As a strategy for deciding the layout the squarified layout invented by Prof. van Wijk et. al. (Technical University of Eindhoven) has proven to be effective:
 * [Information on tree maps](http://www.cs.umd.edu/hcil/treemap-history/index.shtml)
 * [Paper on squarified tree maps](http://www.win.tue.nl/~vanwijk/stm.pdf)

This library primarily exists as the UI foundation for my other project, [treemapmat](https://github.com/smurf667/treemapmat/).

## Downloads
The `.jar` files and the Eclipse site can be found [here](https://github.com/smurf667/treemaplib/releases/).

# The library
The library consists of a "core" which is then used by the different versions, i.e. Swing, SWT and SVG support at the moment. The JavaScript library is special in that it is not built on the core and currently a bit of a toy, although usable, of course.

For using the tree map standalone, you need to have `treemap.core-*.jar` on your classpath as well as the appropriate UI portion of the library, i.e. `treemap.swing-*.jar` or `treemap.swt-*.jar`. For use in Eclipse you can simply install the feature, require it in your plugin/feature and you will have access to the core and SWT parts of the library. The JavaScript version can simply be included into an HTML document.

I will be happy about contributions such as bug fixes, extensions (for example different rendering styles, layouts etc.) or feature requests.

# The examples
Several examples are provided, going from simple to more complex. For Swing and SWT equivalent examples are given. Both the Java version as well as the JavaScript version support navigation in the tree by left click and right click for zoom in and zoom out, respectively.

Here are some screenshots of the simple samples:

![Swing example screenshot](https://raw.githubusercontent.com/smurf667/treemaplib/master/javadoc/screenshots/swing_samples.png)

The tree map widget requires some information to do its job:
 * It requires input in the form of a weighted tree model. This is a tree model in which each node has an associated weight. This associated weight must be computed from the individual weights of all its children and its own weight. This probably sounds more complicated than it is – please see the examples for details.
 * A layout – currently only the squarified layout approach is supported; it can be parameterized, however. Please refer to the JavaDoc for details.
 * Look-and-feel providers in the form of a rectangle renderer (how to render a rectangle), a color provider (what base colors to use for a rectangle) and a label provider (optional, for textual additional information about a rectangle). These are all interfaces that you can implemented yourself; some defaults are provided as well.

The examples make use of this information to show how to use the tree map widget.

There is one more complex example. It is resembling a tool supplied by the Technical University of Eindhoven called [Sequoia View](http://www.win.tue.nl/sequoiaview/):

It shows the disk space usage of a given folder and sub folders using the tree map widget. Here is a screenshot:

![Screenshot of file viewer](https://raw.githubusercontent.com/smurf667/treemaplib/master/javadoc/screenshots/swt_fileviewer_small.jpg)

You can also view this in [big](https://raw.githubusercontent.com/smurf667/treemaplib/master/javadoc/screenshots/swt_fileviewer.png).

To build, issue `mvn install` in `treemap.build`. After you've built you can run the file viewer example:

* Swing: `mvn initialize -f run-fileviewer-swing.xml`
* SWT: `mvn initialize -f run-fileviewer-swt.xml`

Congratulations, you have read this far! There is not much more to add; I recommend you try it out and/or read the JavaDoc. 
