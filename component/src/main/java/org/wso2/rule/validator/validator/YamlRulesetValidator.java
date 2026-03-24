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

import org.wso2.rule.validator.InvalidContentTypeException;
import org.wso2.rule.validator.utils.Util;
import org.wso2.rule.validator.validator.ruleset.RulesetValidator;

import java.util.List;
import java.util.Map;

/**
 * Ruleset validation for YAML files
 */
public class YamlRulesetValidator extends RulesetValidator {
    /**
     * Validates YAML ruleset content using default validation options.
     *
     * @param rulesetString ruleset content
     * @return list of validation errors
     * @throws InvalidContentTypeException if ruleset content is invalid YAML
     * @deprecated Use {@link #validateRuleset(String, ValidationOptions)} to pass validation options.
     */
    @Deprecated
    public static List<RulesetValidationError> validateRuleset(String rulesetString)
            throws InvalidContentTypeException {
        return validateRuleset(rulesetString, ValidationOptions.defaults());
    }

    /**
     * Validates YAML ruleset content using provided validation options.
     *
     * @param rulesetString     ruleset content
     * @param validationOptions validation options
     * @return list of validation errors
     * @throws InvalidContentTypeException if ruleset content is invalid YAML
     */
    public static List<RulesetValidationError> validateRuleset(String rulesetString,
            ValidationOptions validationOptions) throws InvalidContentTypeException {
        Object yamlContent = Util.loadYaml(rulesetString, validationOptions);
        if (!(yamlContent instanceof Map)) {
            throw new InvalidContentTypeException("Invalid YAML ruleset content.");
        }
        return RulesetValidator.validate((Map<String, Object>) yamlContent);
    }
}
