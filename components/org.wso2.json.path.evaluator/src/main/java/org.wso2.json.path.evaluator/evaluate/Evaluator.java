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
package org.wso2.json.path.evaluator.evaluate;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wso2.json.path.evaluator.InvalidJSONPathException;
import org.wso2.json.path.evaluator.document.Document;
import org.wso2.json.path.evaluator.document.TraversalMapData;
import org.wso2.json.path.evaluator.document.wrappers.BooleanWrapper;
import org.wso2.json.path.evaluator.document.wrappers.NumberWrapper;
import org.wso2.json.path.evaluator.document.wrappers.StringWrapper;
import org.wso2.json.path.evaluator.functions.FunctionHandler;
import org.wso2.json.path.evaluator.predicate.EvaluatePredicate;
import org.wso2.json.path.evaluator.utils.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluation
 */
public class Evaluator {
    private final TraversalMapData traversalInstance;
    private List<String> expandedAnObjectToArrayList;
    private List<AdvancedFeatureBlock> advancedFeatures;
    public static final Configuration CONFIG = Configuration.builder().options(Option.AS_PATH_LIST).build();
    private static final Logger logger = LoggerFactory.getLogger(Evaluator.class);


    public Evaluator(Document root) {
        this.advancedFeatures = new ArrayList<>();
        this.expandedAnObjectToArrayList = new ArrayList<>();
        this.traversalInstance = root.getTraversalInstanceDetails();
    }

    public List<String> evaluate(String jsonPathExpression, Document doc) throws Exception {
        this.advancedFeatures = new ArrayList<>();
        this.expandedAnObjectToArrayList = new ArrayList<>();
        Object root = doc.getRootDocument();
        String replaceExpression = "";
        String result = jsonPathExpression;
        List<String> filteredResults = new ArrayList<>();
        String remainingString = "";

        if (Util.hasAdvancedFeatures(jsonPathExpression) || FunctionHandler.hasFunction(jsonPathExpression)) {
            advancedFeatures = extractAdvancedFeatures(jsonPathExpression);
            Queue<String> pathQueue = new LinkedList<>();
            for (int i = 0; i < advancedFeatures.size(); i++) {
                String subStrBeforeFirstAdvFeature = result.substring(0, advancedFeatures.get(i).start);
                List<String> pathsBeforeFirstAdvFeature;
                if (i == 0) {
                    pathsBeforeFirstAdvFeature = getPathsBeforeFiltering(subStrBeforeFirstAdvFeature, root);
                    filteredResults = pathsBeforeFirstAdvFeature;
                } else {
                    pathsBeforeFirstAdvFeature = new ArrayList<>(filteredResults);
                }

                if (advancedFeatures.get(i).expression.equals("^")) {
                    List<String> parentResults = new ArrayList<>(List.of());
                    if (i < advancedFeatures.size() - 1) {
                        remainingString = result.substring
                                (advancedFeatures.get(i).end, advancedFeatures.get(i + 1).start);
                    } else {
                        remainingString = result.substring(advancedFeatures.get(i).end);
                    }
                    for (String path : filteredResults) {
                        Object parentNode = traversalInstance.getParent
                                (JsonPath.using(Configuration.builder().build()).parse(root).read(path));
                        parentResults.add(traversalInstance.getPath(parentNode));
                    }
                    filteredResults = new ArrayList<>();
                    getPathsForRemainingString(root , parentResults , remainingString , filteredResults);
                } else if (advancedFeatures.get(i).expression.equals("~")) {
                    List<String> propertyResults = new ArrayList<>();
                    if (i + 1 < advancedFeatures.size()) {
                        remainingString = result.substring
                                (advancedFeatures.get(i).end , advancedFeatures.get(i + 1).start);
                    } else {
                        remainingString = result.substring(advancedFeatures.get(i).end);
                    }
                    for (String path : filteredResults) {
                        Object currentNode = JsonPath.parse(root).read(path);
                        propertyResults.add(traversalInstance.getPath(currentNode));
                    }
                    filteredResults = new ArrayList<>();
                    getPathsForRemainingString(root , propertyResults , remainingString , filteredResults);
                } else {
                    // This is the advanced features with the square braces.
                    filteredResults = new ArrayList<>(List.of());

                    List<String> finalPaths = expandPaths
                            (subStrBeforeFirstAdvFeature , root , pathsBeforeFirstAdvFeature);
                    pathQueue.addAll(finalPaths);

                    while (!pathQueue.isEmpty()) {
                        String currentPath = pathQueue.poll();
                        Object node = JsonPath.parse(root).read(currentPath);
                        Object parentNode = traversalInstance.getParent(node);
                        jsonPathExpression = result.replace(subStrBeforeFirstAdvFeature, currentPath);
                        if (i < advancedFeatures.size() - 1) {
                            logger.info(advancedFeatures.get(i + 1).expression);
                            int nextPredicateIndex = jsonPathExpression.indexOf(advancedFeatures.get(i + 1).expression);
                            remainingString = result.substring(
                                    advancedFeatures.get(i).end + 1, advancedFeatures.get(i + 1).start).trim();
                            logger.info(String.valueOf(nextPredicateIndex));
                            if (FunctionHandler.hasFunction(advancedFeatures.get(i + 1).expression) ||
                                    advancedFeatures.get(i + 1).expression.equals("^") ||
                                    advancedFeatures.get(i + 1).expression.equals("~")) {
                                int remainingStringIndex = jsonPathExpression.indexOf(remainingString);
                                if (!remainingString.isEmpty()) {
                                    replaceExpression = jsonPathExpression.substring(0 , remainingStringIndex);
                                } else {
                                    replaceExpression = jsonPathExpression.substring(0 , nextPredicateIndex);
                                }
                            } else {
                                int remainingStringIndex = jsonPathExpression.indexOf(remainingString);
                                if (!remainingString.isEmpty()) {
                                    replaceExpression = jsonPathExpression.substring(0, remainingStringIndex);
                                } else {
                                    replaceExpression = jsonPathExpression.substring(0 , nextPredicateIndex - 1);
                                }
                            }
                        } else {
                            logger.info("JSONPath Expression : " + jsonPathExpression);
                            logger.info("Boundaries: " + advancedFeatures.get(i).end +
                                    1 + "Result Length: " + result.length());
                            remainingString = result.substring(advancedFeatures.get(i).end + 1).trim();
                            logger.info("Remaining String:" + remainingString);
                            int remainingStringIndex = jsonPathExpression.indexOf(remainingString);
                            if (!remainingString.isEmpty()) {
                                replaceExpression = jsonPathExpression.substring(0, remainingStringIndex);
                            } else {
                                replaceExpression = jsonPathExpression;
                            }
                        }
                        try {
                            if (FunctionHandler.hasFunction(replaceExpression)) {
                                List<String> functionResults = FunctionHandler.processFunctions(
                                        replaceExpression, root);
                                getPathsForRemainingString(root, functionResults, remainingString, filteredResults);

                            } else if (replaceExpression.contains(".match")) {
                                handleMatchFunction(replaceExpression, node, filteredResults);

                            } else if (node instanceof StringWrapper ||
                                    node instanceof NumberWrapper || node instanceof BooleanWrapper) {
                                List<String> primitiveResults = handlePrimitives(node,
                                        advancedFeatures.get(i).expression, root);
                                getPathsForRemainingString(root, primitiveResults, remainingString, filteredResults);
                            } else if (node instanceof List && parentNode instanceof Map &&
                                    expandedAnObjectToArrayList.contains(currentPath)) {
                                List<String> arrayResults = handleArrays(node, advancedFeatures.get(i).expression,
                                        root);
                                getPathsForRemainingString(root, arrayResults, remainingString, filteredResults);
                            } else {
                                Predicate predicate = new EvaluatePredicate.PredicateFeatures(replaceExpression, doc);
                                replaceExpression = replaceExpression.replace(advancedFeatures.get(i).expression, "?");
                                int predicateIndex = replaceExpression.indexOf("?");
                                replaceExpression = replaceExpression.substring(0, predicateIndex + 2);
                                List<String> predicateResults = JsonPath.using(CONFIG).parse(root).read(
                                        replaceExpression, predicate);
                                getPathsForRemainingString(root, predicateResults, remainingString, filteredResults);
                            }
                        } catch (PathNotFoundException e) {
                            logger.warn("Path not found: " + currentPath);

                        } catch (JsonPathException | IllegalArgumentException e) {
                            logger.error("Invalid expression at path: " + currentPath + " -> " + e.getMessage());

                        } catch (Exception e) {
                            logger.error("Unexpected failure at path: " + currentPath, e);
                        }
                    }
                }
            }
        } else {
            // No advanced features or functions passed directly to Jayways
            filteredResults.addAll(JsonPath.using(CONFIG).parse(root).read(jsonPathExpression));

        }
        return filteredResults;
    }

    // To keep track of the indices of the advanced features in the given JSONPath expression
    private  List<AdvancedFeatureBlock> extractAdvancedFeatures(String expr) throws InvalidJSONPathException {
        Stack<Integer> stack = new Stack<>();

        for (String function : FunctionHandler.ADVANCED_FUNCTIONS) {
            int index = expr.indexOf(function);
            while (index != -1) {
                advancedFeatures.add(new AdvancedFeatureBlock(index, index + function.length() - 1, function));
                index = expr.indexOf(function, index + function.length());
            }
        }
        int start = -1;

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (c == '[') {
                if (stack.isEmpty()) {
                    start = i;
                }
                stack.push(i);
            } else if (c == ']') {
                if (stack.isEmpty()) {
                    throw new InvalidJSONPathException("Extra closing bracket at position " + i);
                }
                stack.pop();

                if (stack.isEmpty() && start != -1) {
                    String filter = expr.substring(start + 1, i);
                    if (Util.hasAdvancedFeatures(filter)) {
                        advancedFeatures.add(new AdvancedFeatureBlock(start, i, filter));
                    }
                    start = -1;
                }
            } else if (c == '^') {
                start = i;
                advancedFeatures.add(new AdvancedFeatureBlock(start, i + 1 , "^"));
            } else if (c == '~') {
                start = i;
                advancedFeatures.add(new AdvancedFeatureBlock(start, i + 1, "~"));
            }
        }
        if (!stack.isEmpty()) {
            throw new InvalidJSONPathException("Unbalanced brackets in expression ");
        }

        return advancedFeatures;
    }

    // To get all the paths before the first advanced square brace containing the advanced features
    private static List<String> getPathsBeforeFiltering(String jsonPathExpression, Object root) {
        List<String> paths;
        List<String> filteredPaths = new ArrayList<>(List.of());
        if (jsonPathExpression.matches(".*\\.\\.$")) {
            jsonPathExpression = jsonPathExpression.replaceAll("\\.\\.$", "..*");
        }
        paths = JsonPath.using(CONFIG).parse(root).read(jsonPathExpression);
        filteredPaths.addAll(paths);
        return filteredPaths;
    }

    // To expand the paths if the node is an instance of Map
    private List<String> expandPaths(String jsonPathExpression , Object root , List<String> paths) {
        List<String> expanded = new ArrayList<>();
        expandedAnObjectToArrayList = new ArrayList<>();
        List<String> removeDuplicatesInArray = new ArrayList<>();
        for (String pathObj : paths) {
            String path = pathObj;
            Object node = JsonPath.parse(root).read(path);
            if (jsonPathExpression.endsWith("..*") || jsonPathExpression.endsWith("..")) {
                if (!removeDuplicatesInArray.contains(path)) {
                    expanded.add(path);
                    removeDuplicatesInArray.add(path);

                }


                if (node instanceof Map) {
                    Map<Object , Object> mapNode = (Map<Object , Object>) node;
                    for (Map.Entry<Object, Object> entry : mapNode.entrySet()) {
                        Object key = entry.getKey();
                        //Object value = entry.getValue();
                        String childPath = path + "['" + key + "']";
                        if (!removeDuplicatesInArray.contains(childPath)) {
                            expanded.add(childPath);
                            removeDuplicatesInArray.add(childPath);
                        }
                        expandedAnObjectToArrayList.add(childPath);

                    }
                } else {
                    if (!removeDuplicatesInArray.contains(path)) {
                        if (node instanceof StringWrapper || node instanceof NumberWrapper ||
                                node instanceof BooleanWrapper) {
                            continue;
                        } else {
                            expanded.add(path);
                            removeDuplicatesInArray.add(path);
                        }
                    }
                }
            } else {
                if (node instanceof Map) {
                    Map<Object , Object> mapNode = (Map<Object , Object>) node;
                    for (Map.Entry<Object , Object> entry : mapNode.entrySet()) {
                        Object key = entry.getKey();
                        //Object value = entry.getValue();
                        String childPath = path + "['" + key + "']";
                        if (!removeDuplicatesInArray.contains(childPath)) {
                            expanded.add(childPath);
                            removeDuplicatesInArray.add(childPath);
                        }
                        expandedAnObjectToArrayList.add(childPath);

                    }
                } else {
                    if (!removeDuplicatesInArray.contains(path)) {
                        if (node instanceof StringWrapper || node instanceof NumberWrapper ||
                                node instanceof BooleanWrapper) {
                            continue;
                        } else {
                            expanded.add(path);
                            removeDuplicatesInArray.add(path);

                        }
                    }
                }
            }
        }
        return expanded;
    }

    // Filter operations cannot be applied to primitives in Jayways. Manually handling
    private List<String> handlePrimitives(Object node, String subStringPath , Object root) {
        List<String> primitiveResults = new ArrayList<>();

        boolean truthy = true;
        int index = 0;
        int endIndex = 0;
        String reducedExpr = "";
        String expression = Util.replaceAdvancedFeaturesWithActualValues(traversalInstance , subStringPath , node);
        if (expression.contains("?")) {
            index = expression.indexOf("(");
            endIndex = expression.lastIndexOf(")");
            reducedExpr = expression.substring(index , endIndex + 1);
        } else {
            index = expression.indexOf("(");
            endIndex = expression.lastIndexOf(")");
            reducedExpr = expression.substring(index + 1, endIndex);
        }
        reducedExpr = Util.comparisonOfPathsAndReplacingPathsWithActualValues(traversalInstance , reducedExpr , root);

        Object evaluatedResult = Util.evaluateExpression(reducedExpr);
        evaluatedResult = Util.isTruthy(evaluatedResult);

        truthy = (Boolean) evaluatedResult;
        if (truthy) {
            String path = traversalInstance.getPath(node);
            primitiveResults.add(path);
        }
        return primitiveResults;
    }

    private List<String> handleArrays(Object node, String subStringPath , Object root) {
        List<String> arrayResults = new ArrayList<>();

        int index = 0;
        int endIndex = 0;
        boolean truthy = true;
        String reducedExpr = "";
        String expression = Util.replaceAdvancedFeaturesWithActualValues(traversalInstance , subStringPath , node);
        if (expression.contains("?")) {
            index = expression.indexOf("(");
            endIndex = expression.lastIndexOf(")");
            reducedExpr = expression.substring(index + 1, endIndex);
        } else {
            index = expression.indexOf("[");
            endIndex = expression.lastIndexOf("]");
            reducedExpr = expression.substring(index + 1, endIndex);
        }
        reducedExpr = Util.comparisonOfPathsAndReplacingPathsWithActualValues(traversalInstance , reducedExpr , root);

        Object evaluatedResult = Util.evaluateExpression(reducedExpr);
        evaluatedResult = Util.isTruthy(evaluatedResult);

        truthy = (Boolean) evaluatedResult;
        if (truthy) {
            String path = traversalInstance.getPath(node);
            arrayResults.add(path);
        }
        return arrayResults;
    }

    private static List<String> getPathsForRemainingString(Object root, List<String> intermediateResults,
                                                           String remainingString, List<String> filteredResults) {
        if (!remainingString.isEmpty()) {
            try {

                String normalizedPath = remainingString.startsWith("$") ? remainingString : "$" + remainingString;

                for (String path : intermediateResults) {
                    Object midNode = JsonPath.parse(root).read(path);
                    List<String> midPaths = JsonPath.using(CONFIG).parse(midNode).read(normalizedPath);

                    for (String subPath : midPaths) {
                        String combined = setPathsForRemainingString(path, subPath);
                        filteredResults.add(combined);
                    }
                }

            } catch (Exception e) {
                logger.info("Error applying intermediate path: " + e.getMessage());
            }
        } else {
            filteredResults.addAll(intermediateResults);
        }
        return filteredResults;
    }

    private static String setPathsForRemainingString(String base, String relative) {
        String cleaned = relative.replaceFirst("^\\$\\.?" , "");
        if (base.endsWith("]")) {
            return base  + cleaned;
        } else {
            return base + "['" + cleaned + "']";
        }
    }

    public void handleMatchFunction(String expr, Object currentNode, List<String> filteredResults) {
        Pattern pattern = Pattern.compile("((?:@[a-zA-Z0-9_\\.]*|[a-zA-Z0-9_\\.]+))\\.match\\(/(.*?)/([a-z]*)\\)");
        Matcher matcher = pattern.matcher(expr);

        if (matcher.find()) {
            String valueExpr = matcher.group(1);
            String regex = matcher.group(2);
            String flags = matcher.group(3);

            Object evaluatedValue = Util.replaceAdvancedFeaturesWithActualValues
                    (traversalInstance , valueExpr , currentNode);
            if (evaluatedValue.toString().startsWith("$")) {
                evaluatedValue = currentNode;
            }
            String strValue = evaluatedValue != null ? evaluatedValue.toString() : "";

            if (strValue.startsWith("\"") && strValue.endsWith("\"") && strValue.length() >= 2) {
                strValue = strValue.substring(1, strValue.length() - 1);
            }

            int flagBits = 0;
            if (flags != null) {
                if (flags.contains("i")) {
                    flagBits |= Pattern.CASE_INSENSITIVE;
                }
            }
            boolean matches = false;
            if (!strValue.isEmpty()) {
                matches = Pattern.compile(regex, flagBits).matcher(strValue).find();
            }
            if (matches) {
                filteredResults.add(traversalInstance.getPath(currentNode));
            }
        }
    }
}
