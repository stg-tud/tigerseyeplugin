# Installation

This section describes the installation process for multiple use cases.
One subsection describes the installation for the $\tiger$ plug-in for
development. A further subsection describes the installation process of
a language designed with $\tiger$.

## Installation for Development

The plug-in itself consists of multiple separate plug-ins and has
further dependencies to third party plug-ins and libraries. It uses
classes and extensions from the Groovy Plug-in and Eclipse's JDT
plug-ins, makes some use of different `apache.commons` libraries and
uses further libraries to process code. The transformation is performed via 
the *Earley Parser* implementation `parlex` and the $\tiger$ builder. The builder is currently
included in the core plug-in. The following table shows the necessary
plug-ins and the required versions, if any.

  Plug-in name                        Version         Description
  ----------------------------------- --------------- ---------------------------------------------------------------------
  de.tud.stg.tigerseye.eclipse.core   $\versionnum$   Core functionality.
  de.tud.stg.tigerseye.eclipse.ui     $\versionnum$   User Interface functionality.
  de.tud.stg.tigerseye.eclipse        $\versionnum$   Contains libraries and plug-ins used by multiple $\tiger$ plug-ins.
  Groovy Eclipse Feature              2.1.1           Groovy Eclipse Plug-ins.
  org.apache.commons.collections      -               Apache utility classes for collection handling.
  org.apache.commons.io               -               Apache IO utility classes.
  org.apache.commons.lang             -               Apache general language utility classes.
  org.apache.log4j                    [1.2 - 1.3)     Employed logging framework.
  parlex                              -               Earley parser, performs transformations.
  slf4j-log4j12                       -               Employed logging facade with log4j binding.

  : Necessary Plug-ins

The next table lists
further Eclipse projects relevant for the $\tiger$ development.

  Project name             Description
  ------------------------ --------------------------------------
  TigerseyeConfiguration   Contains configuration related data.
  documentation            Contains documentation resources.

  : Further $\tiger$ related Eclipse projects

To set up an Eclipse workspace in order to run the $\tiger$ IDE the
following steps must be performed.

1.  Install the Groovy Plug-in for your Eclipse.
2.  Copy all other plug-ins into your Eclipse workspace (usually you
    will check them out from a repository).
3.  Perform a complete build of your workspace. It might be necessary to
    clean all projects before an error free build will be performed.

Once all plug-ins have been set up, $\tiger$ can be started using one of the
predefined `Tigerseye_IDE` launch configurations.
They are contained in the TigerseyeConfiguration plug-in and end with the 
operating system name they are defined for. In order to use them they first have 
to be renamed to a file ending with `.launch`.
Alternatively a new Eclipse Plug-in configuration can be
started with all available plug-ins active.

## Installing a Language

To install a newly created language it is currently necessary to export
it as a plug-in. As in the [Examples](#examples) section described the
project can be converted into a plug-in project using the Eclipse PDE
tools. It is than necessary to add the `de.tud.stg.tigerseye.eclipse` and the
`de.tud.stg.tigerseye.eclipse.core` plug-ins as dependencies. The core
plug-in provides the `dslLanguages` extension point. This point has to
be extended by declaring what the language class of the exported
language is. Optionally a user friendly name and a default file
extension can be defined.

Since the language definition will typically be a Groovy file, of which the PDE Builder
is not aware of, the
default behavior when exporting a plug-in has to be adjusted.
Two different approaches can be taken.

### Using class Files Compiled in the Workspace 

Instead of
a specific source folder, which would be compiled during an export
using the PDE Export Wizard, the binaries created during the development
have to be included, so that they are available on the classpath of the
exported language. This can be done in different ways:

-   Assuming that the output folder for the `class` files is `bin`,  the `bin`
    folder can be added
    to the `build.properties` file as value for the `build.includes` property
    and the entry `bin` has to be added to the `MANIFEST.MF` file as
    `Bundle-Classpath` entry (the default classpath is the root of the
    created jar file).
-   Alternatively the binary folder can be specified as source folder,
    in which case the `MANIFEST.MF` does not have to be modified. The
    resulting `build.properties` file will look something like
    this:

        source.. = src/,\
                   bin/
        bin.includes = META-INF/,\
                       .,\
                       plugin.xml
        output.. = bin/

### Using Groovy-specific Build Properties

When the Groovy plug-in is installed, additional properties for the
`build.properties` file can be used to tell the PDE to include Groovy
files during an export. Simply add the following three lines to your build.properties file:

    sourceFileExtensions=*.java, *.groovy
    compilerAdapter=org.codehaus.groovy.eclipse.ant.GroovyCompilerAdapter
    compilerAdapter.useLog=true

The resulting build.properties file will look similar to this:

    source.. = src/
    bin.includes = META-INF/,\
                   .,\
                   plugin.xml,\
    output.. = bin/
    sourceFileExtensions=*.java, *.groovy
    compilerAdapter=org.codehaus.groovy.eclipse.ant.GroovyCompilerAdapter
    compilerAdapter.useLog=true

The additional entries will cause a compilation of Java *and* Groovy files.
For general information about valid values for the `build.properties` file check out
the Eclipse Help following the path `Plug-in Development Environment Guide > Reference > Build Configuration > Feature and Plug-in Build configuration`.
For more information about the Groovy specific build properties have a look
at [Andrew Eisenberg's Blog][andrewsblog].
 

[andrewsblog]: http://contraptionsforprogramming.blogspot.com/2010/08/groovy-pde-redux.html

### Additional Remarks

If the exported language uses libraries it has to be in an exported state
when installed in the plug-ins folder. Doing that 
$\tiger$ can determin the necessary libraries and load them 
on the classpath.

