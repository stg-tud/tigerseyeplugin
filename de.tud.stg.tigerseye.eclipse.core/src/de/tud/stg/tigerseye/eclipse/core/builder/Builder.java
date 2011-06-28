package de.tud.stg.tigerseye.eclipse.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.DSLResourceVisitor;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.GroovyResourceVisitor;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.JavaResourceVisitor;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.ResourceHandler;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.ResourceVisitor;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntime;

//FIXME refactoring and tests for logic
public class Builder extends IncrementalProjectBuilder {
    private static final Logger logger = LoggerFactory.getLogger(Builder.class);

    private final ResourceVisitor[] visitors = { new DSLResourceVisitor(),
	    new GroovyResourceVisitor(), new JavaResourceVisitor() };

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
	if (monitor == null)
	    monitor = new NullProgressMonitor();
	try {
	    int totalWork = 1000;
	    int cleanwork = totalWork / 10;
	    int fullBuildWork = totalWork - cleanwork;

	    monitor.beginTask("Cleaning " + getProject(), totalWork);
	    String outputDirectoryPath = TigerseyeRuntime
		    .getOutputDirectoryPath();
	    logger.trace("cleaning output directory {} for  {}",
		    outputDirectoryPath, getProject());

	    IJavaProject jp = JavaCore.create(getProject());
	    IPackageFragmentRoot[] packageFragmentRoots;
	    packageFragmentRoots = jp.getPackageFragmentRoots();
	    for (IPackageFragmentRoot packRoot : packageFragmentRoots) {
		if (!(packRoot.getKind() == IPackageFragmentRoot.K_SOURCE))
		    continue;

		if (isTigerseyeOutputSourceDirectory(packRoot)) {
		    IPath projectRelativePath = packRoot.getResource()
			    .getProjectRelativePath();
		    IFolder outputsrcfolder = getProject().getFolder(
			    projectRelativePath);
		    int includeAll = IContainer.INCLUDE_HIDDEN
			    | IContainer.INCLUDE_PHANTOMS
			    | IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS;
		    IResource[] members = outputsrcfolder.members(includeAll);
		    int onedeletework = (int) ((float) cleanwork / members.length);
		    for (IResource resource : members) {
			if (monitor.isCanceled())
			    return;
			resource.delete(false, new SubProgressMonitor(monitor,
				onedeletework));
		    }
		}

	    }
	    this.fullBuild(new SubProgressMonitor(monitor, fullBuildWork));
	} finally {
	    monitor.done();
	}

    }

    private boolean isTigerseyeOutputSourceDirectory(IJavaElement packRoot) {
	IFolder tigerseyeoutputfolder = getProject().getFolder(
		TigerseyeRuntime.getOutputDirectoryPath());
	IPath projectRelativePath = tigerseyeoutputfolder.getFullPath();
	IPath packRootPath = packRoot.getPath();
	boolean isTigerseyeOutputSourceDirectory = packRootPath
		.equals(projectRelativePath);
	return isTigerseyeOutputSourceDirectory;
    }

    @Override
    protected IProject[] build(int kind,
	    @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor) {
	if (monitor == null) {
	    monitor = new NullProgressMonitor();
	}
	try {
	    monitor.beginTask("Tigerseye Build", 100);
	    if (kind == IncrementalProjectBuilder.CLEAN_BUILD) {
		this.fullBuild(monitor);
	    } else if (kind == IncrementalProjectBuilder.FULL_BUILD) {
		this.fullBuild(monitor);
	    } else {
		IResourceDelta delta = this.getDelta(this.getProject());
		if (delta == null) {
		    this.fullBuild(monitor);
		} else {
		    this.incrementalBuild(delta, monitor);
		}
	    }
	} finally {
	    monitor.done();
	}
	return null;
    }

    private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
	this.fullBuild(monitor);
    }

    private void fullBuild(IProgressMonitor monitor) {
	if (monitor == null)
	    monitor = new NullProgressMonitor();
	logger.info("starting full build");
	try {
	    int totalWork = 10000;
	    monitor.beginTask("Building", totalWork);
	    IProject project = this.getProject();
	    IResourceDelta delta = this.getDelta(project);

	    IJavaProject jp = JavaCore.create(project);

	    if (delta != null) {
		for (ResourceVisitor visitor : visitors) {
		    monitor.subTask("Delta:" + delta.getResource()
			    + " with Visitor:"
			    + visitor.getClass().getSimpleName());
		    logger.info("Starting build with visitor {}", visitor);
		    delta.accept(visitor);
		    monitor.worked(totalWork / visitors.length);
		}
	    } else {
		IPackageFragmentRoot[] packageFragmentRoots = jp
			.getPackageFragmentRoots();
		List<IPackageFragmentRoot> sourcesToBuild = new ArrayList<IPackageFragmentRoot>();
		for (IPackageFragmentRoot packRoot : packageFragmentRoots) {
		    if (!(packRoot.getKind() == IPackageFragmentRoot.K_SOURCE))
			continue;
		    if (!isTigerseyeOutputSourceDirectory(packRoot)) {
			sourcesToBuild.add(packRoot);
		    }
		}

		int sourceDirWorked = totalWork / sourcesToBuild.size();
		for (IPackageFragmentRoot sourceDirRoot : sourcesToBuild) {
		    buildResourcesInSourceDirectory(new SubProgressMonitor(
			    monitor, sourceDirWorked), sourceDirRoot);
		}
	    }
	} catch (CoreException e) {
	    logger.warn("Build failed", e);
	} finally {
	    monitor.done();
	}
    }

    private void buildResourcesInSourceDirectory(IProgressMonitor monitor,
	    IPackageFragmentRoot packRoot) throws JavaModelException {
	try {
	    Object[] nonJavaResources = packRoot.getNonJavaResources();
	    float totalWork = 1000;
	    monitor.beginTask(
		    "Building source directory " + packRoot.getElementName(),
		    (int) totalWork);
	    int oneResourceWork = (int) (totalWork / nonJavaResources.length);
	    for (Object object : nonJavaResources) {
		IResource resource = (IResource) object;
		for (ResourceVisitor visitor : visitors) {
		    if (visitor.isInteresstedIn(resource)) {
			monitor.subTask("resource:" + object + " with Visitor:"
				+ visitor.getClass().getSimpleName());
			ResourceHandler newResourceHandler = visitor
				.newResourceHandler();
			newResourceHandler.handleResource(resource);
		    }
		    // monitor.worked(1);
		}
		monitor.worked(oneResourceWork);
	    }
	} finally {
	    monitor.done();
	}

    }
}
