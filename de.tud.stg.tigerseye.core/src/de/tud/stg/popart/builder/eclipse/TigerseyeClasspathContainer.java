package de.tud.stg.popart.builder.eclipse;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

import de.tud.stg.tigerseye.TigerseyeLibraryProvider;

public class TigerseyeClasspathContainer implements IClasspathContainer {

    private final Set<IClasspathEntry> cpEntries = new HashSet<IClasspathEntry>();

    private final IProject project;

    public static final Path CONTAINER_ID = new Path("TIGERSEYE_SUPPORT");


    public TigerseyeClasspathContainer(IProject project) {
	this.project = project;
	reset();
    }

    public void reset() {
	cpEntries.clear();
	File allRuntimeJars = TigerseyeLibraryProvider
		.getTigerseyeRuntimeLibraryFolder();
	File[] runtimeJars = allRuntimeJars.listFiles(new FilenameFilter() {
	    @Override
	    public boolean accept(File parentFile, String fileName) {
		return fileName.endsWith(".jar");
	    }
	});
	for (File jar : runtimeJars) {
	    Path path = new Path(jar.getAbsolutePath());
	    IClasspathEntry entry2 = JavaCore.newLibraryEntry(path, null, null);
	    cpEntries.add(entry2);
	}
	IClasspathEntry[] classpathEntries = new TigerseyeDSLDefinitionsCPContainer(project).getClasspathEntries();
	Collections.addAll(cpEntries, classpathEntries);
    }

    @Override
    public IClasspathEntry[] getClasspathEntries() {
	return this.cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
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
