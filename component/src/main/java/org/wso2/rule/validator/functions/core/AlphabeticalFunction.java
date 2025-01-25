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
 * Function to check if the given string is alphabetical
 */
@FunctionName("alphabetical")
public class AlphabeticalFunction extends LintFunction {

    public AlphabeticalFunction(Map<String, Object> options) {
        super(options);
    }

    @Override
    public List<String> validateFunctionOptions(Map<String, Object> options) {

        ArrayList<String> errors = new ArrayList<>();

        if (options == null) {
            return errors;
        }

        for (Map.Entry<String, Object> entry : options.entrySet()) {
            if (!entry.getKey().equals("value")) {
                errors.add("Invalid option '" + entry.getKey() + "' for function 'alphabetical'");


                if (entry.getKey().equals("keyedBy")) {
                    if (!(options.get(entry.getKey()) instanceof String)) {
                        errors.add("The value of 'keyedBy' should be a string");
                    }
                } else {
                    errors.add("Unknown option '" + entry.getKey() + "' for alphabetical function.");
                }
            }
        }

        return errors;

    }

    public boolean execute(LintTarget target) {
        return target.value.toString().matches("^[a-zA-Z\\s.,!?;:'\"-]*$");
    }

}
