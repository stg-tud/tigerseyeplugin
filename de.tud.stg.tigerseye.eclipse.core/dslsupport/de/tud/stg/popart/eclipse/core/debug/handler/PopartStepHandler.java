package de.tud.stg.popart.eclipse.core.debug.handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;

import de.tud.stg.popart.eclipse.core.debug.PopartDebugUtils;
import de.tud.stg.popart.eclipse.core.debug.PopartInvisibleLineBreakpoint;

import de.tud.stg.popart.eclipse.core.debug.PopartSourceFileKeywordRegistry;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartSourceFileKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartSourceFileStructuredElementKeyword;


/**
 * Popart Step Handler.
 * Handles step commands in DSL Languages. Each step is performed as follows:
 * Set temporary breakpoint to desired line, resume, delete temporary breakpoint 
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartStepHandler implements IDebugEventSetListener {
private static final Logger logger = LoggerFactory.getLogger(PopartStepHandler.class);


	private static PopartStepHandler INSTANCE = new PopartStepHandler();
	private int currentLine;
	private int runToLine;
	private IThread thread;
	private PopartSourceFileKeyword currentKeyword;
	private ArrayList<PopartInvisibleLineBreakpoint> lastBreakpoints = new ArrayList<PopartInvisibleLineBreakpoint>();
	private boolean secondResume = false;

	private ArrayList<IObserver> observers = new ArrayList<IObserver>();

	public void addObserver(ArrayList<IObserver> observer) {
		logger.info("adding observers");
		observers.clear();
		observers.addAll(observer);
	}

	private PopartStepHandler() {

	}

	public static PopartStepHandler getInstance() {
		return INSTANCE;
	}

	public PopartSourceFileKeyword getCurrentKeyword() {
		return currentKeyword;
	}

	/**
	 * StepInto: Only possible on Structured Elements. Sets breakpoint at first
	 * element of the SE if present and resumes.
	 */
	public void stepInto() {
		PopartSourceFileStructuredElementKeyword se = (PopartSourceFileStructuredElementKeyword) currentKeyword;
		PopartSourceFileKeyword firstkeyword = se.getKeyword(0);
		if (firstkeyword != null) {
			/*
			 * set breakpoint if no breakpoint at the SE to continue with
			 * 2-times resume
			 */
			setBreakPointAtLine(currentLine);
			setBreakPointOnClosure();
			runToLine = firstkeyword.getLineNr();
			int line = se.getKeyword(0).getLineNr();
			setBreakPointAtLine(line, false);
			resume();
		}
	}

	/**
	 * StepOver: On a structured element, step over the SE. On an operation step
	 * to next keyword.
	 */
	public void stepOver() {
		// on a SE
		if (currentKeyword instanceof PopartSourceFileStructuredElementKeyword) {
			/*
			 * set breakpoint if no breakpoint at the SE to continue with
			 * 2-times resume
			 */
			setBreakPointAtLine(currentLine);
			clearBreakpointsInSE((PopartSourceFileStructuredElementKeyword) currentKeyword);
			setBreakPointAfterSE((PopartSourceFileStructuredElementKeyword) currentKeyword);
			setBreakPointOnClosure();
		}
		// on an operation
		else {
			stepToNextKeyword();
		}
		resume();
	}

	/**
	 * StepReturn: Only possible inside a structured element. Sets breakpoint to
	 * first element after the SE
	 */
	public void stepReturn() {

		clearBreakpointsInSE(currentKeyword.getParent());
		setBreakPointAfterSE(currentKeyword.getParent());
		resume();

	}

	/**
	 * 
	 * Handles a problem with groovy debugging.
	 * 
	 * If a breakpoint is set on a SE and the closure begins in the same line
	 * you need 2-times resume to come to the next line. So after the first
	 * resume at "SE {" we suspend on "{" the beginning of the closure, because
	 * of the breakpoint in this line.
	 * 
	 * Another code style would be to place the "{" in the next line. In this
	 * case set a temporary breakpoint on the closure and also proceed 2
	 * resumes.
	 * 
	 */
	private void setBreakPointOnClosure() {

		InputStream inputstream;
		try {
			inputstream = PopartDebugUtils.getGroovyTempFile().getContents();
			InputStreamReader reader = new InputStreamReader(inputstream);
			BufferedReader bff = new BufferedReader(reader);
			for (int i = 0; i < currentLine; i++) {
				bff.readLine();
			}
			String line = bff.readLine();
			int lineNumber = currentLine + 1;
			while (line.trim().equals("")) {
				line = bff.readLine();
				lineNumber++;
			}
			if (line.trim().equals("{")) {
				setBreakPointAtLine(lineNumber);
			}
		} catch (CoreException e) {
			logger.warn("Generated log statement",e);

		} catch (IOException e) {
			logger.warn("Generated log statement",e);
		}

		secondResume = true;
	}

	private void stepToNextKeyword() {
		runToLine = PopartSourceFileKeywordRegistry.getInstance()
				.nextLineWithKeyword(currentLine + 1);
		stepToKeywordAtRunToLine();
	}

	private void getInfos() {
		IDebugTarget[] targets = DebugPlugin.getDefault().getLaunchManager()
				.getDebugTargets();
		try {
			for (int i = 0; i < targets.length; i++) {

				IThread[] threads = targets[i].getThreads();
				if (threads.length > 0) {
					for (int j = 0; j < threads.length; j++) {
						if (threads[j].getTopStackFrame() != null) {
							currentLine = threads[j].getTopStackFrame()
									.getLineNumber();
							thread = threads[j];
							currentKeyword = PopartSourceFileKeywordRegistry
									.getInstance()
									.getKeywordAtLine(currentLine);
							notifyObservers();
							return;
						}
					}
				}
			}
		} catch (DebugException e) {
			logger.warn("Generated log statement",e);
		}

	}

	private void notifyObservers() {
		for (Iterator<IObserver> iterator = observers.iterator(); iterator
				.hasNext();) {
			IObserver o = iterator.next();
			o.update();
		}

	}

	private void stepToKeywordAtRunToLine() {
		if (runToLine != -1) {
			setBreakPointAtLine(runToLine);
		}
	}

	/**
	 * tries to set a breakpoint after this se
	 * 
	 * @param se
	 */
	private void setBreakPointAfterSE(
			PopartSourceFileStructuredElementKeyword se) {
		int line = PopartSourceFileKeywordRegistry.getInstance()
				.nextLineWithKeyword(se.getEndLine());
		setBreakPointAtLine(line);
	}

	/**
	 * Sets temporary breakpoint at the line
	 * 
	 * @param line
	 */
	private void setBreakPointAtLine(int line) {
		setBreakPointAtLine(line, true);
	}

	/**
	 * Sets temporary breakpoint. Only if delete is true the breakpoint will be
	 * deleted at suspend.
	 * 
	 * @param line
	 * @param delete
	 */
	private void setBreakPointAtLine(int line, boolean delete) {
		if (getBreakPointAtLine(line) == null) {
			try {
				PopartInvisibleLineBreakpoint tempbp;
				tempbp = new PopartInvisibleLineBreakpoint(PopartDebugUtils
						.getGroovyTempFile(), PopartDebugUtils.getClassName(),
						line, -1, -1, 0, true, Collections.emptyMap());
				tempbp.setTemporary(true);
				if (delete) {
					lastBreakpoints.add(tempbp);
				}
				logger.info("+@line " + line + ". delete:" + delete);
			} catch (DebugException e) {
				logger.warn("Generated log statement",e);
			}
		}
	}

	private void resume() {
		try {
			thread.resume();
		} catch (DebugException e) {
			logger.warn("Generated log statement",e);
		}
	}

	/**
	 * Handles behavior after reaching a breakpoint (that could be temporally)
	 */
	public void handleDebugEvents(DebugEvent[] events) {

		if (containsEvent(events, DebugEvent.SUSPEND)) {
			if (secondResume) {
				secondResume = false;
				resume();
				return;
			}
			getInfos();
			if (currentLine == runToLine) {
				clearBreakPoints();
			}
		}
		if (containsEvent(events, DebugEvent.TERMINATE)) {
			clearAllBreakPoints();
		}

	}

	private boolean containsEvent(DebugEvent[] events, int event) {
		boolean result = false;
		for (int i = 0; i < events.length; i++) {
			if (events[i].getKind() == event) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * returns temporary breakpoint at line if present else null
	 * 
	 * @param line
	 * @return
	 */
	private PopartInvisibleLineBreakpoint getBreakPointAtLine(int line) {
		IBreakpointManager bpManager = DebugPlugin.getDefault()
				.getBreakpointManager();
		IBreakpoint[] bp = bpManager.getBreakpoints();
		for (int i = 0; i < bp.length; i++) {
			if (bp[i] instanceof PopartInvisibleLineBreakpoint) {
				try {
					if (((PopartInvisibleLineBreakpoint) bp[i]).getLineNumber() == line) {
						return (PopartInvisibleLineBreakpoint) bp[i];
					}
				} catch (CoreException e) {
					logger.warn("Generated log statement",e);
				}
			}
		}
		return null;
	}

	/**
	 * removes all PopartInvisibleLineBreakpoint inside a SE
	 * 
	 * @param se
	 */
	private void clearBreakpointsInSE(
			PopartSourceFileStructuredElementKeyword se) {

		for (int i = 0; i < se.numChildren(); i++) {

			PopartSourceFileKeyword child = se.getKeyword(i);

			PopartInvisibleLineBreakpoint bp = getBreakPointAtLine(child
					.getLineNr());
			try {
				logger.info("clearing inside SE line "
						+ child.getLineNr());
				DebugPlugin.getDefault().getBreakpointManager()
						.removeBreakpoint(bp, true);
			} catch (CoreException e) {
				logger.warn("Generated log statement",e);
			}

			// if child is SE continue in this SE
			if (child instanceof PopartSourceFileStructuredElementKeyword) {
				clearBreakpointsInSE((PopartSourceFileStructuredElementKeyword) child);
			}
		}

	}

	/**
	 * Deletes all breakpoints, that were set in the last step.
	 */
	private void clearBreakPoints() {
		try {
			for (int i = 0; i < lastBreakpoints.size(); i++) {
				PopartInvisibleLineBreakpoint bp = lastBreakpoints.get(i);

				if (bp.isTemporary()) {

					logger.info("deleting temporary at line "
							+ bp.getLineNumber());

					DebugPlugin.getDefault().getBreakpointManager()
							.removeBreakpoint(bp, true);

				} else {
					if (!bp.isEnabled()) {
						bp.setEnabled(true);
						logger.info("enabling breakpoint at line "
								+ bp.getLineNumber());
					}
				}
			}
			lastBreakpoints.clear();

		} catch (CoreException e) {
			logger.warn("Generated log statement",e);
		}

	}

	/**
	 * Clears all breakpoints after termination of the debug session
	 */
	private void clearAllBreakPoints() {
		try {

			IBreakpoint[] bp = DebugPlugin.getDefault().getBreakpointManager()
					.getBreakpoints();

			for (int i = 0; i < bp.length; i++) {
				if (bp[i] instanceof PopartInvisibleLineBreakpoint) {
					DebugPlugin.getDefault().getBreakpointManager()
							.removeBreakpoint(bp[i], true);
				}
			}

			lastBreakpoints.clear();

		} catch (CoreException e) {
			logger.warn("Generated log statement",e);
		}

	}

}
