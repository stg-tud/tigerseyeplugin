package de.tud.stg.popart.eclipse.core.debug;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugException;
//FIXME is a solution without a discouraged access possible
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaLineBreakpoint;

/**
 * Represents a breakpoint that is to be used for internal DSL debugging purposes only.
 * PopartInvisibleLineBreakpoints are set during debug time in the temporarily generated
 * groovy file.
 * The temporary flag which can be set/read via setTemporay/getTemporary is used
 * to indicate whether is breakpoint is to be removed immediately (which means the
 * breakpoint is temporary) after the breakpoint has been reached.
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartInvisibleLineBreakpoint extends JavaLineBreakpoint {
	
	private static final String POPART_INVISIBLE_LINE_BREAKPOINT = "de.tud.stg.popart.eclipse.popartInvisibleLineBreakpointMarker";
	private boolean temporary = false;
	
	/**
	 * @see JDIDebugModel#createLineBreakpoint(IResource, String, int, int, int, int, boolean, Map)
	 */
	public PopartInvisibleLineBreakpoint(IResource resource, String typeName, int lineNumber, int charStart, int charEnd, int hitCount, boolean add, Map attributes) throws DebugException {
		super(resource, typeName, lineNumber, charStart, charEnd, hitCount, add, attributes, POPART_INVISIBLE_LINE_BREAKPOINT);
	}
	
	/**
	 * Returns the type of marker associated with PopartInvisibleLineBreakpoints
	 */
	public static String getMarkerType() {
		return POPART_INVISIBLE_LINE_BREAKPOINT;
	} 
	
	/**
	 * Returns whether this PopartInvisibleLineBreakpoint is temporary.
	 * 
	 * @return true, if the breakpoint is temporary; false otherwise
	 */
	public boolean isTemporary() {
		return temporary;		
	}

	/**
	 * Sets this breakpoint to be temporary, i.e. the breakpoint is to be removed
	 * after reaching it.
	 * 
	 * @param temporary Set it to true, if the breakpoint is temporary; use false otherwise
	 */
	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
	}
}
