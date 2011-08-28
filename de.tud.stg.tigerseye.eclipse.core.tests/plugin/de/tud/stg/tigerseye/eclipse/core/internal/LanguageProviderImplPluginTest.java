package de.tud.stg.tigerseye.eclipse.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import utilities.PluginTest;
import utilities.PluginTestRule;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.internal.WorkspaceDSLDefintionsResolver.WorkspaceDSL;
import de.tud.stg.tigerseye.eclipse.core.preferences.TigerseyePreferenceConstants;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants;

//TODO currently only Happy Paths and using local configuration dependent DSL plug-ins and DSL workspace projects. 
public class LanguageProviderImplPluginTest {

	@Rule
	public PluginTestRule ptr = new PluginTestRule();

	private static final String debugLogoClassPath = "de.tud.stg.tigerseye.examples.logo.LogoDSL";
	private LanguageProviderImpl provider;
	private IPreferenceStore tigerseyeCoreStore;

	@BeforeClass
	public static void bc() throws Exception {
		// TODO should be changed to an example project that is available from
		// this plug-ins location
		if (Platform.isRunning())
			PluginTestUtilities.linkExampleProject();
	}

	@Before
	public void setUp() throws Exception {
		if(!Platform.isRunning())
			return;
		MockitoAnnotations.initMocks(this);
		IConfigurationElement[] configurationElementsFor = RegistryFactory
				.getRegistry()
				.getConfigurationElementsFor(
						TigerseyeCoreConstants.DSLDEFINITIONS_EXTENSION_POINT_ID);
		WorkspaceDSLDefintionsResolver wdslr = new WorkspaceDSLDefintionsResolver(
				PluginRegistry.getWorkspaceModels());
		Set<WorkspaceDSLDefintionsResolver.WorkspaceDSL> wdsls = wdslr
				.getWorkspaceDSLDefintions();
		tigerseyeCoreStore = TigerseyeCore.getPreferences();

		HashSet<DSLConfigurationElement> dslConfs = new HashSet<DSLConfigurationElement>();
		for (WorkspaceDSL workspaceDSL : wdsls) {
			dslConfs.add(new WorkspaceDSLConfigurationElement(workspaceDSL));
		}
		for (IConfigurationElement iConfigurationElement : configurationElementsFor) {
			dslConfs.add(new PluginDSLConfigurationElement(
					iConfigurationElement));
		}

		provider = new LanguageProviderImpl(tigerseyeCoreStore, dslConfs);
	}

	@Test
	@PluginTest
	public void testGetDSLDefinitions() {
		String shouldContain = debugLogoClassPath
				+ "\n"
				+ "de.tud.stg.tigerseye.examples.logo.SimpleLogo\n"
				+ "de.tud.stg.tigerseye.examples.logo.UCBLogo\n"
				+ "de.tud.stg.tigerseye.examples.logo.FunctionalLogo\n"
				+ "de.tud.stg.tigerseye.examples.logo.ConciseLogo\n"
				+ "de.tud.stg.tigerseye.examples.mapdsl.MapDSL\n"
				+ "de.tud.stg.tigerseye.examples.setdsl.SetDSL\n"
				+ "de.tud.stg.tigerseye.examples.simplesql.SimpleSqlDSL\n"
				+ "de.tud.stg.tigerseye.examples.statefuldsl.StatefulDSL\n"
				+ "de.tud.stg.tigerseye.examples.tinysql.TinySQL\n"
				+ "de.tud.stg.tigerseye.examples.statemachine.StateMachineDSL\n"
				+ "de.tud.stg.tigerseye.example.dzoneunits.UnitsDSL\n"
				+ "de.lroos.tigerseye.tri.LambdaCalculus\n";

		Collection<DSLDefinition> dslDefinitions = provider.getDSLDefinitions();
		List<String> classPaths = new ArrayList<String>();
		for (DSLDefinition dslDefinition : dslDefinitions) {
			classPaths.add(dslDefinition.getClassPath());
		}

		ArrayList<String> notContained = new ArrayList<String>();
		Scanner scanner = new Scanner(shouldContain);
		while (scanner.hasNextLine()) {
			String classPath = scanner.nextLine();
			if (!classPaths.contains(classPath)) {
				notContained.add(classPath);
			}
		}
		assertTrue("Following dsls where not found " + notContained,
				notContained.isEmpty());
	}

	@Test
	@PluginTest
	public void bundleShouldHaveCorrectAttributes() throws Exception {
		DSLDefinition activeDSLForExtension = provider
				.getActiveDSLForExtension("logo");
		String dslName = "Logo DSL With Debug Annotations";
		String contributorSymbolicName = "de.tud.stg.tigerseye.examples.LogoDSL";
		String classPath = debugLogoClassPath;
		DSLDefinitionImpl expectedDSL = makeDSLDefinition(dslName,
				contributorSymbolicName, classPath);

		assertEquals(expectedDSL, activeDSLForExtension);
	}

	private DSLDefinitionImpl makeDSLDefinition(String dslName,
			String contributorSymbolicName, String classPath) {

		DSLConfigurationElement contribMock = mock(DSLConfigurationElement.class);
		when(contribMock.getId()).thenReturn(
				contributorSymbolicName + "somelanguagekey");
		DSLDefinitionImpl expectedDSL = new DSLDefinitionImpl(classPath,
				contribMock, dslName);
		return expectedDSL;
	}

	@Test
	@PluginTest
	public void workSpaceDSLShouldHaveCorrectAttributes() throws Exception {
		DSLDefinition activeDSLForExtension = provider
				.getActiveDSLForExtension("lambda");
		DSLDefinitionImpl expectedDSL = makeDSLDefinition("Lambda Calculus",
				"de.tud.stg.tigerseye.examples.WorkspaceLanguage",
				"de.lroos.tigerseye.tri.LambdaCalculus");

		assertEquals(expectedDSL, activeDSLForExtension);
	}

	@Test
	@PluginTest
	public void shouldHaveDefaultActivationState() throws Exception {
		List<DSLDefinition> wrongActive = new ArrayList<DSLDefinition>();
		Collection<DSLDefinition> dslDefinitions = provider.getDSLDefinitions();
		boolean defaultActiveState = tigerseyeCoreStore
				.getBoolean(TigerseyePreferenceConstants.DEFAULT_LANGUAGE_ACTIVE_KEY);
		for (DSLDefinition dsl : dslDefinitions) {
			Boolean active = dsl.isActive();
			if (active != defaultActiveState) {
				wrongActive.add(dsl);
			}
		}
		assertTrue("Following dsls had wrong default active state ("
				+ defaultActiveState + ")" + wrongActive, wrongActive.isEmpty());
	}

	@Test
	@PluginTest
	public void testGetActiveDSLForExtensionSuccess() {
		DSLDefinition activeDSLForExtension = provider
				.getActiveDSLForExtension("logo");
		assertNotNull("expected " + debugLogoClassPath, activeDSLForExtension);
		assertEquals(debugLogoClassPath, activeDSLForExtension.getClassPath());
	}

	@Test
	@PluginTest
	public void testGetActiveDSLForExtensionFailure() {
		DSLDefinition activeDSLForExtension = provider
				.getActiveDSLForExtension("noLangWithThisExtension");
		assertNull("Was " + activeDSLForExtension, activeDSLForExtension);
	}

}
