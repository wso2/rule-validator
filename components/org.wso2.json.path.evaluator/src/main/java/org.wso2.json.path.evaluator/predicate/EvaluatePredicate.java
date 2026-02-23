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
package org.wso2.json.path.evaluator.predicate;

import com.jayway.jsonpath.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.json.path.evaluator.Constants;
import org.wso2.json.path.evaluator.document.AdvancedFeatures;
import org.wso2.json.path.evaluator.document.Document;
import org.wso2.json.path.evaluator.document.TraversalMapData;
import org.wso2.json.path.evaluator.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Predicate support for JSONPath filtering.
 */
public class EvaluatePredicate {

    private static final Log log = LogFactory.getLog(EvaluatePredicate.class);

    /**
     * Predicate implementation that supports JSONPath Plus expressions.
     */
    public static class PredicateFeatures implements Predicate {
        private String jsonPathExpression;
        private Object rootDocument;
        private TraversalMapData traversalInstance;

        /**
         * Creates a predicate evaluator for the provided expression.
         *
         * @param jsonPathExpression JSONPath predicate expression
         * @param doc source document
         */
        public PredicateFeatures(String jsonPathExpression, Document doc) {
            this.jsonPathExpression = jsonPathExpression;
            this.rootDocument = doc.getRootDocument();
            this.traversalInstance = doc.getTraversalInstanceDetails();
        }

        /**
         * Applies the predicate to the current context node.
         *
         * @param context predicate context
         * @return {@code true} if the node satisfies the predicate
         */
        @Override
        public boolean apply(PredicateContext context) {
            Object currentNode = context.item();
            String expression = Util.replaceAdvancedFeaturesWithActualValues(traversalInstance,
                    jsonPathExpression, currentNode);
            int startIndex, endIndex = 0;
            String reducedExpr = Constants.EMPTY_STRING;
            boolean boolResult = false;
            if (expression.contains(Constants.QUESTION_MARK)) {
                startIndex = expression.indexOf(Constants.QUESTION_MARK);
                endIndex = expression.lastIndexOf(Constants.CLOSE_BRACKET);
                reducedExpr = expression.substring(startIndex + 1, endIndex);
            } else {
                startIndex = expression.indexOf(Constants.OPEN_PARENTHESES);
                endIndex = expression.indexOf(Constants.CLOSE_BRACKET, startIndex);
                reducedExpr = expression.substring(startIndex , endIndex);
            }
            reducedExpr = Util.comparisonOfPaths(traversalInstance ,
                    reducedExpr , rootDocument);

            Object evaluatedValue = Util.evaluateExpression(reducedExpr);
            Object parent = Util.returnValuesForAdvancedFeatures(traversalInstance , currentNode ,
                    AdvancedFeatures.PARENT);
            if (!expression.contains(Constants.QUESTION_MARK)) {
                if (parent instanceof List && evaluatedValue instanceof Integer) {
                    List targetNodes;
                    targetNodes = new ArrayList<>((List<?>) parent);
                    boolResult = (currentNode.equals(targetNodes.get((Integer) evaluatedValue)));
                } else {
                    return false;
                }
            } else {
                evaluatedValue = Util.isTruthy(evaluatedValue);
                boolResult = (Boolean) evaluatedValue;
            }
            return boolResult;
        }
    }
}
