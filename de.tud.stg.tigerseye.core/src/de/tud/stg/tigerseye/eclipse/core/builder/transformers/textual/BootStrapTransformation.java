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

import de.tud.stg.popart.builder.transformers.Context;
import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.popart.builder.transformers.TextualTransformation;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.popart.dslsupport.InterpreterCombiner;

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

	private final static Pattern dslRegexp = Pattern.compile("(\\w+)");

	private StringBuffer addBootstrap(Context context, StringBuffer input) {

		Matcher matcher = regexp.matcher(input);

		StringBuffer out = new StringBuffer();

		LinkedList<String> imports = new LinkedList<String>();

		if (matcher.find()) {
			String dslExtensions = matcher.group(2);
			logger.trace("group: " + dslExtensions);

			Matcher matcher2 = dslRegexp.matcher(dslExtensions);

			List<Class<? extends DSL>> dsls = new LinkedList<Class<? extends DSL>>();

			while (matcher2.find()) {
				String dslExtension = matcher2.group();
				Class<? extends DSL> e = context.dslClasses.get(dslExtension);
		if (e != null)
				dsls.add(e);
		else {
		    logger.debug("no class found for " + dslExtension);
		}
			}

			if (dsls.isEmpty()) {
			    	logger.error("No dsls found");
				return input;
			}
			logger.trace("found dsls: {}", Arrays.toString(dsls.toArray()));

			StringBuilder sb = new StringBuilder("$1");

			for (Class<?> clazz : dsls) {
				imports.add(clazz.getCanonicalName());
			}

			if (dsls.size() == 1) {
				sb.append('\n');
				sb.append("new ").append(dsls.get(0).getSimpleName()).append("()");

				sb.append(".eval(name:'$3')");
			} else {
		// "de.tud.stg.popart.dslsupport.InterpreterCombiner";
		imports.add(InterpreterCombiner.class.getCanonicalName());
				sb.append('\n');
				sb.append("new InterpreterCombiner([");

				for (Class<? extends DSL> dsl : dsls) {
					sb.append("new ").append(dsl.getSimpleName()).append("()").append(',').append(' ');
				}

				sb.delete(sb.length() - 2, sb.length());
				sb.append("], [name:'$3']).eval()");
			}

			sb.append("$4{$5}$6");

			matcher.appendReplacement(out, sb.toString());
		}

		matcher.appendTail(out);

		this.addImports(imports, out);

		return out;
	}

	private void addImports(LinkedList<String> imports, StringBuffer out) {
		Matcher matcher = packagePosition.matcher(out);

		int position = 0;

		if (matcher.find()) {
			position = matcher.end();
		}

		StringBuilder sb = new StringBuilder();

		for (String im : imports) {
			sb.append("import ").append(im).append(';');
			sb.append('\n');
		}

		out.insert(position, sb);
	}

	private static Pattern packagePosition = Pattern.compile("package [A-Za-z0-9\\.]+?;?\\s+");

	@Override
	public String toString() {
		return "bootStrap: " + super.toString();
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
		return TransformationUtils.getSetForFiletypes(FileType.TIGERSEYE);
	}

	@Override
	public String getDescription() {
		return "Makes first transformations. Default DSL dependencies are added and constructs available to all DSLs transformed";
	}
}
