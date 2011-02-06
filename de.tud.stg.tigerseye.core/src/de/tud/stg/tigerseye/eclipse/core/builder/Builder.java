package de.tud.stg.tigerseye.eclipse.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
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
    protected IProject[] build(int kind,
	    @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor) {
	if (monitor == null) {
	    monitor = new NullProgressMonitor();
	}
	try {
	    if (kind == IncrementalProjectBuilder.CLEAN_BUILD) {
		IJavaProject jp = JavaCore.create(getProject());
		IPackageFragmentRoot[] packageFragmentRoots;
		packageFragmentRoots = jp.getPackageFragmentRoots();
		for (IPackageFragmentRoot packRoot : packageFragmentRoots) {
		    if (!(packRoot.getKind() == IPackageFragmentRoot.K_SOURCE))
			continue;
		    String path = packRoot.getPath().toString();
		    if (path.endsWith(TigerseyeRuntime.getOutputDirectoryPath())) {
			IJavaElement[] resource = packRoot.getChildren();
			for (IJavaElement iJavaElement : resource) {

			    if (iJavaElement instanceof IPackageFragment) {
				ICompilationUnit[] compilationUnits = ((IPackageFragment) iJavaElement)
					.getCompilationUnits();
				for (ICompilationUnit iCompilationUnit : compilationUnits) {
				    try {
					iCompilationUnit.delete(false,
						new SubProgressMonitor(monitor,
							1));
				    } catch (JavaModelException e) {
					logger.warn(
						"Could not delete {} during full build",
						iCompilationUnit);
				    }
				}
			    }
			}
		    }
		    this.fullBuild(monitor);
		}
	    } else

	    if (kind == IncrementalProjectBuilder.FULL_BUILD) {
		this.fullBuild(monitor);
	    } else {
		IResourceDelta delta = this.getDelta(this.getProject());
		if (delta == null) {
		    this.fullBuild(monitor);
		} else {
		    this.incrementalBuild(delta, monitor);
		}

	    }
	} catch (JavaModelException e) {

	}
	return null;
    }

    private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
	this.fullBuild(monitor);
    }

    private void fullBuild(IProgressMonitor monitor) {

	logger.info("starting full build");
	try {
	    IProject project = this.getProject();
	    IResourceDelta delta = this.getDelta(project);

	    IJavaProject jp = JavaCore.create(project);

	    if (delta != null) {
		for (ResourceVisitor visitor : visitors) {
		    logger.info("Starting build with visitor {}", visitor);
		    delta.accept(visitor);
		}
	    } else {

		IPackageFragmentRoot[] packageFragmentRoots = jp
			.getPackageFragmentRoots();
		for (IPackageFragmentRoot packRoot : packageFragmentRoots) {
		    String path = packRoot.getPath().toString();

		    if (!(packRoot.getKind() == IPackageFragmentRoot.K_SOURCE))
			continue;

		    if (!path.endsWith(TigerseyeRuntime.getOutputDirectoryPath())) {
			Object[] nonJavaResources = packRoot
				.getNonJavaResources();
			for (Object object : nonJavaResources) {
			    IResource resource = (IResource) object;
			    for (ResourceVisitor visitor : visitors) {
				if (visitor.isInteresstedIn(resource)) {
				    ResourceHandler newResourceHandler = visitor
					    .newResourceHandler();
				    newResourceHandler.handleResource(resource);
				}
			    }
			}
		    }
		}

	    }
	} catch (CoreException e) {
	    logger.warn("Build failed", e);
	}
    }
}
