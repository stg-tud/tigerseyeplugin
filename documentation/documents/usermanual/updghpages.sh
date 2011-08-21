#!/bin/bash

GENDIR="../../../build"
GENFILE="documentation.html"

SCRIPTLOC=`dirname $0`
cd $SCRIPTLOC

if [ ! -d $GENDIR ]; then
	mkdir $GENDIR
	echo "non existent final folder: $GENDIR created"	
fi

echo "writing $GENFILE to $GENDIR"
./gendoc.sh html "$GENDIR/$GENFILE"
rsync -av pics $GENDIR/../
