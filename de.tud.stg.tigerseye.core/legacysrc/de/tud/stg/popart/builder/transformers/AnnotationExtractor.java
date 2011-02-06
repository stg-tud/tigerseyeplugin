package de.tud.stg.popart.builder.transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tud.stg.popart.builder.eclipse.EDSL;

/**
 * {@link AnnotationExtractor} is a class capable of scanning an input text for a given {
private static final Logger logger = LoggerFactory.getLogger(capable.class);
@link Annotation}. If the
 * annotation is found, it is dynamically instantiated with the found values and returned.
 * 
 * @author Kamil Erhard
 * 
 * @param <T>
 *            The Class of the Annotation to scan for
 */
public class AnnotationExtractor<T extends Annotation> {

	private static final Logger logger = LoggerFactory
			.getLogger(AnnotationExtractor.class);

	private final Pattern compile;
	private static final Map<Class<?>, ElementHandler<?>> elementHandlers = new HashMap<Class<?>, ElementHandler<?>>();

	private static Pattern packageImportPattern = Pattern.compile("import ([A-za-z0-9_\\$\\.]+)(?:;|\n)");

	static {
		setElementHandler(new IntegerElementHandler(), int.class, int[].class);
		setElementHandler(new LongElementHandler(), long.class, long[].class);
		setElementHandler(new FloatElementHandler(), float.class, float[].class);
		setElementHandler(new DoubleElementHandler(), double.class, double[].class);
		setElementHandler(new StringElementHandler(), String.class, String[].class);
		setElementHandler(new ClassElementHandler(), Class.class, Class[].class);
	}

	private static final EnumElementHandler enumElementHandler = new EnumElementHandler();

	private final Class<T> annotation;
	private static final Map<String, String> packageImports = new HashMap<String, String>();
	private Matcher matcher;
	private int startPosition;
	private int endPosition;

	private static final Pattern singleValueArrayPattern = Pattern.compile("\\{(.*?)\\}");
	private static final Pattern singleValuePattern = Pattern.compile("([^,]+)");

	public AnnotationExtractor(Class<T> annotation) {
		this.annotation = annotation;
		this.compile = Pattern.compile("@(\\Q" + annotation.getSimpleName() + "\\E|\\Q" + annotation.getCanonicalName()
				+ "\\E)\\s*\\((.*?)\\)");
	}

	private static void setElementHandler(ElementHandler<?> handler, Class<?>... classes) {
		for (Class<?> clazz : classes) {
			elementHandlers.put(clazz, handler);
		}
	}

	private static Class<?> determineFullyQualifiedName(String s) {
		try {
			return Class.forName(s);
		} catch (ClassNotFoundException e) {
			String fullyQualifiedName = packageImports.get(s);

			if (fullyQualifiedName == null) {
				fullyQualifiedName = "java.lang." + s;
			}

			try {
				return Class.forName(fullyQualifiedName);
			} catch (ClassNotFoundException e1) {
				return null;
			}
		}
	}

	public void setInput(String input) {
		this.matcher = this.compile.matcher(input);

		this.determinePackageImports(input);
	}

	/**
	 * Finds the next occurrence of the set annotation and returns an instance of it.
	 * 
	 * @return a new instance of the annotation
	 */
	public T find() {
		if (this.matcher == null) {
			throw new IllegalStateException("No input text set");
		}

		if (this.matcher.find()) {
			this.startPosition = this.matcher.start();
			this.endPosition = this.matcher.end();

			String annotationContent = this.matcher.group(2);

			logger.info("AnnotationContent: " + annotationContent);

			Map<Method, Object> map = new HashMap<Method, Object>();

			boolean fieldOccured = false;

			Set<Method> methods = new LinkedHashSet<Method>();
			methods.addAll(Arrays.asList(this.annotation.getDeclaredMethods()));
			try {
				Method method = this.annotation.getMethod("value");
				methods.remove(method);
				methods.add(method);
			} catch (SecurityException e) {
				logger.warn("Generated log statement",e);
			} catch (NoSuchMethodException e) {
				logger.warn("Generated log statement",e);
			}

			for (Method m : this.annotation.getDeclaredMethods()) {

				boolean required = m.getDefaultValue() == null;

				Class<?> returnType = m.getReturnType();
				ElementHandler<?> elementHandler = elementHandlers.get(returnType);

				if (elementHandler == null) {
					elementHandler = enumElementHandler;
				}

				logger.info("Method: " + m.getName() + " -> " + elementHandler.getClass());

				Pattern p;

				if (!fieldOccured && m.getName().equals("value")) {
					if (returnType.isArray()) {
						p = singleValueArrayPattern;
					} else {
						p = singleValuePattern;
					}
				} else {
					if (returnType.isArray()) {
						p = Pattern.compile(m.getName() + "\\s*=\\s*\\{(.*?)\\}");
					} else {
						p = Pattern.compile(m.getName() + "\\s*=\\s*([^,]+)+");
					}
				}

				Matcher matcher2 = p.matcher(annotationContent);

				if (matcher2.find()) {
					String fieldContent = matcher2.group(1);

					logger.info("fieldContent: " + fieldContent);

					Object value = elementHandler.find(m, fieldContent, returnType.isArray());

					fieldOccured |= value != null;

					if (required && value == null) {
						throw new RuntimeException("Annotation misses required field: " + m.getName());
					}

					map.put(m, value);
				}
			}

			return DynamicAnnotationBuilder.dynamicAnnotation(this.annotation, map);
		}

		return null;
	}

	public int[] getBounds() {
		return new int[] { this.startPosition, this.endPosition };
	}

	private void determinePackageImports(String input) {
		Matcher matcher = packageImportPattern.matcher(input);

		while (matcher.find()) {
			String group = matcher.group(1);
			logger.info("found package import: " + group);

			String[] split = group.split("\\.");

			packageImports.put(split[split.length - 1], group);
		}
	}

	private static abstract class ElementHandler<E> {
		private Pattern pattern;

		public ElementHandler(String regExp) {
			this.pattern = Pattern.compile(regExp);
		}

		public ElementHandler() {
		}

		public Object find(Method m, String input, boolean isArray) {
			Matcher matcher = this.pattern.matcher(input);

			LinkedList<E> list = new LinkedList<E>();

			if (!matcher.find()) {
				return null;
			}

			do {
				E element = this.handleMatch(matcher);
				logger.info("[" + m.getName() + "] captured group: " + element);

				list.add(element);
			} while (matcher.find());

			if (isArray) {
				return this.getArray(list);
			} else {
				return list.getFirst();
			}
		}

		public abstract Object getArray(List<E> list);

		public abstract E handleMatch(Matcher m);

		public void setPattern(String regExp) {
			this.pattern = Pattern.compile(regExp);
		}
	}

	private static class StringElementHandler extends ElementHandler<String> {

		public StringElementHandler() {
			super("\"(.*?)\"");
		}

		@Override
		public String handleMatch(Matcher m) {
			String str = m.group(1);
			return str;
		}

		@Override
		public Object getArray(List<String> list) {
			return list.toArray(new String[list.size()]);
		}
	}

	private abstract static class NumberElementHandler<T> extends ElementHandler<T> {

		public NumberElementHandler() {
			super("([^,\\s]+)");
		}

		@Override
		public T handleMatch(Matcher m) {
			String str = m.group(1);

			T i = this.parseNumber(str);

			return i;
		}

		public abstract T parseNumber(String str);
	}

	private static class IntegerElementHandler extends NumberElementHandler<Integer> {

		@Override
		public Object getArray(List<Integer> list) {
			int[] array = new int[list.size()];

			int i = 0;
			for (int element : list) {
				array[i] = element;
				i++;
			}

			return array;
		}

		@Override
		public Integer parseNumber(String str) {
			return Integer.parseInt(str);
		}
	}

	private static class LongElementHandler extends NumberElementHandler<Long> {

		@Override
		public Object getArray(List<Long> list) {
			long[] array = new long[list.size()];

			int i = 0;
			for (long element : list) {
				array[i] = element;
				i++;
			}

			return array;
		}

		@Override
		public Long parseNumber(String str) {
			return Long.parseLong(str);
		}
	}

	private static class FloatElementHandler extends NumberElementHandler<Float> {

		@Override
		public Object getArray(List<Float> list) {
			float[] array = new float[list.size()];

			int i = 0;
			for (float element : list) {
				array[i] = element;
				i++;
			}

			return array;
		}

		@Override
		public Float parseNumber(String str) {
			return Float.parseFloat(str);
		}
	}

	private static class DoubleElementHandler extends NumberElementHandler<Double> {

		@Override
		public Object getArray(List<Double> list) {
			double[] array = new double[list.size()];

			int i = 0;
			for (double element : list) {
				array[i] = element;
				i++;
			}

			return array;
		}

		@Override
		public Double parseNumber(String str) {
			return Double.parseDouble(str);
		}
	}

	private static class ClassElementHandler extends ElementHandler<Class<?>> {

		public ClassElementHandler() {
			super("([A-Za-z0-9_\\$\\.]+)\\s*\\.\\s*class");
		}

		@Override
		public Class<?> handleMatch(Matcher m) {
			String str = m.group(1);

			return determineFullyQualifiedName(str);
		}

		@Override
		public Object getArray(List<Class<?>> list) {
			return list.toArray(new Class[list.size()]);
		}

	}

	private static class EnumElementHandler extends ElementHandler<Enum<?>> {

		@Override
		public Object find(Method m, String input, boolean isArray) {
			StringBuilder sb = new StringBuilder();

			Class<?> clazz = m.getReturnType();
			Field[] fields = clazz.getDeclaredFields();

			sb.append('(').append(clazz.getSimpleName()).append('|').append(clazz.getCanonicalName()).append(')');
			sb.append("\\s*\\.\\s*(");

			for (Field f : fields) {
				if (!f.getName().equals("$VALUES")) {

					sb.append(f.getName());
					sb.append('|');
				}
			}

			sb.deleteCharAt(sb.length() - 1);
			sb.append(')');

			this.setPattern(sb.toString());

			return super.find(m, input, isArray);
		}

		@Override
		public Enum<?> handleMatch(Matcher m) {

			String str = m.group(1);
			String value = m.group(2);

			Class<Enum> enumClass = (Class<Enum>) determineFullyQualifiedName(str);
			Enum<?> enumInstance = Enum.valueOf(enumClass, value);

			return enumInstance;
		}

		@Override
		public Object getArray(List<Enum<?>> list) {
			return list.toArray(new Enum[list.size()]);
		}

	}

	static class DynamicAnnotationBuilder implements InvocationHandler {
		private Map<Method, Object> map = new HashMap<Method, Object>();

		public DynamicAnnotationBuilder(Map<Method, Object> map) {
			this.map = map;
		}

		@SuppressWarnings("unchecked")
		public static <A extends Annotation> A dynamicAnnotation(Class<A> annotation, Map<Method, Object> map) {
			return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation },
					new DynamicAnnotationBuilder(map));
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			logger.info("Invoking {}", this.map);

			Object object = this.map.get(method);

			if (object == null) {
				object = method.getDefaultValue();
			}

			return object;
		}
	}

	static class DynamicEnumBuilder implements InvocationHandler {

		@SuppressWarnings("unchecked")
		public static <A extends Enum> A dynamicAnnotation(Class<A> annotation) {
			return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation },
					new DynamicEnumBuilder());
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return proxy;
		}
	}

	public @interface TestAnnotation {
		RetentionPolicy retention() default RetentionPolicy.SOURCE;

		Class<?> clazz() default Integer.class;

		int[] num() default { 5, 5, 5 };

		String name() default "no name";
	}

	public static void main(String[] args) {
		// AnnotationExtractor<TestAnnotation> extractor = new
		// AnnotationExtractor<TestAnnotation>(TestAnnotation.class);
		// extractor.setInput("@TestAnnotation(name=\"Peter\") @TestAnnotation(name=\"Jack\")");
		// TestAnnotation find = extractor.find();
		// logger.info(find.name());

		AnnotationExtractor<EDSL> extractor = new AnnotationExtractor<EDSL>(EDSL.class);
		extractor.setInput("@EDSL({\"map\", \"math\"})");

		EDSL annotation = extractor.find();

		for (String dslName : annotation.value()) {
			logger.info(dslName);
		}
	}
}
