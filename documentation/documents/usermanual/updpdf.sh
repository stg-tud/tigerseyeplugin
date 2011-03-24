#!/bin/bash

BUILDDIR="build"

SCRIPTLOC=`dirname $0`
cd $SCRIPTLOC

if [ ! -d $BUILDDIR ]; then
	mkdir $BUILDDIR	
fi

./gendoc.sh latex $BUILDDIR/tmp.textmp
cd $BUILDDIR
pdflatex tmp.textmp
