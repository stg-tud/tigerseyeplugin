/**
 * 
 */
package de.tud.stg.popart.dslsupport.analysis.syntax;

import de.tud.stg.tigerseye.dslsupport.Interpreter;

/**
 * This is an abstract interpreter that can be used to determine all keywords a DSL program uses.
 * @author dinkelaker
 */
public class UsedKeywordsAnalysis extends Interpreter {

	private final boolean DEBUG = true;
	
	/**
	 * When accessing a literal using its accessors, the analysis does not treat this as an operation (default true).
	 */
	public final boolean OPTION_IGNORE_ACCESSOR_KEYWORDS = true; 
	
	/**
	 * When containing nested abstractions, the analysis also analyses the nested structure (default true).
	 */
	public final boolean OPTION_IGNORE_NESTED_KEYWORDS = false; 
	 
	/**
	 * The set of used keywords by the program.
	 */
	private Set<String> keywords = new HashSet();

	/**
	 * The set of used literal keywords by the program.
	 */
	private Set<String> literals = new HashSet();
	
	/**
	 * The set of used operation keywords by the program.
	 */
	private Set<String> operations = new HashSet();
	
	/**
	 * The set of used abstraction keywords by the program.
	 */
	private Set abstractions = new HashSet();
	
    public Object missingMethod(String name, Object args) {
	    if (DEBUG) println this.getClass().getName()+":: DSL program uses keyword '$name'";
    	if (name.startsWith("get") && name.size() > 3) {
    		//Special case: a method call starting with "get" may also be mapped to the getter method
            def firstLetter = name[3]
        	def remainderLetters = name[4..name.size()-1];
    	    def keywordName = firstLetter.toLowerCase()+remainderLetters; 
    	    if (DEBUG) println this.getClass().getName()+":: DSL program uses getter literal keyword '$keywordName'";
    	    if (DEBUG) println this.getClass().getName()+":: \\-- adapted keyword name firstLetter='$firstLetter' remainderLetters='$remainderLetters' -> '$keywordName'";
    	    keywords.add(keywordName);
    	    literals.add(keywordName);
    	    if (OPTION_IGNORE_ACCESSOR_KEYWORDS) return;
    	}
  		  
    	if (name.startsWith("set") && name.size() > 3) {
    		//Special case: a method call starting with "set" may also be mapped to the setter method
            def firstLetter = name[3]
        	def remainderLetters = name[4..name.size()-1];
    	    def keywordName = firstLetter.toLowerCase()+remainderLetters; 
    	    if (DEBUG) println this.getClass().getName()+":: DSL program uses setter literal keyword '$keywordName'";
    	    if (DEBUG) println this.getClass().getName()+":: \\-- adapted keyword name firstLetter='$firstLetter' remainderLetters='$remainderLetters' -> '$keywordName'";
    	    keywords.add(keywordName);
    	    literals.add(keywordName);
    	    if (OPTION_IGNORE_ACCESSOR_KEYWORDS) return;
    	}
 	
    	//default case: a method called 
    	if (DEBUG) println this.getClass().getName()+":: DSL program uses keyword '$name' with args=${args} args=${args.length}";
    	keywords.add(name);
  		
    	Object lastParam = null;
    	if (DEBUG) println this.getClass().getName()+":: \\-- 1";    	    	
    	if (args.length > 0) {
        	if (DEBUG) println this.getClass().getName()+":: \\-- 2";    	    	
    		lastParam = args[args.length - 1];
        	if (DEBUG) println this.getClass().getName()+":: \\-- 3";    	    	
    	}
    	if (DEBUG) println this.getClass().getName()+":: \\-- 4";    	    	

    	if (DEBUG) println this.getClass().getName()+":: \\-- args[last]="+lastParam;    	    	
    	if (DEBUG) println this.getClass().getName()+":: \\-- args[last].class=${lastParam?.class}";    	    	
    	if (lastParam instanceof Closure) {
            //Special case: nested abstraction 
    		if (DEBUG) println this.getClass().getName()+":: DSL program uses nested abstraction keyword '$name'";
        	abstractions.add(name);    		
        	if (!OPTION_IGNORE_NESTED_KEYWORDS) lastParam.call();
    	} else {
    		//Default case: operation
    	   	if (DEBUG) println this.getClass().getName()+":: DSL program uses operation keyword '$name'";
        	operations.add(name);
    	}
	}
    
    public void getProperty(String name, Object value) { 
		if (DEBUG) println this.getClass().getName()+":: DSL program uses literal keyword '$name'";
		keywords.add(name);
	    literals.add(name);
    }
    
    public Object setProperty(String name) { 
		if (DEBUG) println this.getClass().getName()+":: DSL program uses literal keyword '$name'";
		keywords.add(name);
	    literals.add(name);
    }
    
    public Set<String> getKeywords() { return keywords; }  
    public Set<String> getLiterals() { return literals; }  
    public Set<String> getOperations() { return operations; }  
    public Set<String> getAbstractions() { return abstractions; }  
}
