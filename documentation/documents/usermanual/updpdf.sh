#!/bin/bash

./gendoc.sh latex build/tmp.textmp
cd build
pdflatex tmp.textmp