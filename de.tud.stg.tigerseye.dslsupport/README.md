DSL Support Plug-in Read-Me
===========================

This is a client-site dependent on plug-in.
It provides the tigerseye support classes necessary to implement Tigerseye DSLs.
 
Considerations For Configurations
---------------------------------

If modifying the plug-in configuration keep the following things in mind.

### Plug-in Dependencies

This plug-in is supposed to be dependent on by clients.
Therefore the dependency impact with this plug-in should be held minimal.
Additionally no plug-ins should be re-exported to leave the client
in full control over his further plug-in dependencies.

### Library generation.

During development the library 

    lib/tigerseye-support.jar
    
is not in sync with the actual source code-base.
So if something changes in the `de.tud.stg.tigerseye.dslsupport` plug-in
and it is supposed to be accessed
from a test Eclipse instance the tigerseye-support.jar
has to be updated manually.

#### Update the `tigerseye-support` Jar File

To do so simply run the

	`update-tigerseye-support.jardesc`
	
file.
This will replace the current support library with the
classes of the currently compiled files
from the support source directory. 

Alternatively one can use the PDE Export Wizard.
1.  Let him deploy the plug-in somewhere,
2.  If you have deployed the complete plug-in as JAR expand it,
3.  go to the `lib` directory and copy `tigerseye-support.jar` to the actual plug-in `lib` directory.
    (Probably overwrite an older version.) 
