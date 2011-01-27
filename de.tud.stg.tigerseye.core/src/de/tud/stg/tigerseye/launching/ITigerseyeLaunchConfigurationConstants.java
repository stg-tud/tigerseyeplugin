package de.tud.stg.tigerseye.launching;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Constants used by different tigerseye classes to prepare the launch for a
 * tigerseye file.
 * 
 * @author Leo Roos
 * 
 * @see TigerseyeLaunchDelegate
 */
public interface ITigerseyeLaunchConfigurationConstants {

    /**
     * The launch configuration type for Tigerseye
     * 
     * @see ILaunchManager#getLaunchConfigurationType(String)
     */
    public static final String TIGERSEYE_LAUNCH_CONFIGURATION_TYPE = "de.tud.stg.tigerseye.eclipse.core.tigerseyeLaunchConfigurationType";
    /**
     * Indicating that the launch configuration has no adjustments from the
     * user. Therefore the configuration for groovy types will be set with the
     * default groovy launch configuration
     * 
     * @see TigerseyeLaunchDelegate
     */
    public static final String ATTR_IS_DEFAULT_GROOVY_LAUNCH = "de.tud.stg.tigerseye.launch.attribute.isDefaultGroovyLaunchConfiguration";
    /**
     * The transformed DSLs can be groovy scripts which might only be runnable
     * when started with the GroovyStarter. This is the fully qualified name of
     * the according type which can be resolved via
     * {@link IJavaProject#findType(String)}. Of course the class will have to
     * be available on the project's classpath in order to successfully resolve
     * it.
     */
    public static final String GROOVY_STARTER_TYPE_ID = "org.codehaus.groovy.tools.GroovyStarter";

}
