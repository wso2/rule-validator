package org.wso2.rule.validator;

/**
 * Exception to handle invalid content type. Content type must be either JSON or YAML.
 */
public class InvalidContentTypeException extends Exception {
    public InvalidContentTypeException(String message) {
        super("Invalid content type. Rulesets and documents must be either valid JSON or YAML." + "\n" + message);
    }
}
