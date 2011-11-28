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
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.RegExCollection;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TextualTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationConstants;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationUtils;

/**
 * Imports necessary packages. Only searches for a valid package declaration and
 * adds imports afterwards.
 * 
 * @author Leo_Roos
 * 
 */
public class PackageImporter implements TextualTransformation {

    @Override
    public String transform(Context context, String sb, Map<String, Object> data) {
	sb = this.importPackages(context, sb);
	return sb;
    }

    private String importPackages(Context context, String input) {

	LinkedList<String> imports = new LinkedList<String>();
	// working to unify under DSLInvoker
	imports.add(DSLInvoker.class.getCanonicalName());
	for (Class<?> clazz : context.getDSLClasses()) {
	    imports.add(clazz.getCanonicalName());
	}


	String result = addImports(imports, input);
	return result;
    }

    private static Pattern packagePosition = RegExCollection.packagePattern;

    public static String addImports(LinkedList<String> imports, String input) {
	Matcher matcher = packagePosition.matcher(input);

	int position = 0;

	if (matcher.find()) {
	    position = matcher.end();
	}

	StringBuilder importsString = new StringBuilder();

	for (String im : imports) {
	    importsString.append("\nimport ").append(im).append(';');
	}

	return new StringBuffer(input).insert(position, importsString).toString();
    }

    @Override
    public String toString() {
	return "PackageImporter:" + super.toString();
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
	return TransformationUtils.FILE_TYPE_SET;
    }

    @Override
    public String getDescription() {
	return "Adds necessary import statements specific for each DSL. If no valid package has been declared the result might be unexpected.";
    }

    @Override
    public int getBuildOrderPriority() {
	return TransformationConstants.PACKAGE_IMPORTER_TRANSFORMATION;
    }

}
