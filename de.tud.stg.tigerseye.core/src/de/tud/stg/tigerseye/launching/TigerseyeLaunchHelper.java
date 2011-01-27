package de.tud.stg.tigerseye.launching;

import java.util.Map;

import org.codehaus.groovy.eclipse.launchers.GroovyScriptLaunchShortcut;
import org.eclipse.debug.core.DebugPlugin;
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

    @Override
    public Map<String, String> createLaunchProperties(IType runType,
	    IJavaProject javaProject) {

	return super.createLaunchProperties(runType, javaProject);
    }

    @Override
    public ILaunchConfigurationType getGroovyLaunchConfigType() {
	return DebugPlugin
		.getDefault()
		.getLaunchManager()
		.getLaunchConfigurationType(
			ITigerseyeLaunchConfigurationConstants.TIGERSEYE_LAUNCH_CONFIGURATION_TYPE);
    }

}
