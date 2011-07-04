package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

import de.tud.stg.tigerseye.eclipse.TigerseyeLibraryProvider;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeRuntimeException;

public class TigerseyeClasspathContainer implements IClasspathContainer {


    private Set<IClasspathEntry> cpEntries = new HashSet<IClasspathEntry>();

    private final IProject project;

    public static final Path CONTAINER_ID = new Path("TIGERSEYE_SUPPORT");


    public TigerseyeClasspathContainer(IProject project) {
	this.project = project;
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
	 * languages that one only needs the runtime support. Found no working
	 * solution hitherto, therefore I just add DSL definition libraries to
	 * the main runtime Tigerseye class path container.
	 */
	IClasspathEntry[] classpathEntries = new TigerseyeDSLDefinitionsCPContainer(project).getClasspathEntries();
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

}
