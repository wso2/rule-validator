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
package org.wso2.rule.validator.document;

import java.util.List;

/**
 * This class represents a lint target in a document.
 */
public class LintTarget {
    public List<String> jsonPath;
    public final Object value;

    public LintTarget(List<String> jsonPath, Object value) {
        this.jsonPath = jsonPath;
        this.value = value;
    }

    public static String getPathString(List<String> jsonPath) {
        StringBuilder resultPath = new StringBuilder();
        for (String path : jsonPath) {
            resultPath.append("[").append(path).append("]");
        }
        return resultPath.toString();
    }

    public String getTargetName() {
        try {
            return jsonPath.get(jsonPath.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }
}
