package de.tud.stg.tigesrseye.tests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import runner.AllTestsCorePluginTests;




@RunWith(Suite.class)
@SuiteClasses({
	AllJUnitTests.class, //
	AllTestsCorePluginTests.class, //
})

public class AllTests {

}
