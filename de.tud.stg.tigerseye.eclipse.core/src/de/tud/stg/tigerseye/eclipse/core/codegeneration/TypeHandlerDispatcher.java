package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import static de.tud.stg.tigerseye.util.Utils.illegalForArg;
import groovy.lang.Closure;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.BooleanHandler;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ClassHandler;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ClassTypeHandler;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ClosureHandler;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.NumberHandler;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.StringHandler;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.utils.HandlingDispatcherHelper;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.utils.WhitespaceCategoryDefinition;

public class TypeHandlerDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(TypeHandlerDispatcher.class);

    private static final Map<Class<?>, ClassTypeHandler> initialClassHandlers = initHandlers();

    private static Map<Class<?>, ClassTypeHandler> initHandlers() {
	Hashtable<Class<?>, ClassTypeHandler> classHandlerMap = new Hashtable<Class<?>, ClassTypeHandler>();
	registerTypeHandler(classHandlerMap, new StringHandler(), String.class);
	registerTypeHandler(classHandlerMap, new BooleanHandler(), Boolean.class, boolean.class);
	registerTypeHandler(classHandlerMap, new NumberHandler(), Integer.class, int.class, Double.class, double.class,
		Float.class, float.class);
	registerTypeHandler(classHandlerMap, new ClosureHandler(), Closure.class);
	registerTypeHandler(classHandlerMap, new ClassHandler(), Class.class);
	return Collections.unmodifiableMap(classHandlerMap);
    }

    private final IGrammar<String> grammar;

    private final Map<Class<?>, ClassTypeHandler> classHandlers = new HashMap<Class<?>, ClassTypeHandler>(
	    initialClassHandlers);

    private static void registerTypeHandler(Map<Class<?>, ClassTypeHandler> classHandlerMap, ClassTypeHandler handler,
	    Class<?>... types) {
	if (types != null) {
	    for (Class<?> type : types) {
		classHandlerMap.put(type, handler);
	    }
	}
    }

    /**
     * @param grammar
     */
    public TypeHandlerDispatcher(IGrammar<String> grammar) {
	this.grammar = grammar;
    }

    public ICategory<String> handle(Type type, Map<ConfigurationOptions, String> parameterOptions) {

	if (type instanceof Class) {
	    return this.handleClass(type, parameterOptions);
	} else if (type instanceof ParameterizedType) {
	    return this.handleParameterizedType((ParameterizedType) type, parameterOptions);
	} else if (type instanceof GenericArrayType) {
	    return this.handleGenericArrayType((GenericArrayType) type, parameterOptions);
	} else if (type instanceof TypeVariable<?>) {
	    return this.handleTypeVariable((TypeVariable<?>) type, parameterOptions);
	} else {
	    throw new IllegalArgumentException("invalid type: " + type);
	}
    }

    private ICategory<String> handleTypeVariable(TypeVariable<?> type,
	    Map<ConfigurationOptions, String> parameterOptions) {
	return this.handleClass(Object.class, parameterOptions);
    }

    private ICategory<String> handleParameterizedType(ParameterizedType type,
	    Map<ConfigurationOptions, String> parameterOptions) {
	/* e.g. Collection<String> */
	return this.handleClass(type.getRawType(), parameterOptions);
    }

    private ICategory<String> handleGenericArrayType(GenericArrayType type,
	    Map<ConfigurationOptions, String> parameterOptions) {
	Type componentType = type.getGenericComponentType();

	if (componentType instanceof TypeVariable<?>) {
	    return this.handleTypeVariable((TypeVariable<?>) componentType, parameterOptions);
	} else if (componentType instanceof ParameterizedType) {
	    ParameterizedType parameterizedType = (ParameterizedType) componentType;
	    ICategory<String> handleClass = this.handleClass(
		    Array.newInstance((Class<?>) parameterizedType.getRawType(), 0).getClass(), parameterOptions);
	    return handleClass;
	} else if (componentType instanceof Class) {
	    // Array is array of class
	    Class<?> clazz = (Class<?>) componentType;
	    Class<? extends Object> classToHandle = Array.newInstance(clazz, 0).getClass();
	    logger.info("{} -> {}", type, classToHandle);
	    return this.handleClass(classToHandle, parameterOptions);
	} else {
	    throw illegalForArg(componentType.getClass());
	}
    }

    private ICategory<String> handleClass(Type type, Map<ConfigurationOptions, String> parameterOptions) {

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

    private ICategory<String> handleObjectArray(Class<?> clazz, Map<ConfigurationOptions, String> parameterOptions) {
	ICategory<String> objects = HandlingDispatcherHelper.getObjectHierarchy(this.grammar, clazz);
	logger.debug("Objects in array are {} for class {} with parameterOptions {}", new Object[] { objects, clazz,
		parameterOptions });

	Class<?> componentType = clazz.getComponentType();
	ICategory<String> componentCategory = this.handle(componentType, parameterOptions);

	// TODO why is object variable not used
	// ICategory<String> object = this.getObjectHierarchy(this.grammar,
	// clazz.getComponentType());
	Rule r1 = null;
	if (parameterOptions.get(ConfigurationOptions.ARRAY_DELIMITER).matches("\\s+")) {
	    r1 = new Rule(objects, objects, WhitespaceCategoryDefinition.getAndSetRequiredWhitespace(this.grammar),
		    objects);
	} else if (parameterOptions.get(ConfigurationOptions.ARRAY_DELIMITER).isEmpty()) {
	    r1 = new Rule(objects, objects, objects);
	} else {
	    Category ad = new Category(parameterOptions.get(ConfigurationOptions.ARRAY_DELIMITER), true);
	    this.grammar.addCategory(ad);
	    ICategory<String> WS = WhitespaceCategoryDefinition.getAndSetOptionalWhitespace(this.grammar);
	    r1 = new Rule(objects, objects, WS, ad, WS, objects);
	    // Rule r1a = new Rule(objects, objects, ad, WS, objects);
	    // Rule r1b = new Rule(objects, objects, WS, ad, objects);
	    // Rule r1c = new Rule(objects, objects, ad, objects);
	    // this.grammar.addRules(r1c, r1b, r1a);
	}

	Rule r2 = new Rule(objects, componentCategory);

	this.grammar.addRules(r1, r2);

	this.grammar.addCategory(objects);

	return objects;
    }

    private ICategory<String> handleObject(Class<?> clazz) {
	ICategory<String> object = HandlingDispatcherHelper.getObjectHierarchy(this.grammar, clazz);

	return object;
    }

    public void configurationOptions(Map<ConfigurationOptions, String> configurationOptions) {
	for (Entry<Class<?>, ClassTypeHandler> e : classHandlers.entrySet()) {
	    ClassTypeHandler handler = e.getValue();
	    Class<?> classToHandle = e.getKey();
	    handler.handle(this.grammar, classToHandle, configurationOptions);
	}
    }

    public void addAdditionalTypeRules(Set<Class<? extends TypeHandler>> typeRules) {
	for (Class<? extends TypeHandler> handler : typeRules) {
	    TypeHandler newHandler;
	    try {
		newHandler = handler.newInstance();
		newHandler.setGrammar(this.grammar);

		registerTypeHandler(this.classHandlers, newHandler, newHandler.getMainType());
		registerTypeHandler(this.classHandlers, newHandler, newHandler.getAdditionalTypes());
	    } catch (InstantiationException e) {
		// TODO Auto-generated catch block
		logger.warn("Generated log statement", e);
	    } catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		logger.warn("Generated log statement", e);
	    } catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		logger.warn("Generated log statement", e);
	    } catch (SecurityException e) {
		// TODO Auto-generated catch block
		logger.warn("Generated log statement", e);
	    }
	}
    }

}
