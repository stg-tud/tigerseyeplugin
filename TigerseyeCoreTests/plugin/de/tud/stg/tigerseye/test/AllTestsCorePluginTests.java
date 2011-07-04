package de.tud.stg.tigerseye.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tud.stg.tigerseye.eclipse.core.internal.LanguageProviderImplPluginTest;
import de.tud.stg.tigerseye.eclipse.core.runtime.DSLClasspathResolverTest;

/**
 * All <i>run as plug-in</i> tests 
 * 
 * @author Leo Roos
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
	//TODO this test depends on local test environment configuration
	LanguageProviderImplPluginTest.class, //
	DSLClasspathResolverTest.class, //
})
public class AllTestsCorePluginTests {

}
