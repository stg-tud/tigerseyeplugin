package de.tud.stg.tigerseye.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.ProjectModificationUtilities;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntime;

public class UpdateTigerseyeClasspathHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory
	    .getLogger(UpdateTigerseyeClasspathHandler.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	TigerseyeRuntime.updateTigerseyeClassPaths();
	ProjectModificationUtilities.createTigerseyeSourceFoldersIfNotExisting();
	return null;
    }

}