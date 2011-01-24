package de.lroos;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sfl4jtransformer {

	private static final Logger logger = LoggerFactory
			.getLogger(Sfl4jtransformer.class);
	private File outPutFolder;
	private File rootFolder;
	private boolean modified;

	public Sfl4jtransformer(File rootFolder) {
		this(rootFolder, rootFolder);
	}

	public Sfl4jtransformer(File rootFolder, File outputFolder) {
		this.outPutFolder = outputFolder;
		this.rootFolder = rootFolder;
	}

	public void transform() throws IOException {
		Collection listFiles = FileUtils.listFiles(rootFolder,
				new String[] { "java" }, true);

		for (Object object : listFiles) {
			if (object instanceof File) {
				File javaFile = (File) object;
				transformToLoggingClass(javaFile);
			} else
				logger.error("Expected files, got {}", object);
		}
	}

	private void transformToLoggingClass(File javaFile) throws IOException {
		String path = javaFile.getPath();
		int indexOf = path.indexOf("/");
		String relPath = path.substring(indexOf + 1);
		File targetFile = new File(outPutFolder, relPath);

		transformFromTo(javaFile, targetFile);

	}

	private void transformFromTo(File javaFile, File targetFile)
			throws IOException {
		try {
			String origin = FileUtils.readFileToString(javaFile);
			String targetContent = makeStringTransformations(origin);
			logger.info("transforming from {} to {} ", javaFile, targetFile);
			FileUtils.writeStringToFile(targetFile, targetContent);
		} catch (NoTransformationsMadeException e) {
			logger.debug("Ignoring: {}" + javaFile);
		}
	}

	private String makeStringTransformations(final String origin)
			throws NoTransformationsMadeException {
		try {
			String result = origin;
			result = replaceSystemOuts(result);
			result = replaceSysErrs(result);
			result = replaceStackTracePrints(result);
			if (modified) {
				result = addPackageImports(result);
				return result;
			} else {
				throw new NoTransformationsMadeException();
			}
		} finally {
			modified = false;
		}

	}

	private String replaceStackTracePrints(String result) {
		int eCounter = 3;
		for (int i = 0; i < eCounter; i++) {
			String ePrefix = "e";
			if (i > 0)
				ePrefix += i;
			String printStatement = ePrefix + ".printStackTrace\\(\\)";
			result = containsPatternReplaceWith(result, printStatement,
					"logger.warn(\"Generated log statement\"," + ePrefix
							+ ")");
		}
		return result;
	}

	private String replaceSysErrs(String result) {
		String newResult = containsPatternReplaceWith(result,
				"System.err.println", "logger.error");
		return newResult;
	}

	private String replaceSystemOuts(String result) {
		String newResult = containsPatternReplaceWith(result,
				"System.out.println", "logger.info");
		return newResult;
	}

	private String containsPatternReplaceWith(String origin, String toReplace,
			String withReplace) {
		if (!modified) {
			modified = origin.contains(toReplace);
		}
		String replaceAll = origin.replaceAll(toReplace, withReplace);
		return replaceAll;
	}

	private String addPackageImports(String origin) {

		String import1 = "\nimport org.slf4j.Logger;";
		String import2 = "\nimport org.slf4j.LoggerFactory;";

		Pattern pattern = Pattern.compile("package.*;");

		Matcher packageStatementMatcher = pattern.matcher(origin);
		boolean find = packageStatementMatcher.find();
		if (!find) {
			logger.error("Pattern seems to be wrong: " + pattern.toString());
		}
		logger.info("Found:{}",
 origin.substring(
				packageStatementMatcher.start(), packageStatementMatcher.end()));

		int end = packageStatementMatcher.end();

		StringBuilder result = new StringBuilder(origin.substring(0, end));

		if (!origin.contains(import1))
			result.append(import1);
		if (!origin.contains(import2))
			result.append(import2);

		result.append(origin.substring(end));

		String addedImports = result.toString();
		
		logger.info("add loggerField");
		
		Pattern compile = Pattern
				.compile("[public |private |protected ]{0,1}[ final ]{0,1}class ");
		
		Matcher classBeginningMatcher = compile.matcher(addedImports);
		classBeginningMatcher.find();

		logger.info("Found class declaration: {}", addedImports.substring(
				classBeginningMatcher.start(), classBeginningMatcher.end()));
		int beginClassName = classBeginningMatcher.end();
		
		classBeginningMatcher.usePattern(Pattern.compile("\\{"));
		classBeginningMatcher.find();
		
		int beginClass = classBeginningMatcher.end();
		
		String[] split = addedImports.substring(beginClassName).split("[ |<]");
		String className = split[0];
		
		logger.info("For className {} inserting ", className);

		StringBuilder withLogger = new StringBuilder(addedImports.substring(0,
				beginClass));
		withLogger
				.append("\nprivate static final Logger logger = LoggerFactory.getLogger("
						+ className + ".class);\n");
		withLogger.append(addedImports.substring(beginClass));
		
		return withLogger.toString();
	}

}
