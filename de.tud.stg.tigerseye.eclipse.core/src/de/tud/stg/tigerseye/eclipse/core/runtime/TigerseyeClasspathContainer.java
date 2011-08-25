package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.TigerseyeLibraryProvider;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeRuntimeException;

public class TigerseyeClasspathContainer implements IClasspathContainer {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeClasspathContainer.class);

    private Set<IClasspathEntry> cpEntries = new HashSet<IClasspathEntry>();

    private final IProject project;

    public static final Path CONTAINER_ID = new Path("TIGERSEYE_SUPPORT");

    private final Set<IClasspathEntry> alreadyOnPath;

    public TigerseyeClasspathContainer(IProject project) {
	this.project = project;
	// alreadyOnPath = new HashSet<IClasspathEntry>();
	// IJavaProject hostJP = JavaCore.create(project);
	// Set<IClasspathEntry> hostComputeDefaultRuntimeClassPath =
	// computeDefaultRuntimeClassPath(hostJP);
	// try {
	// IClasspathContainer classpathContainer = JavaCore
	// .getClasspathContainer(CONTAINER_ID, hostJP);
	// if (classpathContainer != null) {
	// IClasspathEntry[] oldTigerseyeEntries = classpathContainer
	// .getClasspathEntries();
	// for (IClasspathEntry oldEntry : oldTigerseyeEntries) {
	// boolean doesthiswork = hostComputeDefaultRuntimeClassPath
	// .remove(oldEntry);
	//
	// File absoluteOldEntry = oldEntry.getPath().toFile()
	// .getAbsoluteFile();
	//
	// }
	// }
	// } catch (JavaModelException e) {
	// logger.info("Failed to remove old tigerseye entries from to remove entries");
	// }
	// alreadyOnPath.addAll(hostComputeDefaultRuntimeClassPath);
	alreadyOnPath = Collections.emptySet();
    }

    private Set<IClasspathEntry> createCPEntries() {
	Set<IClasspathEntry> cpEntries = new HashSet<IClasspathEntry>();
	File[] runtimeJars;
	try {
	    runtimeJars = TigerseyeLibraryProvider
		    .getTigerseyeRuntimeLibraries();

	} catch (IOException e) {
	    throw new TigerseyeRuntimeException(e);
	}
	for (File jar : runtimeJars) {
	    Path path = new Path(jar.getAbsolutePath());
	    IClasspathEntry libEntry = JavaCore.newLibraryEntry(path, null,
		    null);
	    cpEntries.add(libEntry);
	}
	/*
	 * FIXME the TigerseyeDSLDefinitionsCPContainer should be added as
	 * separate container, since it is possible, e.g. when designing
	 * languages that they only need the runtime support. Found no working
	 * solution hitherto, therefore I just add DSL definition libraries to
	 * the main runtime Tigerseye class path container.
	 */
	TigerseyeDSLDefinitionsCPContainer container = new TigerseyeDSLDefinitionsCPContainer(
		project);
	Set<IClasspathEntry> recomputeClassPathEntries = container
		.recomputeClassPathEntries();
	// Set<IClasspathEntry> filterDuplicates =
	// filterDuplicates(recomputeClassPathEntries);
	Set<IClasspathEntry> filterDuplicates = recomputeClassPathEntries;
	IClasspathEntry[] classpathEntries = filterDuplicates
		.toArray(new IClasspathEntry[0]);

	Collections.addAll(cpEntries, classpathEntries);

	return cpEntries;
    }

    @Override
    public IClasspathEntry[] getClasspathEntries() {
	if (cpEntries == null || cpEntries.isEmpty())
	    cpEntries = createCPEntries();
	return this.cpEntries.toArray(new IClasspathEntry[0]);
    }

    @Override
    public String getDescription() {
	return "Tigerseye Library";
    }

    @Override
    public int getKind() {
	return IClasspathContainer.K_APPLICATION;
    }

    @Override
    public IPath getPath() {
	return CONTAINER_ID;
    }

    private Set<IClasspathEntry> filterDuplicates(
	    Set<IClasspathEntry> newEntryCandidates) {
	HashSet<File> comparableFormat = new HashSet<File>();
	for (IClasspathEntry iClasspathEntry : alreadyOnPath) {
	    IPath path = iClasspathEntry.getPath();
	    File file = path.toFile();
	    comparableFormat.add(file);
	}

	HashSet<IClasspathEntry> finalEntriesToAdd = new HashSet<IClasspathEntry>();
	for (IClasspathEntry icp : newEntryCandidates) {
	    if (!alreadyOnPath.contains(icp)) {
		// int entryKind = icp.getEntryKind();
		// if (entryKind == IClasspathEntry.CPE_LIBRARY
		// || entryKind == IClasspathEntry.CPE_PROJECT) {
		File file = icp.getPath().toFile();
		if (!comparableFormat.contains(file))
		    finalEntriesToAdd.add(icp);
		else {
		    logger.trace("Ignoring {} is already on classpath", icp);
		}
		// }
	    } else
		logger.trace("Ignoring {} is already on classpath", icp);
	}
	return finalEntriesToAdd;
    }

    private Set<IClasspathEntry> computeDefaultRuntimeClassPath(
	    IJavaProject javaProject) {
	String[] computeDefaultRuntimeClassPath;
	try {
	    computeDefaultRuntimeClassPath = JavaRuntime
		    .computeDefaultRuntimeClassPath(javaProject);
	} catch (CoreException e) {
	    logger.warn(
		    "could not compute default classpath. Redundant libraries might be on the classpath",
		    e);
	    return Collections.emptySet();
	}

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

}
