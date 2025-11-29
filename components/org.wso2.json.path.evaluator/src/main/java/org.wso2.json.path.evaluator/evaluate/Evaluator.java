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

import com.jayway.jsonpath.*;


import org.wso2.json.path.evaluator.Constants;
import org.wso2.json.path.evaluator.InvalidJSONPathException;
import org.wso2.json.path.evaluator.JSONPathException;
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
    private List<String> expandedArray;
    private List<AdvancedFeatureBlock> advancedFeatures;
    public static final Configuration CONFIG = Configuration.builder().options(Option.AS_PATH_LIST).build();


    public Evaluator(Document root) {
        this.traversalInstance = root.getTraversalInstanceDetails();
    }

    // Returns a final list of paths for the given JSONPath Expression
    public List<String> evaluate(String jsonPathExpression, Document doc) throws JSONPathException {
        List<String> filteredResults = new ArrayList<>();
        List<String> finalPathResults = new ArrayList<>();
        List<String> multiFieldsList = expandMultiFields(jsonPathExpression);
        for (String expression : multiFieldsList) {
            jsonPathExpression = expression;
            this.advancedFeatures = new ArrayList<>();
            this.expandedArray = new ArrayList<>();
            Object root = doc.getRootDocument();
            String replaceExpression = "";
            String remainingString = "";
            String result = jsonPathExpression;


            /** It checks whether the given json expression contains any JSONPath Plus features
             *  Functions like (@number() , @string() , @boolean(), and others) are also JSONPath Plus features
             *  If it contains plus features, then we use Jayways Predicate and then evaluate using Jayways Path engine
             */

            if (Util.hasAdvancedFeatures(jsonPathExpression) || FunctionHandler.hasFunction(jsonPathExpression)) {
                advancedFeatures = extractAdvancedFeatures(jsonPathExpression);
                Queue<String> pathQueue = new LinkedList<>();
                for (int i = 0; i < advancedFeatures.size(); i++) {
                    String subStrBeforeFirstAdvFeature = result.substring(0, advancedFeatures.get(i).startIndex);
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
                                    (advancedFeatures.get(i).endIndex, advancedFeatures.get(i + 1).startIndex);
                        } else {
                            remainingString = result.substring(advancedFeatures.get(i).endIndex);
                        }
                        int indexOfCaret = jsonPathExpression.indexOf("^");
                        String subStringBeforeCaret = jsonPathExpression.substring(0,indexOfCaret);
                        if(subStringBeforeCaret.endsWith("..")) {
                            filteredResults = resolvingEndingDots(subStringBeforeCaret,root);
                        }
                        for (String path : filteredResults) {
                            Object parentNode = traversalInstance.getParent
                                    (JsonPath.using(Configuration.builder().build()).parse(root).read(path));
                            String parentPath = traversalInstance.getPath(parentNode);
                            if (parentResults.contains(parentPath)) {
                                continue;
                            } else {
                                parentResults.add(parentPath);
                            }

                        }
                        filteredResults = new ArrayList<>();
                        updatePathsForRemainingString(root, parentResults, remainingString, filteredResults);
                    } else if (advancedFeatures.get(i).expression.equals("~")) {
                        List<String> propertyResults = new ArrayList<>();
                        if (i + 1 < advancedFeatures.size()) {
                            remainingString = result.substring
                                    (advancedFeatures.get(i).endIndex, advancedFeatures.get(i + 1).startIndex);
                        } else {
                            remainingString = result.substring(advancedFeatures.get(i).endIndex);
                        }
                        int indexOfTilde = jsonPathExpression.indexOf("~");
                        String subStringBeforeTilde = jsonPathExpression.substring(0,indexOfTilde);
                        if(subStringBeforeTilde.endsWith("..")) {
                            filteredResults = resolvingEndingDots(subStringBeforeTilde,root);
                        } else if(subStringBeforeTilde.endsWith(".") && !subStringBeforeTilde.endsWith("..")) {
                            subStringBeforeTilde = subStringBeforeTilde.replace(".","..");
                            filteredResults = resolvingEndingDots(subStringBeforeTilde,root);
                        }
                        for (String path : filteredResults) {
                            Object currentNode = JsonPath.parse(root).read(path);
                            propertyResults.add(traversalInstance.getPath(currentNode));
                        }
                        filteredResults = new ArrayList<>();
                        updatePathsForRemainingString(root, propertyResults, remainingString, filteredResults);
                    } else {
                        // This else part handles, advanced features with the square braces.
                        filteredResults = new ArrayList<>(List.of());

                        List<String> finalPaths = expandPaths
                                (subStrBeforeFirstAdvFeature, root, pathsBeforeFirstAdvFeature);
                        pathQueue.addAll(finalPaths);

                        while (!pathQueue.isEmpty()) {
                            String currentPath = pathQueue.poll();
                            Object node = JsonPath.parse(root).read(currentPath);
                            Object parentNode = traversalInstance.getParent(node);
                            jsonPathExpression = result.replace(subStrBeforeFirstAdvFeature, currentPath);
                            if (i < advancedFeatures.size() - 1) {
                                int nextPredicateIndex = jsonPathExpression.indexOf(
                                        advancedFeatures.get(i + 1).expression);
                                remainingString = result.substring(
                                        advancedFeatures.get(i).endIndex + 1, advancedFeatures.get(i + 1).startIndex).trim();
                                if (FunctionHandler.hasFunction(advancedFeatures.get(i + 1).expression) ||
                                        advancedFeatures.get(i + 1).expression.equals("^") ||
                                        advancedFeatures.get(i + 1).expression.equals("~")) {
                                    int remainingStringIndex = jsonPathExpression.indexOf(remainingString);
                                    if (!remainingString.isEmpty()) {
                                        replaceExpression = jsonPathExpression.substring(0, remainingStringIndex);
                                    } else {
                                        replaceExpression = jsonPathExpression.substring(0, nextPredicateIndex);
                                    }
                                } else {
                                    int remainingStringIndex = jsonPathExpression.indexOf(remainingString);
                                    if (!remainingString.isEmpty()) {
                                        replaceExpression = jsonPathExpression.substring(0, remainingStringIndex);
                                    } else {
                                        replaceExpression = jsonPathExpression.substring(0, nextPredicateIndex - 1);
                                    }
                                }
                            } else {
                                remainingString = result.substring(advancedFeatures.get(i).endIndex + 1).trim();

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
                                            replaceExpression, node);
                                    updatePathsForRemainingString(root, functionResults, remainingString,
                                            filteredResults);

                                } else if (replaceExpression.contains(".match")) {
                                    handleMatchFunction(replaceExpression, node, filteredResults);

                                } else if (node instanceof StringWrapper ||
                                        node instanceof NumberWrapper || node instanceof BooleanWrapper) {
                                    List<String> primitiveResults = handlePrimitivesAndArrays(node,
                                            advancedFeatures.get(i).expression, root);
                                    updatePathsForRemainingString(root, primitiveResults, remainingString,
                                            filteredResults);
                                } else if (node instanceof List && parentNode instanceof Map &&
                                        expandedArray.contains(currentPath)) {
                                    List<String> arrayResults = handlePrimitivesAndArrays(node,
                                            advancedFeatures.get(i).expression,
                                            root);
                                    updatePathsForRemainingString(root, arrayResults, remainingString,
                                            filteredResults);
                                } else {
                                    Predicate predicate = new EvaluatePredicate.PredicateFeatures(replaceExpression, doc);
                                    replaceExpression = replaceExpression.replace(advancedFeatures.get(i).expression, "?");
                                    int predicateIndex = replaceExpression.indexOf("?");
                                    replaceExpression = replaceExpression.substring(0, predicateIndex + 2);
                                    List<String> predicateResults = JsonPath.using(CONFIG).parse(root).read(
                                            replaceExpression, predicate);
                                    updatePathsForRemainingString(root, predicateResults, remainingString, filteredResults);
                                }
                            } catch (PathNotFoundException e) {
                                /**
                                 * Consider this (e.g.,
                                 * $.store.book[?(@property==0)]..[?(@parentProperty!==0)]).
                                 * A failure in one path should not stop the entire evaluation.
                                 * If a path fails, simply skip it and continue,
                                 * since other paths may still produce valid results.
                                 */
                                continue;
                            }
                        }
                    }
                }
                finalPathResults.addAll(filteredResults);
            } else {
                // If the JSONPath expression does not contain any plus features, simply evaluate using Jayways
                if(jsonPathExpression.endsWith("..") || jsonPathExpression.endsWith(".")) {
                    finalPathResults.addAll(resolvingEndingDots(jsonPathExpression,root));
                }
                 else {
                    filteredResults.addAll(JsonPath.using(CONFIG).parse(root).read(jsonPathExpression));
                    finalPathResults.addAll(filteredResults);
                    filteredResults = new ArrayList<>();
                }
            }

        }
        //return filteredResults;
        return finalPathResults;
    }

    /** This extracts the advanced part in the given JSONPath Expression.
     * Get the index of the starting point and the ending point and the content inside
     * Example : [?(@property ==0)] then,
     * startIndex = indexOf('['),
     * endIndex = indexOf(']'),
     * expression = "?(@property!==0)"
     */

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

    /** To get all the paths before the first advanced square brace containing the advanced features
     * If the given JSONPath Expression : "$.store.book[?(@property!==0)]"
     * The advanced part : [?(@property!==0)],
     * Then this method returns the path before the advanced part , (i.e). "$.store.book"
     */
    private static List<String> getPathsBeforeFiltering(String jsonPathExpression, Object root) {
        List<String> paths;
        List<String> filteredPaths = new ArrayList<>(List.of());
        if (jsonPathExpression.endsWith("..")) {
            jsonPathExpression = jsonPathExpression.replaceAll(Constants.REPLACE_ENDING_DOT_REGEX, "..*");
        } else if(jsonPathExpression.endsWith(".") && !jsonPathExpression.endsWith("..")) {
            jsonPathExpression = jsonPathExpression.substring(0,jsonPathExpression.length() - 1);
        }
        paths = JsonPath.using(CONFIG).parse(root).read(jsonPathExpression);
        filteredPaths.addAll(paths);
        return filteredPaths;
    }

    // To expand the paths if the node is an instance of Map
    private List<String> expandPaths(String jsonPathExpression , Object root , List<String> paths) {
        List<String> expanded = new ArrayList<>();
        expandedArray = new ArrayList<>();
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
                        String childPath = path + "['" + key + "']";
                        if (!removeDuplicatesInArray.contains(childPath)) {
                            expanded.add(childPath);
                            removeDuplicatesInArray.add(childPath);
                        }
                        expandedArray.add(childPath);

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
                        String childPath = path + "['" + key + "']";
                        if (!removeDuplicatesInArray.contains(childPath)) {
                            expanded.add(childPath);
                            removeDuplicatesInArray.add(childPath);
                        }
                        expandedArray.add(childPath);

                    }
                } else {
                    if (!removeDuplicatesInArray.contains(path)) {
                        // These are leaf nodes, so no further expansion is needed
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

    /** Handling manually without passing to Jayways Predicate for the primitives since
     * filter operations cannot be applied to primitives in Jayways.
     * Manually handling when the current node is an array expanded from an object (Special case)
     */

    private List<String> handlePrimitivesAndArrays(Object node, String subStringPath , Object root) {
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

    /** Updates the intermediate paths by appending the remaining part of the JSONPath expression.
     * Consider the given JSONPath Expression : $.store.book[?(@parent.bicycle)].category[?(@parentProperty!==0)]
     * First it take the paths up to "$.store.book[?(@parent.bicycle)]"
     * remaining string is the subString in between two advanced predicates. Here, remainingString is ".category"
     * Then, we add "$" at the front. So, now it becomes "$.category".
     * If the intermediate path is $.store.book[0], then this method returns $.store.book[0].category
     * This path will be used by the next predicate
     */
    private static List<String> updatePathsForRemainingString(Object root, List<String> intermediateResults,
                                                              String remainingString, List<String> filteredResults)
            throws JSONPathException {
        if (!remainingString.isEmpty()) {
            try {
                String normalizedPath = remainingString.startsWith("$") ? remainingString : "$" + remainingString;
                if(normalizedPath.endsWith("..")) {
                    normalizedPath = normalizedPath.substring(0 , normalizedPath.length() - 2) + "..*";
                }
                if(normalizedPath.endsWith(".") && !normalizedPath.endsWith("..")) {
                    normalizedPath = normalizedPath.substring(0,normalizedPath.length()-1);
                }
                for (String path : intermediateResults) {
                    Object midNode = JsonPath.parse(root).read(path);
                    List<String> midPaths = JsonPath.using(CONFIG).parse(midNode).read(normalizedPath);

                    for (String subPath : midPaths) {
                        String combined = setPathsForRemainingString(path, subPath);
                        filteredResults.add(combined);
                    }
                }
            } catch (Exception e) {
                throw new JSONPathException("Invalid expression at path: "  + e.getMessage());
            }
        } else {
            filteredResults.addAll(intermediateResults);
        }
        return filteredResults;
    }

    /** Consider the given JSONPath : "$.store.book[?(@parent.bicycle)].category[?(@parentProperty!==0)]"
     *  Here base : paths obtained after the predicate, (i.e.) paths obtained from "$.store.book[?(@parent.bicycle)]"
     *  relative : Here, it is going to be "$.category" (subString between two predicates after normalizing)
     *  This method return paths by replacing "$." with intermediate paths.
     *  Example : It returns "$.store.book[0].category"
     */
    private static String setPathsForRemainingString(String base, String relative) {
        String replaceDollar = relative.replaceFirst(Constants.LEADING_DOLLAR_REGEX , "");
        if (base.endsWith("]")) {
            return base  + replaceDollar;
        } else {
            return base + "['" + replaceDollar + "']";
        }
    }

    // To handle match() function (e.g.) "$.store.book[?(@property.match(/'bn$'/i))]"
    private void handleMatchFunction(String expr, Object currentNode, List<String> filteredResults) {
        Pattern pattern = Pattern.compile(Constants.MATCH_FUNCTION_REGEX);
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

    // To handle multiple key fields inside a square brace. (e.g.) "$.store.book['category','author']"
    private List<String> expandMultiFields(String jsonPathExpression) {
        List<String> results = new ArrayList<>();
        results.add(jsonPathExpression);
        Pattern pattern = Pattern.compile("\\[\\s*'([^']+)'\\s*(?:,\\s*'([^']+)')+\\s*\\]");
        //$.store.book['name','price']['category','price']
        boolean hasMoreMultiFields = true;
        while(hasMoreMultiFields) {
            List<String> collectionOfMultiFields = new ArrayList<>();
            hasMoreMultiFields = false;
            for (String expression : results) {
                jsonPathExpression = expression;
                Matcher match = pattern.matcher(expression);
                if (!match.find()) {
                    collectionOfMultiFields.add(expression);
                    continue;
                }
                hasMoreMultiFields = true;
                String multiFieldsGroup = match.group(0);
                int startIndexOfMultiFields = match.start();
                int endIndexOfMultipleFields = match.end();
                String multiFields = multiFieldsGroup.substring(1, multiFieldsGroup.length() - 1); // 'category','author'
                String[] fields = multiFields.split(",");
                for (String fieldName : fields) {
                    String cleaned = fieldName.trim().replace("'", "");
                    String replaced = jsonPathExpression.substring(0, startIndexOfMultiFields) + "['" + cleaned + "']" + jsonPathExpression.substring(endIndexOfMultipleFields);
                    collectionOfMultiFields.add(replaced);
                }
            }
            results = collectionOfMultiFields;

        }
        return  results;
    }

    // In Jayways, expression should not end with ".", so handling expressions ending with dots
    private List<String> resolvingEndingDots(String jsonPathExpression, Object root) {
        List<String> result = new ArrayList<>();

        if(jsonPathExpression.endsWith("..")) {
            String pathBeforeDoubleDot = jsonPathExpression.substring(0 , jsonPathExpression.length() - 2);
            result.addAll(JsonPath.using(CONFIG).parse(root).read(pathBeforeDoubleDot));
            jsonPathExpression = pathBeforeDoubleDot + "..*";
            List<String> pathsEndingWithDoubleDot = new ArrayList<>(JsonPath.using(CONFIG).parse(root).read(jsonPathExpression));
            for(String path : pathsEndingWithDoubleDot) {
                Object node = JsonPath.using(Configuration.builder().options().build()).parse(root).read(path);
                if(node instanceof NumberWrapper || node instanceof StringWrapper || node instanceof BooleanWrapper) {
                    continue;
                } else {
                    result.add(path);
                }
            }
        } else if(jsonPathExpression.endsWith(".") && !jsonPathExpression.endsWith("..")) {
            String pathBeforeSingleDot = jsonPathExpression.substring(0 , jsonPathExpression.length() - 1);
            result.addAll(JsonPath.using(CONFIG).parse(root).read(pathBeforeSingleDot));
        }
        return  result;

    }
}
