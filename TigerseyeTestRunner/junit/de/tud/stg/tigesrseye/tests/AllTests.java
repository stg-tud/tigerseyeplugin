package de.tud.stg.tigesrseye.tests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tud.stg.tigerseye.examples.test.AllTestsExampleDSLs;
import de.tud.stg.tigerseye.test.AllTestsCoreJUnitTests;
import de.tud.stg.tigerseye.test.AllTestsCoreTranformationTests;



@RunWith(Suite.class)
@SuiteClasses({
	AllTestsCoreTranformationTests.class,//
	AllTestsExampleDSLs.class,//
	AllTestsCoreJUnitTests.class, //
})

public class AllTests {

}
