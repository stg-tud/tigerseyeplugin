package utilities;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import de.tud.stg.tigerseye.util.OSInfo;
import de.tud.stg.tigerseye.util.OSInfo.OSType;

/**
 * Rule to handle tests can be turned off, like {@link LongrunningTest} and
 * {@link TodoTest} annotated test cases depending on the user specfied system
 * property value. If set to false annotated tests will be skipped for these
 * rules.
 * <p>
 * Or depending on predefined values e.g.
 * 
 * @author Leo_Roos
 * 
 */
// TODO(Leo_Roos;Nov 18, 2011) consider implementing global register where every
// annotation can provide it's own logic, what should happen if it is called in
// the context of this rule.
public class SystemPropertyRule implements MethodRule {

	private static final String FALSE = "false";

	private static final String TRUE = "true";

	private static final boolean activate_longrunning_tests_default = false;

	private boolean runtest;

	private List<String> skipReasons;

	private FrameworkMethod currentFrameworkMethod;

	@Override
	public Statement apply(Statement arg0, FrameworkMethod fm, Object arg2) {
		reset(fm);
		// Could be refactored to use list of handler instead
		handleLongRunning();

		handleTodoTest();

		handleOSDependent();

		if (runtest)
			return arg0;
		else
			return new SkipAndLogStatement(fm, prettyReasons());
	}

	private void handleTodoTest() {
		if (!hasAnnotation(TodoTest.class)) {
			return;
		}
		if (isPropertyOfBooleanValue(TodoTest.PROPERTY_KEY_TESTS_TODO, false)) {
			skipWithReason("TODO tests have been disabled.");
		}
	}

	private void handleLongRunning() {
		if (!hasAnnotation(LongrunningTest.class)) {
			return;
		}
		if (isPropertyOfBooleanValue(LongrunningTest.PROPERTY_KEY_TESTS_LONGRUNNING, false)) {
			skipWithReason("Long running Tests have been disabled.");
		}else{
			if(activate_longrunning_tests_default){
				return;
			}else
				skipWithReason("Found LongRunningTest annotation, default is to skip.");
		}
	}

	private void handleOSDependent() {
		OSDependent annotation = this.currentFrameworkMethod.getAnnotation(OSDependent.class);
		if (annotation == null) {
			return;
		}
		OSType osType = OSInfo.getOSType();
		OSType[] supportedOSs = annotation.value();
		if (ArrayUtils.contains(supportedOSs, osType)) {
			return;
		} else {
			skipWithReason("Test works only for OSs " + Arrays.toString(supportedOSs) + ". Current determined is "
					+ osType);
		}
	}

	private String prettyReasons() {
		StringBuilder stringBuilder = new StringBuilder();
		for (String r : skipReasons) {
			stringBuilder.append(r).append("\n");
		}
		return stringBuilder.toString();
	}

	private void reset(FrameworkMethod fm) {
		// run as default
		runtest = true;
		// new reasons list
		skipReasons = new LinkedList<String>();
		currentFrameworkMethod = fm;
	}

	/**
	 * @param propKey
	 * @param expectedBool
	 * @return <code>true</code> if the value for the property key is of the
	 *         expected boolean value <code>false</code> otherwise, i.e. if it
	 *         is a boolean not of the expected value or if the string
	 *         representation of the value is not a boolean at all.
	 */
	private boolean isPropertyOfBooleanValue(String propKey, boolean expectedBool) {
		final String propVal = System.getProperty(propKey);
		if (propVal == null)
			return false;

		Boolean propBool;

		if (TRUE.equals(propVal))
			propBool = true;
		else if (FALSE.equals(propVal))
			propBool = false;
		else
			propBool = null;

		if (propBool == null) {
			return false;
		} else if (expectedBool) {
			return propBool;
		} else {
			return !propBool;
		}
	}

	private <T extends Annotation> boolean hasAnnotation(Class<T> class1) {
		T annotation = this.currentFrameworkMethod.getMethod().getAnnotation(class1);
		if (annotation == null)
			return false;
		else
			return true;
	}

	private void skipWithReason(String reason) {
		this.runtest = false;
		this.skipReasons.add(reason);
	}

}
