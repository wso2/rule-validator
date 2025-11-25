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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        private static final Logger logger = LoggerFactory.getLogger(EvaluatePredicate.class);

        public PredicateFeatures(String jsonPathExpression, Document doc) {
            this.jsonPathExpression = jsonPathExpression;
            this.rootDocument = doc.getRootDocument();
            this.traversalInstance = doc.getTraversalInstanceDetails();
        }

        @Override
        public boolean apply(PredicateContext context) {
            Object currentNode = context.item();
            String expression = Util.replaceAdvancedFeaturesWithActualValues(
                    traversalInstance, jsonPathExpression, currentNode);
            boolean isFilterExpression = expression.contains("?");
            int startIndex;
            int endIndex;
            String reducedExpr;
            boolean boolResult = false;
            if (isFilterExpression) {
                startIndex = expression.indexOf("?");
                endIndex = expression.lastIndexOf("]");
                if (startIndex < 0 || endIndex <= startIndex) {
                    logger.warn("Invalid predicate filter expression: {}", expression);
                    return false;
                }
                reducedExpr = expression.substring(startIndex + 1, endIndex);
            } else {
                startIndex = expression.indexOf("(");
                endIndex = expression.indexOf("]", startIndex);
                if (startIndex < 0 || endIndex <= startIndex) {
                    logger.warn("Invalid index-based predicate expression: {}", expression);
                    return false;
                }
                reducedExpr = expression.substring(startIndex, endIndex);
            }
            //e.g. : ($.store.book === $.store.book)
            reducedExpr = Util.comparisonOfPathsAndReplacingPathsWithActualValues(traversalInstance,
                    reducedExpr, rootDocument);

            Object value = Util.evaluateExpression(reducedExpr);
            Object parent = Util.returnValuesForAdvancedFeatures(traversalInstance,
                    currentNode, AdvancedFeatures.PARENT);
            if (!isFilterExpression) {
                if (!(value instanceof Number)) {
                    logger.warn("Index-based predicate did not evaluate to a number: {} (value={})",
                            reducedExpr, value);
                    return false;
                }
                if (!(parent instanceof List)) {
                    logger.warn("Index-based predicate requires a list parent, but found: {}",
                            parent == null ? "null" : parent.getClass().getName());
                    return false;
                }
                List<?> targetNodes = new ArrayList<>((List<?>) parent);
                int index = ((Number) value).intValue();
                if (index < 0 || index >= targetNodes.size()) {
                    return false;
                }
                boolResult = currentNode.equals(targetNodes.get(index));
            } else {
                boolResult = Util.isTruthy(value);
            }

            return boolResult;
        }
    }
}





