package org.wso2.rule.validator.validator;

import com.jayway.jsonpath.JsonPath;
import org.wso2.rule.validator.validator.ruleset.RulesetValidator;

import java.util.List;

/**
 * Ruleset validation for JSON files
 */
public class JsonRulesetValidator extends RulesetValidator {
    public static List<RulesetValidationError> validateRuleset(String rulesetString) {
        return RulesetValidator.validate(JsonPath.parse(rulesetString).json());
    }
}
