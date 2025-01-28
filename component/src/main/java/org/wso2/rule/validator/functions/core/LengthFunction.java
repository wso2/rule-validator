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

import org.wso2.rule.validator.Constants;
import org.wso2.rule.validator.document.LintTarget;
import org.wso2.rule.validator.functions.FunctionName;
import org.wso2.rule.validator.functions.LintFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Length function implementation
 */
@FunctionName("length")
public class LengthFunction extends LintFunction {

    public LengthFunction(Map<String, Object> options) {
        super(options);
    }

    @Override
    public List<String> validateFunctionOptions() {
        ArrayList<String> errors = new ArrayList<>();

        if (options == null) {
            errors.add("Length function requires at least a min or a max value.");
            return errors;
        }

        if (!options.containsKey(Constants.RULESET_LENGTH_MIN) && !options.containsKey(Constants.RULESET_LENGTH_MAX)) {
            errors.add("Length function requires at least a min or a max value.");
            return errors;
        }

        if (options.containsKey(Constants.RULESET_LENGTH_MIN) &&
                !(options.get(Constants.RULESET_LENGTH_MIN) instanceof Integer)) {
            errors.add("Length function min value should be an integer.");
        }

        if (options.containsKey(Constants.RULESET_LENGTH_MAX) &&
                !(options.get(Constants.RULESET_LENGTH_MAX) instanceof Integer)) {
            errors.add("Length function max value should be an integer.");
        }

        return errors;
    }

    public boolean executeFunction(LintTarget target) {
        int length;

        if (target.value instanceof String) {
            length = ((String) target.value).length();
        } else if (target.value instanceof List) {
            length = ((List) target.value).size();
        } else if (target.value instanceof Map) {
            length = ((Map) target.value).size();
        } else {
            return true;
        }

        if (options.containsKey(Constants.RULESET_LENGTH_MIN) && options.containsKey(Constants.RULESET_LENGTH_MAX)) {
            int min = (int) options.get(Constants.RULESET_LENGTH_MIN);
            int max = (int) options.get(Constants.RULESET_LENGTH_MAX);
            return length >= min && length <= max;
        } else  if (options.containsKey(Constants.RULESET_LENGTH_MIN)) {
            int min = (int) options.get(Constants.RULESET_LENGTH_MIN);
            return length >= min;
        } else if (options.containsKey(Constants.RULESET_LENGTH_MAX)) {
            int max = (int) options.get(Constants.RULESET_LENGTH_MAX);
            return length <= max;
        } else {
            return false;
        }
    }
}
