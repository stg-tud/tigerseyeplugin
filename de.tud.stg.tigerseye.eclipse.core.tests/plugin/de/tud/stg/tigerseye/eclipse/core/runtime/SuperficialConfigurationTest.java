package de.tud.stg.tigerseye.eclipse.core.runtime;

import static org.junit.Assert.assertNotNull;

import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.junit.Rule;
import org.junit.Test;

import utilities.PluginTest;
import utilities.PluginTestRule;

/**
 * Execute as JUnit Plug-in Test
 * 
 * @author Leo Roos
 *
 */
public class SuperficialConfigurationTest {

	
	@Rule
	public PluginTestRule ptr = new PluginTestRule();
	
	@Test
	@PluginTest
	public void testPluginRegistryAccessible() throws Exception {		
		IPluginModelBase[] workspaceModels = PluginRegistry.getWorkspaceModels();
		//Must be at least empty array
		assertNotNull(workspaceModels);
	}
	
	
}
