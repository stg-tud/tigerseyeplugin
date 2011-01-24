package de.tud.stg.popart.builder.eclipse;

import java.util.*;

import org.apache.commons.lang.UnhandledException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import de.tud.stg.popart.builder.eclipse.dialoge.PreferencesStoreUtils;
import de.tud.stg.popart.builder.transformers.ASTTransformation;
import de.tud.stg.popart.builder.transformers.Filetype;
import de.tud.stg.popart.builder.transformers.TextualTransformation;
import de.tud.stg.popart.builder.transformers.Transformation;
import de.tud.stg.tigerseye.core.TigerseyeCore;

/**
 * 
 * FIXME This class was once the builder plug-in activator. It now has no
 * definite purpose but holds still some functionality. Rename it accordingly to
 * what it does now or move the different functionalities.
 * 
 */
public class DSLBuilderActivator implements
		ITransformerConfigurationListener {
	private static final String transformersExtensionId = "de.tud.stg.tigerseye.transformers";

	private static final Logger logger = LoggerFactory
			.getLogger(DSLBuilderActivator.class);

	private final List<Class<? extends Transformation>> transformations = new ArrayList<Class<? extends Transformation>>();

    private final Map<String, Map<String, Boolean>> transformers = new HashMap<String, Map<String, Boolean>>();
	/**
	 * The constructor
	 */
    public DSLBuilderActivator() {
		try {
			initializePluginTransformers();
		} catch (CoreException e) {
			logger.error("Failed initialization of DSLBuilder");
		}
	}



	// private void pluginLanguageProvider() throws CoreException {
	// IConfigurationElement[] config =
	// Platform.getExtensionRegistry().getConfigurationElementsFor(
	// "de.tud.stg.popart.builder.languageProvider");
	//
	// if(config.length < 1){
	// logger.warn("No language extensions found, language provider will be null");
	// }
	//
	// if (config.length > 0) {
	// languageProvider = (ILanguageProvider)
	// config[0].createExecutableExtension("class");
	// logger.info("language provider set");
	// }
	// languageProvider = new PopartLanguageProvider();
	// }

	private void initializePluginTransformers() throws CoreException {
		setConfiguredTransformers();
	this.transformers.clear();
	this.transformers.putAll(PreferencesStoreUtils
		.getConfiguration(TigerseyeCore.getPreferences()));
		logger.info("found {} transformations: {}",
				Integer.toString(transformations.size()), transformations);
	}

	private void setConfiguredTransformers() throws CoreException {
	this.transformations.clear();
	    ArrayList<Transformation> configuredTransformations = getConfiguredTransformations();
	    for (Transformation transformation : configuredTransformations) {
	    	this.addToList(transformation);
	    }
	}

	private ArrayList<Transformation> getConfiguredTransformations()
			throws CoreException
 {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(transformersExtensionId);

		ArrayList<Transformation> transformationsList = new ArrayList<Transformation>();
		for (IConfigurationElement configEl : config) {
			for (IConfigurationElement children : configEl.getChildren()) {
				// String name = children.getAttribute("name");
				Transformation t = (Transformation) children
						.createExecutableExtension("class");
				transformationsList.add(t);
			}
		}
		return transformationsList;
	}

	private void addToList(Transformation t) {
		if ((t instanceof ASTTransformation) || (t instanceof TextualTransformation)) {
			transformations.add(t.getClass());
		} else {
			throw new IllegalArgumentException(
					"Unsupported transformation type");
		}
	}

    // TODO #getASTTransformations and # getTextualTransformations do pretty
    // much the same only for different Superclasses. Should be possible to
    // generalize the functionality
	private List<Class<? extends ASTTransformation>> getASTTransformations()
 {
		ArrayList<Class<? extends ASTTransformation>> arrayList = new ArrayList<Class<? extends ASTTransformation>>();
		for (Class<? extends Transformation> t : transformations) {
			Transformation newInstance = getInstantiation(t);
			if (newInstance instanceof ASTTransformation) {
				arrayList.add((Class<? extends ASTTransformation>) newInstance
						.getClass());
			}
		}
		return arrayList;
	}

	private List<Class<? extends TextualTransformation>> getTextualTransformations() {
		ArrayList<Class<? extends TextualTransformation>> arrayList = new ArrayList<Class<? extends TextualTransformation>>();
		for (Class<? extends Transformation> t : transformations) {
			Transformation newInstance = getInstantiation(t);
			if (newInstance instanceof TextualTransformation) {
				arrayList
						.add((Class<? extends TextualTransformation>) newInstance
								.getClass());
			}
		}
		return arrayList;
	}

	private Transformation getInstantiation(Class<? extends Transformation> t) {
		try {
			return t.newInstance();
		} catch (InstantiationException e) {
			throw new UnhandledException(e);
		} catch (IllegalAccessException e) {
			throw new UnhandledException(e);
		}
	}

	@Override
	public Map<String, Collection<String>> getAvailableTransformers(
			String extension) {

		Map<String, Collection<String>> map = new HashMap<String, Collection<String>>();

		List<Transformation> transformers = new LinkedList<Transformation>();
		for (Class<? extends Transformation> clazz : transformations) {
			try {
				transformers.add(clazz.newInstance());
			} catch (InstantiationException e) {
				logger.error("Transformer class instantiation failed", e);
			} catch (IllegalAccessException e) {
				logger.error("Transformer class instantiation failed", e);
			}
		}

	for (Filetype ext : Filetype.values()) {
			List<String> list = new LinkedList<String>();

			for (Transformation t : transformers) {
				Set<Filetype> supportedFileExtensions = t
						.getSupportedFiletypes();
				if (supportedFileExtensions.contains(ext)) {
					list.add(t.getClass().getCanonicalName());
				}
			}

			map.put(ext.name, list);
		}

		// Adding empty list to avoid null pointer in using class
		map.put(extension, new LinkedList<String>());

		return map;
	}

    private <T> Collection<T> getConfiguredTransformers(
			Collection<Class<? extends T>> availableTransformers,
			Filetype filetype, String... extensions) {
	try {
	    setConfiguredTransformers();
	} catch (CoreException e1) {
	    logger.error("Failed to return transformations.");
	    return new ArrayList<T>();
	}
		Set<Class<? extends T>> set = new LinkedHashSet<Class<? extends T>>();

		for (Class<? extends T> clazz : availableTransformers) {
			// interessted in filetype
			Boolean active = this.getMap(filetype.name()).get(
					clazz.getCanonicalName());

			if ((active != null) && active.booleanValue()) {
				set.add(clazz);

			} else {
				for (String ext : extensions) {
					// interessted in dsl extension
					active = this.getMap(ext).get(clazz.getCanonicalName());

					if ((active != null) && active.booleanValue()) {
						set.add(clazz);
						break;
					}
				}
			}
		}

		Collection<T> result = new ArrayList<T>(set.size());
		try {
			for (Class<? extends T> clazz : set) {
				result.add(clazz.newInstance());
			}
		} catch (InstantiationException e) {
			logger.warn("Generated log statement", e);
		} catch (IllegalAccessException e) {
			logger.warn("Generated log statement", e);
		}

		logger.trace("For filetype " + filetype + " available transformers"
				+ Arrays.toString(availableTransformers.toArray()));
		logger.trace("For extensions " + Arrays.toString(extensions)
				+ " configured transformers  " + set);
		return result;
	}

    public Collection<TextualTransformation> getConfiguredTextualTransformers(
			Filetype filetype, String... extensions) {
		return this.getConfiguredTransformers(getTextualTransformations(),
				filetype,
				extensions);
	}

	public Collection<ASTTransformation> getConfiguredASTTransformers(
			Filetype filetype, String... extensions) {
		return this.getConfiguredTransformers(getASTTransformations(),
				filetype,
				extensions);
	}

	@Override
	public String getInformation(String transformer) {

		try {
			Transformation t = (Transformation) Class.forName(transformer)
					.newInstance();
			return this.getTransformerInformation(t);
		} catch (Exception e) {
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

		Set<Filetype> supportedFileExtensions = t.getSupportedFiletypes();

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
