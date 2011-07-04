package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;

public class TigerseyeDSLDefinitionsCPContainer implements IClasspathContainer {

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
	reset();
    }

    public void reset() {
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
    }

    private void logFail(DSLDefinition dslDefinition, Exception e) {
	logger.error("Failed to add dsl  {} to classpath", dslDefinition, e);
    }

    private void addProjectPathForDSL(DSLDefinition dslDefinition)
	    throws IOException, CoreException, BundleException {

	String id = dslDefinition.getContributor().getId();
	IPluginModelBase model = PluginRegistry.findEntry(id).getModel();
	if (model == null) {
	    logger.error("Could not determine a plug-in representation for {}",
		    id);
	    return;
	}
	Set<IClasspathEntry> newEntryCandidates;
	IResource underlyingResource = model.getUnderlyingResource();
	if (underlyingResource == null) {
	    newEntryCandidates = calculateUncompleteBundleClassPath(dslDefinition);
	    // FIXME(Leo Roos;Jul 3, 2011) could calculate every dependency, but
	    // this still produces some problems.
	    // newEntryCandidates = calculateBundleClassPath(id);
	} else {
	    newEntryCandidates = computeClassPathForWorkspaceDependencies(model);
	}
	HashSet<IClasspathEntry> finalEntriesToAdd = filterDuplicates(newEntryCandidates);
	this.cpEntries.addAll(finalEntriesToAdd);
    }

    private HashSet<IClasspathEntry> filterDuplicates(
	    Set<IClasspathEntry> newEntryCandidates) {
	HashSet<File> entryPaths = new HashSet<File>();
	for (IClasspathEntry iClasspathEntry : this.cpEntries) {
	    IPath path = iClasspathEntry.getPath();
	    File file = path.toFile();
	    entryPaths.add(file);
	}

	Set<IClasspathEntry> alreadyOnPath = new HashSet<IClasspathEntry>(
		this.cpEntries);
	HashSet<IClasspathEntry> finalEntriesToAdd = new HashSet<IClasspathEntry>();
	for (IClasspathEntry icp : newEntryCandidates) {
	    if (!alreadyOnPath.contains(icp)) {
		int entryKind = icp.getEntryKind();
		if (entryKind == IClasspathEntry.CPE_LIBRARY
			|| entryKind == IClasspathEntry.CPE_PROJECT) {
		    File file = icp.getPath().toFile();
		    if (!entryPaths.contains(file))
			finalEntriesToAdd.add(icp);
		}
	    } else
		logger.trace("Ignoring {} is already on classpath", icp);
	}
	return finalEntriesToAdd;
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
	String[] computeDefaultRuntimeClassPath = JavaRuntime
		.computeDefaultRuntimeClassPath(javaProject);

	Set<IClasspathEntry> newEntryCandidates = new HashSet<IClasspathEntry>();
	// RequiredPluginsClasspathContainer rpcc = new
	// RequiredPluginsClasspathContainer(
	// model);
	// IClasspathEntry[] externalEntries = RequiredPluginsClasspathContainer
	// .getExternalEntries(model);
	// IClasspathEntry[] requiredPlugins = rpcc.getClasspathEntries();
	// Collections.addAll(newEntryCandidates, requiredPlugins);
	// Collections.addAll(newEntryCandidates, externalEntries);
	for (String string : computeDefaultRuntimeClassPath) {
	    IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(
		    new Path(string), null, null);
	    newEntryCandidates.add(newLibraryEntry);
	}
	return newEntryCandidates;
    }

    private Set<IClasspathEntry> calculateUncompleteBundleClassPath(
	    DSLDefinition dslDefinition) throws CoreException {
	String contributorSymbolicName = dslDefinition.getContributor().getId();
	Bundle bundle = Platform.getBundle(contributorSymbolicName);
	Set<File> cpEntriesToAdd = new HashSet<File>();
	if (bundle != null) {
	    DSLClasspathResolver resolver = new DSLClasspathResolver();
	    File[] resolveCPEntriesForBundle = resolver
		    .resolveCPEntriesForBundle(bundle);
	    if (resolveCPEntriesForBundle == null) {
		logger.error("Failed to resolve ClassPath for contriburo {}",
			contributorSymbolicName);
		cpEntriesToAdd = Collections.emptySet();
	    } else {
		Collections.addAll(cpEntriesToAdd, resolveCPEntriesForBundle);
	    }
	} else {
	    throw new IllegalStateException("Cannot handle workspace plug-ins");
	}

	HashSet<IClasspathEntry> finalEntries = new HashSet<IClasspathEntry>();
	for (File file : cpEntriesToAdd) {
	    IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(
		    new Path(file.getPath()), null, null);
	    finalEntries.add(newLibraryEntry);
	}

	return finalEntries;
    }

    @Override
    public IClasspathEntry[] getClasspathEntries() {
	return this.cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
    }

    @Override
    public String getDescription() {
	return "Tigerseye DSL Definitions";
    }

    @Override
    public int getKind() {
	return IClasspathContainer.K_APPLICATION;
    }

    @Override
    public IPath getPath() {
	return CONTAINER_ID;
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