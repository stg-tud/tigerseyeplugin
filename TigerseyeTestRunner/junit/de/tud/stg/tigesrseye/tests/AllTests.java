package de.tud.stg.tigesrseye.tests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tud.stg.tigerseye.examples.AllTestsExampleDSLs;
import de.tud.stg.tigerseye.test.AllTestsCoreJUnitTests;
import de.tud.stg.tigerseye.test.AllTestsCorePluginTests;
import de.tud.stg.tigerseye.test.AllTestsCoreTranformationTests;



@RunWith(Suite.class)
@SuiteClasses({
	AllJUnitTests.class, //
	AllTestsCorePluginTests.class, //
})

public class AllTests {

}
