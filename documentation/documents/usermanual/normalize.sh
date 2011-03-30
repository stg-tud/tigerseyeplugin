#!/bin/bash
# This script will let pandoc transform all source from markdown to markdown which
# formats the text, but might destroy irregular structures.
# For exmaple
# *	the latex style \newcommand macro will be deleted
# *	if standalone option is not passed the title/author/date defintions will be deleted

#don't include "title.md" into the normalize process,
#since that will destroy the newcommand definitions and possibly the standalone data!

PARTS=("introduction.md" "installation.md" "examples.md" "features.md")
SRCDIR="text"

SCRIPTLOC=`dirname $0`
cd $SCRIPTLOC

for target in ${PARTS[@]}; do
	INPUTTARGET="$SRCDIR/$target"
	cp $INPUTTARGET "$INPUTTARGET~"
	TMP="$INPUTTARGET.tmp"
	pandoc -f markdown -t markdown --normalize $INPUTTARGET -o $TMP
	mv $TMP $INPUTTARGET
done