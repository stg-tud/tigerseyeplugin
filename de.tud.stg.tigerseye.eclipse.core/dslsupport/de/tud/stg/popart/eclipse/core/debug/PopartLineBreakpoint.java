package de.tud.stg.popart.eclipse.core.debug;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaLineBreakpoint;

/**
 * Represents a line breakpoint in a Popart DSL program. A PopartLineBreakpoint
 * is created when a user edits a Popart source file and sets a breakpoint on a
 * specific line.
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartLineBreakpoint extends JavaLineBreakpoint {
	
	private static final String POPART_LINE_BREAKPOINT = "de.tud.stg.popart.eclipse.popartLineBreakpointMarker";
	
	/**
	 * Empty constructor; needed by Eclipse to restore a persisted breakpoint via reflection.
	 */
	public PopartLineBreakpoint() {
		super();
	}
	
	/**
	 * @see JDIDebugModel#createLineBreakpoint(IResource, String, int, int, int, int, boolean, Map)
	 */
	public PopartLineBreakpoint(IResource resource, String typeName, int lineNumber, int charStart, int charEnd, int hitCount, boolean add, Map attributes) throws DebugException {
		super(resource, typeName, lineNumber, charStart, charEnd, hitCount, add, attributes, POPART_LINE_BREAKPOINT);
	}
	
	/**
	 * Returns the type of marker associated with PopartLineBreakpoints
	 */
	public static String getMarkerType() {
		return POPART_LINE_BREAKPOINT;
	} 

}
