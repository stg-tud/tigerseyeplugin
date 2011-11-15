package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.AnnotationExtractor;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.IllegalAnnotationFormat;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TextualTransformation;

public class KeywordTranslationTransformation implements TextualTransformation {
    private static final String possiblyInsufficientCharset = "This might be insufficient if characters are used that depend on a specific encoding.";

    private static final Logger logger = LoggerFactory.getLogger(KeywordTranslationTransformation.class);

    /**
     * 
     * 
     * @author Leo_Roos
     * 
     */
    private static class TranslationAlias {

	private static final ConcurrentHashMap<String, Pattern> patternCache = new ConcurrentHashMap<String, Pattern>();

	public TranslationAlias(String from, String to) {
	    this.from = from;
	    this.to = to;
	    this.fromPattern = compileLiteralWordPattern(from);
	}

	/**
	 * Pattern:
	 * 
	 * <pre>
	 * \b -> word boundary
	 * \Q...\E -> take word in between literally
	 * 
	 * <pre>
	 */
	private Pattern compileLiteralWordPattern(String from) {
	    Pattern pattern = patternCache.get(from);
	    if (pattern == null) {
		pattern = Pattern.compile("\\b(\\Q" + from + "\\E)\\b");
		patternCache.put(from, pattern);
	    }
	    return pattern;
	}

	public final String from;
	public final String to;
	public final Pattern fromPattern;

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
		return false;
	    }
	    if (obj == this) {
		return true;
	    }
	    if (obj.getClass() != this.getClass()) {
		return false;
	    }
	    KeywordTranslationTransformation.TranslationAlias other = (KeywordTranslationTransformation.TranslationAlias) obj;
	    return new EqualsBuilder().append(from, other.from).append(to, other.to).isEquals();
	}

	@Override
	public int hashCode() {
	    return new HashCodeBuilder(15, 919).append(from).append(to).toHashCode();
	}

    }

    @Override
    public StringBuffer transform(Context context, StringBuffer sb) {

	AnnotationExtractor<Translation> annotationExtractor = new AnnotationExtractor<Translation>(Translation.class);
	annotationExtractor.setInput(sb.toString());

	List<Translation> translations = findAndRemoveTranslationAnnotations(sb, annotationExtractor);

	Set<TranslationAlias> aliasesCollector = collectTranslationAliases(context, translations);

	sb = replaceAliases(aliasesCollector, sb);

	return sb;
    }

    private Set<TranslationAlias> collectTranslationAliases(Context context, List<Translation> translations) {
	Set<TranslationAlias> aliasesCollector = new HashSet<TranslationAlias>();
	for (Translation translation : translations) {
	    String translationPropertiesFileString = translation.file();
	    List<TranslationAlias> aliases = collectTranslationsForOneMapping(context, translationPropertiesFileString);
	    if (aliases == null)
		logger.warn("No translations for {} determined.", translationPropertiesFileString);
	    else
		aliasesCollector.addAll(aliases);
	}
	return aliasesCollector;
    }

    private List<Translation> findAndRemoveTranslationAnnotations(StringBuffer sb,
	    AnnotationExtractor<Translation> annotationExtractor) {
	LinkedList<int[]> bounds = new LinkedList<int[]>();
	LinkedList<Translation> translations = new LinkedList<Translation>();
	Translation nextTranslation = findNextValidAnnotation(annotationExtractor);
	while (nextTranslation != null) {
	    translations.add(nextTranslation);
	    bounds.add(annotationExtractor.getBounds());
	    nextTranslation = findNextValidAnnotation(annotationExtractor);
	}

	for (int[] b : bounds) {
	    int start = b[0];
	    int end = b[1];
	    String toDelete = sb.substring(start, end);
	    sb.delete(start, end);
	    logger.trace("deleting annotation {} in anticipated bounds {}", toDelete, b[0] + " - " + b[1]);
	}
	return translations;
    }

    private Translation findNextValidAnnotation(AnnotationExtractor<Translation> annotationExtractor) {
	while (true) {
	    try {
		return annotationExtractor.find();
	    } catch (IllegalAnnotationFormat e) {
		logger.trace("resource had illegal annotation format", e);
	    }
	}
    }

    private @CheckForNull
    List<TranslationAlias> collectTranslationsForOneMapping(Context context, String translationPropertiesFileString) {

	IFile translationFile = buildTranslationFileLocation(translationPropertiesFileString,
		context.getTransformedFile());

	if (!translationFile.exists()) {
	    logger.debug("translations file does not exist: {}", translationFile);
	    return null;
	}

	Properties loadedProps = getLoadedPropertiesFor(translationFile);
	if (loadedProps == null)
	    return null;

	LinkedList<TranslationAlias> collectedAliases = new LinkedList<TranslationAlias>();
	for (Entry<Object, Object> e : loadedProps.entrySet()) {
	    String key = (String) e.getKey();
	    String value = (String) e.getValue();
	    logger.trace("found alias from {} to {}", value, key);
	    TranslationAlias alias = new TranslationAlias(value, key);
	    collectedAliases.add(alias);
	}
	return collectedAliases;
    }

    private @CheckForNull
    Properties getLoadedPropertiesFor(IFile translationFile) {
	InputStream contents = getContentOrNull(translationFile);
	if (contents == null)
	    return null;

	String charset = determineCharsetToUse(translationFile);

	InputStreamReader reader = null;
	Properties p = null;
	try {
	    reader = createReader(contents, charset);
	    p = new Properties();
	    p.load(reader);

	} catch (IOException e1) {
	    return null;
	} finally {
	    IOUtils.closeQuietly(reader);
	}
	return p;
    }

    private InputStreamReader createReader(InputStream contents, String charset) {
	InputStreamReader reader;
	try {
	    reader = new InputStreamReader(contents, charset);
	} catch (UnsupportedEncodingException e) {
	    logger.warn("Charset is not supported will use default. " + possiblyInsufficientCharset);
	    reader = new InputStreamReader(contents);
	}
	return reader;
    }

    private InputStream getContentOrNull(IFile translationFile) {
	InputStream contents;
	try {
	    contents = translationFile.getContents();
	} catch (CoreException e) {
	    logger.error("expected to find resource {}. But it cannot be accessed.", translationFile, e);
	    return null;
	}
	return contents;
    }

    private String determineCharsetToUse(IFile translationFile) {
	String charset;
	try {
	    charset = translationFile.getCharset();
	} catch (CoreException e1) {
	    logger.warn("failed to determine file specific charset trying to use project specific."
		    + possiblyInsufficientCharset, e1);
	    try {
		charset = translationFile.getProject().getDefaultCharset();
	    } catch (CoreException e) {
		logger.warn("failed to determine project specific charset trying to use system default."
			+ possiblyInsufficientCharset, e1);
		charset = Charset.defaultCharset().name();
	    }
	}
	return charset;
    }

    private IFile buildTranslationFileLocation(String translationPropertiesFileString, IFile transformedFile) {
	IProject project = transformedFile.getProject();
	IPath projectRelativePath = transformedFile.getProjectRelativePath();
	IFile translationFile;
	if (translationPropertiesFileString.startsWith("/")) {
	    translationFile = project.getFile(translationPropertiesFileString);
	} else {
	    IPath fileRelativePath = projectRelativePath.removeLastSegments(1).append(translationPropertiesFileString);
	    translationFile = project.getFile(fileRelativePath);
	}
	return translationFile;
    }

    private StringBuffer replaceAliases(Set<TranslationAlias> aliasesCollector, StringBuffer sb) {
	StringBuffer out = sb;
	for (TranslationAlias p : aliasesCollector) {
	    Matcher matcher = p.fromPattern.matcher(out);
	    out = new StringBuffer();
	    while (matcher.find()) {
		matcher.appendReplacement(out, p.to);
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
