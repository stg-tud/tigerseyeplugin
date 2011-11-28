package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TextualTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationConstants;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationUtils;

public class PurificationReinserter implements TextualTransformation {

    private static final Logger logger = LoggerFactory.getLogger(PurificationReinserter.class);

    @Override
    public String getDescription() {
	return "Reinserst the extracted code part of the PurificationExtractor";
    }

    @Override
    public Set<FileType> getSupportedFileTypes() {
	return TransformationUtils.FILE_TYPE_SET;
    }

    @Override
    public int getBuildOrderPriority() {
	return TransformationConstants.PURIFICATION_REINSERTION_TRANSFORMATION;
    }

    @Override
    public String transform(Context context, String input, Map<String, Object> data) {
	Object object = data.get(PurificationExtractor.PURIFICATION_EXTRACTED_STRING_KEY);
	if (object != null) {
	    if (object instanceof String) {
		String pre = (String) object;
		return pre + input;
	    } else {
		logger.warn("data packet of unexpected type {}.", object);
	    }
	}

	return input;
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
