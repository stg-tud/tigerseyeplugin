package utilities;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rule, handling {@link LongrunningTest} annotated test cases depending on the
 * system property value of {@code tests.longrunning}. If set to false annotated
 * tests will be skipped.
 * 
 * @author Leo_Roos
 * 
 */
public class LongrunningTestRule implements MethodRule {

	private static final Logger logger = LoggerFactory
			.getLogger(LongrunningTestRule.class);

	private static final String PROPERTY_KEY_TESTS_LONGRUNNING = "tests.longrunning";

	private static final String longRunningTests = System
			.getProperty(PROPERTY_KEY_TESTS_LONGRUNNING);

	private static final String FALSE = "false";

	private static final String TRUE = "true";

	private static AtomicBoolean isValueIllegalFormat = new AtomicBoolean(false);

	@Override
	public Statement apply(Statement arg0, FrameworkMethod fm, Object arg2) {
		if (isValueIllegalFormat.get())
			return arg0;

		LongrunningTest annotation = fm.getAnnotation(LongrunningTest.class);
		if (annotation == null)
			return arg0;

		if (TRUE.equals(longRunningTests)) {
			return arg0;
		}

		if (FALSE.equals(longRunningTests)) {
			return new SkipAndLogStatement(fm,
					"Long running Tests have been disabled.");
		}

		isValueIllegalFormat.compareAndSet(false, true);
		logger.error("Failed to parse " + longRunningTests
				+ " it is not a recognized value for the property "
				+ PROPERTY_KEY_TESTS_LONGRUNNING + ". Will turn on all tests.");
		return arg0;

	}

}
