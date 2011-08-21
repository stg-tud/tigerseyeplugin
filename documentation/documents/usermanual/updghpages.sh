#!/bin/bash

GENDIR="../../../"
GENFILE="documentation.html"

SCRIPTLOC=`dirname $0`
cd $SCRIPTLOC

if [ ! -d $GENDIR ]; then
	echo "non existent final folder: $GENDIR"
	exit	
fi

echo "writing $GENFILE to $GENDIR"
./gendoc.sh html "$GENDIR/$GENFILE"
