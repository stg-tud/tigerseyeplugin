package utilities.tests;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import junit.framework.AssertionFailedError;
import learningtests.fileaccess.ReadingEncodingdependendUnicode;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utilities.OSDependent;
import utilities.SystemPropertyRule;
import de.tud.stg.tigerseye.util.OSInfo;
import de.tud.stg.tigerseye.util.OSInfo.OSType;

public class SystemPropertyRuleTest {

	private static final String SHOULDRUNON_LINUX_AND_WINDOWS = "shouldrunonLinuxAndWindows";

	private static final String SHOULD_ONLY_RUN_IF_LINUX = "shouldOnlyRunIfLinux";

	private static final String SHOULD_RUN_IF_WINDOWS = "shouldRunIfWindows";

	@Rule
	public SystemPropertyRule spr = new SystemPropertyRule();

	private OSType currentOS = OSInfo.getOSType();

	private static void iRan(String string) {
		runnerCollector.add(string);
	}

	private static Collection<String> runnerCollector = Collections.synchronizedList(new ArrayList<String>());

	private static Object[] expectedRunnerLinux = { SHOULD_ONLY_RUN_IF_LINUX, SHOULDRUNON_LINUX_AND_WINDOWS };
	private static Object[] expectedRunnerWindows = { SHOULD_RUN_IF_WINDOWS, SHOULDRUNON_LINUX_AND_WINDOWS };

	@AfterClass
	public static void af() {
		OSType osType = OSInfo.getOSType();
		switch (osType) {
		case LINUX:
			assertThat(runnerCollector).containsOnly(expectedRunnerLinux);
			break;
		case WINDOWS:
			assertThat(runnerCollector).containsOnly(expectedRunnerWindows);
			break;
		default:
			throw new AssertionFailedError("No os depending tests designed for" + osType);
		}

	}

	@Test
	public void shouldBeExpectedOS() throws Exception {
		String property = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
		if (property.contains("linux"))
			assertThat(currentOS).isEqualTo(OSType.LINUX);
		if (property.contains("windows"))
			assertThat(currentOS).isEqualTo(OSType.WINDOWS);
		if (property.contains("mac"))
			assertThat(currentOS).isEqualTo(OSType.MAC);
	}

	@Test
	@OSDependent(value = OSType.WINDOWS)
	public void shouldbeskippedIfNotWindows() throws Exception {
		if (!OSInfo.getOSType().equals(OSType.WINDOWS))
			fail("should be skipped if not on windows");
	}

	@Test
	@OSDependent(value = OSType.WINDOWS)
	public void shouldRunIfWindows() throws Exception {
		if (!OSInfo.getOSType().equals(OSType.WINDOWS))
			fail("should be skipped if not on windows");
		iRan(SHOULD_RUN_IF_WINDOWS);
	}

	@Test
	@OSDependent(value = OSType.LINUX)
	public void shouldOnlyRunIfLinux() throws Exception {
		if (!OSInfo.getOSType().equals(OSType.LINUX)) {
			fail("should be skipped if not on linux");
		}
		iRan(SHOULD_ONLY_RUN_IF_LINUX);
	}

	@Test
	@OSDependent({ OSType.LINUX, OSType.WINDOWS })
	public void shouldrunonLinuxAndWindows() throws Exception {
		if (!OSInfo.getOSType().equals(OSType.LINUX)) {
			fail("should be skipped if not on linux");
		}
		iRan(SHOULDRUNON_LINUX_AND_WINDOWS);
	}
}
