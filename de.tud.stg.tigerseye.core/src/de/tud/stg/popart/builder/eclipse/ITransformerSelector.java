package de.tud.stg.popart.builder.eclipse;

import java.util.Collection;

import de.tud.stg.popart.builder.transformers.Transformation;

public interface ITransformerSelector {
	public void configureTransformers(Collection<Class<? extends Transformation>> list,
			ITransformerConfigurationListener listener);
}
