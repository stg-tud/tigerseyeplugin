# Installation

# Installation for Development

The plug-in itself consists of multiple separate plug-ins and has
further dependencies to other plug-ins and libraries. It uses classes
and extensions from the Groovy Plug-in and Eclipse's JDT plug-ins, makes
some use of different `apache.commons` libraries and uses further
libraries to process code. It's core dependency is the used preprocessor
$\parlex$ . The following table shows the necessary plug-ins and the
required version, if any.

  Plug-in name                        Version         Description
  ----------------------------------- --------------- ----------------------------------------------------------------------
  de.tud.stg.tigerseye.eclipse.core   $\versionnum$   Core functionality.
  de.tud.stg.tigerseye.eclipse.ui     $\versionnum$   User Interface functionality.
  de.tud.stg.tigerseye                $\versionnum$   (Re)Exports commonly used libraries and plug-ins.
  Groovy Eclipse Feature              2.1.1           Groovy Eclipse Plug-ins with version that is known to be compatible.
  org.apache.commons.collections      -               Apache utility classes for
  collections.org.apache.commons.io   -               Apache IO utility
  classes.org.apache.commons.lang     -               Apache general language utility classes.
  org.apache.log4j                    [1.2 - 1.3)     Employed logging framework.
  parlex                              -               Preprocessor, performing the transformations.
  slf4j-log4j12                       -               Employed logging facade with log4j binding.

  : Necessary Plug-ins

Once all the necessary plug-ins have been installed the plug-in can be
started using the predefined `Tigerseye_IDE` launch configuration, which
is located in the core plug-in. It is possible that depending on the
used operating system this launch configuration has to be adjusted.
Alternatively a new Eclipse Plug-in configuration can be started with
all available plug-ins active.

# Installing a language

To install a newly created language it is currently necessary to export
it as a plug-in. As in the [Examples](#examples) section described the
project can be converted into a plug-in project using the Eclipse PDE
tools. It is than necessary to add the `de.tud.stg.tigerseye` and the
`de.tud.stg.tigerseye.eclipse.core` plug-ins as dependencies. The core
plug-in provides the `dslLanguages` extension point. This point has to
be extended by declaring what the language class of the exported
language is. Optionally a user friendly name and a default file
extension can be defined. Additionally the package containing all the
classes that need to be accessible at runtime must be exported.

Since the language definition will typically be a Groovy file the
default behavior when exporting a plug-in has to be adjusted. Rather
then a specific source folder which would be compiled the created
binaries have to be put on the classpath of the exported language and an
according in the Manifest file made. For example if the output folder is
`bin` it has to be put into the `build.properties` file under the
`build.includes` property and the entry `bin` has to be added to the
`MANIFEST.MF` file as `Bundle-Classpath` entry.

Alternatively the binary folder can be specified as source folder, in
which case the `MANIFEST.MF` does not have to be modified, assuming the
correct packages are declared *exported*.
The resulting `build.properties` file could result in something like this:

~~~~~~~~~~~~~~~~~~~~~~~~~~ {.properties}
source.. = src/,\
           bin/
bin.includes = META-INF/,\
               .,\
               plugin.xml
output.. = bin/
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Additionally the current $\tiger$ version assumes that languages can be found
in an accessible folder. So the exported language has to be unpacked into
the `plugins` folder of the target Eclipse installation.
