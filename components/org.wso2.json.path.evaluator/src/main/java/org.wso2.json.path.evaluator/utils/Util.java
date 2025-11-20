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
package org.wso2.json.path.evaluator.utils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wso2.json.path.evaluator.document.AdvancedFeatures;
import org.wso2.json.path.evaluator.document.TraversalMapData;
import org.wso2.json.path.evaluator.document.wrappers.BooleanWrapper;
import org.wso2.json.path.evaluator.document.wrappers.NumberWrapper;
import org.wso2.json.path.evaluator.document.wrappers.StringWrapper;
import org.wso2.json.path.evaluator.evaluate.Evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util methods
 */
public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Evaluator.class);
    public static Object returnValuesForAdvancedFeatures(TraversalMapData traversalInstance,
                                                         Object currentNode, AdvancedFeatures key) {
        Object parentNode = traversalInstance.getParent(currentNode);
        Object grandParentNode = traversalInstance.getParent(parentNode);
        Object nodePath = traversalInstance.getPath(currentNode);

        switch (key) {
            case LENGTH:
                if (parentNode instanceof List) {
                    return ((List<?>) parentNode).size();
                } else if (currentNode instanceof StringWrapper) {
                    return ((StringWrapper) currentNode).value.length();
                } else {
                    return null;
                }
            case PROPERTY:
                return getPropertyName(traversalInstance , currentNode);
            case PARENT_PROPERTY:
                return getPropertyName(traversalInstance , parentNode);
            case PARENT:
                return parentNode;
            case GRANDPARENT:
                return getPropertyName(traversalInstance , grandParentNode);
            case PATH:
                return nodePath;

            default:
                return null;
        }
    }

    private static String getPropertyName(TraversalMapData traversalInstance, Object node) {
        Object parent = traversalInstance.getParent(node);

        if (parent == null) {
            return null;
        }
        if (parent instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) parent;
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                if (entry.getValue() == node) {
                    return entry.getKey().toString();
                }
            }
        } else if (parent instanceof ArrayList) {
            List<Object> list = (List<Object>) parent;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == node) {
                    return String.valueOf(i);
                }
            }
        }
        return null;
    }

    public static boolean hasAdvancedFeatures(String givenPath) {
        String[] unsupportedFeatures = {
                "@.",
                "@.length",
                "@property",
                "@path",
                "@parent",
                "@root",
                "@parentProperty",
                "^",
                "~"
        };

        for (String pattern : unsupportedFeatures) {
            if (givenPath.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasStringMethods(String expression) {
        String [] stringMethods = {
                "charAt()",
                "codePointAt()",
                "concat()",
                "endsWith()",
                "includes()",
                "indexOf()",
                "lastIndexOf()",
                "match()",
                "replace()",
                "replaceAll()",
                "split()",
                "startsWith()",
                "substring()",
                "toLowerCase()",
                "toString()",
                "toUpperCase()",
                "trim()",
                "valueOf()"

        };
        for (String strMethod : stringMethods) {
            if (expression.contains(strMethod)) {
                return true;
            }
        }
        return false;

    }

    public static String replaceAdvancedFeaturesWithActualValues(TraversalMapData traversalInstance,
                                                                 String jsonPathExpression, Object currentNode) {
        String result = jsonPathExpression;
        if (jsonPathExpression.contains("@.length")) {
            result = replaceLength(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains("@property")) {
            result = replaceProperty(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains("@parentProperty")) {
            result = replaceParentProperty(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains("@parent")) {
            result = replaceParent(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains("@path")) {
            result = replacePath(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains("@root")) {
            result = replaceRoot(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains("@")) {
            result = replaceAt(traversalInstance, result, currentNode);
        }
        return result;
    }


    private static String replaceLength(TraversalMapData traversalInstance, String jsonPathExpression,
                                        Object currentNode) {
        String result = jsonPathExpression;
        Object getLengthObject = returnValuesForAdvancedFeatures(
                traversalInstance, currentNode, AdvancedFeatures.LENGTH);
        logger.info("Length: " + getLengthObject);
        if (getLengthObject instanceof Integer) {
            result = result.replace("@.length", String.valueOf(getLengthObject));
        }
        return result;
    }

    private static String replaceProperty(TraversalMapData traversalInstance, String jsonPathExpression,
                                          Object currentNode) {
        String result = jsonPathExpression;
        Object getPropertyObject = returnValuesForAdvancedFeatures(traversalInstance ,
                currentNode, AdvancedFeatures.PROPERTY);
        logger.info((String) getPropertyObject);
        try {
            Integer.parseInt(String.valueOf(getPropertyObject));
        } catch (Exception e) {
            getPropertyObject = "\"" + String.valueOf(getPropertyObject) + "\"";
        }
        result = result.replace("@property", String.valueOf(getPropertyObject));
        return result;
    }

    private static String replaceParentProperty(TraversalMapData traversalInstance, String jsonPathExpression,
                                                Object currentNode) {
        String result = jsonPathExpression;
        Object getParentObject = returnValuesForAdvancedFeatures(traversalInstance, currentNode,
                AdvancedFeatures.PARENT_PROPERTY);
        try {
            Integer.parseInt(String.valueOf(getParentObject));
        } catch (Exception e) {
            getParentObject = "\"" + String.valueOf(getParentObject) + "\"";
        }
        result = result.replace("@parentProperty", String.valueOf(getParentObject));
        return result;
    }

    private static String replaceParent(TraversalMapData traversalInstance, String jsonPathExpression,
                                        Object currentNode) {
        String result = jsonPathExpression;
        String parentValue;
        Object parentNode = returnValuesForAdvancedFeatures(traversalInstance, currentNode, AdvancedFeatures.PARENT);
        Object grandParentNode = traversalInstance.getParent(parentNode);
        if (grandParentNode != null) {
            parentValue = result.replace("@parent", "$.");
        } else {
            parentValue = result.replace("@parent", "$");
        }

        return parentValue;
    }

    private static String replacePath(TraversalMapData traversalInstance, String jsonPathExpression,
                                      Object currentNode) {
        String result = jsonPathExpression;
        Object getPath = returnValuesForAdvancedFeatures(traversalInstance, currentNode, AdvancedFeatures.PATH);
        result = result.replace("@path", "\"" + String.valueOf(getPath) + "\"");
        return result;
    }

    private static String replaceRoot(TraversalMapData traversalInstance, String jsonPathExpression,
                                      Object currentNode) {
        String result = jsonPathExpression;
        result = result.replace("@root", "$");
        return result;
    }

    private static String replaceAt(TraversalMapData traversalInstance, String jsonPathExpression, Object currentNode) {
        String result = jsonPathExpression;
        result = result.replace("@" , String.valueOf(returnValuesForAdvancedFeatures(traversalInstance,
                currentNode, AdvancedFeatures.PATH)));
        return result;
    }

    public static boolean isTruthy(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Integer) {
            return (Integer) value != 0;
        }

        if (value instanceof Double) {
            return (Double) value != 0;
        }

        if (value instanceof String) {
            return !((String) value).isEmpty();
        }

        if (value instanceof List) {
            return !((List<Object>) value).isEmpty();
        }

        if (value instanceof Map) {
            return !((Map<Object, Object>) value).isEmpty();
        }

        if (value instanceof StringWrapper) {
            return !((StringWrapper) value).value.isEmpty();
        }
        if (value instanceof NumberWrapper) {
            return ((NumberWrapper) value).value.doubleValue() != 0;
        }
        if (value instanceof BooleanWrapper) {
            return ((BooleanWrapper) value).value;
        }

        return true;
    }


    public static String comparisonOfPathsAndReplacingPathsWithActualValues(TraversalMapData traversalInstance,
                                                                            String expression, Object root) {
        // Example scenario : $.store.book[2] === @root..['book'][2]
        String pathsOnBothSides =
                ".*(?:\\\"|')?\\$+(?:\\.?[A-Za-z0-9_\\[\\]\\.']+)+(?:\\\"|')?\\s*(?:===|!==|==|!=|>=|<=|>|<)\\s*" +
                        "(?:\\\"|')?\\$+(?:\\.?[A-Za-z0-9_\\[\\]\\.']+)+(?:\\\"|')?.*";

        if (expression.matches(pathsOnBothSides)) {
            logger.info("Skipped substitution since both sides are JSONPaths : " + expression);
            Pattern pattern = Pattern.compile(
                    "(\\$+(?:\\.?[A-Za-z0-9_\\[\\]'\".]+)+)\\s*(===|!==|==|!=|>=|<=|>|<)\\s*" +
                            "(\\$+(?:\\.?[A-Za-z0-9_\\[\\]'\".]+)+)");
            Matcher matcher = pattern.matcher(expression);

            if (matcher.find()) {
                String leftPath = matcher.group(1).trim();
                String rightPath = matcher.group(3).trim();

                Object leftVal = JsonPath.parse(root).read(leftPath);
                Object rightVal = JsonPath.parse(root).read(rightPath);

                String newLeftPath = "\"" + traversalInstance.getPath(leftVal) + "\"";
                String newRightPath = "\"" + traversalInstance.getPath(rightVal) + "\"";

                logger.info("Left Path: " + newLeftPath);
                logger.info("Right Path: " + newRightPath);
                expression = expression.replace(leftPath, newLeftPath);
                expression = expression.replace(rightPath, newRightPath);
                logger.info(expression);
            }
            return expression;
        }

        Pattern pattern = Pattern.compile("\\$(?:\\.\\.?[a-zA-Z0-9_\\$]+|\\[[^\\]]+\\])+");
        Matcher matcher = pattern.matcher(expression);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String jsonPath = matcher.group().trim();
            Configuration config = Configuration.builder().options().build();
            Object nodeValue;
            try {
                nodeValue = JsonPath.using(config).parse(root).read(jsonPath);
            } catch (Exception e) {
                nodeValue = null;
            }

            Object finalValue = nodeValue;
            if (finalValue instanceof List) {
                List<?> list = (List<?>) finalValue;
                if (list.size() == 1) {
                    Object single = list.get(0);
                    if (single instanceof Map && ((Map<Object, Object>) single).size() == 1 &&
                            ((Map<Object, Object>) single).containsKey("value")) {
                        finalValue = ((Map<?, ?>) single).get("value");
                    } else {
                        finalValue = single;
                    }
                }
            }
            int endIdx = matcher.end();
            String afterPath = expression.substring(endIdx);

            boolean hasComparisonAfter = afterPath.matches("\\s*(===|!==|==|!=|>=|<=|>|<).*");
            String replacement;
            // Example testcase : $.store.book[?(@parent.bicycle)]
            if (!hasComparisonAfter) {
                replacement = String.valueOf(isTruthy(finalValue));
            } else {
                try {
                    Integer.parseInt(String.valueOf(finalValue));
                    replacement = String.valueOf(finalValue);
                } catch (Exception e1) {
                    try {
                        Double.parseDouble(String.valueOf(finalValue));
                        replacement = String.valueOf(finalValue);
                    } catch (Exception e2) {
                        replacement = "\"" + String.valueOf(finalValue) + "\"";
                    }
                }
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        expression = sb.toString();
        if (hasStringMethods(expression)) {
            expression = handleStringFunctions(expression);
        }
        return expression;
    }
    public static String handleStringFunctions(String expression) {
        Pattern functionPattern = Pattern.compile("\"([^\"]+)\"\\.(\\w+)\\(([^)]*)\\)");
        String previousExpression;

        do {
            //E.g. :"hello".toLowerCase().indexOf("e")
            previousExpression = expression;
            Matcher matcher = functionPattern.matcher(expression);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String base = matcher.group(1);
                String functionName = matcher.group(2);
                String argument = matcher.group(3).trim();
                logger.info("Argument: " + argument);

                String result;
                try {
                    String arg = argument.replaceAll("^'|'$", "");
                    logger.info("Argument: " + arg);
                    switch (functionName) {
                        case "charAt":
                            result = String.valueOf(base.charAt(Integer.parseInt(arg)));
                            break;
                        case "codePointAt":
                            result = String.valueOf(base.codePointAt(Integer.parseInt(arg)));
                            break;
                        case "concat":
                            result = base.concat(arg);
                            break;
                        case "endsWith":
                            result = String.valueOf(base.endsWith(arg));
                            break;
                        case "includes":
                            result = String.valueOf(base.contains(arg));
                            break;
                        case "indexOf":
                            result = String.valueOf(base.indexOf(arg));
                            break;
                        case "lastIndexOf":
                            result = String.valueOf(base.lastIndexOf(arg));
                            break;
                        case "startsWith":
                            result = String.valueOf(base.startsWith(arg));
                            break;
                        case "toLowerCase":
                            result = "\"" + base.toLowerCase(Locale.ENGLISH) + "\"";
                            break;
                        case "toUpperCase":
                            result = "\"" + base.toUpperCase(Locale.ENGLISH) + "\"";
                            break;
                        default:
                            result = "\"" + base + "\"";
                            break;
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    result = "\"" + base + "\"";
                }

                matcher.appendReplacement(sb, result);
            }


            matcher.appendTail(sb);
            expression = sb.toString();
        } while(!previousExpression.equals(expression));
        return expression;
    }

    //Using Jexl Evaluator
    public static Object evaluateExpression(String reducedExpression) {
        JexlEngine jexl  = new JexlBuilder().create();
        JexlContext context = new MapContext();
        JexlExpression expression = jexl.createExpression(reducedExpression);
        Object resulting = expression.evaluate(context);
        logger.info("Resulting : " + resulting);
        return resulting;
    }
}
