#!/bin/sh
. config.properties
java -cp build/tm_swing_examples_${tm.version}.jar;build/tm_core_${tm.version}.jar;build/tm_swing_${tm.version}.jar;build/tm_core_examples_${tm.version}.jar de.engehausen.treemap.examples.swing.FileViewer
