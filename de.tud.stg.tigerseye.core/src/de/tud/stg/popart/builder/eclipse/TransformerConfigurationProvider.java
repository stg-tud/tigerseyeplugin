package de.tud.stg.popart.builder.eclipse;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.transformers.ASTTransformation;
import de.tud.stg.popart.builder.transformers.FileType;
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

    private static final Logger logger = LoggerFactory
	    .getLogger(TransformerConfigurationProvider.class);

    private List<TransformationHandler> configuredTransformations = Collections
	    .emptyList();;

    public TransformerConfigurationProvider() {
	setConfiguredTransformations();
    }

    private void setConfiguredTransformations() {
	this.configuredTransformations = TigerseyeCore
		.getTransformationProvider().getConfiguredTransformations();
    }

    private List<TransformationHandler> getTransformations() {
	return this.configuredTransformations;
    }

    @Deprecated
    public Map<FileType, Collection<TransformationHandler>> getAvailableTransformers() {

	Map<FileType, Collection<TransformationHandler>> map = new HashMap<FileType, Collection<TransformationHandler>>();

	for (FileType filetype : FileType.values()) {
	    List<TransformationHandler> list = new LinkedList<TransformationHandler>();

	    for (TransformationHandler t : getTransformations()) {
		Set<FileType> supportedFileExtensions = t.getTransformation()
			.getSupportedFileTypes();
		if (supportedFileExtensions.contains(filetype)) {
		    list.add(t);
		}
	    }

	    map.put(filetype, list);
	}
	return map;
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
	    List<TransformationHandler> transformations = getTransformations();
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
