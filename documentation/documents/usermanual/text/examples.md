# Examples

This section showcases typical use cases for the $\tiger$ IDE.

## Creating a new Language Definition

The usage of the New Tigerseye Language Definition wizard will be
explained by creating a Trivalent-DSL. A Trivalent logic DSL adds an
unknown value `U` to the boolean values true `T` and false `F`. So that
`T&U` is `U` and `T|U` is `T` and so on.

First create a new Java Project. This Project will contain the Trivalent
language created by the *New Tigerseye Language* wizard. In this example
a project called `de.tud.stg.tigerseye.examples.trivalent` will be
created. In the context menu of the Project choose `New > Other`. Choose
*Tigerseye Language Definition* in the Tigerseye folder.\
![Choosing the New Tigerseye Language
Wizard](../pics/new_tigesreye_language.png "Choosing the New Tigerseye Language Wizard")

Each language definition consists of a Groovy class that defines all
operations, literals and structured elements of the DSL. Type
TrivalentDSL as the class name, de.tud.stg.tigerseye.examples.trivalent
as the package and select `Next`.\
![New TrivalentDSL Language
Defintion](../pics/example_newlang_newlangclass.png "New TrivalentDSL Language Defintion")

Now add three new literals: `T` (true), `F` (false) and `U` (unknown).
Set the type for every literal to `Trivalent`.\
![TrivalentDSL Language Definition with Literals
added](../pics/example_newlang_literalsadded.png)

When finishing the wizard these three classes and their super type
Trivalent will be created. Notice that `T`, `F` and `U` extend
Trivalent. The only operation will be an enhanced `println` which takes
a String and a Trivalent expression and prints both. For example

    puts("T|U: ",T|U)

produces

    T|U: T

The return type will be `void` so we leave the `Return type:` field
empty. For each operation you can choose if setting a breakpoint on a
line containing this keyword should be possible. You can define
parameters and their types in the `Parameter:` section. Parameters are
added by clicking on the `+` button.\
![TrivalentDSL Language Definition Operation puts
added](../pics/example_newlang_operationadded.png "TrivalentDSL Language Definition Operation puts added")

Now add a repeat-statement. The following will simply print `T: T` ten
times to standard out.

    repeat(10) {
       puts("T: ",T);
    }

The return type will be void and there is one parameter named n. Select
explicit parameters.\
![TrivalentDSL Language Definition Structured Element repeat
added](../pics/example_newlang_structuredelementadded.png)

Again, you can choose if it should be possible to set a breakpoint on a
line containing this keyword. After you select `Finish` a dialog will
pop up asking you if you want to add the $\tiger$ runtime libraries.\
![Question Dialog to add Runtime
Libraries](../pics/example_newlang_addruntime.png)

Usually you should say yes, since language definitions have dependencies
to the runtime libraries. The next figure shows the generated classes and project structure.\
![TrivalentDSL Generated Classes and
Code](../pics/example_newlang_generatedcode.png)

For the new type `Trivalent` as well as for the literals `T`,`U` and `F`
a separate Groovy class has been created. The language configuration is
defined in the `TrivalentDSL` class.

## Deployment of a Language

This section shows how to deploy a $\tiger$ language to a plug-in
project. This plug-in project will declare its dependencies to two
plug-ins:

-   de.tud.stg.tigerseye
-   de.tud.stg.tigerseye.eclipse.core

The `de.tud.stg.tigerseye` plug-in provides the dependent on runtime
libraries and the `de.tud.stg.tigerseye.eclipse.core` plug-in the
extension which declares that this plug-in project actually provides a
new language. The following steps have to be performed:

1.  Convert the language definition project to a plug-in project.\
     ![Add Plug-in Project
    Nature](../pics/example_deploy_converttoplugin.png)

2.  In the MANIFEST.MF file add the dependencies to the two Tigerseye
    plug-ins.\
     ![Add Plug-in Dependencies
    to](../pics/example_deploy_addplugindependencies.png)

3.  Now open the plugin.xml and go the Extensions tab. Add the
    `de.tud.stg.tigerseye.dslDefinitions` extension point. On the
    extension point select `New > language`. There you can define the
    language class to be used. In this example 
    `de.tud.stg.tigerseye.examples.trivalent.Trivalent` is used. Additionally you
    should define a user friendly name of your new language, e.g.
    *Trivalent DSL*. Optionally you can define the default extension
    identifying your language, such as `tri`. The following figure shows
    the configuration.\
     ![dslDefinitions Extension Point
    Configuration](../pics/example_deploy_extensionpoint.png) The
    `extension` field can later also be configured via the preference pages.

4.  Currently only the deployment for development is supported. A language
    can either be copied or linked inside the eclipse instance in which
    the plug-in is developed. The next time the Eclipse instance is
    started the language will be visible in the preference pages and can
    be used.

