package learningtests.osgi;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCoreActivator;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;

/**
 * Must be run as Plug-in Test.
 * 
 * @author Leo Roos
 * 
 */
public class RefreshOSGIBundlesTest {

	private static final Logger logger = LoggerFactory
			.getLogger(RefreshOSGIBundlesTest.class);

	@Test
	public void shouldRefreshBundles() throws Exception {
		Set<DSLDefinition> dslDefinitions = TigerseyeCore.getLanguageProvider()
				.getDSLDefinitions();
		Set<Bundle> dslBundles = new HashSet<Bundle>(dslDefinitions.size());
		for (DSLDefinition dslDefinition : dslDefinitions) {
			String contributorSymbolicName = dslDefinition.getContributor()
					.getId();
			Bundle bundle = Platform.getBundle(contributorSymbolicName);
			if (bundle != null)
				dslBundles.add(bundle);
		}
		//Execution
		// XXX To turn this test from a smoke test into a real test:
		// 1. should check state of a physical representation of a class,
		// 2. Then should change that class and
		// 3. check that the loaded entity still
		// is equivalent to the beforehand checked
		refreshBundles(dslBundles);
		//Verification
		// 4. Assert that state is now that of the changed class.
	}

	private void refreshBundles(Set<Bundle> bundlesToRefresh) {
		Bundle[] bundles = bundlesToRefresh.toArray(new Bundle[0]);
		BundleContext bc = TigerseyeCoreActivator.getDefault().getBundle()
				.getBundleContext();
		if (bc == null) {
			logger.error("Could not retrieve BundleContext. Can not refresh DSL Bundles");
			return;
		}
		/*
		 * Code stolen from
		 * FrameworkCommandProvider#_refresh(CommandInterpreter)
		 */
		org.osgi.framework.ServiceReference packageAdminRef = bc
				.getServiceReference(PackageAdmin.class.getName());
		if (packageAdminRef != null) {
			org.osgi.service.packageadmin.PackageAdmin packageAdmin = (org.osgi.service.packageadmin.PackageAdmin) bc
					.getService(packageAdminRef);
			if (packageAdmin != null) {
				try {
					packageAdmin.refreshPackages(bundles);
				} finally {
					bc.ungetService(packageAdminRef);
				}
			}
		}
	}

}
