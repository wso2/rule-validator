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
package org.wso2.json.path.evaluator.predicate;

import com.jayway.jsonpath.Predicate;

import org.wso2.json.path.evaluator.document.AdvancedFeatures;
import org.wso2.json.path.evaluator.document.Document;
import org.wso2.json.path.evaluator.document.TraversalMapData;
import org.wso2.json.path.evaluator.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Checking a node satisfies the condition or not
 */
public class EvaluatePredicate {
    /**
     * Implementing a predicate
     */
    public static class PredicateFeatures implements Predicate {
        private String jsonPathExpression;
        private Object rootDocument;
        private TraversalMapData traversalInstance;

        public PredicateFeatures(String jsonPathExpression, Document doc) {
            this.jsonPathExpression = jsonPathExpression;
            this.rootDocument = doc.getRootDocument();
            this.traversalInstance = doc.getTraversalInstanceDetails();
        }

        @Override
        public boolean apply(PredicateContext context) {
            Object currentNode = context.item();
            String expression = Util.replaceAdvancedFeaturesWithActualValues(traversalInstance,jsonPathExpression, currentNode);
            System.out.println(expression);
            int startIndex, endIndex = 0;
            String reducedExpr = "";
            List<?> targetNodes = List.of();
            int newValue = 0;
            boolean boolResult = false;
            if (expression.contains("?")) {
                startIndex = expression.indexOf("?");
                endIndex = expression.lastIndexOf("]");
                reducedExpr = expression.substring(startIndex + 1, endIndex);
            } else {
                startIndex = expression.indexOf("(");
                endIndex = expression.indexOf("]",startIndex);
                reducedExpr = expression.substring(startIndex , endIndex);
            }
            System.out.println(reducedExpr);
            reducedExpr = Util.comparisonOfPathsAndReplacingPathsWithActualValues(traversalInstance,reducedExpr,rootDocument);
            System.out.println(reducedExpr);
            Object evaluatedValue = Util.evaluateExpression(reducedExpr);
            Object parent = Util.returnValuesForAdvancedFeatures(traversalInstance,currentNode, AdvancedFeatures.PARENT);
            if (!expression.contains("?"))
            {
                if (parent instanceof List) {
                    targetNodes = new ArrayList<>((List<?>) parent);
                }
                boolResult = (currentNode.equals(targetNodes.get((Integer) evaluatedValue)));
            } else {
                evaluatedValue = Util.isTruthy(evaluatedValue);
                boolResult = (Boolean) evaluatedValue;
            }
            return boolResult;
        }
    }
}





