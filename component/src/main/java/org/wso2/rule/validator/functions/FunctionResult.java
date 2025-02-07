package org.wso2.rule.validator.functions;

/**
 * Represents the result of a function execution
 */
public class FunctionResult {
    public boolean passed;
    public String message;

    public FunctionResult(boolean passed, String message) {
        this.passed = passed;
        this.message = message;
    }
}
