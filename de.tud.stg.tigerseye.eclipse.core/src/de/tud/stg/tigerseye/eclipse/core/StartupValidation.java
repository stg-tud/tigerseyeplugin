package de.tud.stg.tigerseye.eclipse.core;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.internal.DSLActivationState;
import de.tud.stg.tigerseye.eclipse.core.internal.DSLConfigurationElementResolver;
import de.tud.stg.tigerseye.eclipse.core.internal.LanguageProviderFactory;
import de.tud.stg.tigerseye.eclipse.core.internal.PluginDSLConfigurationElement;
import de.tud.stg.tigerseye.eclipse.core.runtime.ProjectLinker;

/**
 * This class performs validations which may only be accessed when the Platform
 * is running.
 * 
 * @author Leo_Roos
 * 
 */
public class StartupValidation {

    private static final Logger logger = LoggerFactory.getLogger(StartupValidation.class);

    static void scheduleConsistencyCheck(final IPreferenceStore preferences) {
        /*
         * Is a Job so that this action is executed only after everything it
         * depends on has been loaded.
         */
        Job consitencycheck = new Job(TigerseyeCoreActivator.PLUGIN_ID + " startup check") {
    
        @Override
        protected IStatus run(IProgressMonitor monitor) {
    
		linkActiveDSLProjectsIntoWorkspace(preferences);
		ILanguageProvider provider = new LanguageProviderFactory().createLanguageProvider(preferences);
            Map<DSLDefinition, Throwable> validateDSLDefinitionsState = provider
        	    .validateDSLDefinitionsStateReturnInvalidDSLs();
            if (validateDSLDefinitionsState.size() > 0) {
        	logDSLsNotloadable(validateDSLDefinitionsState);
            }
    
    
            return Status.OK_STATUS;
        }
        };
        consitencycheck.schedule();
    }

    public static void logDSLsNotloadable(Map<DSLDefinition, Throwable> validateDSLDefinitionsState) {
	final Shell shell = getWorkbenchWindowOrNull();
        ArrayList<IStatus> stati = new ArrayList<IStatus>();
        for (Entry<DSLDefinition, Throwable> dslDefinition : validateDSLDefinitionsState.entrySet()) {
            Status status = new Status(IStatus.WARNING, TigerseyeCoreActivator.PLUGIN_ID, dslDefinition.getKey().toString()
        	    + " not loadable because: " + dslDefinition.getValue(), dslDefinition.getValue());
            stati.add(status);
        }
        final MultiStatus status = new MultiStatus(TigerseyeCoreActivator.PLUGIN_ID, IStatus.WARNING,
        	stati.toArray(new IStatus[stati.size()]),
        	"See Details and Error log (To see errors in log you might have to configure it).", null);
    
        Display.getDefault().asyncExec(new Runnable() {
    
            @Override
            public void run() {
        	ErrorDialog.openError(shell, "Tigerseye Error", "Not all DSLs could be loaded", status);
            }
        });
    
	logger.error("Following DSLs not loadable {}", validateDSLDefinitionsState.entrySet());
    }

    private static Shell getWorkbenchWindowOrNull() {
	boolean workbenchRunning = PlatformUI.isWorkbenchRunning();
	if (workbenchRunning) {
	    IWorkbench workbench = PlatformUI.getWorkbench();
	    IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
	    if (activeWorkbenchWindow != null)
		return activeWorkbenchWindow.getShell();
	}
	return null;
    }

    // FIXME(Leo_Roos;Aug 25, 2011) should be tested
    private static void linkActiveDSLProjectsIntoWorkspace(IPreferenceStore preferences) {
	Set<PluginDSLConfigurationElement> installedDSLConfigurationElements = DSLConfigurationElementResolver
		.getInstalledDSLConfigurationElements();
	DSLActivationState activationState = new DSLActivationState(preferences);

	for (PluginDSLConfigurationElement confEl : installedDSLConfigurationElements) {
	    boolean active = activationState.getValue(confEl.getId());
	    String id = confEl.getContributor().getId();
	    if (active) {
		Bundle bundle = Platform.getBundle(id);
		if (DSLConfigurationElementResolver.isBundleWorkspaceProject(bundle)) {
		    // is linked in this workspace
		    ProjectLinker.linkOpenedPluginIntoWorkspace(bundle);
		}
	    }
	}
    }

}
