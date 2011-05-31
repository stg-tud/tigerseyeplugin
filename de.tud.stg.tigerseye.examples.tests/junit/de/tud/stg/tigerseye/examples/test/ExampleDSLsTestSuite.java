package de.tud.stg.tigerseye.examples.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tud.stg.tigerseye.examples.test.logo.LogoDSLTest;
import de.tud.stg.tigerseye.examples.test.mapdsl.MapDSLTest;


@RunWith(Suite.class)
@SuiteClasses({
	LogoDSLTest.class, //
	MapDSLTest.class, //
	LogoDSLTest.class, //
})
public class ExampleDSLsTestSuite {

}
