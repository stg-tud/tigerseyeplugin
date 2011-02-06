/**
 *
 */
package de.tud.stg.popart.builder.eclipse;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ResourceVisitor implements IResourceDeltaVisitor {

	private static final Logger logger = LoggerFactory
		.getLogger(ResourceVisitor.class);
	@Override
	public boolean visit(IResourceDelta delta) {

	// FIXME add default functionality when resource is added or removed

		try {
			if (this.isInteresstedIn(delta.getResource())) {
				this.newResourceHandler().handleResource(delta.getResource());
			}
			return true;
		} catch (Exception e) {
			logger.error("failed vissiting delta {}", delta, e);
		}
		return false;
	}

	protected abstract boolean isInteresstedIn(IResource resource);

	protected abstract ResourceHandler newResourceHandler();
}