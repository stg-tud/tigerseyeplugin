package de.tud.stg.tigerseye.eclipse.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang.time.StopWatch;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
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

//FIXME(Leo_Roos;Aug 25, 2011) and tests for logic
public class Builder extends IncrementalProjectBuilder {
    private static final Logger logger = LoggerFactory.getLogger(Builder.class);

    private final ResourceVisitor[] visitors = { new DSLResourceVisitor(),
	    new GroovyResourceVisitor(), new JavaResourceVisitor() };

    public Builder() {
	// Must be provided see Javadoc
    }

    @Override
    public void setInitializationData(IConfigurationElement config,
	    String propertyName, Object data) throws CoreException {
	super.setInitializationData(config, propertyName, data);
    }

    @Override
    protected void clean(IProgressMonitor monitor)
	    throws CoreException {
	if (monitor == null)
	    monitor = new NullProgressMonitor();
	try {
	    int totalWork = 1000;
	    int cleanwork = totalWork / 10;

	    monitor.beginTask("Cleaning " + getProject(), totalWork);
	    String outputDirectoryPath = TigerseyeRuntime
		    .getOutputDirectoryPath();
	    logger.debug("cleaning output directory {} for  {}",
		    outputDirectoryPath, getProject());

	    IJavaProject jp = JavaCore.create(getProject());
	    IPackageFragmentRoot[] packageFragmentRoots;
	    packageFragmentRoots = jp.getPackageFragmentRoots();
	    for (IPackageFragmentRoot packRoot : packageFragmentRoots) {
		checkCancelAndAct(monitor);
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
			checkCancelAndAct(monitor);
			resource.delete(false, new SubProgressMonitor(monitor,
				onedeletework));
		    }
		}

	    }
	    // this.fullBuild(new SubProgressMonitor(monitor, totalWork
	    // - cleanwork));
	} finally {
	    monitor.done();
	}
    }

    private void checkCancelAndAct(@Nonnull IProgressMonitor monitor) {
	if (monitor.isCanceled()) {
	    throw new OperationCanceledException();
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
	StopWatch sw = new StopWatch();
	sw.start();
	try {
	    monitor.beginTask("Start build", 100);
	    if (kind == IncrementalProjectBuilder.CLEAN_BUILD) {
		// never accessed?
		this.clean(new SubProgressMonitor(monitor, 10));
		this.fullBuild(new SubProgressMonitor(monitor, 90));
	    } else if (kind == IncrementalProjectBuilder.FULL_BUILD) {
		this.fullBuild(new SubProgressMonitor(monitor, 1));
	    } else if (kind == INCREMENTAL_BUILD || kind == AUTO_BUILD) {
		IResourceDelta delta = this.getDelta(this.getProject());
		if (delta == null) {
		    throw new IllegalStateException(
			    "Received Incremental or Auto kind: " + kind
				    + " but delta was null");
		} else {
		    this.incrementalBuild(delta, monitor);
		}
	    } else
		throw new IllegalArgumentException("Unexpected build kind "
			+ kind);
	} catch (CoreException e) {
	    logger.warn("Build failed because ", e);
	} finally {
	    monitor.done();
	}
	sw.stop();
	logger.info("{} ms took build process of kind {}", sw.getTime(),
		getStringKind(kind));
	return null;
    }

    private String getStringKind(int kind) {
	switch (kind) {
	case FULL_BUILD:
	    return format("FULL_BUILD", kind);
	case AUTO_BUILD:
	    return format("AUTO_BUILD", kind);
	case CLEAN_BUILD:
	    return format("CLEAN_BUILD", kind);
	case INCREMENTAL_BUILD:
	    return format("INCREMENTAL_BUILD", kind);
	default:
	    return format("Unknown", kind);
	}
    }

    private String format(String string, int kind) {
	return string + " kind[" + kind + "]";
    }

    private void incrementalBuild(@Nonnull IResourceDelta delta,
	    @Nonnull IProgressMonitor monitor)
	    throws CoreException {
	int totalWork = 10000;
	int work = totalWork / visitors.length;
	monitor.beginTask("Building", totalWork);
	// IProject project = this.getProject();
	if (delta != null) {
	    for (ResourceVisitor visitor : visitors) {
		checkCancelAndAct(monitor);
		monitor.subTask("Delta:" + delta.getFullPath()
			+ " with Visitor:" + visitor.getClass().getSimpleName());
		logger.trace("Starting build with visitor {}", visitor);
		delta.accept(visitor);
		monitor.worked(work);
	    }
	}
	monitor.done();
    }

    private void fullBuild(@Nonnull IProgressMonitor monitor)
	    throws JavaModelException {
	logger.debug("starting full build");
	try {
	    int totalWork = 10000;
	    monitor.beginTask("Building", totalWork);
	    IProject project = this.getProject();

	    IJavaProject jp = JavaCore.create(project);

	    IPackageFragmentRoot[] packageFragmentRoots = jp
		    .getPackageFragmentRoots();
	    List<IPackageFragmentRoot> sourcesToBuild = new ArrayList<IPackageFragmentRoot>();
	    for (IPackageFragmentRoot packRoot : packageFragmentRoots) {
		if (monitor.isCanceled())
		    return;
		if (!(packRoot.getKind() == IPackageFragmentRoot.K_SOURCE))
		    continue;
		if (!isTigerseyeOutputSourceDirectory(packRoot)) {
		    sourcesToBuild.add(packRoot);
		}
	    }

	    int sourceDirWorked = totalWork / (1 + sourcesToBuild.size());
	    for (IPackageFragmentRoot sourceDirRoot : sourcesToBuild) {
		buildResourcesInSourceDirectory(new SubProgressMonitor(monitor,
			sourceDirWorked), sourceDirRoot);
	    }
	} finally {
	    monitor.done();
	}
    }

    private void buildResourcesInSourceDirectory(
	    @Nonnull IProgressMonitor monitor,
	    IPackageFragmentRoot packRoot) throws JavaModelException {
	try {
	    Object[] nonJavaResources = packRoot.getNonJavaResources();
	    float totalWork = 1000;
	    monitor.beginTask(
		    "Building source directory " + packRoot.getElementName(),
		    (int) totalWork);
	    int oneResourceWork = (int) (totalWork / nonJavaResources.length);
	    int oneVisitorWork = oneResourceWork / visitors.length;
	    for (Object object : nonJavaResources) {
		IResource resource = (IResource) object;
		for (ResourceVisitor visitor : visitors) {
		    checkCancelAndAct(monitor);
		    if (visitor.isInteresstedIn(resource)) {
			monitor.subTask("Handler "
				+ visitor.getClass().getSimpleName()
				+ " on resource:" + object);
			ResourceHandler newResourceHandler = visitor
				.newResourceHandler();
			newResourceHandler.handleResource(resource);
		    }
		    monitor.worked(oneVisitorWork);
		}
	    }
	} finally {
	    monitor.done();
	}

    }
}
