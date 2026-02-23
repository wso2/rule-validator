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
package org.wso2.rule.validator.validator;

import org.wso2.rule.validator.DiagnosticSeverity;

/**
 * Class to represent the result of a document validation that is sent to the user
 */
public class DocumentValidationResult {
    public final String path;
    public final String message;
    public final String ruleName;
    public final String severity;

    public DocumentValidationResult(String path, String message, String ruleName, DiagnosticSeverity severity) {
        this.path = path;
        this.message = message;
        this.ruleName = ruleName;
        this.severity = DiagnosticSeverity.getSeverityString(severity);
    }
}
