#!/bin/bash

BUILDDIR="build"
TMPTEX="tmp.tex"

SCRIPTLOC=`dirname $0`
cd $SCRIPTLOC

if [ ! -d $BUILDDIR ]; then
	mkdir $BUILDDIR	
fi


./gendoc.sh latex "$BUILDDIR/$TMPTEX"
cd $BUILDDIR
pdflatex $TMPTEX
