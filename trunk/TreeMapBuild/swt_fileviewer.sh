#!/bin/sh
. config.properties
java -cp ../TreeMapSWT/lib/swt.jar;build/tm_swt_examples_${tm.version}.jar;build/tm_core_${tm.version}.jar;build/tm_swt_${tm.version}.jar;build/tm_core_examples_${tm.version}.jar de.engehausen.treemap.examples.swing.FileViewer
