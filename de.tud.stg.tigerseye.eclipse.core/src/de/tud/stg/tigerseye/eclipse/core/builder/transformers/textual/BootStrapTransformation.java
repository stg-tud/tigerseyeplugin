package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.dslsupport.DSLInvoker;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationConstants;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TextualTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationUtils;

// XXX(Leo_Roos;Nov 18, 2011) General problem of the regexes is that they don't consider comments in between keywords and parenthesis for example.  
public class BootStrapTransformation implements TextualTransformation {

    private static final Logger logger = LoggerFactory.getLogger(BootStrapTransformation.class);


	@Override
	public StringBuffer transform(Context context, StringBuffer sb) {
		logger.trace("starting bootstrapping");
		sb = this.addBootstrap(context, sb);
		return sb;
	}

	private final static Pattern regexp = Pattern.compile(
			"(.*?)([\\w,]+)\\s*\\(\\s*name\\s*:\\s*'(\\w+)'\\s*\\)(\\s*)\\{(.*)\\}(.*)", Pattern.DOTALL);

    private final static Pattern tigerprefix = Pattern.compile(
	    "(.*?)(tigerseyeblock)\\s*\\(\\s*(\\w+)?\\s*\\)(\\s*)\\{(.*)\\}(.*)", Pattern.DOTALL);

	private final static Pattern dslRegexp = Pattern.compile("(\\w+)");

    private StringBuffer addBootstrap(Context context, StringBuffer input) {

	Matcher dslNamesMatcher = regexp.matcher(input);

		StringBuffer out = new StringBuffer();


	// DSLs are known from context a consistent indication syntax is
	// sufficient
	Matcher tigerformmatcher = tigerprefix.matcher(input);

	Matcher effectiveMatcher;

	boolean dslNamesFormFound = dslNamesMatcher.find();
	if (dslNamesFormFound) {
	    effectiveMatcher = dslNamesMatcher;
	} else if (tigerformmatcher.find()) {
	    effectiveMatcher = tigerformmatcher;
	} else {
	    effectiveMatcher = null;
	}

	List<DSLDefinition> dsls = context.getDsls();
	LinkedList<String> imports = new LinkedList<String>();

	// if (effectiveMatcher != null) {
	//
	// // redundant because known from context
	// String dslExtensions = effectiveMatcher.group(2);
	// logger.trace("group: " + dslExtensions);
	//
	// Matcher matcher2 = dslRegexp.matcher(dslExtensions);
	//
	// // redundant
	// // List<Class<? extends DSL>> dsls = new LinkedList<Class<? extends
	// // DSL>>();
	// //
	// // while (matcher2.find()) {
	// // String dslExtension = matcher2.group();
	// //
	// //
	// //
	// // Class<? extends DSL> e = context
	// // .getDSLForExtension(dslExtension);
	// // if (e != null)
	// // dsls.add(e);
	// // else {
	// // logger.debug("no class found for " + dslExtension);
	// // }
	// // }
	//
	//
	//
	// if (dsls.isEmpty()) {
	// logger.warn("Searched for {} but no dsls found",
	// context.getDsls());
	// out = input;
	// }
	// else {

	if (effectiveMatcher != null) {
	    logger.trace("found dsls: {}", Arrays.toString(dsls.toArray()));

	    StringBuilder sb = new StringBuilder("$1");

	    String name = effectiveMatcher.group(3);
	    if (name == null || name.isEmpty()) {
		name = "Unnamed_DSL";
	    }

	    // Should move this also to an AST operation

	    String dslInvokerName = DSLInvoker.class.getSimpleName();
	    sb.append("\n");
	    sb.append(dslInvokerName).append(".eval([");

	    if (dsls.size() == 1) {
		DSLDefinition dslDefinition = dsls.get(0);
		String dslClassName = getSimpleNameIfLoadableOrDescriptiveErrorStatement(dslDefinition);
		sb.append('\n');
		// sb.append("new ").append(dslClassName).append("()");
		// sb.append(".eval(name:'" + name + "')");
		sb.append(dslClassName).append(".class");

	    } else {

		// imports.add(InterpreterCombiner.class.getCanonicalName());
		// sb.append('\n');
		// sb.append("new ").append(InterpreterCombiner.class.getSimpleName()).append("([");

		for (DSLDefinition dsl : dsls) {
		    String dslName = getSimpleNameIfLoadableOrDescriptiveErrorStatement(dsl);
		    sb.append(dslName).append(".class").append(",");
		    // sb.append("new ").append(dslName).append("()").append(',').append(' ');
		}

		sb.delete(sb.length() - 1, sb.length());
		// sb.append("], [name:'" + name + "']).eval()");
	    }

	    sb.append("])");

	    // String group4 = effectiveMatcher.group(4);
	    // String group5 = effectiveMatcher.group(5);
	    // String group6 = effectiveMatcher.group(6);

	    sb.append("$4{$5}$6");

	    effectiveMatcher.appendReplacement(out, sb.toString());
	    effectiveMatcher.appendTail(out);
	} else {
	    // No Transformation
	    out = input;
	}

	
	// Import of DSLs already happens in PackageImprter
	// XXX(Leo_Roos;Nov 18, 2011) should make it consistent, only imoprt
	// that is left is InterpreterCombiner
	PackageImporter.addImports(imports, out);

		return out;
	}

	private String getSimpleNameIfLoadableOrDescriptiveErrorStatement(DSLDefinition dslDefinition) {
	    String dslClassName = dslDefinition.getKeyFor(DSLKey.EXTENSION) + "_not_resolvable";
	    if (dslDefinition.isDSLClassLoadable()) {
	        dslClassName = dslDefinition.getDSLClassChecked().getSimpleName();
	    }
	    return dslClassName;
	}

	@Override
	public String toString() {
	return getClass().getSimpleName() + ": " + super.toString();
	}

	@Override
	public Set<String> getAssurances() {
		return Collections.emptySet();
	}

	@Override
	public Set<String> getRequirements() {
		return Collections.emptySet();
	}

	@Override
	public Set<TransformationType> getSupportedFileTypes() {
		return TransformationUtils.getSetForFiletypes(FileType.TIGERSEYE);
	}

	@Override
	public String getDescription() {
		return "Makes first transformations. Default DSL dependencies are added and constructs available to all DSLs transformed";
	}

    @Override
    public int getBuildOrderPriority() {
	return TransformationConstants.BOOT_STRAP_TRANSFORMATION;
    }
}
