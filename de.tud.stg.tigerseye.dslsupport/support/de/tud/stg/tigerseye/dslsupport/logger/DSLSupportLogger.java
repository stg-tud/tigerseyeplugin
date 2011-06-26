package de.tud.stg.tigerseye.dslsupport.logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A simple logger for classes of the dslsupport project.
 * 
 * @author Leo Roos
 * 
 */
public class DSLSupportLogger {

	public enum Level {
		DEBUG("DEBUG", -1), INFO("INFO", 0), ERROR("ERROR", 1);

		private final String name;
		private final int prio;

		Level(String name, int prio) {
			this.name = name;
			this.prio = prio;
		}

		private boolean hasLowerPriorityThan(Level l) {
			return this.prio <= l.prio;
		}
	}

	public static Level level = Level.ERROR;

	private String name;

	public DSLSupportLogger(String loggername) {
		this.name = loggername;
	}

	public DSLSupportLogger(Class<?> loggername) {
		this.name = loggername.getSimpleName();
	}

	public void debug(String msg) {
		out(Level.DEBUG, msg, null);
	}

	private String formatLogMsg(Level error, String msg) {
		return getLogPrefix(error.name) + ":" + msg;
	}

	public void info(String msg) {
		out(Level.INFO, msg, null);
	}

	private String getLogPrefix(String severity) {
		return "[" + severity + "] " + name;
	}

	public void error(String msg) {
		error(msg, null);
	}

	public void error(String msg, Throwable e) {
		if (level.hasLowerPriorityThan(Level.ERROR))
			out(Level.ERROR, msg, e);
	}

	private void out(Level error, String msg, Throwable e) {
		if (level.hasLowerPriorityThan(error)) {
			String formatted = formatLogMsg(error,
					formatMsgWithException(msg, e));
			System.err.println(formatted);
		}
	}

	private String formatMsgWithException(String msg, Throwable e) {
		StringWriter stringWriter = new StringWriter();
		if (e != null) {
			e.printStackTrace(new PrintWriter(stringWriter));
			return msg + " " + stringWriter;
		} else {
			return msg;
		}
	}

}
