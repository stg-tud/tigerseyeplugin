package de.tud.stg.popart.builder.eclipse;

import java.util.Collection;
import java.util.Map;

public interface ITransformerConfigurationListener {
	public String getInformation(String transformer);

	public void setEnabled(String extension, String transformer, boolean enabled);

	public Map<String, Collection<String>> getAvailableTransformers(String extension);
}
