# Features

The current version of $\tiger$ provides the fundamental functionalities
in order to be able to use it as a language workbench. New languages can
easily be created and deployed. After a restart they can be used in
projects that have the $\tiger$ nature. A $\tiger$ nature can easily be
added and removed via the context menu. The installed DSLs can be
configured through the preference pages, as well as the employed
transformations. The editor is an extended version of the Groovy editor
and provides keyword coloring for keywords of installed and activated
DSLs.

## Preferences

The preference pages are an important part of $\tiger$, since they
provide the configuration of registered DSLs. In the main prefrence
page\
![Tigerseye Main Preference
Page](../pics/preferences_main.png "Tigerseye Main Preference Page")

the output source folder can be adjusted. Registered languages can be
configured on the languages preference page.\
![Tigerseye Language Configuration
Page](../pics/preferences_languages.png "Tigerseye Language Configuration Page")

DSLs have to be set into activated state in order to use them. The
extensions, that indicate which concrete DSL class is responsible for
which files of the according extension can be adjusted in the Extensions
column. When a DSL is selected its keywords are shown below in the
*Declared Keywords of Selected DSL* table.\
 In the transformations preference page used transformations can be
configured. A transformation usually only supports a subset of available
resources. Its active state for each resource can be modified.\
![Tigerseye Transformations Preference
Page](../pics/preferences_transformations.png "Tigerseye Transformations Preference Page")

For each resource and DSL different transformations might be available
and active. These can be configured using the *Select Transformations*
dialog.\
![Tigerseye Transformations
Configuration](../pics/preferences_transformations_selected.png "Tigerseye Transformations Configuration")

The dialog also shows additional informations about the currently
selected transformation in a tray window that can be opened clicking on
the additinal information button. ![Additional Information
Button](../pics/additional_information_button.png "Additional Information Button")

The editor provides keyword coloring for active DSLs. The colors can be
configured using the *Tigerseye Editor* preference page, where every DSL
can be configured separately and the general keyword coloring can be
activated or deactivated.\
![Tigerseye Editor Preference
Page](../pics/preferences_editor.png "Tigerseye Editor Preference Page")

## Add and Remove Nature

$\tiger$ has additional requirements which will be imported when adding
the $\tiger$ nature to a project. The project must have at least the
Java nature otherwise the transformation to a $\tiger$ project is not
possible. Following figure shows the available context menu to add the
nature to a project.\
![Add the Nature to a Java
Project](../pics/convert_to_tigerseye.png "Add the Nature to a Java Project")

This will do two things:

1.  A seperate source folder will be created into which the translated
    DSL files will be put (here: `src-tigerseye`).

2.  A new class path container will be added which contains the runtime
    libraries (popartAnnotations.jar, popart.jar, edslNature.jar) and
    the libraries of registered DSLs as well as their dependencies. For
    example `de.tud.stg.tigerseye.examples.LogoDSL` and
    `de.tud.stg.tigerseye.examples.DSLDefinitions`. Additionally the
    GroovyNature will be added if not already configured.\

Following figure shows a possible resulting configuration.\
![Dependencies](../pics/tigerseye_dependencies.png "Dependencies")\
 Removing a $\tiger$ nature can also be accomplished via the context
menu of a $\tiger$ project.\
![Remove the Nature from a
Project](../pics/remove_tigerseye_nature.png "Remove the Nature from a Project")

## Language Definition Wizard

A new language can be created using the *Tigerseye Language Wizard*. The
Wizard can be accessed via `File > New > Other`\
![Choosing the New Tigerseye Language
Wizard](../pics/new_tigesreye_language.png "Choosing the New Tigerseye Language Wizard")\
 The next figure shows the first page of the Wizard.\
![Tigerseye Language Definition Wizard Page
1](../pics/tigerseye_language_definition_page1.png "Tigerseye Language Definition Wizard Page 1")

There the name of the main language class can be defined. As shown the
default package is not a valid package for a language definition, since
this will cause problems when trying to use the language within a Java
class. The next figure shows the actual language definition page.\
![Tigerseye Language Definition Wizard Page
2](../pics/tigerseye_language_definition_page2.png "Tigerseye Language Definition Wizard Page 2")

On the language definition page the different literals, operations and
structured elements can be added. In Section [Examples](#examples) the
usage of the wizard is showcased.

## New Tigerseye Class Wizard

The *New Tigerseye Class* Wizard enables easy creation of new DSL
classes.\
![New Tigerseye Class
Wizard](../pics/new_tigerseye_class_wizard.png "New Tigerseye Class Wizard")

It is basically a version of the *Groovy Class* Wizard. Not all modifiable
areas have influence on the generated code. The useful fields
for generation are `Source folder`, `Package`, `Name` and
the DSL extension.

## Launch Tigerseye DSL

A DSL can be launched using a *launch shortcut* or via the *Run
Configurations* dialog. The next figure shows a launch using a launch
shortcut.\
![Launch via Context
Menu](../pics/launch_shortcut.png "Launch via Context Menu")

The following figure shows the launch via the Run Configurations
Dialog.\
![Launch via the Run Configurations
Dialog](../pics/launch_run_configurations_dialog.png "Launch via the Run Configurations Dialog")

In the Run Configurations Dialog a new launch can be configured or a
previous launch adjusted. On the `Tigerseye` tab the project from which
a DSL will be launched as well as the DSL file to launch can be chosen.
When using the launch shortcut the Groovy default launch configuration
is assumed, which will set additional classpath properties. Later these
can be modified using this dialog.
