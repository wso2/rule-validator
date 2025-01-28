/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.rule.validator.functions;

import org.reflections.Reflections;
import org.wso2.rule.validator.functions.core.AlphabeticalFunction;
import org.wso2.rule.validator.functions.core.CasingFunction;
import org.wso2.rule.validator.functions.core.DefinedFunction;
import org.wso2.rule.validator.functions.core.EnumerationFunction;
import org.wso2.rule.validator.functions.core.FalsyFunction;
import org.wso2.rule.validator.functions.core.LengthFunction;
import org.wso2.rule.validator.functions.core.PatternFunction;
import org.wso2.rule.validator.functions.core.SchemaFunction;
import org.wso2.rule.validator.functions.core.TruthyFunction;
import org.wso2.rule.validator.functions.core.UndefinedFunction;
import org.wso2.rule.validator.functions.core.XorFunction;

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

//    public static LintFunction getFunction(String functionName, Map<String, Object> functionOptions) {
//        Class<? extends LintFunction> functionClass = functionRegistry.get(StringUtils.toRootLowerCase(functionName));
//        if (functionClass == null) {
//            throw new IllegalArgumentException("Unknown function: " + functionName);
//        }
//        try {
//            return (LintFunction) functionClass.getDeclaredConstructors()[0].newInstance(functionOptions);
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException("Error creating function instance", e);
//        }
//    }
//
//    public static boolean isFunction(String functionName) {
//        return functionRegistry.containsKey(StringUtils.toRootLowerCase(functionName));
//    }

    public static boolean isFunctionTemp(String functionName) {
        return functionName.equals("alphabetical") || functionName.equals("casing") || functionName.equals("defined")
                || functionName.equals("enumeration") || functionName.equals("falsy") || functionName.equals("length")
                || functionName.equals("pattern") || functionName.equals("schema") || functionName.equals("truthy")
                || functionName.equals("undefined") || functionName.equals("xor");
    }

    public static LintFunction getFunctionTemp(String functionName, Map<String, Object> functionOptions) {
        if (functionName.equals("alphabetical")) {
            return new AlphabeticalFunction(functionOptions);
        } else if (functionName.equals("casing")) {
            return new CasingFunction(functionOptions);
        } else if (functionName.equals("defined")) {
            return new DefinedFunction(functionOptions);
        } else if (functionName.equals("enumeration")) {
            return new EnumerationFunction(functionOptions);
        } else if (functionName.equals("falsy")) {
            return new FalsyFunction(functionOptions);
        } else if (functionName.equals("pattern")) {
            return new PatternFunction(functionOptions);
        } else if (functionName.equals("truthy")) {
            return new TruthyFunction(functionOptions);
        } else if (functionName.equals("length")) {
            return new LengthFunction(functionOptions);
        } else if (functionName.equals("schema")) {
            return new SchemaFunction(functionOptions);
        } else if (functionName.equals("undefined")) {
            return new UndefinedFunction(functionOptions);
        } else if (functionName.equals("xor")) {
            return new XorFunction(functionOptions);
        } else {
            return null;
        }
    }
}
