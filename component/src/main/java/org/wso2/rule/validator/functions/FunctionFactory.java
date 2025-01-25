package org.wso2.rule.validator.functions;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to create LintFunction instances
 */
public class FunctionFactory {
    private static final Map<String, Class<? extends LintFunction>> functionRegistry = new HashMap<>();

    // Static block to initialize the registry
    static {
        registerFunctions("org.wso2.rule.validator.functions.core"); // Replace with your package
    }

    private static void registerFunctions(String packageName) {
        Reflections reflections = new Reflections(packageName);
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(FunctionName.class)) {
            FunctionName annotation = clazz.getAnnotation(FunctionName.class);
            functionRegistry.put(annotation.value(), clazz.asSubclass(LintFunction.class));
        }
    }

    public static LintFunction getFunction(String functionName, Map<String, Object> functionOptions) {
        Class<? extends LintFunction> functionClass = functionRegistry.get(StringUtils.toRootLowerCase(functionName));
        if (functionClass == null) {
            throw new IllegalArgumentException("Unknown function: " + functionName);
        }
        try {
            return (LintFunction) functionClass.getDeclaredConstructors()[0].newInstance(functionOptions);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error creating function instance", e);
        }
    }

    public static boolean isFunction(String functionName) {
        return functionRegistry.containsKey(StringUtils.toRootLowerCase(functionName));
    }
}
