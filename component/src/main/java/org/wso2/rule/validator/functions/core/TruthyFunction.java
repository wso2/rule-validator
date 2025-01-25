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
package org.wso2.rule.validator.functions.core;

import org.wso2.rule.validator.document.LintTarget;
import org.wso2.rule.validator.functions.FunctionName;
import org.wso2.rule.validator.functions.LintFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Function to check if a value is truthy
 */
@FunctionName("truthy")
public class TruthyFunction extends LintFunction {

    public TruthyFunction(Map<String, Object> options) {
        super(null);
    }

    @Override
    public List<String> validateFunctionOptions(Map<String, Object> options) {
        ArrayList<String> errors = new ArrayList<>();

        if (options != null && !options.isEmpty()) {
            errors.add("Truthy function does not accept any options.");
        }

        return errors;
    }

    public boolean execute(LintTarget target) {
        if (target.value instanceof String) {
            return !((String) target.value).isEmpty();
        } else if (target.value instanceof List) {
            return !((List) target.value).isEmpty();
        } else if (target.value instanceof Map) {
            return !((Map) target.value).isEmpty();
        } else {
            return target.value != null;
        }
    }
}
