package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.CheckForNull;

import de.tud.stg.popart.builder.transformers.ASTTransformation;
import de.tud.stg.popart.builder.transformers.TextualTransformation;
import de.tud.stg.popart.builder.transformers.Transformation;
import de.tud.stg.popart.builder.transformers.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.TransformationHandler;

/**
 * 
 * FIXME This class was once the builder plug-in activator. It now has no
 * definite purpose but holds still some functionality. Rename it accordingly to
 * what it does now or move the different functionalities.
 * 
 */
public class TransformerConfigurationProvider {

    private Collection<TransformationHandler> configuredTransformations = Collections
	    .emptySet();;

    public TransformerConfigurationProvider() {
	init();
    }

    private void init() {
	this.configuredTransformations = TigerseyeCore
		.getTransformationProvider().getConfiguredTransformations();
    }

    private Collection<TransformationHandler> getTransformations() {
	return this.configuredTransformations;
    }


    public Set<TextualTransformation> getConfiguredTextualTransformers(
	    TransformationType... identiables) {
	return getTransformations(
		new TransformationFilter<TextualTransformation>() {
		    @Override
		    public TextualTransformation instanceOrNull(Transformation t) {
			if (t instanceof TextualTransformation)
			    return (TextualTransformation) t;
			return null;
		    }

		}, identiables);
    }

    private <T extends Transformation> Set<T> getTransformations(
	    TransformationFilter<T> filter, TransformationType... identiables) {
	Set<T> ts = new LinkedHashSet<T>();
	for (TransformationType i : identiables) {
	    Collection<TransformationHandler> transformations = getTransformations();
	    for (TransformationHandler h : transformations) {
		if (h.isActiveFor(i)) {
		    Transformation t = h.getTransformation();
		    T instanceOrNull = filter.instanceOrNull(t);
		    if (instanceOrNull != null)
			ts.add(instanceOrNull);
		}
	    }
	}
	return ts;
    }

    private static interface TransformationFilter<T extends Transformation> {

	public @CheckForNull
	T instanceOrNull(Transformation t);

    }

    public Set<ASTTransformation> getConfiguredASTTransformers(
	    TransformationType... identiables) {
	return getTransformations(
		new TransformationFilter<ASTTransformation>() {
		    @Override
		    public ASTTransformation instanceOrNull(Transformation t) {
			if (t instanceof ASTTransformation)
			    return (ASTTransformation) t;
			return null;
		    }
		}, identiables);
    }




}
