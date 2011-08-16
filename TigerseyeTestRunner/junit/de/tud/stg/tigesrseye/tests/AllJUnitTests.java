package de.tud.stg.tigesrseye.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import runner.AllTestsCoreJUnitTests;
import runner.AllTestsCoreTranformationTests;
import runner.AllTestsExampleDSLs;


@RunWith(Suite.class)
@SuiteClasses({
	AllTestsCoreTranformationTests.class,//
	AllTestsExampleDSLs.class,//
	AllTestsCoreJUnitTests.class, //
})
public class AllJUnitTests {

}
