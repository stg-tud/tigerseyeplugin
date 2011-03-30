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
uses further libraries to process code. It's core dependency is an
*Earley* parser and the $\tiger$ builder. The builder is currently
included in the core plug-in. The following table shows the necessary
plug-ins and the required versions, if any.

  Plug-in name                        Version         Description
  ----------------------------------- --------------- ---------------------------------------------------------------------
  de.tud.stg.tigerseye.eclipse.core   $\versionnum$   Core functionality.
  de.tud.stg.tigerseye.eclipse.ui     $\versionnum$   User Interface functionality.
  de.tud.stg.tigerseye                $\versionnum$   Contains libraries and plug-ins used by multiple $\tiger$ plug-ins.
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

1.  Install the Groovy Feature into your Eclipse.
2.  Copy all other plug-ins into your Eclipse workspace (usually you
    will check them out from a repository).
3.  Perform a complete build of your workspace. It might be necessary to
    clean all projects before an error free build will be performed.

Once all plug-ins have been set up, $\tiger$ can be started using the
predefined `Tigerseye_IDE` launch configuration. The configuration is
located in the TigerseyeConfiguration project. It is possible that
depending on the used operating system this launch configuration has to
be adjusted. Alternatively a new Eclipse Plug-in configuration can be
started with all available plug-ins active.

## Installing a Language

To install a newly created language it is currently necessary to export
it as a plug-in. As in the [Examples](#examples) section described the
project can be converted into a plug-in project using the Eclipse PDE
tools. It is than necessary to add the `de.tud.stg.tigerseye` and the
`de.tud.stg.tigerseye.eclipse.core` plug-ins as dependencies. The core
plug-in provides the `dslLanguages` extension point. This point has to
be extended by declaring what the language class of the exported
language is. Optionally a user friendly name and a default file
extension can be defined.

Since the language definition will typically be a Groovy file the
default behavior when exporting a plug-in has to be adjusted. Rather
then a specific source folder, which would be compiled during the export
using the PDE Export Wizard, the binaries created during the development
have to be included, so that they are available on the classpath of the
exported language. This can be done in different ways. Two schemes are
as follows.

-   If the output folder for the `class` files is `bin` it can be put
    into the `build.properties` file under the `build.includes` property
    and the entry `bin` has to be added to the `MANIFEST.MF` file as
    `Bundle-Classpath` entry (the default classpath is the root of the
    create jar file).
-   Alternatively the binary folder can be specified as source folder,
    in which case the `MANIFEST.MF` does not have to be modified. The
    resulting `build.properties` file will result in something like
    this:

        source.. = src/,\
                   bin/
        bin.includes = META-INF/,\
                       .,\
                       plugin.xml
        output.. = bin/

Additionally the current $\tiger$ implementation (Version 0.0.1) assumes
that languages can be found in an accessible folder. So the exported
language has to be unpacked into the `plugins` folder of the target
Eclipse installation.
