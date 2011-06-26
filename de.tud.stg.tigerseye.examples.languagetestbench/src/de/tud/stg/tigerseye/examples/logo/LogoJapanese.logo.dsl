package de.tud.stg.tigerseye.examples.logo
//In order to work the TranslationTransformer transformer has to be activated:
//Preferences->Tigerseye->Transformations: choose dsl for this extension,
//choose Edit DSL specific Transformers and activate the TranslationTransformer 

//The path to the actual translation file has to be provided as absolute path. 
@Translation(file="/home/leo/wss/runtime-EclipseApplication42/de.tud.stg.tigerseye.examples.languagetestbench/src/de/tud/stg/tigerseye/examples/logo/translation.jpn")
logo(name:'Test'){
	オンワード 50
	みぎ 90
	あと 50

	ひだり 90
	オンワード  50
	みぎ 90
}  