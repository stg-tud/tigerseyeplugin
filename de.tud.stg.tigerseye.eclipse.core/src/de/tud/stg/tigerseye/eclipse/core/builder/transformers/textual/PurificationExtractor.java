package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;

import static de.tud.stg.tigerseye.eclipse.core.builder.transformers.RegExCollection.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TextualTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationConstants;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationUtils;

public class PurificationExtractor implements TextualTransformation {

    public static final String PURIFICATION_EXTRACTED_STRING_KEY = "de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual.PurificationExtractor.ExtractedString";

    @Override
    public String getDescription() {
	return "Extracts the parts of a file that usually should not be processed by the conversion mechanism."
		+ " This prevents for example packages and imports to be transformed if they contain keywords by accident."
		+ " Its counterpart the PurificationReinserter reinserts the removed code after the transformation process.";
    }

    @Override
    public Set<FileType> getSupportedFileTypes() {
	return TransformationUtils.FILE_TYPE_SET;
    }

    @Override
    public int getBuildOrderPriority() {
	return TransformationConstants.PURIFICATION_EXTRACTION_TRANSFORMATION;
    }

    @Override
    public String transform(Context context, String input, Map<String, Object> data) {

	int findCodePrologLastIndex = findCodePrologLastIndex(input);
	String extracted = input.substring(0, findCodePrologLastIndex);
	String codeLeftToProcess = input.substring(findCodePrologLastIndex);
	data.put(PURIFICATION_EXTRACTED_STRING_KEY, extracted);
	return codeLeftToProcess;
    }

    private int findCodePrologLastIndex(String input) {
	Pattern whiteSpacePattern = Pattern.compile("\\s+", Pattern.DOTALL);

	Pattern[] ps = { whiteSpacePattern, blockComment, lineComment, packagePattern, imports };

	List<Matcher> ms = new ArrayList<Matcher>(ps.length);
	for (Pattern pattern : ps) {
	    ms.add(pattern.matcher(input));
	}

	int pos = 0;
	while (true) {

	    boolean oneMatcherFoundSomething = false;
	    for (Matcher m : ms) {
		m.region(pos, input.length());
		if (m.lookingAt()) {
		    pos = m.end();
		    oneMatcherFoundSomething = true;
		    continue;
		}
	    }
	    if (!oneMatcherFoundSomething) {
		break;
	    }

	}
	return pos;
    }

    @Override
    public Set<String> getRequirements() {
	return Collections.emptySet();
    }

    @Override
    public Set<String> getAssurances() {
	return Collections.emptySet();
    }

}
