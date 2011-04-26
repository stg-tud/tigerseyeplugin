package de.tud.stg.tigerseye.eclipse.core.launching;

import java.util.Map;

import org.codehaus.groovy.eclipse.launchers.GroovyScriptLaunchShortcut;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;

/**
 * This class provides access to protected methods of GroovyScriptLaunchShortcut
 * to avoid copying the actual source. The information is used to create default
 * groovy script launch configurations.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeLaunchHelper extends GroovyScriptLaunchShortcut {

    /**
     * Main purpose is to be usable by {@link TigerseyeLaunchDelegate} to create
     * a default groovy launch configuration.
     */
    @Override
    public Map<String, String> createLaunchProperties(IType runType,
	    IJavaProject javaProject) {

	return super.createLaunchProperties(runType, javaProject);
    }

    /**
     * Used in the process of
     * {@link GroovyScriptLaunchShortcut#createLaunchConfig(Map, String)}, so
     * has to be adjusted to work for Tigerseye launch configuration type.
     */
    @Override
    public ILaunchConfigurationType getGroovyLaunchConfigType() {
	return getLaunchManager()
		.getLaunchConfigurationType(
			ITigerseyeLaunchConfigurationConstants.TIGERSEYE_LAUNCH_CONFIGURATION_TYPE);
    }

}
