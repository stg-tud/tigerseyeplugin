package utilities;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Rule, handling tests can be turned off, like {@link LongrunningTest} and
 * {@link TodoTest} annotated test cases depending on the system property value.
 * If set to false annotated tests will be skipped for these rules.
 * 
 * @author Leo_Roos
 * 
 */
public class SystemPropertyRule implements MethodRule {

	private static final String FALSE = "false";

	private static final String TRUE = "true";

	@Override
	public Statement apply(Statement arg0, FrameworkMethod fm, Object arg2) {

		Statement longrunning = handleLongRunning(arg0, fm);
		if (longrunning != null)
			return longrunning;

		Statement todo = handleTodoTest(arg0, fm);
		if (todo != null)
			return todo;

		return arg0;
	}

	private Statement handleTodoTest(Statement arg0, FrameworkMethod fm) {
		TodoTest annotation = fm.getAnnotation(TodoTest.class);
		if (annotation == null)
			return null;
		Boolean property = isProperty(TodoTest.PROPERTY_KEY_TESTS_TODO, false);
		if (property == null)
			return null;
		if (property)
			return new SkipAndLogStatement(fm, "TODO tests have been disabled.");
		return null;
	}

	/**
	 * @param propKey
	 * @param bool
	 * @return threestate <code>null</code> if value neither "true" nor "false"
	 *         otherwise checks for the passed bool to be of that value
	 */
	private Boolean isProperty(String propKey, boolean bool) {
		final String todotest = System.getProperty(propKey);
		if (todotest == null)
			return null;

		if (bool)
			return TRUE.equals(todotest);
		else
			return FALSE.equals(todotest);
	}

	private Statement handleLongRunning(Statement arg0, FrameworkMethod fm) {
		LongrunningTest annotation = fm.getAnnotation(LongrunningTest.class);
		if (annotation == null)
			return null;
		Boolean longTestOff = isProperty(LongrunningTest.PROPERTY_KEY_TESTS_LONGRUNNING, false);
		if (longTestOff == null)
			return null;
		if (longTestOff) {
			return new SkipAndLogStatement(fm, "Long running Tests have been disabled.");
		}
		return null;
	}

}
