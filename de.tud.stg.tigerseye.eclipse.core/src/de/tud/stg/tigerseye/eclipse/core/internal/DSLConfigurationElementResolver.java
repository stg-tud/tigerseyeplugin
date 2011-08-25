package de.tud.stg.tigerseye.eclipse.core.internal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeRuntimeException;
import de.tud.stg.tigerseye.eclipse.core.internal.WorkspaceDSLDefintionsResolver.WorkspaceDSL;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants;

/**
 * Resolves the DSL configurations from installed plug-ins and from workspace
 * plug-ins. Configurations are simply wrapped into a container of the same
 * supertype.
 * 
 * @author Leo Roos
 * 
 */
public class DSLConfigurationElementResolver {

    private static final Logger logger = LoggerFactory
	    .getLogger(DSLConfigurationElementResolver.class);

    public static Set<WorkspaceDSLConfigurationElement> getWorkspaceDSLConfigurationElements() {
	// Prefer workspace location plug-ins
	IPluginModelBase[] workspaceModels;
	try {
	    workspaceModels = PluginRegistry.getWorkspaceModels();
	} catch (NullPointerException e) {
	    /**
	     * Can happen inside AbstractBundleContainer
	     * 
	     * <pre>
	     * FrameworkAdmin fwAdmin = (FrameworkAdmin) PDECore.getDefault().acquireService(
	     * 	FrameworkAdmin.class.getName());
	     * if (fwAdmin == null) {
	     *     Bundle fwAdminBundle = Platform.getBundle(FWK_ADMIN_EQ);
	     *     fwAdminBundle.start();
	     *     fwAdmin = (FrameworkAdmin) PDECore.getDefault().acquireService(
	     * 	    FrameworkAdmin.class.getName());
	     * }
	     * Manipulator manipulator = fwAdmin.getManipulator();
	     * </pre>
	     * 
	     * where fwAdmin might still be null after the call to
	     * acquireService.
	     * <p>
	     * XXX(Leo Roos;Aug 24, 2011) What is the protocol to call that
	     * method? Works if all equinox plug-ins are enabled
	     */
	    throw new TigerseyeRuntimeException(
		    "Failed access the PluginRegistry. Some implicit dependencies seem to miss. In particular check the equinox plug-ins.",
		    e);
	}
	WorkspaceDSLDefintionsResolver wdslr = new WorkspaceDSLDefintionsResolver(
		workspaceModels);
	Set<WorkspaceDSLDefintionsResolver.WorkspaceDSL> wdsls = wdslr
		.getWorkspaceDSLDefintions();

	Set<WorkspaceDSLConfigurationElement> wdslConfs = new HashSet<WorkspaceDSLConfigurationElement>();
	for (WorkspaceDSL workspaceDSL : wdsls) {
	    wdslConfs.add(new WorkspaceDSLConfigurationElement(workspaceDSL));
	}
	return wdslConfs;
    }

    public static Set<PluginDSLConfigurationElement> getInstalledDSLConfigurationElements() {
	Set<PluginDSLConfigurationElement> installedPlugins = new HashSet<PluginDSLConfigurationElement>();
	IConfigurationElement[] configurationElementsFor = getConfigurationElementsOfDSLExtensionPoint();

	for (IConfigurationElement iConfigurationElement : configurationElementsFor) {
	    installedPlugins.add(new PluginDSLConfigurationElement(
		    iConfigurationElement));

	}

	return installedPlugins;
    }

    public static IConfigurationElement[] getConfigurationElementsOfDSLExtensionPoint() {
	IConfigurationElement[] configurationElementsFor = new IConfigurationElement[0];
	IExtensionRegistry registry = RegistryFactory.getRegistry();
	if (registry != null)
	    configurationElementsFor = registry
		    .getConfigurationElementsFor(TigerseyeCoreConstants.DSLDEFINITIONS_EXTENSION_POINT_ID);
	return configurationElementsFor;
    }

    private boolean isBundleInstallation(WorkspaceDSL workspaceDSL) {
	boolean isBundleInstallation = false;
	Bundle bundle = Platform.getBundle(workspaceDSL.dslLanguageDefinition
		.getPluginBase().getId());
	if (bundle != null) {
	    String installLocation = workspaceDSL.dslLanguageDefinition
		    .getPluginModel().getInstallLocation();
	    File installFile = new File(installLocation);
	    try {
		File bundleFile = FileLocator.getBundleFile(bundle);
		isBundleInstallation = bundleFile.equals(installFile);
	    } catch (IOException e) {
		logger.debug(
			"Failed to determine whether bundle {} is plug-in or workspace project of a teststarting Eclipse Instance. Will assume pessimistic that it is not.",
			bundle);
		isBundleInstallation = true;
	    }
	}
	return isBundleInstallation;
    }

    /**
     * Tries to determine whether bundle is loaded from a workspace project.
     * 
     * @param bundle
     * @return <code>true</code> if bundle is probably a workspace project,
     *         <code>false</code> otherwise.
     */
    public static boolean isBundleWorkspaceProject(@Nonnull Bundle bundle) {
	File bundleFile;
	try {
	    bundleFile = FileLocator.getBundleFile(bundle).getAbsoluteFile();
	} catch (IOException e) {
	    logger.warn(
		    "Failed to locate bundle file for {} will assume is installed plugin",
		    bundle, e);
	    return false;
	}
	if (!bundleFile.isDirectory())
	    return false;
	File platformfile = getPlatformFileOrNull();
	if (platformfile == null)
	    return false;

	String platformPath = platformfile.getPath();
	String installPath = bundleFile.getPath();
	if (installPath.contains(platformPath)) {
	    return false;
	}

	// some additional heuristics
	boolean containsAll = containsEntries(bundle, ".classpath",
		"build.properties");
	// perhaps some more ...
	return containsAll;
    }

    private static boolean containsEntries(Bundle bundle, String... entries) {
	for (String entry : entries) {
	    URL cpEntry = bundle.getEntry(entry);
	    if (cpEntry == null)
		return false;
	}
	return true;
    }

    private static File getPlatformFileOrNull() {
	Location installLocation = Platform.getInstallLocation();
	if (installLocation == null)
	    return null;
	URL url = installLocation.getURL();
	try {
	    File platformfile = new File(url.toURI()).getAbsoluteFile();
	    return platformfile;
	} catch (URISyntaxException e) {
	    logger.warn(
		    "Failed to determine URI will assume pessimistic and handle bundle as installed plugin.",
		    e);
	    return null;
	}
    }

}
