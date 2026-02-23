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
package org.wso2.json.path.evaluator;

import org.wso2.json.path.evaluator.document.Document;
import org.wso2.json.path.evaluator.evaluate.Evaluator;

import java.util.List;

/**
 * Entry point for evaluating JSONPath expressions against a document.
 */
public class JSONPathEvaluator {
    private final Document rootDoc;
    private final Evaluator evaluator;

    /**
     * Creates an evaluator instance for the provided JSON or YAML content.
     *
     * @param content root document content
     */
    public JSONPathEvaluator(String content) {
        this.rootDoc = new Document(content);
        this.evaluator = new Evaluator(rootDoc);
    }

    /**
     * Evaluates a JSONPath expression and returns matching paths.
     *
     * @param jsonPathExpression JSONPath expression to evaluate
     * @return matching JSONPath results as path strings
     * @throws JSONPathException when evaluation fails
     */
    public List<String> jsonPathEvaluate(String jsonPathExpression) throws JSONPathException {
        return evaluator.evaluate(jsonPathExpression, rootDoc);
    }
}
