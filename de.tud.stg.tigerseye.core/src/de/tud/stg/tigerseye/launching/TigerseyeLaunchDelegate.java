package de.tud.stg.tigerseye.launching;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.transformers.FileType;

/**
 * {@link TigerseyeLaunchDelegate} launches the transformed DSL files. It
 * adjusts launch configurations before it delegates to the
 * {@link JavaLaunchDelegate} class to support java files and groovy scripts
 * which need distinct configurations.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeLaunchDelegate extends JavaLaunchDelegate implements
	ILaunchConfigurationDelegate, ITigerseyeLaunchConfigurationConstants {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeLaunchDelegate.class);
    @Override
    public void launch(ILaunchConfiguration configuration, String mode,
	    ILaunch launch, IProgressMonitor monitor) throws CoreException {
	IJavaProject javaProject = getJavaProject(configuration);
	String dslMainType = configuration.getAttribute(
		IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
		(String) null);
	/*
	 * Check the type to apply distinct configurations for groovy or java
	 * files.
	 */
	IType foundType = javaProject.findType(dslMainType);
	IResource resource = foundType.getResource();
	/*
	 * The IType _foundType_ may not be a project resource (e.g. the
	 * GroovyStartet is used) in which case the passed launch configuration
	 * will be taken.
	 */
	FileType type = null;
	if (resource != null && resource.exists()) {
	    type = FileType.getTypeForOutputResource(resource
		.getFullPath().toString());
	}
	/*
	 * If _foundType_ did not originate from a java file override default
	 * Java settings with Groovy configurations. This is necessary to
	 * support groovy script launches which needs a special starter class in
	 * some cases and an extended classpath.
	 */
	if (!FileType.JAVA.equals(type)) {
	    /*
	     * If the configuration already considers groovy don't change it so
	     * that it can be adjusted using the Run Configurations Dialog. If
	     * not set the default is used.
	     */
	    boolean defaultLaunch = configuration.getAttribute(
		    ATTR_IS_DEFAULT_GROOVY_LAUNCH, true);
	    if (defaultLaunch) {
		ILaunchConfigurationWorkingCopy workingCopy = configuration
			.getWorkingCopy();
		Map<String, String> createLaunchProperties = new TigerseyeLaunchHelper()
			.createLaunchProperties(foundType, javaProject);
		Set<Entry<String, String>> entrySet = createLaunchProperties
			.entrySet();
		for (Entry<String, String> entry : entrySet) {
		    workingCopy.setAttribute(entry.getKey(), entry.getValue());
		}
		/*
		 * After everything a DSL needs to be started is set reconfigure
		 * the main type. Although the groovy script configuration is
		 * needed the actual started main type stays the transformed
		 * DSL.
		 */
		workingCopy.setAttribute(
			IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
			dslMainType);
		configuration = workingCopy.doSave();
	    }
	}
	if (logger.isDebugEnabled()) {
	    logCompleteConfiguration(configuration);
	}
	super.launch(configuration, mode, launch, monitor);
    }

    private void logCompleteConfiguration(ILaunchConfiguration configuration)
 {
	@SuppressWarnings("rawtypes")
	Map attributes;
	try {
	    attributes = configuration.getAttributes();
	} catch (CoreException e) {
	    logger.warn("Failed to log configuration {}", configuration);
	    return;
	}
	@SuppressWarnings("rawtypes")
	Set keySet = attributes.keySet();
	StringBuilder sb = new StringBuilder(
	    "Final launch configuration before delegating to Java:\n");
	for (Object key : keySet) {
	sb.append(key).append("\t").append(attributes.get(key))
		.append("\n");
	}
	logger.debug(sb.toString());
    }

}
