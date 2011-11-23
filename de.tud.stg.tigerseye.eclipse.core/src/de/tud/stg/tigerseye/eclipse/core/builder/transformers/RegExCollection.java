package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.regex.Pattern;

/**
 * A collection of regular expressions that should be used consistently.
 * 
 * The regExs in general do not consider any context e.g. if the pattern is
 * partially found within a string.
 * 
 * @author Leo_Roos
 * 
 */
public interface RegExCollection {

    Pattern blockComment = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    Pattern lineComment = Pattern.compile("//.*");

    /**
     * a valid package name component accoording to
     * 
     * <a href=
     * "http://java.sun.com/docs/books/jls/third_edition/html/names.html#6.8%29"
     * >http://java.sun.com/docs/books/jls/third_edition/html/names.html#6.8
     * %29}</a>
     * 
     */
    String validSinglePackage = "[a-zA-Z][a-zA-Z_$0-9]*";

    /**
     * searches for complete package names
     */
    String packageConventionLocal = validSinglePackage + "(\\." + validSinglePackage + ")*";

    /**
     * A package declaration pattern with an optional semicolon
     */
    Pattern packagePattern = Pattern.compile("(package\\s+" + packageConventionLocal + "(;)?)", Pattern.DOTALL);

    /**
     * Finds all valid simple java class names
     */
    String classPattern = "[a-zA-Z][a-zA-Z_$0-9]*";
    String optionalSemicolon = "(;)?";

    /**
     * finds valid import and import static statements with an optional
     * semicolon to support groovy and java.
     */
    Pattern imports = Pattern.compile("import\\s+(static\\s)?((" + packageConventionLocal + ")+\\.)?" + "("
	    + classPattern + "|\\*)" + optionalSemicolon);

}
