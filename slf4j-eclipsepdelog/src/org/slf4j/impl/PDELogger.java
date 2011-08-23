/*
 * Copyright (c) 2005, John J. Franey
 *
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute, and/or sell copies of  the Software, and to permit persons
 * to whom  the Software is furnished  to do so, provided  that the above
 * copyright notice(s) and this permission notice appear in all copies of
 * the  Software and  that both  the above  copyright notice(s)  and this
 * permission notice appear in supporting documentation.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR  A PARTICULAR PURPOSE AND NONINFRINGEMENT
 * OF  THIRD PARTY  RIGHTS. IN  NO EVENT  SHALL THE  COPYRIGHT  HOLDER OR
 * HOLDERS  INCLUDED IN  THIS  NOTICE BE  LIABLE  FOR ANY  CLAIM, OR  ANY
 * SPECIAL INDIRECT  OR CONSEQUENTIAL DAMAGES, OR  ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS  OF USE, DATA OR PROFITS, WHETHER  IN AN ACTION OF
 * CONTRACT, NEGLIGENCE  OR OTHER TORTIOUS  ACTION, ARISING OUT OF  OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * Except as  contained in  this notice, the  name of a  copyright holder
 * shall not be used in advertising or otherwise to promote the sale, use
 * or other dealings in this Software without prior written authorization
 * of the copyright holder.
 *
 */
package org.slf4j.impl;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.pde.SLF4jPlugin;
import org.slf4j.helpers.MessageFormatter;


/**
 * @author john
 *
 */
public class PDELogger extends MarkerIgnoringBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1411149558838961115L;

	private static final int DEBUG_STATUS = IStatus.OK;
	private static final int TRACE_STATUS = IStatus.OK;
	private static final int ERROR_STATUS = IStatus.ERROR;
	private static final int WARNING_STATUS = IStatus.WARNING;
	private static final int INFO_STATUS = IStatus.INFO;

	transient Bundle bundle;

	public PDELogger(String n, Bundle b) {
		this.name = n;
		this.bundle = b;
	}
	
	
	private IStatus mkStatus(int severity, int code, String msg, Throwable t) {
		return new Status(severity,
				bundle.getSymbolicName(),
				code,
				msg,
				t);
	}

	private IStatus mkStatus(int severity, String msg, Throwable t) {
		return mkStatus(severity, 0, msg, t);
	}

	private IStatus mkStatus(int severity, String msg) {
		return mkStatus(severity, msg, null);
	}


	private void log(int severity, String msg, Throwable t) {
		getLog().log(mkStatus(severity, msg, t));
	}

	private void log(int severity, FormattingTuple formattingTuple) {
		getLog().log(
				mkStatus(severity, formattingTuple.getMessage(),
						formattingTuple.getThrowable()));
	}


	private void log(int severity, String msg) {
		getLog().log(mkStatus(severity, msg));
	}


	private ILog getLog() {
		return Platform.getLog(bundle);
	}
	

	private FormattingTuple format(String format, Object arg1, Object arg2) {
		return MessageFormatter.format(format, arg1, arg2);
	}


	private FormattingTuple format(String format, Object arg) {
		return MessageFormatter.format(format, arg);
	}




	private FormattingTuple arrayFormat(String format, Object[] arg) {
		return MessageFormatter.arrayFormat(format, arg);
	}


	public boolean isDebugEnabled() {
		return SLF4jPlugin.getDefault().isDebugging(); 
	}


	public void debug(String msg) {
		if(isDebugEnabled()) {
			log(DEBUG_STATUS, msg);
		}
	}


	public void debug(String format, Object arg) {
		if (isDebugEnabled()) {
			log(DEBUG_STATUS, format(format, arg));
		}
	}


	public void debug(String format, Object arg1, Object arg2) {
		if(isDebugEnabled()) {
			log(DEBUG_STATUS, format(format, arg1, arg2));
		}
	}


	public void debug(String msg, Throwable t) {
		if(isDebugEnabled()) {
			log(DEBUG_STATUS, msg, t);
		}
	}


	@Override
	public void debug(String format, Object[] argArray) {
		if (isDebugEnabled()) {
			log(DEBUG_STATUS, arrayFormat(format, argArray));
		}
	}


	public boolean isInfoEnabled() {
		return true;
	}


	public void info(String msg) {
		if(isInfoEnabled()) {
			log(INFO_STATUS, msg);
		}

	}


	public void info(String format, Object arg) {
		if(isInfoEnabled()) {
			log(INFO_STATUS, format(format, arg));
		}
	}


	public void info(String format, Object arg1, Object arg2) {
		if(isInfoEnabled()) {
			log(INFO_STATUS, format(format, arg1, arg2));
		}
	}


	public void info(String msg, Throwable t) {
		if(isInfoEnabled()) {
			log(INFO_STATUS, msg, t);
		}
	
	}


	@Override
	public void info(String format, Object[] argArray) {
		if (isInfoEnabled()) {
			log(INFO_STATUS, arrayFormat(format, argArray));
		}
	}


	public boolean isWarnEnabled() {
		return true;
	}


	public void warn(String msg) {
		if(isWarnEnabled()) {
			log(WARNING_STATUS, msg);
		}
	}


	public void warn(String format, Object arg) {
		if(isWarnEnabled()) {
			log(WARNING_STATUS, format(format, arg));
		}
	}


	public void warn(String format, Object arg1, Object arg2) {
		if(isWarnEnabled()) {
			log(WARNING_STATUS, format(format, arg1, arg2));
		}
	}


	public void warn(String msg, Throwable t) {
		if(isWarnEnabled()) {
			log(WARNING_STATUS, msg);
		}
	}


	@Override
	public void warn(String format, Object[] argArray) {
		if (isWarnEnabled()) {
			log(WARNING_STATUS, arrayFormat(format, argArray));
		}
	}


	public boolean isErrorEnabled() {
		return true;
	}


	public void error(String msg) {
		if(isErrorEnabled()) {
			log(ERROR_STATUS, msg);
		}
	}


	public void error(String format, Object arg) {
		if(isErrorEnabled()) {
			log(ERROR_STATUS, format(format, arg));
		}
	}


	public void error(String format, Object arg1, Object arg2) {
		if(isErrorEnabled()) {
			log(ERROR_STATUS, format(format, arg1, arg2));
		}
	}


	public void error(String msg, Throwable t) {
		if(isErrorEnabled()) {
			log(ERROR_STATUS, msg);
		}

	}


	@Override
	public void error(String format, Object[] argArray) {
		if (isErrorEnabled()) {
			log(ERROR_STATUS, arrayFormat(format, argArray));
		}
	}


	@Override
	public boolean isTraceEnabled() {
		return isDebugEnabled();
	}

	@Override
	public void trace(String msg) {
		if (isTraceEnabled()) {
			log(TRACE_STATUS, msg);
		}
	}

	@Override
	public void trace(String format, Object arg) {
		if (isTraceEnabled()) {
			log(TRACE_STATUS, format(format, arg));
		}
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		if (isTraceEnabled()) {
			log(TRACE_STATUS, format(format, arg1, arg2));
		}
	}


	@Override
	public void trace(String format, Object[] argArray) {
		if (isTraceEnabled()) {
			log(TRACE_STATUS, arrayFormat(format, argArray));
		}
	}

	@Override
	public void trace(String msg, Throwable t) {
		if (isTraceEnabled()) {
			log(TRACE_STATUS, msg, t);
		}
	}

}
