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
	
	private static Level currentLogLevel = Level.ERROR;
	
	static {
		String property = System.getProperty("tigerseye.dsllogger");
		if(property != null){
			Level toSet = Level.parse(property);
			if(toSet != null)
				currentLogLevel = toSet;
			else
				new DSLSupportLogger(DSLSupportLogger.class).error("Unknown Level " + property);
		}
	}

	public enum Level {
		DEBUG("DEBUG", -1), INFO("INFO", 0), ERROR("ERROR", 1);

		private final String name;
		private final int prio;

		Level(String name, int prio) {
			this.name = name;
			this.prio = prio;
		}

		public static Level parse(String property) {
			Level[] values = Level.values();
			for (Level level : values) {
				if(level.name.equalsIgnoreCase(property))
					return level;
			}
			return null;
		}

		private boolean isIncludedIn(Level l) {
			return this.prio >= l.prio;
		}
		
		public boolean isEnabled(){
			return this.isIncludedIn(currentLogLevel);
		}
	}
	
	public boolean isDebugEnabled(){
		return Level.DEBUG.isEnabled();
	}
	
	public Level getLevel() {
		return currentLogLevel;
	}

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
	
	public void debug(String msg, Throwable e) {
		out(Level.DEBUG, msg, e);
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
		out(Level.ERROR, msg, e);
	}

	private void out(Level logLevel, String msg, Throwable e) {
		if (logLevel.isIncludedIn(currentLogLevel)) {
			String formatted = formatLogMsg(logLevel, formatMsgWithException(msg, e));
			System.err.println(formatted);
		}
	}

	private String formatMsgWithException(String msg, Throwable e) {
		if (e != null) {
			StringWriter stringWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stringWriter));
			return "DSLSupport:" +msg + " " + stringWriter.toString();
		} else {
			return msg;
		}
	}

}
