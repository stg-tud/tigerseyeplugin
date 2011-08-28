package utilities;

import java.lang.annotation.Annotation;

import org.eclipse.core.runtime.Platform;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory
			.getLogger(PluginTestRule.class);

	private final static class DoNothing extends Statement {
		@Override
		public void evaluate() throws Throwable {
			// Ignore and succeed
		}
	}

	@Override
	public Statement apply(final Statement s, FrameworkMethod fm, Object o) {
		PluginTest annotation = fm.getAnnotation(PluginTest.class);
		if (annotation == null) {
			return s;
		} else {
			if (Platform.isRunning())
				return s;
			else {
				logger.warn("IGNORING: " + fm.getName()
						+ " since platform is not running");
				return new DoNothing();
			}
		}
	}

}
