%
%Leo Roos
%19.03.2001

# Generating the *TigersEye* documentation

## Generate documentation
Two scripts are available.

`gendoc.sh`
:   executes the markdown processor `pandoc` and generate html of latex output.
    Optionally the output will be written to an output file. Usage is as follows:
    
        ./gendoc html|latex [outputfile]
        
`updpdf.sh`
:   Is a shortcut script that executes `gendoc` with the latex parameter and a specified output file.
    It then executes pdflatex on the generated tex file and produces a PDF.
    It will put all generated files into the subdirectory `build`. Usage:
    
        ./updpdf
    
The convention is to output all files to the directory `build`.

## Directory structure

The text source files are located in the `text` directory, pictures in the `pics` directory.
The `tmpl` directory contains templates, such as an `tudreport` latex template to generate 
a TU Design styled PDF with `pandoc`.

## Used Markdown processor

Instead of the default markdown script I use `pandoc` with its markdown extensions.
It is more sophisticated and unlike the original markdown philosophy its
only target is not HTML but many other output formats such as `Latex`.

## Installation
*   `pandoc` has to be installed and accessible via command line.
    I use version `1.8.1.1`, it won't work with `1.5` (from the
    Ubuntu packages) since I use the `\newcommand` tex macro to
    define macros which is supported since `1.6`.
*   to generate html `pandoc` is sufficient
*   to generate a pdf `pdflatex` has to be accessible via command line.
*   Since the TU design is used, the according latex packages must be
    installed

## `pandoc` Commands 
1.  To create a html file

        pandoc -f markdown -t html <file>
        
2.  To create a latex file

        pandoc -f markdown -t latex <file>

## Images
*   Since the documents are supposed to be used with `html` and `latex`
    some care has to be taken when defining images. If they are defined
    as a separate paragraph than `pandoc` will generate a `figure` environment
    in latex which might lead to the situation that the picture is put somewhere
    completely out of context.\
    Instead the picture can be put directly behind the text it belongs to with 
    a backslash character at the end of the text that comes before the picture.
    This will lead to the simple use of the `\includegraphics` macro
    while the picture will still start in the next line.
