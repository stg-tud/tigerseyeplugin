#!/bin/bash

PARTS=("title.md" "introduction.md" "installation.md" "examples.md" "features.md")
SRCDIR="text"

#Pass additional custom key value pairs. Attention: whitespace is not allowed inside a value
#Uncomment following line to activate additional code
	#KEYVALS=("listoffigures=''" "listoftables=''")

#Passed Parameters
TOFORMAT="html"
STANDALONE="-s"
TOC="--toc"
PARSERAW="-R"
NORMALIZE="--normalize"
LATEXTEMPLATE="tmpl/usermanualPandocTemplate.tmpl"


#change working directory
SCRIPTLOC=`dirname $0`
cd $SCRIPTLOC

#Add format variable for output format
if [ -z $1 ]; then
  echo "no target format defined. Default is $TOFORMAT."
elif [ $1 = "html" ]; then
  TOFORMAT=$1
elif [ $1 = "latex" ]; then
  TOFORMAT=$1
  if [ -n "${LATEXTEMPLATE}" ]; then
  	USETEMPLATE="--template $LATEXTEMPLATE"
  fi
else
  echo "unsupported format: $1"
  exit
fi

#Set output file if argument set otherwise the generated document will be printed on stdout
if [ -n "${2}" ]; then
	TOTARGET="-o $2"
fi

#Add additional key value pairs, which will be filled in a template
for kv in ${KEYVALS[@]}; do
	VARIABLES="$VARIABLES -V $kv"
done

for target in ${PARTS[@]}; do
	INPUTTARGETS="$INPUTTARGETS $SRCDIR/$target"
done

#Combine configuration
CMD="pandoc -f markdown -t $TOFORMAT $VARIABLES $USETEMPLATE $STANDALONE $TOC $PARSERAW $NORMALIZE $INPUTTARGETS $TOTARGET"

echo "performing: '$CMD'"

$CMD