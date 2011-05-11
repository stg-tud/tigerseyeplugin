package de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling;
import groovy.lang.Closure;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.IRule;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;

public class HandlingDispatcher {
private static final Logger logger = LoggerFactory.getLogger(HandlingDispatcher.class);


// TODO check cyclic Dependency between Grammar and HandlingDispatcher
	private final IGrammar<String> grammar;
	private final Map<Class<?>, ClassTypeHandler> classHandlers = new HashMap<Class<?>, ClassTypeHandler>();

	private void initHandlers() {
		registerTypeHandler(new StringHandler(), String.class);
		registerTypeHandler(new BooleanHandler(), Boolean.class, boolean.class);
		registerTypeHandler(new NumberHandler(), Integer.class, int.class, Double.class, double.class, Float.class,
				float.class);
		registerTypeHandler(new ClosureHandler(), Closure.class);
		registerTypeHandler(new ClassHandler(), Class.class);
	}

	private void registerTypeHandler(ClassTypeHandler handler, Class<?>... types) {
		if (types != null) {
			for (Class<?> type : types) {
				classHandlers.put(type, handler);
			}
		}
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public HandlingDispatcher(){
		this(new IGrammar<String>(){

			@Override
			public void setCategories(Set categories) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Set getCategories() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setRules(Set rules) {
				throw new UnsupportedOperationException();
				
			}

			@Override
			public Set getRules() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setStartRule(IRule startRule) {
				throw new UnsupportedOperationException();
			}

			@Override
			public IRule getStartRule() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void addRule(IRule rule) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void addRules(IRule... rules) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void addCategory(ICategory category) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void addCategories(ICategory... categories) {
				throw new UnsupportedOperationException();
			}});
	}

	/**
	 * FIXME never used?
	 * @param grammar
	 */
	public HandlingDispatcher(IGrammar<String> grammar) {
		this.grammar = grammar;
		initHandlers();
	}

	public ICategory<String> handle(Type type, Map<String, String> parameterOptions) {

		if (type instanceof Class) {
			return this.handleClass(type, parameterOptions);
		} else if (type instanceof ParameterizedType) {
			return this.handleParameterizedType(type, parameterOptions);
		} else if (type instanceof GenericArrayType) {
			return this.handleGenericArrayType(type, parameterOptions);
		} else if (type instanceof TypeVariable<?>) {
			return this.handleTypeVariable(type, parameterOptions);
		} else {
			throw new IllegalArgumentException("invalid type: " + type);
		}
	}

	private ICategory<String> handleTypeVariable(Type type, Map<String, String> parameterOptions) {
		return this.handleClass(Object.class, parameterOptions);
	}

	private ICategory<String> handleGenericArrayType(Type type, Map<String, String> parameterOptions) {
	// GenericArrayType gat = (GenericArrayType) type;

		Type componentType = ((GenericArrayType) type).getGenericComponentType();

		if (componentType instanceof TypeVariable<?>) {
	    // TODO why is t not used
	    // TypeVariable<?> t = (TypeVariable<?>) componentType;
			return this.handleClass(Object[].class, parameterOptions);
		} else if (componentType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) componentType;
			ICategory<String> handleClass = this.handleClass(Array.newInstance(
					(Class<?>) parameterizedType.getRawType(), 0).getClass(), parameterOptions);

			// Category category = new Category(((Class<?>) parameterizedType.getRawType()).
			// + Arrays.toString(parameterizedType.getActualTypeArguments()).replaceAll("\\[([^\\[\\]]+)\\]",
			// "<$1>") + "[]", false);
			//
			// Rule rule = new Rule(handleClass, category);
			// this.grammar.addRule(rule);
			return handleClass;
		} else if (componentType instanceof Class) {
			Class<?> clazz = (Class<?>) componentType;
			logger.info("[HandlingDispatcher] " + type + " -> " + Array.newInstance(clazz, 0).getClass());
			return this.handleClass(Array.newInstance(clazz, 0).getClass(), parameterOptions);
		}

		throw new IllegalArgumentException("type " + componentType.getClass() + " not supported");
	}

	private ICategory<String> handleParameterizedType(Type type, Map<String, String> parameterOptions) {
		ParameterizedType t = (ParameterizedType) type;
		return this.handleClass((t.getRawType()), parameterOptions);
	}

	private ICategory<String> handleClass(Type type, Map<String, String> parameterOptions) {

		Class<?> clazz = (Class<?>) type;

		ClassTypeHandler classHandler = classHandlers.get(clazz);
		if (classHandler != null) {
			return classHandler.handle(this.grammar, clazz, parameterOptions);
		}

		if (clazz.isArray()) {
			return this.handleObjectArray(clazz, parameterOptions);
		} else {
			return this.handleObject(clazz);
		}
	}

	private ICategory<String> handleObjectArray(Class<?> clazz, Map<String, String> parameterOptions) {
		ICategory<String> objects = HandlingDispatcherHelper.getObjectHierarchy(this.grammar, clazz);

		Class<?> componentType = clazz.getComponentType();
		ICategory<String> componentCategory = this.handle(componentType, parameterOptions);

	// TODO why is object variable not used
	// ICategory<String> object = this.getObjectHierarchy(this.grammar,
	// clazz.getComponentType());

		//FIXME GrammarBuilder has obviously some methods used by different classes independently, he should be decomposed in independent modules 
		GrammarBuilder gb = new GrammarBuilder();
		
		Rule r1 = null;
		if (parameterOptions.get("arrayDelimiter").matches("\\s+")) {
			r1 = new Rule(objects, objects, gb.getWhitespaceCategory(this.grammar, false), objects);
		} else if (parameterOptions.get("arrayDelimiter").isEmpty()) {
			r1 = new Rule(objects, objects, objects);
		} else {
			Category ad = new Category(parameterOptions.get("arrayDelimiter"), true);
			this.grammar.addCategory(ad);
			ICategory<String> WS = gb.getWhitespaceCategory(this.grammar, true);
			r1 = new Rule(objects, objects, WS, ad, WS, objects);
		}

		Rule r2 = new Rule(objects, componentCategory);

		this.grammar.addRules(r1, r2);

		this.grammar.addCategories(objects);

		return objects;
	}

	private ICategory<String> handleObject(Class<?> clazz) {
		ICategory<String> object = HandlingDispatcherHelper.getObjectHierarchy(this.grammar, clazz);

		return object;
	}


    public void handleDefaults(Map<String, String> methodOptions) {
		for (Entry<Class<?>, ClassTypeHandler> e : classHandlers.entrySet()) {
			e.getValue().handle(this.grammar, e.getKey(), methodOptions);
		}
	}

	public void addAdditionalTypeRules(Class<? extends TypeHandler>[] typeRules) {
		for (Class<? extends TypeHandler> handler : typeRules) {
			TypeHandler newInstance;

			try {
				newInstance = handler.newInstance();
				newInstance.setGrammar(this.grammar);

				registerTypeHandler(newInstance, newInstance.getMainType());
				registerTypeHandler(newInstance, newInstance.getAdditionalTypes());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				logger.warn("Generated log statement",e);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				logger.warn("Generated log statement",e);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				logger.warn("Generated log statement",e);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				logger.warn("Generated log statement",e);
			}
		}

	}

}
