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
 * Enumeration function to check whether a value is contained in a set of values
 */
@FunctionName("enumeration")
public class EnumerationFunction extends LintFunction {

    public EnumerationFunction(Map<String, Object> options) {
        super(options);
    }

    @Override
    public List<String> validateFunctionOptions(Map<String, Object> options) {
        ArrayList<String> errors = new ArrayList<>();

        if (options == null) {
            errors.add("Enumeration function requires the set of values.");
            return errors;
        }

        if (!options.containsKey("values")) {
            errors.add("Enumeration function requires the set of values.");
            return errors;
        }

        return errors;
    }

    public boolean execute(LintTarget target) {
        String[] values = (String[]) options.get("values");
        for (String value : values) {
            if (target.value.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
