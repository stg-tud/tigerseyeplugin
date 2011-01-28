package de.tud.stg.popart.builder.eclipse;

import java.util.Collection;
import java.util.Map;

import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.TransformationHandler;

public interface ITransformerConfigurationListener {
	public String getInformation(String transformer);

	public void setEnabled(String extension, String transformer, boolean enabled);

    public Map<FileType, Collection<TransformationHandler>> getAvailableTransformers();
}
