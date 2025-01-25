package org.wso2.rule.validator.validator;

import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.wso2.rule.validator.validator.ruleset.RulesetValidator;

import java.util.List;
import java.util.Map;

/**
 * Ruleset validation for YAML files
 */
public class YamlRulesetValidator extends RulesetValidator {
    public static List<RulesetValidationError> validateRuleset(String rulesetString) {
        Load settings = new Load(LoadSettings.builder().build());
        return RulesetValidator.validate((Map<String, Object>) settings.loadFromString(rulesetString));
    }
}
