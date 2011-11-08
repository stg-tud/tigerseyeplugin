package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.ProjectModificationUtilities;
import de.tud.stg.tigerseye.eclipse.core.StartupValidation;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;

/**
 * Provides static functions for configuration of Tigerseye nature Projects.
 * 
 * @see TigerseyeCoreConstants
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeRuntime {
    private static final Logger logger = LoggerFactory.getLogger(TigerseyeRuntime.class);

    /**
     * Performs in all Projects with Tigerseye Nature an update of Tigerseye
     * classpath containers according to current preferences.
     */
    public static void updateTigerseyeClassPaths() {
	StopWatch sw = new StopWatch();
	sw.start();
	ILanguageProvider updateLanguageProvider = TigerseyeCore.updateLanguageProvider();
	Map<DSLDefinition, Throwable> invalidDSLs = updateLanguageProvider
		.validateDSLDefinitionsStateReturnInvalidDSLs();
	if (invalidDSLs.size() > 0) {
	    StartupValidation.logDSLsNotloadable(invalidDSLs);
	}
	sw.split();
	logger.debug("{}ms took languageprovider update", sw.getSplitTime());

	List<IProject> tigerseyeWorkspaceProjects = ProjectModificationUtilities.getOpenTigerseyeProjects();

	IJavaProject[] ps = new IJavaProject[tigerseyeWorkspaceProjects.size()];
	for (int i = 0; i < tigerseyeWorkspaceProjects.size(); i++) {
	    ps[i] = JavaCore.create(tigerseyeWorkspaceProjects.get(i));
	}

	try {
	    ProjectModificationUtilities.updateTigerseyeClasspathContainerOnProjects(ps);
	} catch (Exception e) {
	    logger.error("Classpath update failed", e);
	}

	logger.debug("{} ms took complete classpath update", sw.getTime());
    }

}
