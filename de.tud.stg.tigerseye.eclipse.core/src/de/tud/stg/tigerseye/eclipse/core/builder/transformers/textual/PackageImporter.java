package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tud.stg.tigerseye.dslsupport.DSLInvoker;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TextualTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationConstants;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationUtils;

public class PackageImporter implements TextualTransformation {

    @Override
    public String transform(Context context, String sb, Map<String, Object> data) {
	sb = this.importPackages(context, sb);
	return sb;
    }

    private final static Pattern packageDeclaration = Pattern.compile(
	    "(.*)(package (?:.*?);)(.*?)(?:@EDSL\\(.*?\\))?(.*)", Pattern.DOTALL);

    private String importPackages(Context context, String input) {

	Matcher matcher = packageDeclaration.matcher(input);

	StringBuffer out = new StringBuffer();
	StringBuilder sb = new StringBuilder();

	LinkedList<String> imports = new LinkedList<String>();

	for (Class<?> clazz : context.getDSLClasses()) {
	    imports.add(clazz.getCanonicalName());
	}
	// working to unify under DSLInvoker
	imports.add(DSLInvoker.class.getCanonicalName());

	if (matcher.find() && !(context.getDSLClasses().length < 1)) {
	    sb.append("$1");
	    sb.append("$2\n");
	    sb.append('\n');

	    // imports.add(DSLInvoker.class.getCanonicalName());
	    sb.append("$3");
	    sb.append("$4");

	    matcher.appendReplacement(out, sb.toString());
	} else if (!(context.getDSLClasses().length < 1)) {
	    out.append(sb);
	}

	matcher.appendTail(out);

	String result = addImports(imports, out.toString());
	return result;
    }

    private static Pattern packagePosition = Pattern.compile("package [A-Za-z0-9\\.]+?;?\\s+");

    // XXX(Leo_Roos;Nov 18, 2011) only static until BootStrapTransformation no
    // longer necessary
    public static String addImports(LinkedList<String> imports, String input) {
	Matcher matcher = packagePosition.matcher(input);

	int position = 0;

	if (matcher.find()) {
	    position = matcher.end();
	}

	StringBuilder sb = new StringBuilder();

	for (String im : imports) {
	    sb.append("import ").append(im).append(';');
	    sb.append('\n');
	}

	return new StringBuffer(input).insert(position, sb).toString();
    }

    @Override
    public String toString() {
	return "package importer: " + super.toString();
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
    public Set<FileType> getSupportedFileTypes() {
	return TransformationUtils.getSetForFiletypes(FileType.values());
    }

    @Override
    public String getDescription() {
	return "Adds necessary import statements specific for each DSL.";
    }

    @Override
    public int getBuildOrderPriority() {
	return TransformationConstants.PACKAGE_IMPORTER_TRANSFORMATION;
    }

}
