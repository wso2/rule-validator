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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Pattern function implementation
 */
@FunctionName("pattern")
public class PatternFunction extends LintFunction {

    public PatternFunction(Map<String, Object> options) {
        super(options);
    }

    @Override
    public List<String> validateFunctionOptions(Map<String, Object> options) {
        ArrayList<String> errors = new ArrayList<>();

        if (options == null) {
            errors.add("Pattern function requires a regex pattern");
            return errors;
        }

        if (!options.containsKey("match") && !options.containsKey("notMatch")) {
            errors.add("Pattern function requires either match or notMatch options");
        }

        if (options.containsKey("match") && options.containsKey("notMatch")) {
            errors.add("Pattern function cannot contain both match and notMatch options.");
        }

        if (options.containsKey("match") && !(options.get("match") instanceof String)) {
            errors.add("Pattern function match option must be a string.");
        }

        if (options.containsKey("notMatch") && !(options.get("notMatch") instanceof String)) {
            errors.add("Pattern function notMatch option must be a string.");
        }

        if (options.containsKey("match") && !isValidRegex((String) options.get("match"))) {
            errors.add("Pattern function match option is not a valid regex pattern.");
        }

        if (options.containsKey("notMatch") && !isValidRegex((String) options.get("notMatch"))) {
            errors.add("Pattern function notMatch option is not a valid regex pattern.");
        }

        return errors;
    }

    private boolean isValidRegex(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    public boolean execute(LintTarget target) {
        Object match = options.get("match");
        Object notMatch = options.get("notMatch");

        if (target.value == null) {
            return false;
        }
        if (!(target.value instanceof String)) {
            return true;
        }

        try {
            if (match != null) {
                return target.value.toString().matches((String) match);
            } else if (notMatch != null) {
                return !target.value.toString().matches((String) notMatch);
            } else {
                throw new RuntimeException("Pattern function requires either match or notMatch options");
            }
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
