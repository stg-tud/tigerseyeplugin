package runner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import runner.AllTestsCoreJUnitTests;
import runner.AllTestsCoreTranformationTests;


@RunWith(Suite.class)
@SuiteClasses({
	AllTestsCoreTranformationTests.class,//
//	AllTestsExampleDSLs.class,
	/*I don't want a dependency inside the 
	TigerseyeTestRunner to an optional project set*/
	AllTestsCoreJUnitTests.class, //
})
public class AllJUnitTests {

}
