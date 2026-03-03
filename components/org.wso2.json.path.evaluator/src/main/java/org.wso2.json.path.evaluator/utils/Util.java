/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

import org.wso2.json.path.evaluator.Constants;
import org.wso2.json.path.evaluator.document.AdvancedFeatures;
import org.wso2.json.path.evaluator.document.TraversalMapData;
import org.wso2.json.path.evaluator.document.wrappers.BooleanWrapper;
import org.wso2.json.path.evaluator.document.wrappers.NumberWrapper;
import org.wso2.json.path.evaluator.document.wrappers.StringWrapper;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods used during JSONPath Plus evaluation.
 */
public class Util {

    /**
     * Resolves the runtime value for a JSONPath Plus feature for the current node.
     *
     * @param traversalInstance traversal metadata
     * @param currentNode current node
     * @param key advanced feature to resolve
     * @return resolved feature value
     */
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

    /**
     * Returns the property value of the given node.
     * For arrays, the property corresponds to the array index.
     * For maps, the property corresponds to the map key.
     */

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
        } else if (parent instanceof List) {
            List<Object> list = (List<Object>) parent;
            int index = 0;
            for (Object item : list) {
                if (item == node) {
                    return String.valueOf(index);
                }
                index++;
            }
        }
        return null;
    }

    /**
     * Checks whether an expression contains any supported JSONPath Plus feature.
     *
     * @param givenPath expression to inspect
     * @return {@code true} when at least one advanced feature is present
     */
    public static boolean hasAdvancedFeatures(String givenPath) {
        String[] advancedFeatures = {
                Constants.ADVANCED_FEATURE_CURRENT,
                Constants.ADVANCED_FEATURE_LENGTH,
                Constants.ADVANCED_FEATURE_PROPERTY,
                Constants.ADVANCED_FEATURE_PATH,
                Constants.ADVANCED_FEATURE_PARENT,
                Constants.ADVANCED_FEATURE_ROOT,
                Constants.ADVANCED_FEATURE_PARENT_PROPERTY,
                Constants.CARET,
                Constants.TILDE
        };


        for (String pattern : advancedFeatures) {
            if (givenPath.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether an expression contains supported string method calls.
     *
     * @param expression expression to inspect
     * @return {@code true} when a supported string method is present
     */
    public static boolean hasStringMethods(String expression) {
        String [] stringMethods = {
                Constants.STRING_METHOD_CHAR_AT,
                Constants.STRING_METHOD_CODE_POINT_AT,
                Constants.STRING_METHOD_CONCAT,
                Constants.STRING_METHOD_ENDS_WITH,
                Constants.STRING_METHOD_INCLUDES,
                Constants.STRING_METHOD_INDEX_OF,
                Constants.STRING_METHOD_LAST_INDEX_OF,
                Constants.STRING_METHOD_STARTS_WITH,
                Constants.STRING_METHOD_TO_LOWER_CASE,
                Constants.STRING_METHOD_TO_UPPER_CASE
        };
        for (String strMethod : stringMethods) {
            if (expression.contains(strMethod)) {
                return true;
            }
        }
        return false;

    }

    /**
     * Replaces JSONPath Plus feature tokens with runtime values for the current node.
     *
     * @param traversalInstance traversal metadata
     * @param jsonPathExpression expression containing advanced features
     * @param currentNode current node
     * @return expression with advanced features resolved
     */

    public static String replaceAdvancedFeaturesWithActualValues(TraversalMapData traversalInstance,
                                                                 String jsonPathExpression, Object currentNode) {
        String result = jsonPathExpression;
        if (jsonPathExpression.contains(Constants.ADVANCED_FEATURE_LENGTH)) {
            result = replaceLength(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains(Constants.ADVANCED_FEATURE_PROPERTY)) {
            result = replaceProperty(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains(Constants.ADVANCED_FEATURE_PARENT_PROPERTY)) {
            result = replaceParentProperty(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains(Constants.ADVANCED_FEATURE_PARENT)) {
            result = replaceParent(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains(Constants.ADVANCED_FEATURE_PATH)) {
            result = replacePath(traversalInstance, result, currentNode);
        }
        if (jsonPathExpression.contains(Constants.ADVANCED_FEATURE_ROOT)) {
            result = replaceRoot(result);
        }
        if (jsonPathExpression.contains(Constants.ADVANCED_FEATURE_AT)) {
            result = replaceAt(traversalInstance, result, currentNode);
        }
        return result;
    }

    private static String replaceLength(TraversalMapData traversalInstance, String jsonPathExpression,
                                        Object currentNode) {
        String result = jsonPathExpression;
        Object getLengthObject = returnValuesForAdvancedFeatures(
                traversalInstance, currentNode, AdvancedFeatures.LENGTH);

        if (getLengthObject instanceof Integer) {
            result = result.replace(Constants.ADVANCED_FEATURE_LENGTH, String.valueOf(getLengthObject));
        }
        return result;
    }

    private static String replaceProperty(TraversalMapData traversalInstance, String jsonPathExpression,
                                          Object currentNode) {
        String result = jsonPathExpression;
        Object getPropertyObject = returnValuesForAdvancedFeatures(traversalInstance ,
                currentNode, AdvancedFeatures.PROPERTY);

        try {
            Integer.parseInt(String.valueOf(getPropertyObject));
        } catch (Exception e) {
            getPropertyObject = Constants.DOUBLE_QUOTE + getPropertyObject + Constants.DOUBLE_QUOTE;
        }
        result = result.replace(Constants.ADVANCED_FEATURE_PROPERTY, String.valueOf(getPropertyObject));
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
            getParentObject = Constants.DOUBLE_QUOTE + getParentObject + Constants.DOUBLE_QUOTE;
        }
        result = result.replace(Constants.ADVANCED_FEATURE_PARENT_PROPERTY, String.valueOf(getParentObject));
        return result;
    }

    private static String replaceParent(TraversalMapData traversalInstance, String jsonPathExpression,
                                        Object currentNode) {
        String result = jsonPathExpression;
        String parentValue;
        Object parentNode = returnValuesForAdvancedFeatures(traversalInstance, currentNode, AdvancedFeatures.PARENT);
        Object grandParentNode = traversalInstance.getParent(parentNode);
        if (grandParentNode != null) {
            parentValue = result.replace(Constants.ADVANCED_FEATURE_PARENT, Constants.JSON_PATH_ROOT_DOT);
        } else {
            parentValue = result.replace(Constants.ADVANCED_FEATURE_PARENT, Constants.JSON_PATH_ROOT);
        }

        return parentValue;
    }

    private static String replacePath(TraversalMapData traversalInstance, String jsonPathExpression,
                                      Object currentNode) {
        String result = jsonPathExpression;
        Object getPath = returnValuesForAdvancedFeatures(traversalInstance, currentNode, AdvancedFeatures.PATH);
        result = result.replace(
                Constants.ADVANCED_FEATURE_PATH,
                Constants.DOUBLE_QUOTE + getPath + Constants.DOUBLE_QUOTE
        );
        return result;
    }

    private static String replaceRoot(String jsonPathExpression) {
        String result = jsonPathExpression;
        result = result.replace(Constants.ADVANCED_FEATURE_ROOT, Constants.JSON_PATH_ROOT);
        return result;
    }

    private static String replaceAt(TraversalMapData traversalInstance, String jsonPathExpression, Object currentNode) {
        String result = jsonPathExpression;
        result = result.replace(Constants.ADVANCED_FEATURE_AT , String.valueOf(returnValuesForAdvancedFeatures(
                traversalInstance, currentNode, AdvancedFeatures.PATH)));
        return result;
    }

    /**
     * Evaluates JavaScript-like truthiness for values used in predicates.
     *
     * @param value value to evaluate
     * @return {@code true} if value is truthy
     */
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

    /**
     * Resolves JSONPath operands embedded in an expression before JEXL evaluation.
     *
     * @param traversalInstance traversal metadata
     * @param expression expression to transform
     * @param root root document
     * @return transformed expression
     */
    public static String comparisonOfPaths(TraversalMapData traversalInstance,
                                           String expression, Object root) {
        // Example scenario : $.store.book[2] === @root..['book'][2]
        String pathsOnBothSides = String.valueOf(Constants.JSONPATH_COMPARISON_REGEX);

        if (expression.matches(pathsOnBothSides)) {
            Matcher matcher = Constants.JSONPATH_MATCHER_REGEX.matcher(expression);

            if (matcher.find()) {
                String leftPath = matcher.group(1).trim();
                String rightPath = matcher.group(3).trim();

                Object leftVal = JsonPath.parse(root).read(leftPath);
                Object rightVal = JsonPath.parse(root).read(rightPath);

                String newLeftPath = Constants.DOUBLE_QUOTE + traversalInstance.getPath(leftVal) +
                        Constants.DOUBLE_QUOTE;
                String newRightPath = Constants.DOUBLE_QUOTE + traversalInstance.getPath(rightVal) +
                        Constants.DOUBLE_QUOTE;

                expression = expression.replace(leftPath, newLeftPath);
                expression = expression.replace(rightPath, newRightPath);

            }
            return expression;
        }

        // JSONPath Expression : "$.store.book[?(@.price > 19)]"
        Pattern pattern = Pattern.compile(Constants.NODE_VALUE_JSONPATH_REGEX);
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
                    Object firstElement = list.get(0);
                    if (firstElement instanceof Map && ((Map<Object, Object>) firstElement).size() == 1 &&
                            ((Map<Object, Object>) firstElement).containsKey(Constants.VALUE_KEY)) {
                        finalValue = ((Map<?, ?>) firstElement).get(Constants.VALUE_KEY);
                    } else {
                        finalValue = firstElement;
                    }
                }
            }
            int endIdx = matcher.end();
            String afterPath = expression.substring(endIdx);

            boolean hasOperatorAfter = afterPath.matches(Constants.COMPARISON_REGEX);
            String replacement;
            // Example testcase : $.store.book[?(@parent.bicycle)]
            if (!hasOperatorAfter) {
                replacement = String.valueOf(isTruthy(finalValue));
            } else {
                try {
                    Integer.parseInt(String.valueOf(finalValue));
                    replacement = String.valueOf(finalValue);
                } catch (Exception e) {
                    try {
                        Double.parseDouble(String.valueOf(finalValue));
                        replacement = String.valueOf(finalValue);
                    } catch (Exception e1) {
                        replacement = Constants.DOUBLE_QUOTE + String.valueOf(finalValue) +
                                Constants.DOUBLE_QUOTE;
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

    /**
     * Evaluates supported string function chains in an expression.
     *
     * @param expression expression containing string function calls
     * @return expression with string functions evaluated
     */
    public static String handleStringFunctions(String expression) {
        Pattern functionPattern = Pattern.compile(Constants.STRING_FUNCTIONS_REGEX);
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

                String result;
                try {
                    String arg = argument.replaceAll(Constants.SINGLE_QUOTE_REGEX, Constants.EMPTY_STRING);

                    switch (functionName) {
                        case Constants.STRING_METHOD_NAME_CHAR_AT:
                            result = Constants.DOUBLE_QUOTE + base.charAt(Integer.parseInt(arg))
                                    + Constants.DOUBLE_QUOTE;
                            break;
                        case Constants.STRING_METHOD_NAME_CODE_POINT_AT:
                            result = String.valueOf(base.codePointAt(Integer.parseInt(arg)));
                            break;
                        case Constants.STRING_METHOD_NAME_CONCAT:
                            result = Constants.DOUBLE_QUOTE + base.concat(arg) + Constants.DOUBLE_QUOTE;
                            break;
                        case Constants.STRING_METHOD_NAME_ENDS_WITH:
                            result = String.valueOf(base.endsWith(arg));
                            break;
                        case Constants.STRING_METHOD_NAME_INCLUDES:
                            result = String.valueOf(base.contains(arg));
                            break;
                        case Constants.STRING_METHOD_NAME_INDEX_OF:
                            result = String.valueOf(base.indexOf(arg));
                            break;
                        case Constants.STRING_METHOD_NAME_LAST_INDEX_OF:
                            result = String.valueOf(base.lastIndexOf(arg));
                            break;
                        case Constants.STRING_METHOD_NAME_STARTS_WITH:
                            result = String.valueOf(base.startsWith(arg));
                            break;
                        case Constants.STRING_METHOD_NAME_TO_LOWER_CASE:
                            result = Constants.DOUBLE_QUOTE + base.toLowerCase(Locale.ENGLISH) +
                                    Constants.DOUBLE_QUOTE;
                            break;
                        case Constants.STRING_METHOD_NAME_TO_UPPER_CASE:
                            result = Constants.DOUBLE_QUOTE + base.toUpperCase(Locale.ENGLISH) +
                                    Constants.DOUBLE_QUOTE;
                            break;
                        default:
                            result = Constants.DOUBLE_QUOTE + base + Constants.DOUBLE_QUOTE;
                            break;
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    result = Constants.DOUBLE_QUOTE + base + Constants.DOUBLE_QUOTE;
                }

                matcher.appendReplacement(sb, result);
            }
            matcher.appendTail(sb);
            expression = sb.toString();
        } while(!previousExpression.equals(expression));
        return expression;
    }

    /**
     * Evaluates an expression with JEXL.
     *
     * @param reducedExpression expression to evaluate
     * @return evaluated result
     */
    public static Object evaluateExpression(String reducedExpression) {
        JexlEngine jexl  = new JexlBuilder().create();
        JexlContext context = new MapContext();
        JexlExpression expression = jexl.createExpression(reducedExpression);
        return expression.evaluate(context);
    }
}
