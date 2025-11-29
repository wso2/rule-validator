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
package org.wso2.json.path.evaluator.functions;


import org.wso2.json.path.evaluator.document.wrappers.BooleanWrapper;
import org.wso2.json.path.evaluator.document.wrappers.NumberWrapper;
import org.wso2.json.path.evaluator.document.wrappers.StringWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handling functions
 */
public class FunctionHandler {
    public static  final List<String> ADVANCED_FUNCTIONS = List.of(
            "@number()",
            "@string()",
            "@integer()",
            "@boolean()",
            "@array()",
            "@object()",
            "@null()",
            "@scalar()"
    );

    // This method returns boolean value if the expression contains any functions
    public static boolean hasFunction(String expr) {
        for (String function : ADVANCED_FUNCTIONS) {
            if (expr.contains(function)) {
                return true;
            }
        }
        return false;
    }

    // Returns true if the node matches the specified JSONPath Plus function
    private static boolean matchesFunction(Object node, String function) {
        if (function.equals("@number()")) {
            return node instanceof NumberWrapper;
        }
        if (function.equals("@string()")) {
            return node instanceof StringWrapper;
        }
        if (function.equals("@boolean()")) {
            return node instanceof BooleanWrapper;
        }
        if (function.equals("@integer()")) {
            if (node instanceof NumberWrapper) {
                Number num = ((NumberWrapper) node).value;
                return num.doubleValue() == num.longValue();
            }
        }
        if (function.equals("@array()")) {
            return node instanceof List;
        }
        if (function.equals("@object()")) {
            return (node instanceof Map);
        }
        if (function.equals("@null()")) {
            return (node == null);
        }
        if (function.equals("@scalar()")) {
            return (node instanceof  NumberWrapper) || (node instanceof StringWrapper) ||
                    (node instanceof BooleanWrapper) || (node == null);
        }
        return false;
    }

    // This method return paths when the expression includes any JSONPath functions
    public static List<String> processFunctions(String jsonPathExpression , Object node) {
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
        String basePathSubString = jsonPathExpression.substring(0 , functionStart);
        if (matchesFunction(node , advancedFunction)) {
            finalResults.add(basePathSubString);
        }
        return finalResults;
    }
}
