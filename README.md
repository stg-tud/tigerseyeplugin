# The Tigerseye Plug-in Project

This repository contains the **Tigerseye-Plugin Bundles**.
Additionally examples showcasing the plug-in capabilities are available.
A separate repository for the [*Example Language Plug-ins*][examples] and for a
[*Language Testbench Project*][langtestbench]. Where the latter makes use of the
example language plug-ins. 

The Tigerseye Plug-in has a dependency to **parlex**, a *Earley-Parser* 
implementation, which is hosted on a private Server of the
[Software Technology Group of the Technische Universität Darmstadt](http://www.stg.tu-darmstadt.de).
Without the correct parlex project the Plug-in can not be build.

## Required

The following projects are necessary to run the Tigerseye Plug-in (the content description is not necessarily exhaustive):

  * de.tud.stg.tigerseye.eclipse.core -
  
    Core functionality. (builder, preference configuration, grammar construction)
  * de.tud.stg.tigerseye.eclipse.ui -
   
    User Interface functionality. (Tigereseye editor, preference pages)
  * de.tud.stg.tigerseye.eclipse -
   
    Contains libraries and reexports plug-ins used by multiple Tigerseye plug-ins.
    (libraries: aterm, jjtraveler, jsr305, shared-objects;
    plugins: groovy-plugin, dslsupport, apache.commons.{lang,collections,io}, parlex)
  * de.tud.stg.tigerseye.dslsupport -
  
    Contains the DSL framework classes. Projects that use or define a Tigerseye DSL need these classes.  
   
## Optional
  
Optionally other plug-ins provide further functionality
  
  * slf4j-log4j12 -
  Tigerseye makes use of the SLF4J logging facade.
  This plug-in provides the static binding to log4j
  as the used logging framework. Expects a log4j plug-in.
  * de.tud.stg.eclipse.log4jpdeappender - 
  This plug-in is a log4j appender that forwards the logs to the eclipse error-console.
  Provides convenient logging output directly in Eclipse.  
  Must be activated and configured from a log4j configuration file.
                                                        
## External Dependencies

The external dependencies can be downloaded in one step using a project set located at

    TigerseyeConfiguration/projectsets/OutsideGitDependencies.psf
    
The project set contains

* the parlex project as plug-in (private repository)
* the apache commons libraries as plug-ins (from [orbit][orbit])
* a log4j plug-in (from orbit)

## Testing

When developing it is also recommended to checkout the test projects

  * de.tud.stg.tigerseye.eclipse.core.tests - 
  Tests for the main Tigerseye plug-ins. Partially also contains integration tests that perform transformations using the parlex project.
  * TigerseyeTestRunner - 
  Contains configurations to run different sets of tests.

The tests are somewhat distributed. To easily run all available tests the [*AutomaticTestSuites*][autotests] plug-in
from the [*projectusus* project][projectusus] can be used.
There are two configuration files that execute all tests using the projectusus plug-in.
Those are located in the in the the `TigerseyeTestRunner` project.



[examples]: https://github.com/stg-tud/tigerseyeplugin-examples
[langtestbench]: https://github.com/stg-tud/tigerseyeplugin-examples-languagetestbench
[autotests]: http://code.google.com/p/projectusus/wiki/AutomaticTestSuites
[projectusus]: http://code.google.com/p/projectusus/
[orbit]: http://www.eclipse.org/orbit/