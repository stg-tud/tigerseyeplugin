package de.tud.stg.tigerseye.eclipse.core;

public enum TigerseyeImage {
    
    RunTigerseye("plane16.png"), //
    DebugTigerseye("plane16.png"), //
    FileTypeTigerseye("plane16.png"), //
    FileTypeTigerseye64("plane64.png"), //
    ;
    
    public String imageName;

    private TigerseyeImage(String imageName) {
	this.imageName = imageName;
    }
}

