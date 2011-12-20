package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.internal.DSLConfigurationElementResolver;
import de.tud.stg.tigerseye.eclipse.core.preferences.TigerseyePreferenceConstants;

public class TigerseyeLibraryClasspathResolver {

    public static final IPath CONTAINER_ID = new Path(
	    "TIGERSEYE_DSL_DEFINITIONS_SUPPORT");

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeLibraryClasspathResolver.class);

    private Set<IClasspathEntry> computedEntries;

    public TigerseyeLibraryClasspathResolver() {
    }

    public Set<IClasspathEntry> getComputedClasspathEntries() {
	if (computedEntries == null) {
	    computedEntries = recomputeClassPathEntries();
	}
	return computedEntries;
    }

    public Set<IClasspathEntry> recomputeClassPathEntries() {
	Set<IClasspathEntry> result = new HashSet<IClasspathEntry>();
	Collection<DSLDefinition> dslDefinitions = TigerseyeCore
		.getLanguageProvider().getDSLDefinitions();
	Set<String> iteratedContributor = new HashSet<String>();
	for (DSLDefinition dslDefinition : dslDefinitions) {
	    /*
	     * don't add same DSL from different contributors twice
	     */
	    if (dslDefinition.isActive()
		    && !iteratedContributor.contains(dslDefinition
			    .getContributor().getId())) {
		Set<IClasspathEntry> addProjectPathForDSL = computeClasspathEntriesHandleExceptions(dslDefinition);
		if (addProjectPathForDSL != null) {
		    result.addAll(addProjectPathForDSL);
		    iteratedContributor.add(dslDefinition.getContributor()
			    .getId());
		}
	    }
	}
	logger.trace("added classpathentries: {}", result);
	return result;
    }

    private Set<IClasspathEntry> computeClasspathEntriesHandleExceptions(
	    DSLDefinition dslDefinition) {
	try {
	    Set<IClasspathEntry> addProjectPathForDSL = computeClasspathForDSL(dslDefinition);
	    return addProjectPathForDSL;
	} catch (IOException e) {
	    logFail(dslDefinition, e);
	} catch (CoreException e) {
	    logFail(dslDefinition, e);
	} catch (BundleException e) {
	    logFail(dslDefinition, e);
	}
	return null;
    }

    private void logFail(DSLDefinition dslDefinition, Exception e) {
	logger.error("Failed to add dsl  {} to classpath", dslDefinition, e);
    }

    private Set<IClasspathEntry> computeClasspathForDSL(
	    DSLDefinition dslDefinition) throws IOException, CoreException,
	    BundleException {
	String id = dslDefinition.getContributor().getId();
	ModelEntry findEntry = PluginRegistry.findEntry(id);
	if (findEntry == null) {
	    logger.error("Did not even find one plug-in for the id {}", id);
	    return Collections.emptySet();
	}
	IPluginModelBase model = findEntry.getModel();
	if (model == null) {
	    logger.error("Could not determine a plug-in representation for {}",
		    id);
	    return Collections.emptySet();
	}
	Set<IClasspathEntry> newEntryCandidates;
	IResource underlyingWorkspaceResource = model.getUnderlyingResource();
	if (underlyingWorkspaceResource == null) {
	    Bundle bundle = Platform.getBundle(id);
	    if (DSLConfigurationElementResolver
		    .isBundleWorkspaceProject(bundle)) {
		IProject linkedP = ProjectLinker
			.linkOpenedPluginIntoWorkspace(bundle);
		String locForErDesc = bundle.getLocation();
		if (linkedP == null) {
		    logger.error("Could not link plug-in {} altough is probably workspace project at {}", bundle,
			    locForErDesc);
		    return Collections.emptySet();
		}
		IPluginModelBase findModel = PluginRegistry.findModel(linkedP);
		if (findModel == null) {
		    logger.warn("Found no PluginModel for {} located at {}", linkedP, locForErDesc);
		    return Collections.emptySet();
		}
		Assert.isNotNull(findModel.getUnderlyingResource());
		newEntryCandidates = computeClassPathForWorkspaceDependencies(findModel);
	    } else {
		newEntryCandidates = calculateBundleClassPath(dslDefinition);
	    }
	} else {
	    newEntryCandidates = computeClassPathForWorkspaceDependencies(model);
	}
	Set<IClasspathEntry> finalEntriesToAdd = newEntryCandidates;
	return finalEntriesToAdd;
    }

    private Set<IClasspathEntry> computeClassPathForWorkspaceDependencies(
	    IPluginModelBase dslPluginModel) throws CoreException {
	IProject project = getProjectByLocation(dslPluginModel);
	IJavaProject javaProject = JavaCore.create(project);
	Set<IClasspathEntry> newEntryCandidates = computeProjectRuntimeClassPath(javaProject);
	return newEntryCandidates;
    }

    private IProject getProjectByLocation(IPluginModelBase model) {
	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	String installLocation = model.getInstallLocation();
	IProject project = root.getProject(new File(installLocation).getName());
	return project;
    }

    /**
     * IClasspathEntry of kind CPE_LIBRARY and CPE_PROJECT can be added as is.
     * Other entry kinds have to be resolved to one of the previous kinds. See
     * the JavaDoc description of
     * {@link IClasspathContainer#getClasspathEntries()}.
     * 
     */
    private Set<IClasspathEntry> computeProjectRuntimeClassPath(
	    IJavaProject javaProject) throws CoreException {

	IClasspathEntry[] entries = javaProject.getRawClasspath();

	List<IClasspathEntry> addCandidates = new ArrayList<IClasspathEntry>(
		Arrays.asList(entries));

	IClasspathEntry outputLoc = getDefaultOutputLocationOrNull(javaProject);
	if (outputLoc != null)
	    addCandidates.add(outputLoc);

	HashSet<IClasspathEntry> result = new HashSet<IClasspathEntry>();

	for (IClasspathEntry entry : addCandidates) {
	    int entryKind = entry.getEntryKind();
	    switch (entryKind) {
	    case IClasspathEntry.CPE_LIBRARY:
		//$FALL-THROUGH$
	    case IClasspathEntry.CPE_PROJECT:
		result.add(entry);
		break;
	    case IClasspathEntry.CPE_VARIABLE:
		IClasspathEntry resolvedClasspathEntry = JavaCore
			.getResolvedClasspathEntry(entry);
		result.add(resolvedClasspathEntry);
		break;
	    case IClasspathEntry.CPE_CONTAINER:
		IClasspathContainer container = JavaCore.getClasspathContainer(
			entry.getPath(), javaProject);
		if (TigerseyeClasspathContainer.CONTAINER_ID.equals(entry
			.getPath())) {
		    logger.trace("ignoring myself as a classpath when calculating that of others.");
		} else {
		    IClasspathEntry[] classpathEntries = container
			    .getClasspathEntries();
		    result.addAll(Arrays.asList(classpathEntries));
		}
		break;
	    case IClasspathEntry.CPE_SOURCE:
		IPath outputLocation = entry.getOutputLocation();
		if (outputLocation == null) {
		    // default location is used
		    break;
		}
		IPath sourcePath = entry.getPath();
		String tigerseyeGen = TigerseyeCore
			.getPreferences()
			.getString(
				TigerseyePreferenceConstants.TIGERSEYE_OUTPUT_FOLDER_PATH_KEY);
		Path tigerseyeGenPath = new Path(tigerseyeGen);
		int matchingFirstSegments = tigerseyeGenPath
			.matchingFirstSegments(sourcePath);
		if (!(tigerseyeGenPath.segmentCount() == matchingFirstSegments)) {
		    IClasspathEntry outputEntry = JavaCore.newLibraryEntry(
			    outputLocation, null, null);
		    result.add(outputEntry);
		}
		break;
	    default:
		logger.info("Can not add {} to classpath container", entry);
	    }
	}

	return result;
    }

    private IClasspathEntry getDefaultOutputLocationOrNull(
	    IJavaProject javaProject) throws JavaModelException {
	IPath defaultOutputLocation = javaProject.getOutputLocation();
	if (defaultOutputLocation == null)
	    return null;
	IClasspathEntry outputLoc = JavaCore.newLibraryEntry(
		defaultOutputLocation, null, null);
	return outputLoc;
    }

    private Set<IClasspathEntry> calculateBundleClassPath(
	    DSLDefinition dslDefinition) throws CoreException {
	HashSet<IClasspathEntry> finalEntries = new HashSet<IClasspathEntry>();
	String contributorSymbolicName = dslDefinition.getContributor().getId();
	Bundle bundle = Platform.getBundle(contributorSymbolicName);
	Set<File> cpEntriesToAdd = new HashSet<File>();
	if (bundle != null) {
	    boolean bundleWorkspaceProject = DSLConfigurationElementResolver
		    .isBundleWorkspaceProject(bundle);
	    if (bundleWorkspaceProject) {
		throw new IllegalStateException(
			"can not handle bundle from a workspace location: "
				+ dslDefinition);
	    } else {
		BundleClasspathResolver resolver = new BundleClasspathResolver();
		File[] resolveCPEntriesForBundle = resolver
			.resolveCPEntriesForBundle(bundle);
		if (resolveCPEntriesForBundle == null) {
		    logger.error(
			    "Failed to resolve ClassPath for contributor {}",
			    contributorSymbolicName);
		    cpEntriesToAdd = Collections.emptySet();
		} else {
		    Collections.addAll(cpEntriesToAdd,
			    resolveCPEntriesForBundle);
		}
	    }
	} else {
	    throw new IllegalStateException(
		    "Cannot handle bundle {} probably workspace plug-in");
	}

	for (File file : cpEntriesToAdd) {
	    IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(
		    new Path(file.getPath()), null, null);
	    finalEntries.add(newLibraryEntry);
	}

	return finalEntries;
    }

    private Set<IClasspathEntry> resolveWorkspaceClasspath(Bundle bundle) {
	JDTClasspathResolver jdtresolver = new JDTClasspathResolver();
	IClasspathEntry[] resolveClasspath = jdtresolver
		.resolveClasspathAndLinkProject(bundle);
	HashSet<IClasspathEntry> hashSet = new HashSet<IClasspathEntry>();
	Collections.addAll(hashSet, resolveClasspath);
	return hashSet;
    }

    // FIXME(Leo Roos;Jul 4, 2011) Currently not used until some problems
    // regarding the creation of invalid classpath entries are solved.
    // Perhaps reuse some code from
    // RequiredPluginsClasspathContainer#getClassPathEntries
    private Set<IClasspathEntry> calculateBundleClassPath(String id)
	    throws IOException, BundleException {
	Set<IClasspathEntry> newEntryCandidates = new HashSet<IClasspathEntry>();
	Collection<String> bundleClassPath;
	bundleClassPath = RequiredPluginsForBundlesResolver
		.getBundleClassPath(id);
	for (String string : bundleClassPath) {
	    IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(
		    new Path(string), null, null);
	    newEntryCandidates.add(newLibraryEntry);
	}
	return newEntryCandidates;
    }
}