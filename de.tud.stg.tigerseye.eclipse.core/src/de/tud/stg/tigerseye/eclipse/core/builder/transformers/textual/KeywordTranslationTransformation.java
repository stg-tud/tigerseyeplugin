package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.parlex.parser.earley.Pair;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.AnnotationExtractor;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TextualTransformation;

public class KeywordTranslationTransformation implements TextualTransformation {
private static final Logger logger = LoggerFactory.getLogger(KeywordTranslationTransformation.class);


	private static final Collection<Pair<Pattern, String>> patternMapping = new LinkedList<Pair<Pattern, String>>();

	private static final AnnotationExtractor<Translation> annotationExtractor = new AnnotationExtractor<Translation>(
			Translation.class);

	private final Map<String, Set<String>> files = new HashMap<String, Set<String>>();

	public void addTranslations(Pair<String, String> p) {
		patternMapping.add(new Pair<Pattern, String>(Pattern.compile("\\b(\\Q" + p.getY() + "\\E)\\b"), p.getX()));
	}

	@Override
	public StringBuffer transform(Context context, StringBuffer sb) {

		annotationExtractor.setInput(sb.toString());
		Translation translation = null;

		Set<String> currentUsedTranslations = new HashSet<String>();
		Set<String> storedTranslations = files.get(context.getFileName());

		if (storedTranslations == null) {
			storedTranslations = new HashSet<String>();
			files.put(context.getFileName(), storedTranslations);
		}

		LinkedList<int[]> bounds = new LinkedList<int[]>();

	do {
	    translation = annotationExtractor.find();

	    if (translation != null) {
		bounds.add(annotationExtractor.getBounds());
		String f = translation.file();

		currentUsedTranslations.add(f);

		

		if (!storedTranslations.contains(f)) {

		    InputStreamReader reader = null;
		    Properties p = new Properties();
		    try {
			storedTranslations.add(f);

			try {
			    reader = new FileReader(f);
			} catch (FileNotFoundException e) {
			    logger.debug("Translation file not found.", e);
			}
			/*
			 * TODO(Leo Roos;Jun 26, 2011): this is in practice only
			 * code used by test cases. Although it could be useful
			 * the current implementation can not determine a
			 * Translation File on the classpath of a Tigerseye
			 * Project.
			 */
			Class classes[] = context.getDSLClasses();
			if (reader == null && classes.length > 0) {

			    InputStream resourceAsStream = classes[0]
				    .getResourceAsStream(f);
			    if (resourceAsStream != null)
				reader = new InputStreamReader(resourceAsStream);

			}

			if (reader != null) {
			    try {
				p.load(reader);

				for (Entry<Object, Object> e : p.entrySet()) {
				    String key = (String) e.getKey();
				    String value = (String) e.getValue();

				    logger.info("key: " + key + ", value: "
					    + value);
				    this.addTranslations(new Pair<String, String>(
					    key, value));
				}
			    } catch (IOException e) {
				logger.warn(
					"Could not read properties from translation file.",
					e);
			    }
			} else
			    logger.error(
				    "Failed to determine Translation file from {}.",
				    f);
		    } finally {
			IOUtils.closeQuietly(reader);
		    }

		}
	    }

	} while (translation != null);

		for (int[] b : bounds) {
			sb.delete(b[0], b[1]);
	    logger.debug("deleting " + b[0] + " - " + b[1]);
		}

		storedTranslations.retainAll(currentUsedTranslations);
		sb = this.replaceAliases(sb);

		return sb;
	}

	private StringBuffer replaceAliases(StringBuffer sb) {

		StringBuffer out = sb;

		for (Pair<Pattern, String> p : patternMapping) {
			Matcher matcher = p.getX().matcher(out);

			out = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(out, p.getY());
			}
			matcher.appendTail(out);
		}

		return out;
	}

	@Override
	public Set<String> getAssurances() {
	return Collections.emptySet();
	}

	@Override
	public String getDescription() {
		return "This Transformer allows the translation of all keywords of a selected DSL. Use one or more @Translation annotations to specify a properties file, where the keys are the strings to translate from and the values the strings to translate to.";
	}

	@Override
	public Set<String> getRequirements() {
	return Collections.emptySet();
	}

	@Override
	public Set<TransformationType> getSupportedFileTypes() {
		return TextualTransformationUtils.getSetForFiletypes(FileType.DSL);
	}

}
