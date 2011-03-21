# Installation for Development

The plug-in itself consists of multiple separate plug-ins and has
further dependencies to other plug-ins and libraries. It uses
classes and extensions from the Groovy Plug-in and Eclipse's JDT
plug-ins, makes some use of different `apache.commons` libraries and
uses further libraries to process code. It's core dependency is the
used preprocessor $\parlex$ . The following table shows the necessary
plug-ins and the required version, if any.

Plug-in name                            Version                Description
------------------------                -----------------      --------------------------
de.tud.stg.tigerseye.eclipse.core       $\versionnum$          Core functionality.
de.tud.stg.tigerseye.eclipse.ui         $\versionnum$          User Interface functionality.
de.tud.stg.tigerseye                    $\versionnum$          (Re)Exports commonly used libraries and plug-ins.
Groovy Eclipse Feature                  2.1.1                  Groovy Eclipse Plug-ins with version that is known to be compatible.
org.apache.commons.collections          -                      Apache utility classes for
collections.org.apache.commons.io       -                      Apache IO utility
classes.org.apache.commons.lang         -                      Apache general language utility classes.
org.apache.log4j                        [1.2 - 1.3)            Employed logging framework.
parlex                                  -                      Preprocessor, performing the transformations.
slf4j-log4j12                           -                      Employed logging facade with log4j binding.

Table: Necessary Plug-ins

Once all the necessary plug-ins have been installed the plug-in can
be started using the predefined `Tigerseye_IDE` launch configuration, which is
located in the core plug-in. It is possible that depending on the
used operating system this launch configuration has to be adjusted.
Alternatively a new Eclipse Plug-in configuration can be started
with all available plug-ins active.

