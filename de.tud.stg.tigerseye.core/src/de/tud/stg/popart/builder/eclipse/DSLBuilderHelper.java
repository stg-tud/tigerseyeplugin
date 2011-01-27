package de.tud.stg.popart.builder.eclipse;

import java.util.*;

import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import de.tud.stg.popart.builder.eclipse.dialoge.PreferencesStoreUtils;
import de.tud.stg.popart.builder.transformers.ASTTransformation;
import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.popart.builder.transformers.TextualTransformation;
import de.tud.stg.popart.builder.transformers.Transformation;
import de.tud.stg.tigerseye.core.TigerseyeCore;
import de.tud.stg.tigerseye.core.TransformationHandler;

/**
 * 
 * FIXME This class was once the builder plug-in activator. It now has no
 * definite purpose but holds still some functionality. Rename it accordingly to
 * what it does now or move the different functionalities.
 * 
 */
public class DSLBuilderHelper implements ITransformerConfigurationListener {

    private static final Logger logger = LoggerFactory
	    .getLogger(DSLBuilderHelper.class);

    private final List<TransformationHandler> transformations = new ArrayList<TransformationHandler>();

    private final Map<String, Map<String, Boolean>> transformers = new HashMap<String, Map<String, Boolean>>();

    public DSLBuilderHelper() {
	try {
	    setConfiguredTransformations();
	    setConfiguredTransformers();
	} catch (CoreException e) {
	    logger.error("Failed initialization of DSLBuilder");
	}
    }

    private void setConfiguredTransformers() {
	this.transformers.clear();
	this.transformers.putAll(PreferencesStoreUtils
		.getConfiguration(TigerseyeCore.getPreferences()));
	logger.info("found {} transformations: {}",
		Integer.toString(transformations.size()), transformations);
    }

    private void setConfiguredTransformations() throws CoreException {
	this.transformations.clear();
	ArrayList<TransformationHandler> configuredTransformations = TigerseyeCore
		.getConfiguredTransformations();
	transformations.clear();
	transformations.addAll(configuredTransformations);
    }

    private List<ASTTransformation> getASTTransformations() {
	ArrayList<ASTTransformation> arrayList = new ArrayList<ASTTransformation>();
	for (TransformationHandler t : transformations) {
	    Transformation newInstance = t.getTransformation();
	    if (newInstance instanceof ASTTransformation) {
		arrayList.add((ASTTransformation) newInstance);
	    }
	}
	return arrayList;
    }

    private List<TextualTransformation> getTextualTransformations() {
	ArrayList<TextualTransformation> arrayList = new ArrayList<TextualTransformation>();
	for (TransformationHandler t : transformations) {
	    Transformation newInstance = t.getTransformation();
	    if (newInstance instanceof TextualTransformation) {
		arrayList.add((TextualTransformation) newInstance);
	    }
	}
	return arrayList;
    }


    @Override
    public Map<FileType, Collection<TransformationHandler>> getAvailableTransformers() {

	Map<FileType, Collection<TransformationHandler>> map = new HashMap<FileType, Collection<TransformationHandler>>();

	for (FileType filetype : FileType.values()) {
	    List<TransformationHandler> list = new LinkedList<TransformationHandler>();

	    for (TransformationHandler t : transformations) {
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

    private <T extends Transformation> Collection<T> getConfiguredTransformers(
	    Collection<T> availableTransformers,
	    FileType filetype, String... extensions) {
	try {
	    setConfiguredTransformations();
	} catch (CoreException e1) {
	    logger.error("Failed to return transformations.");
	    return new ArrayList<T>();
	}
	Set<T> set = new LinkedHashSet<T>();

	for (T clazz : availableTransformers) {
	    // interessted in filetype
	    Boolean active = this.getMap(filetype.name()).get(
		    clazz.getClass().getCanonicalName());

	    if ((active != null) && active.booleanValue()) {
		set.add(clazz);

	    } else {
		for (String ext : extensions) {
		    // interessted in dsl extension
		    active = this.getMap(ext).get(
			    clazz.getClass().getCanonicalName());

		    if ((active != null) && active.booleanValue()) {
			set.add(clazz);
			break;
		    }
		}
	    }
	}

	Collection<T> result = new ArrayList<T>(set.size());
	for (T clazz : set) {
	    result.add(clazz);
	    }

	logger.trace("For filetype " + filetype + " available transformers"
		+ Arrays.toString(availableTransformers.toArray()));
	logger.trace("For extensions " + Arrays.toString(extensions)
		+ " configured transformers  " + set);
	return result;
    }

    public Collection<TextualTransformation> getConfiguredTextualTransformers(
	    FileType filetype, String... extensions) {
	return this.getConfiguredTransformers(getTextualTransformations(),
		filetype, extensions);
    }

    public Collection<ASTTransformation> getConfiguredASTTransformers(
	    FileType filetype, String... extensions) {
	return this.getConfiguredTransformers(getASTTransformations(),
		filetype, extensions);
    }

    @Override
    public String getInformation(String transformer) {
	Transformation t;
	try {
	    t = (Transformation) Class.forName(transformer).newInstance();
	    return this.getTransformerInformation(t);
	} catch (InstantiationException e) {
	    logger.warn("Generated log statement", e);
	} catch (IllegalAccessException e) {
	    logger.warn("Generated log statement", e);
	} catch (ClassNotFoundException e) {
	    logger.warn("Generated log statement", e);
	}
	return null;
    }

    private String getTransformerInformation(Transformation t) {
	String description = t.getDescription();
	Set<String> assurances = new HashSet<String>();
	Set<String> requirements = new HashSet<String>();

	if (t instanceof TextualTransformation) {
	    assurances = ((TextualTransformation) t).getAssurances();
	    requirements = ((TextualTransformation) t).getRequirements();
	} else if (t instanceof ASTTransformation) {
	    // XXX when is this used?
	    Set<ATerm> assurances2 = ((ASTTransformation) t).getAssurances();
	    Set<ATerm> requirements2 = ((ASTTransformation) t)
		    .getRequirements();

	    for (ATerm aterm : assurances2) {
		assurances.add(aterm.toString());
	    }
	    for (ATerm aterm : requirements2) {
		requirements.add(aterm.toString());
	    }
	}

	Set<FileType> supportedFileExtensions = t.getSupportedFileTypes();

	return "Description:\n" + description + "\n\nSupported Filetypes:\n"
		+ supportedFileExtensions + "\n\nRequirements:\n"
		+ requirements + "\n\nAssurances:\n" + assurances;
    }

    @Override
    public void setEnabled(String extension, String transformer, boolean enabled) {
	Map<String, Boolean> map = this.getMap(extension);

	map.put(transformer, enabled);
    }

    private Map<String, Boolean> getMap(String extension) {
	Map<String, Boolean> map = transformers.get(extension);

	if (map == null) {
	    map = new HashMap<String, Boolean>();
	    transformers.put(extension, map);
	}

	return map;
    }
}
