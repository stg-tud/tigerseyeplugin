package de.tud.stg.tigerseye.eclipse.core.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import org.apache.commons.lang.time.StopWatch;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.Assert;
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

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.DSLResourceHandler;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.DSLResourceVisitor;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.GroovyResourceVisitor;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.JavaResourceVisitor;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileTypeHelper;

//FIXME(Leo_Roos;Aug 25, 2011) and tests for logic
public class Builder extends IncrementalProjectBuilder {
    private static final Logger logger = LoggerFactory.getLogger(Builder.class);

    private final DSLResourceHandler[] visitors = { new DSLResourceVisitor(), new GroovyResourceVisitor(),
	    new JavaResourceVisitor() };

    private final AtomicBoolean mustRecomputeSourcesForFullBuild = new AtomicBoolean();

    private @Nonnull
    List<IFile> actualResourcesToBuildCache = Collections.emptyList();

    enum BuildKind {
	FULL_BUILD(IncrementalProjectBuilder.FULL_BUILD), //
	AUTO_BUILD(IncrementalProjectBuilder.AUTO_BUILD), //
	CLEAN_BUILD(IncrementalProjectBuilder.CLEAN_BUILD), //
	INCREMENTAL_BUILD(IncrementalProjectBuilder.INCREMENTAL_BUILD), //
	;
	public final int kind;

	private BuildKind(int kind) {
	    this.kind = kind;
	}

	public static BuildKind parseBuildKind(int kind) {
	    BuildKind[] values = BuildKind.values();
	    for (BuildKind v : values) {
		if (v.kind == kind)
		    return v;
	    }
	    throw new IllegalArgumentException("Received unknown build kind: " + kind);
	}

	@Override
	public String toString() {
	    return this.name() + "[" + kind + "]";
	}

    }

    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
	    throws CoreException {
	super.setInitializationData(config, propertyName, data);
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
	StopWatch sw = startedStopWatch();
	if (monitor == null)
	    monitor = new NullProgressMonitor();
	try {
	    int totalWork = 1000;
	    int cleanwork = totalWork / 10;

	    monitor.beginTask("Cleaning " + getProject(), totalWork);
	    String outputDirectoryPath = TigerseyeCore.getOutputDirectoryPath();
	    logger.debug("cleaning output directory {} for  {}", outputDirectoryPath, getProject());

	    IJavaProject jp = JavaCore.create(getProject());
	    IPackageFragmentRoot[] packageFragmentRoots;
	    packageFragmentRoots = jp.getPackageFragmentRoots();
	    for (IPackageFragmentRoot packRoot : packageFragmentRoots) {
		checkCancelAndAct(monitor);
		if (!(packRoot.getKind() == IPackageFragmentRoot.K_SOURCE))
		    continue;

		if (isTigerseyeOutputSourceDirectory(packRoot)) {
		    IPath projectRelativePath = packRoot.getResource().getProjectRelativePath();
		    IFolder outputsrcfolder = getProject().getFolder(projectRelativePath);
		    int includeAll = IContainer.INCLUDE_HIDDEN | IContainer.INCLUDE_PHANTOMS
			    | IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS;
		    IResource[] members = outputsrcfolder.members(includeAll);
		    int onedeletework = (int) ((float) cleanwork / members.length);
		    for (IResource resource : members) {
			checkCancelAndAct(monitor);
			resource.delete(false, new SubProgressMonitor(monitor, onedeletework));
		    }
		}

	    }
	    // this.fullBuild(new SubProgressMonitor(monitor, totalWork
	    // - cleanwork));
	} finally {
	    monitor.done();
	}
	logger.debug("{} ms cleaning output directory", sw.getTime());
    }

    private void checkCancelAndAct(@Nonnull IProgressMonitor monitor) {
	if (monitor.isCanceled() || isInterrupted()) {
	    throw new OperationCanceledException();
	}
    }

    private boolean isTigerseyeOutputSourceDirectory(IJavaElement packRoot) {
	IFolder tigerseyeoutputfolder = getProject().getFolder(TigerseyeCore.getOutputDirectoryPath());
	IPath projectRelativePath = tigerseyeoutputfolder.getFullPath();
	IPath packRootPath = packRoot.getPath();
	boolean isTigerseyeOutputSourceDirectory = packRootPath.equals(projectRelativePath);
	return isTigerseyeOutputSourceDirectory;
    }

    @Override
    protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor) {
	if (monitor == null) {
	    monitor = new NullProgressMonitor();
	}
	this.mustRecomputeSourcesForFullBuild.compareAndSet(false, true);
	BuildKind buildKind = BuildKind.parseBuildKind(kind);
	StopWatch sw = startedStopWatch();
	try {
	    buildSources(buildKind, monitor);
	} catch (CoreException e) {
	    logger.warn("Build failed because ", e);
	} finally {
	    monitor.done();
	}
	sw.stop();
	logger.info("{} ms took build process of kind {} for project {}",
		new Object[] { sw.getTime(), buildKind.toString(), getProject() });
	return null;
    }

    public StopWatch startedStopWatch() {
	StopWatch sw = new StopWatch();
	sw.start();
	return sw;
    }

    private void buildSources(BuildKind buildKind, IProgressMonitor monitor) throws CoreException {
	switch (buildKind) {
	case INCREMENTAL_BUILD:
	    //$FALL-THROUGH$
	case AUTO_BUILD:
	    IResourceDelta delta = getAssertedDelta();
	    this.incrementalBuild(delta, monitor);
	    break;
	case CLEAN_BUILD:
	    monitor.beginTask("Start build", 100);
	    this.clean(new SubProgressMonitor(monitor, 10));
	    this.fullBuild(new SubProgressMonitor(monitor, 90));
	    break;
	case FULL_BUILD:
	    this.fullBuild(monitor);
	    break;
	default:
	    throw new IllegalArgumentException("Forgot to add enum to switch?");
	}
    }

    public @Nonnull
    IResourceDelta getAssertedDelta() {
	IResourceDelta delta = this.getDelta(this.getProject());
	Assert.isNotNull(delta);
	return delta;
    }

    private void incrementalBuild(@Nonnull IResourceDelta delta, @Nonnull IProgressMonitor monitor)
	    throws CoreException {
	int totalWork = 10000;
	int work = totalWork / visitors.length;
	monitor.beginTask("Building", totalWork);
	// TODO(Leo_Roos;Nov 10, 2011) Usually the delta will be the root
	// directory containing all further deltas recursively. So I have to add
	// filter capabilities to this incremental as well similar to the full
	// build approach
	if (delta != null) {
	    for (DSLResourceHandler visitor : visitors) {
		checkCancelAndAct(monitor);
		monitor.subTask("Building " + delta.getFullPath()
		/* + " with Visitor:" + visitor.getClass().getSimpleName() */);
		logger.trace("Starting build with visitor {}", visitor);

		delta.accept(visitor);

		monitor.worked(work);
	    }
	}
	monitor.done();
    }

    private void fullBuild(@Nonnull IProgressMonitor monitor) throws JavaModelException {
	logger.debug("starting full build");
	try {
	    int totalWork = 10000;
	    monitor.beginTask("Building", totalWork);
	    List<IFile> actualResourcesToBuild = getSourcesForFullBuild();

	    int sourceDirWorked = totalWork / (1 + actualResourcesToBuild.size());
	    buildResourcesInSourceDirectory(new SubProgressMonitor(monitor, sourceDirWorked), actualResourcesToBuild);

	} finally {
	    monitor.done();
	}
    }

    private List<IFile> getSourcesForFullBuild() {
	if (mustRecomputeSourcesForFullBuild.get()) {
	    List<IFile> actualResourcesToBuild = Collections.emptyList();
	    try {
		actualResourcesToBuild = recomputeSourceForFullBuild();
	    } catch (JavaModelException e) {
		logger.error("Failed to determine which sources to build.", e);
	    } catch (CoreException e) {
		logger.error("Failed to determine which sources to build.", e);
	    }
	    actualResourcesToBuildCache = actualResourcesToBuild;
	    mustRecomputeSourcesForFullBuild.set(false);
	}
	return actualResourcesToBuildCache;
    }

    public ArrayList<IFile> recomputeSourceForFullBuild() throws JavaModelException, CoreException {
	IProject project = this.getProject();
	IJavaProject jp = JavaCore.create(project);
	IPackageFragmentRoot[] packageFragmentRoots = jp.getPackageFragmentRoots();
	List<IPackageFragmentRoot> sourcesToBuild = new ArrayList<IPackageFragmentRoot>();
	for (IPackageFragmentRoot packRoot : packageFragmentRoots) {
	    if (!(packRoot.getKind() == IPackageFragmentRoot.K_SOURCE))
		continue;
	    if (!isTigerseyeOutputSourceDirectory(packRoot)) {
		sourcesToBuild.add(packRoot);
	    }
	}

	final ArrayList<IFile> teSrcFiles = new ArrayList<IFile>();
	for (IPackageFragmentRoot sourceDirRoot : sourcesToBuild) {
	    IResource resource = sourceDirRoot.getResource();
	    resource.accept(new IResourceVisitor() {

		@Override
		public boolean visit(IResource resource) throws CoreException {
		    int type = resource.getType();
		    if (type == IResource.FILE) {
			IFile file = (IFile) resource;
			FileType typeForSrcResource = FileTypeHelper.getTypeForSrcResource(file.getName());
			if (typeForSrcResource != null) {
			    logger.debug("queuing file for build: {}", file);
			    teSrcFiles.add(file);
			}
			return false;
		    }
		    return true;
		}
	    });
	}
	
	return teSrcFiles;
    }

    private void buildResourcesInSourceDirectory(@Nonnull IProgressMonitor monitor, List<IFile> nonJavaResources)
    /* throws JavaModelException */{
	try {
	    int totalWork = Integer.MAX_VALUE;
	    monitor.beginTask("Building", totalWork);
	    if (nonJavaResources.isEmpty()) {
		return;
	    }
	    int oneResourceWork = (totalWork / nonJavaResources.size());
	    for (IResource resource : nonJavaResources) {
		checkCancelAndAct(monitor);
		monitor.subTask("Building: " + resource.getName());
		feedVisitorsHandleExceptions(monitor, resource);
		monitor.worked(oneResourceWork);
	    }
	} finally {
	    monitor.done();
	}

    }

    private void feedVisitorsHandleExceptions(IProgressMonitor monitor, IResource resource) {
	for (DSLResourceHandler visitor : visitors) {
	    try {
		if (visitor.isInteresstedIn(resource)) {
		    resource.accept(visitor);
//		     visitor.handleResource(resource);
		}
	    } catch (Exception e) {
		logBuildError(resource, visitor, e);
	    }
	}
    }

    private void logBuildError(IResource resource, DSLResourceHandler visitor, Exception e) {
	logger.error("Build failed for:" + resource + "witch Visitor: " + visitor, e);
    }

}
