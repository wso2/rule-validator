package org.wso2.rule.validator.validator;

/**
 * Class to represent the result of a ruleset validation.
 */
public class RulesetValidationError {
    public final String ruleName;
    public final String message;

    public RulesetValidationError(String ruleName, String message) {
        this.ruleName = ruleName;
        this.message = message;
    }

    public String toString() {
        return "Rule: " + ruleName + ", Message: " + message;
    }
}
