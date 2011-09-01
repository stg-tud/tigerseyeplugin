package utilities;

import javax.annotation.Nonnull;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Statement that is used instead of the evaluation of a test method. It logs
 * the skipped method and optionally outputs a reason for not running the test.
 * 
 * @author LeoRoos
 * 
 */
final class SkipAndLogStatement extends Statement {
	private static final Logger logger = LoggerFactory.getLogger(SkipAndLogStatement.class);
	private final String methodName;
	private final String reason;

	public SkipAndLogStatement(@Nonnull String name) {
		this(name, null);
	}

	public SkipAndLogStatement(@Nonnull FrameworkMethod fm, String reason) {
		this(fm.getMethod().toString(), reason);
	}

	public SkipAndLogStatement(@Nonnull String name, String reason) {
		this.methodName = name;
		this.reason = reason;
	}

	@Override
	public void evaluate() throws Throwable {
		String actualReason = "";
		if (reason == null)
			actualReason = "Only Bog knows why.";
		else
			actualReason = reason;
		logger.warn("Ignoring Test: {}. Reason: {}", methodName, actualReason);
	}
}