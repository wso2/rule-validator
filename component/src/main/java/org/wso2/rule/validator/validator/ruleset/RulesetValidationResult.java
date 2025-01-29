package org.wso2.rule.validator.validator.ruleset;

/**
 * Represents the result of a ruleset validation that is sent back to the user
 */
public class RulesetValidationResult {
    public final boolean passed;
    public final String message;

    public RulesetValidationResult(boolean passed, String message) {
        this.passed = passed;
        this.message = message;
    }
}
