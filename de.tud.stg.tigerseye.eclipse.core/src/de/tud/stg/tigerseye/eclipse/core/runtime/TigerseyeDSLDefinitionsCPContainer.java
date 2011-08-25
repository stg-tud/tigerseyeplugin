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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
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

public class TigerseyeDSLDefinitionsCPContainer {

    public static final IPath CONTAINER_ID = new Path(
	    "TIGERSEYE_DSL_DEFINITIONS_SUPPORT");

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeDSLDefinitionsCPContainer.class);

    private final Set<IClasspathEntry> cpEntries = new HashSet<IClasspathEntry>();
    // XXX (Leo Roos; Jul 4, 2011): will probably be used when calculating all
    // dependent plug-ins of DSL definitions
    private final IProject hostProject;

    public TigerseyeDSLDefinitionsCPContainer(IProject project) {
	logger.trace("Adding DSL specific classpath for {}", project.getName());
	this.hostProject = project;
    }

    public Set<IClasspathEntry> recomputeClassPathEntries() {
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
		try {
		    addProjectPathForDSL(dslDefinition);
		} catch (IOException e) {
		    logFail(dslDefinition, e);
		} catch (CoreException e) {
		    logFail(dslDefinition, e);
		} catch (BundleException e) {
		    logFail(dslDefinition, e);
		}
	    }
	}

	logger.trace("added classpathentries: {}", this.cpEntries);
	return this.cpEntries;
    }

    private void logFail(DSLDefinition dslDefinition, Exception e) {
	logger.error("Failed to add dsl  {} to classpath", dslDefinition, e);
    }

    private void addProjectPathForDSL(DSLDefinition dslDefinition)
	    throws IOException, CoreException, BundleException {
	String id = dslDefinition.getContributor().getId();
	ModelEntry findEntry = PluginRegistry.findEntry(id);
	if (findEntry == null) {
	    logger.error("Did not even find one plug-in for the id {}", id);
	    return;
	}
	IPluginModelBase model = findEntry.getModel();
	if (model == null) {
	    logger.error("Could not determine a plug-in representation for {}",
		    id);
	    return;
	}
	Set<IClasspathEntry> newEntryCandidates;
	IResource underlyingWorkspaceResource = model.getUnderlyingResource();
	if (underlyingWorkspaceResource == null) {
	    newEntryCandidates = calculateUncompleteBundleClassPath(dslDefinition);
	} else {
	    newEntryCandidates = computeClassPathForWorkspaceDependencies(model);
	}
	Set<IClasspathEntry> finalEntriesToAdd = newEntryCandidates;
	this.cpEntries.addAll(finalEntriesToAdd);
    }

    // @SuppressWarnings("restriction")
    private Set<IClasspathEntry> computeClassPathForWorkspaceDependencies(
	    IPluginModelBase model) throws CoreException {
	// Could also use JavaRuntime.computeClassPathForWorkspaceDependencies
	// since I can only use this for workspace projects and avoiding the use
	// of internal classes
	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	String installLocation = model.getInstallLocation();
	IProject project = root.getProject(new File(installLocation).getName());
	IJavaProject javaProject = JavaCore.create(project);
	Set<IClasspathEntry> newEntryCandidates = computeDefaultRuntimeClassPath(javaProject);
	return newEntryCandidates;
    }

    private Set<IClasspathEntry> computeDefaultRuntimeClassPath(
	    IJavaProject javaProject) throws CoreException {
	// String[] computeDefaultRuntimeClassPath = JavaRuntime
	// .computeDefaultRuntimeClassPath(javaProject);
	// Set<IClasspathEntry> newEntryCandidates = new
	// HashSet<IClasspathEntry>();
	// for (String string : computeDefaultRuntimeClassPath) {
	// IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(
	// new Path(string), null, null);
	// newEntryCandidates.add(newLibraryEntry);
	// }
	// return newEntryCandidates;

	IClasspathEntry[] entries = javaProject.getRawClasspath();

	List<IClasspathEntry> addCandidates = new ArrayList<IClasspathEntry>(
		Arrays.asList(entries));

	IPath outputLocation = javaProject.getOutputLocation();
	IClasspathEntry outputLoc = JavaCore.newLibraryEntry(outputLocation,
		null, null);

	addCandidates.add(outputLoc);

	List<IClasspathEntry> asList = new ArrayList<IClasspathEntry>();

	for (IClasspathEntry entry : addCandidates) {
	    int entryKind = entry.getEntryKind();
	    switch (entryKind) {
	    case IClasspathEntry.CPE_LIBRARY:
	    case IClasspathEntry.CPE_PROJECT:
		asList.add(entry);
		break;
	    case IClasspathEntry.CPE_SOURCE:
	    case IClasspathEntry.CPE_VARIABLE:
	    case IClasspathEntry.CPE_CONTAINER:
	    default:
		logger.info("Can not add {} to classpath container", entry);
	    }
	}

	// IRuntimeClasspathEntry[] entries = JavaRuntime
	// .computeUnresolvedRuntimeClasspath(javaProject);

	// ClassPathDetector classPathDetector = new ClassPathDetector(
	// javaProject.getProject(), null);
	// IClasspathEntry[] classpath = classPathDetector.getClasspath();


	HashSet<IClasspathEntry> hashSet = new HashSet<IClasspathEntry>();
	for (IClasspathEntry iRuntimeClasspathEntry : asList) {
	    hashSet.add(iRuntimeClasspathEntry);
	    // IClasspathEntry classpathEntry = iRuntimeClasspathEntry
	    // .getClasspathEntry();
	    // IClasspathEntry resolvedClasspathEntry = JavaCore
	    // .getResolvedClasspathEntry(classpathEntry);
	    // if (classpathEntry != null)
	    // hashSet.add(classpathEntry);
	}
	return hashSet;
    }

    private Set<IClasspathEntry> calculateUncompleteBundleClassPath(
	    DSLDefinition dslDefinition) throws CoreException {
	HashSet<IClasspathEntry> finalEntries = new HashSet<IClasspathEntry>();
	String contributorSymbolicName = dslDefinition.getContributor().getId();
	Bundle bundle = Platform.getBundle(contributorSymbolicName);
	Set<File> cpEntriesToAdd = new HashSet<File>();
	if (bundle != null) {
	    boolean bundleWorkspaceProject = DSLConfigurationElementResolver
		    .isBundleWorkspaceProject(bundle);
	    if (bundleWorkspaceProject) {
		Set<IClasspathEntry> result = resolveWorkspaceClasspath(bundle);
		finalEntries.addAll(result);
	    } else {

		DSLClasspathResolver resolver = new DSLClasspathResolver();
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
	// IPluginModelBase findModel = PluginRegistry.findModel(bundle
	// .getSymbolicName());
	// IResource underlyingResource = findModel.getUnderlyingResource();
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

    /*
     * _________________________________NOLONGERUSED_______________________
     * ---------------DELETE WHEN REFACTORING FINISHED
     */

    // private <T> HashSet<T> newHashSetFromArray(T[] array) {
    // HashSet<T> hostSet = new HashSet<T>(array.length);
    // Collections.addAll(hostSet, array);
    // return hostSet;
    // }

    // @Deprecated
    // private List<File> getClasspathEntriesForBundleProperties(File
    // bundleFile,
    // File buildProps) {
    // Properties properties = new Properties();
    // FileInputStream fileInputStream = null;
    // try {
    // fileInputStream = new FileInputStream(buildProps);
    // properties.load(fileInputStream);
    // } catch (Exception e) {
    // logger.trace("failed to get inputstream for {}", buildProps, e);
    // IOUtils.closeQuietly(fileInputStream);
    // }
    //
    // List<String> dirFiles = new ArrayList<String>();
    // /*
    // * usually says nothing about classpath
    // */
    // // getResourceNamesForProperty(properties, "output..");
    // List<String> includes = getResourceNamesForProperty(properties,
    // "bin.includes");
    //
    // List<File> jars = new LinkedList<File>();
    // for (String string : includes) {
    // File cpFile = new File(bundleFile, string);
    // if (isJarFile(cpFile)) {
    // jars.add(cpFile);
    // } else if (cpFile.isDirectory()) {
    // dirFiles.add(string);
    // }
    // }
    // List<File> resultCPEntries = new ArrayList<File>();
    // resultCPEntries.addAll(jars);
    // List<File> classFolders = getClassFolders(dirFiles, bundleFile);
    // resultCPEntries.addAll(classFolders);
    // return resultCPEntries;
    // }

    // private List<File> getClassFolders(List<String> cpDirFiles, File
    // bundleFile) {
    // /*
    // * TODO refactor: should consider includes+projectroot as default cp and
    // * the Manifest classpath. Additionally to provide access during
    // * development allow bin folder.
    // */
    // /*
    // * If class output folder exists it can not be nested inside a project
    // * root folder which as well is declared as class folder. Such a
    // * combination will cause unresolved class path problems. I assume that
    // * the relevant class folder is the specifically as "output.." property
    // * defined folder.
    // */
    // int metaInfIndex = hasStringBeginningWith(cpDirFiles, metaInfDir);
    // if (metaInfIndex >= 0) {
    // cpDirFiles.remove(metaInfIndex);
    // }
    // // int stdBinIndex = hasStringBeginningWith(cpDirFiles, defaultBinName);
    // // boolean hasStdBin = stdBinIndex >= 0;
    // // if (!hasStdBin) {
    // boolean hasStdBin = containsBinFolder(bundleFile);
    // if (hasStdBin) {
    // cpDirFiles.add(defaultBinName);
    // // }
    // } else {
    // cpDirFiles.add(projectRootDir);
    // }
    // int rootCPIndex = hasStringBeginningWith(cpDirFiles, projectRootDir);
    // boolean hasRootCP = rootCPIndex >= 0;
    // if (hasRootCP && hasStdBin) {
    // cpDirFiles.remove(rootCPIndex);
    // hasRootCP = false;
    // }
    //
    // List<File> classFolder = new ArrayList<File>();
    // for (String string : cpDirFiles) {
    // File cpFolderFile = new File(bundleFile, string);
    // // TODO refactor this is just a quick and dirty workaround
    // if (!string.startsWith(projectRootDir) && hasRootCP
    // && cpFolderFile.isDirectory()) {
    // logger.trace(
    // "not adding {} to avoid nesting conflicts since root directory is also on classpath",
    // string);
    // } else {
    // classFolder.add(cpFolderFile);
    // }
    // }
    // return classFolder;
    // }

    // /**
    // * Returns error code depending on whether {@code beginningWith} is
    // * contained in {@code cpDirFiles} List.
    // *
    // * @param cpDirFiles
    // * @param beginningWith
    // * @return <ul>
    // * <li>{@code -1} if no string beginning with {@code beginningWith}
    // * is contained
    // * <li>a integer greater or equal {@code 0} if {@code beginningWith}
    // * is contained. The integer represents the entry that starts with
    // * the {@code beginningWith} string.
    // * </ul>
    // */
    // private int hasStringBeginningWith(List<String> cpDirFiles,
    // String beginningWith) {
    // for (int i = 0; i < cpDirFiles.size(); i++) {
    // if (cpDirFiles.get(i).startsWith(beginningWith))
    // return i;
    // }
    // return -1;
    // }
    //
    // private boolean containsBinFolder(File bundleFile) {
    // String[] list = bundleFile.list();
    // for (String string : list) {
    // if (string.equals(defaultBinName))
    // return true;
    // }
    // return false;
    // }
    //
    // private void addJarsAsCPEntry(List<File> jars) {
    // for (File jar : jars) {
    // addFileAsCPEntryIfExistant(jar);
    // }
    // }
    //
    // private List<String> getResourceNamesForProperty(Properties properties,
    // String property) {
    // LinkedList<String> resources = new LinkedList<String>();
    // String resourcesCSV = properties.getProperty(property, null);
    // if (resourcesCSV != null) {
    // String[] resourcesArray = resourcesCSV.split(",");
    // for (String resourceName : resourcesArray) {
    // resources.add(resourceName);
    // }
    // }
    // return resources;
    // }
    //
    // private void addFileAsCPEntryIfExistant(File cpFile) {
    // if (cpFile.exists()) {
    // Path cpEntryPath = new Path(cpFile.getAbsolutePath());
    // IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(
    // cpEntryPath, null, null);
    // this.cpEntries.add(newLibraryEntry);
    // }
    // }
    //
    // private boolean isJarFile(File cpFile) {
    // return cpFile.isFile() && cpFile.getName().endsWith(".jar");
    // }

    // TigerseyeClasspathContainer.CONTAINER_ID
    // .append(new Path("TIGERSEYE_DSL_DEFINITIONS_SUPPORT"));

    // private static final String metaInfDir = "META-INF";
    //
    // private static final String projectRootDir = ".";
    //
    // private static final String defaultBinName = "bin";

}