#Build

The `site.xml` only contains the categories for the features to be build (in order to stay recent).
Simply add via the `Add Feature` button the following features:

* `de.tud.stg.tigerseye.feature.tigerseyeide`
* `de.tud.stg.tigerseye.feature.exampleDSLs`

then move the
 
* tigerseyeide feature to the `de.tud.stg.tigerseye.category.mandatory` category.
* exampleDSLs feature to the `de.tud.stg.tigerseye.category.exampledsls` category

finally build the release by clicking on the `Build All` button.

Before building a new update site old generated files should be deleted.

#Release 

The `updateRelease.xml` ant build file copies the build release into the release folder to

* the `latest` folder and
* to a, for each release unique, release folder.

By convention the release folder/repository is on the same level as this update-site folder.
For a different configuration adjust the predefined properties in the  `updateRelease.xml`. 