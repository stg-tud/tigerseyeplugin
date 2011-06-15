%
%Leo Roos
%24.03.2011

# TigerseyeConfiguration

This folder contains meta configuration for the Tigerseye IDE Eclipse plug-in.

## Launch Configuration

Two configuration files are predefined:

* `Tigerseye_IDE.launch.linux`
* `Tigerseye_IDE.launch.win`

One is for a Windows and one for a Linux Operating systems.
Copy the appropriate file and rename it to `Tigerseye_IDE.launch`.
This launch configuration can then be used to start the Tigerseye IDE, 
assuming the necessary plug-ins have been fetched from the repository.
A minimal configuration can be imported using the `Tigerseye_IDE.psf`
project set.

## Project Sets

The folder `projectSets` contains multiple `.psf` files. These files can be used
to import a pre-configured set of projects from a repository location. The project sets' names
should be self-explanatory. The following `.psf` files are currently defined:

*Example_DSLs*
:   contains a set of DSL language definitions projects. These come in handy when testing the
    ability of the Tigerseye IDE.

*Tigerseye_IDE*
:   the plug-ins that form the plug-in
    
*Tigerseye_IDE_Dependencies*
:   dependencies for the eclipse plug-in. This includes `de.tud.stg.tigerseye.dslsupport` and `de.tud.stg.tigerseye.parlex` bundles     

*Documentation*
:   All projects that contain documentation for the Tigerseye IDE

*Tigerseye_Tests*
:   Projects to test the implemented Tigerseye functionality.

*Deployment*
:   Projects for deployment of the Tigerseye Plug-in.

## Log4j configuration

The `log4j.configuration` file is the default log4j configuration for the
Tigerseye IDE. It is referenced from the [launch configuration](#launch-configuration).