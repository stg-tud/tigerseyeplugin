package runner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import runner.AllTestsCorePluginTests;




@RunWith(Suite.class)
@SuiteClasses({
	AllTestsCorePluginTests.class, //
})
public class AllPluginTests {

}
