package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.CheckForNull;

import de.tud.stg.tigerseye.eclipse.core.api.ITransformationHandler;
import de.tud.stg.tigerseye.eclipse.core.api.ITransformationProvider;
import de.tud.stg.tigerseye.eclipse.core.api.Transformation;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;

/**
 * 
 * FIXME This class was once the builder plug-in activator. It now has no
 * definite purpose but holds still some functionality. Rename it accordingly to
 * what it does now or move the different functionalities.
 * 
 */
public class TransformerConfigurationProvider {

    private Collection<ITransformationHandler> configuredTransformations = Collections
	    .emptySet();
    private final ITransformationProvider provider;;

    public TransformerConfigurationProvider(ITransformationProvider p) {
	this.provider = p;
	init();
    }

    private void init() {
	this.configuredTransformations = provider
		.getConfiguredTransformations();
    }

    private Collection<ITransformationHandler> getTransformations() {
	return this.configuredTransformations;
    }


    public Set<TextualTransformation> getConfiguredTextualTransformers(TransformationType... identfiables) {
	return getTransformations(new TransformationFilter<TextualTransformation>() {
	    @Override
	    public TextualTransformation instanceOrNull(Transformation t) {
		if (t instanceof TextualTransformation)
		    return (TextualTransformation) t;
		return null;
	    }

	}, identfiables);
    }

    private <T extends Transformation> Set<T> getTransformations(
TransformationFilter<T> filter,
	    TransformationType... identfiables) {
	Set<T> ts = new LinkedHashSet<T>();
	for (TransformationType i : identfiables) {
	    Collection<ITransformationHandler> transformations = getTransformations();
	    for (ITransformationHandler h : transformations) {
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
	    TransformationType... identifiables) {
	return getTransformations(
		new TransformationFilter<ASTTransformation>() {
		    @Override
		    public ASTTransformation instanceOrNull(Transformation t) {
			if (t instanceof ASTTransformation)
			    return (ASTTransformation) t;
			return null;
		    }
		}, identifiables);
    }




}
