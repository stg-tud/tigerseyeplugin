package utilities;

import org.eclipse.core.runtime.Platform;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * If tests are annotated with {@link PluginTest} they will be only run if the
 * Eclipse Platform is running.
 * 
 * @author Leo Roos
 * 
 * @see <a
 *      href="http://blog.mycila.com/2009/11/writing-your-own-junit-extensions-using.html">This&nbsp;example</a>
 *      on how to write Rules.
 * 
 */
public class PluginTestRule implements MethodRule {

	@Override
	public Statement apply(final Statement s, FrameworkMethod fm, Object o) {
		PluginTest annotation = fm.getAnnotation(PluginTest.class);
		if (annotation == null) {
			return s;
		} else {
			if (Platform.isRunning())
				return s;
			else {
				return new SkipAndLogStatement(fm,
						"Eclipse platform is not running.");
			}
		}
	}

}
