package de.tud.stg.popart.builder.eclipse;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.core.TigerseyeCore;

//FIXME refactoring and tests for logic
public class Builder extends IncrementalProjectBuilder {
	private static final Logger logger = LoggerFactory.getLogger(Builder.class);

	private final ResourceVisitor[] visitors = { new PopartResourceVisitor(),
			new GroovyResourceVisitor(), new JavaResourceVisitor() };

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

	if (kind == IncrementalProjectBuilder.CLEAN_BUILD) {
	    this.fullBuild(monitor);
	}

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
		    if (!(packRoot.getKind() == IPackageFragmentRoot.K_SOURCE))
			continue;
		    String path = packRoot.getPath().toString();
		    if (path.endsWith(TigerseyeCore.getOutputDirectoryPath())) {
			IJavaElement[] resource = packRoot.getChildren();
			for (IJavaElement iJavaElement : resource) {
			    IResource javaElRes = iJavaElement
				    .getCorrespondingResource();
			    if (javaElRes instanceof IFile) {
				javaElRes.delete(false, new SubProgressMonitor(
					monitor, 1));
			    }
			}

		    } else {
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
