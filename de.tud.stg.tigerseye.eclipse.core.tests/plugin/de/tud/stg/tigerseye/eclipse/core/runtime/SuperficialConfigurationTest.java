package de.tud.stg.tigerseye.eclipse.core.runtime;

import static org.junit.Assert.*;

import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.junit.Test;

/**
 * Execute as JUnit Plug-in Test
 * 
 * @author Leo Roos
 *
 */
public class SuperficialConfigurationTest {

	@Test
	public void testPluginRegistryAccessible() throws Exception {		
		IPluginModelBase[] workspaceModels = PluginRegistry.getWorkspaceModels();
		//Must be at least empty array
		assertNotNull(workspaceModels);
	}
	
}
