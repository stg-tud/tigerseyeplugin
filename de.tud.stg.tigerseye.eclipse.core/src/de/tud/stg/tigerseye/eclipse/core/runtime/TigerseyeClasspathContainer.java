package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
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

    private TigerseyeLibraryClasspathResolver dslResolver;

    public TigerseyeClasspathContainer(IProject project) {
	this.project = project;
    }

    private @Nonnull
    Set<IClasspathEntry> createCPEntries() {
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
	TigerseyeLibraryClasspathResolver container = getDSLClasspath();
	Set<IClasspathEntry> tigerCPEntries = container
		.getComputedClasspathEntries();
	// Set<IClasspathEntry> filterDuplicates =
	// filterDuplicates(recomputeClassPathEntries);
	Set<IClasspathEntry> filterDuplicates = tigerCPEntries;
	IClasspathEntry[] classpathEntries = filterDuplicates
		.toArray(new IClasspathEntry[filterDuplicates.size()]);

	Collections.addAll(cpEntries, classpathEntries);

	return cpEntries;
    }

    public TigerseyeLibraryClasspathResolver getDSLClasspath() {
	if (dslResolver == null) {
	    dslResolver = new TigerseyeLibraryClasspathResolver();
	}
	return dslResolver;
    }

    public void setTigerseyeLibraryClasspathResolver(
	    TigerseyeLibraryClasspathResolver resolver) {
	this.dslResolver = resolver;
    }

    @Override
    public IClasspathEntry[] getClasspathEntries() {
	if (cpEntries.isEmpty())
	    cpEntries = createCPEntries();
	return this.cpEntries
		.toArray(new IClasspathEntry[this.cpEntries.size()]);
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
	    Set<IClasspathEntry> newEntryCandidates,
	    Set<IClasspathEntry> alreadyOnPath) {
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

}
