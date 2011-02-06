package de.tud.stg.popart.eclipse.core.debug.model.keywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A PopartStructuredElementKeyword represents a structured
 * element in a DSL.
 * A structured element can be thought of a keyword that
 * contains other keywords.
 * 
 * For example, see TrivalentDSL which is a trivalent logic
 * DSL. "Repeat" is a structured element.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartStructuredElementKeyword extends PopartParameterKeyword {
private static final Logger logger = LoggerFactory.getLogger(PopartStructuredElementKeyword.class);

	
	public final static int NAMED = 0;
	public final static int EXPLICIT = 1;	
	private int parameterStyle = NAMED;
	protected String returnType;
	
	/**
	 * Constructs an empty PopartStructuredElementKeyword
	 */
	public PopartStructuredElementKeyword() {
		super();
	}

	/**
	 * Constructs a new PopartStrucuturedElementKeyword with
	 * the given name.
	 * 
	 * @param name The name
	 */
	public PopartStructuredElementKeyword(String name) {
		super(name);
		order = 2;
	}
	
	/**
	 * Returns a string representation of this keyword which is used for
	 * code generation.
	 * 
	 * @return The generated code string.
	 */
	@Override
	public String getParameterString() {
		String result = "";
		logger.info(String.valueOf(parameterStyle));
		switch (parameterStyle) {
		case NAMED:			
			result = "HashMap params";
			break;
		case EXPLICIT:
			result = super.getParameterString();
			break;
		}
		result += ", Closure closure";
		return result;
	}

	/**
	 * Sets the parameter style to use for this structured element.
	 * Normal Java parameter passing is based on an ordered list / array
	 * of parameter values. This is called EXPLICIT in the following.
	 * Named Parameter passing is where we name the parameters individually
	 * rather than relying on their order. This is called NAMED in the following.
	 * 
	 * @param style PopartStructuredElementKeyword.NAMED for named parameter passing
	 * 				PopartStructuredElementKeyword.EXPLICIT for explicit parameter passing
	 */
	public void setParameterStyle(int style) {
		this.parameterStyle = style;
	}
	
	/**
	 * Returns the current parameter style for this PopartStructuredElementKeyword.
	 * 
	 * @return The parameter style
	 */
	public int getParameterStyle() {		
		return this.parameterStyle;
	}
	
	/**
	 * Sets the return type of this operation. This is a Java or custom type.
	 * 
	 * @param returnType The return type
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * Returns the return type of this operation.
	 * 
	 * @return The return type
	 */
	public String getReturnType() {
		return returnType;
	}

}
