# TigerseyeConfiguration

This folder contains meta configuration for the Tigerseye IDE Eclipse plug-in.

## Build

Groovy source files in eclipse plug-in projects need some extra configuration
to get compiled successfully. The folder `build` contains a set of files that 
comprise the configuration for a successfully build Groovy plug-in project.


## Launch Configurations

This folder contains some launch configurations. Since, depending on your OS and Eclipse configuration,
the configurations differ the contained configurations are more like a example than actually usable as
they are. 

## Project Sets

The folder `projectSets` contains multiple `.psf` files. These files can be used
to import a pre-configured set of projects from a repository location. The project sets' names
should be self-explanatory. The following `.psf` files are currently defined:

*OutsideGitDependencies*
:   After the transition to Github and because of the still flaky support of
    egit for project sets all not git dependencies are the only left directly downloadable
    through a project set.

## Log4j configuration

The `log4j.configuration` file is the default log4j configuration for the
Tigerseye IDE and is also configured in the launch configurations.