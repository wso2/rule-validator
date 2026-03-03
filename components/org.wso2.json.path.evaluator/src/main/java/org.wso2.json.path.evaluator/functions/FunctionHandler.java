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
package org.wso2.json.path.evaluator.functions;

import org.wso2.json.path.evaluator.Constants;
import org.wso2.json.path.evaluator.document.wrappers.BooleanWrapper;
import org.wso2.json.path.evaluator.document.wrappers.NumberWrapper;
import org.wso2.json.path.evaluator.document.wrappers.StringWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles JSONPath Plus type functions.
 */
public class FunctionHandler {
    public static final List<String> ADVANCED_FUNCTIONS = Constants.ADVANCED_FUNCTIONS;

    /**
     * Checks whether an expression contains any supported function.
     *
     * @param expr expression to inspect
     * @return {@code true} if at least one supported function is present
     */
    public static boolean hasFunction(String expr) {
        for (String function : ADVANCED_FUNCTIONS) {
            if (expr.contains(function)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesFunction(Object node, String function) {
        if (function.equals(Constants.ADVANCED_FUNCTION_NUMBER)) {
            return node instanceof NumberWrapper;
        }
        if (function.equals(Constants.ADVANCED_FUNCTION_STRING)) {
            return node instanceof StringWrapper;
        }
        if (function.equals(Constants.ADVANCED_FUNCTION_BOOLEAN)) {
            return node instanceof BooleanWrapper;
        }
        if (function.equals(Constants.ADVANCED_FUNCTION_INTEGER)) {
            if (node instanceof NumberWrapper) {
                Number num = ((NumberWrapper) node).value;
                return num.doubleValue() == num.longValue();
            }
        }
        if (function.equals(Constants.ADVANCED_FUNCTION_ARRAY)) {
            return node instanceof List;
        }
        if (function.equals(Constants.ADVANCED_FUNCTION_OBJECT)) {
            return (node instanceof Map);
        }
        if (function.equals(Constants.ADVANCED_FUNCTION_NULL)) {
            return (node == null);
        }
        if (function.equals(Constants.ADVANCED_FUNCTION_SCALAR)) {
            return (node instanceof  NumberWrapper) || (node instanceof StringWrapper) ||
                    (node instanceof BooleanWrapper) || (node == null);
        }
        return false;
    }

    /**
     * Evaluates function-based constraints for a node and returns matching paths.
     *
     * @param jsonPathExpression JSONPath expression containing a function call
     * @param node node to evaluate
     * @return matching path list
     */
    public static List<String> handleFunctions(String jsonPathExpression, Object node) {
        List<String> finalResults = new ArrayList<>();
        String advancedFunction = null;
        int functionStart = -1;
        for (String function : ADVANCED_FUNCTIONS) {
            if (jsonPathExpression.contains(function)) {
                functionStart = jsonPathExpression.indexOf(function);
                advancedFunction = function;
                break;
            }
        }
        if (advancedFunction == null) {
            return finalResults;
        }
        String basePathSubString = jsonPathExpression.substring(0, functionStart);
        if (matchesFunction(node, advancedFunction)) {
            finalResults.add(basePathSubString);
        }
        return finalResults;
    }
}
