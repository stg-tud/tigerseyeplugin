# The Tigerseye Plug-in Project

The repository contains the **Tigerseye-Plugin Bundles**.
Additionally some *example language plug-ins* are provided
and a project that showcases the use of those.

The Tigerseye Plug-in has a dependency to **parlex**, an *Earley-Parser* 
implementation, which is contained in a different repository.
Without the correct parlex project the Plug-in can not be build.

To following projects are necessary to compile the Tigerseye Plug-in:

  Plug-in name                        Version         Description
  ----------------------------------- --------------- ---------------------------------------------------------------------
  de.tud.stg.tigerseye.eclipse.core   $\versionnum$   Core functionality.
  de.tud.stg.tigerseye.eclipse.ui     $\versionnum$   User Interface functionality.
  de.tud.stg.tigerseye.eclipse        $\versionnum$   Contains libraries and plug-ins used by multiple $\tiger$ plug-ins.
  slf4j-log4j12                       -               Used logging facade with log4j binding.

  : Necessary Plug-ins

Additionally the dependency project set has to be checked out. It contains several apache.commons libraries and the parlex project.
In order to access the parlex source one must have the appropriate rights.

The [project set](TigerseyeConfiguration/projectsets/NotGitDependencies.psf) can be found under the TigerseyeConfiguration folder. 

When developing it is also recommended to checkout the test projects

  Plug-in name                            Version         Description
  -----------------------------------     --------------- ---------------------------------------------------------------------
  de.tud.stg.tigerseye.eclipse.core.tests -               Core Tests
  TigerseyeTestRunner                     -               Contains currently only the a configuration to run the core tests but
							  will be used to start all available tests.
  : Test Projects

