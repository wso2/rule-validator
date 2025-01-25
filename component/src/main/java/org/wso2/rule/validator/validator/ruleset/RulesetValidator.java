package org.wso2.rule.validator.validator.ruleset;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import org.wso2.rule.validator.functions.FunctionFactory;
import org.wso2.rule.validator.functions.LintFunction;
import org.wso2.rule.validator.ruleset.Format;
import org.wso2.rule.validator.validator.RulesetValidationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements the actual ruleset validation logic
 */
public abstract class RulesetValidator {
    protected static List<RulesetValidationError> validate(Map<String, Object> ruleset) {

        ArrayList<RulesetValidationError> errors = new ArrayList<>();

        if (ruleset == null) {
            errors.add(new RulesetValidationError("", "Ruleset is empty."));
            return errors;
        }

        // Validate rules
        if (!ruleset.containsKey("rules")) {
            errors.add(new RulesetValidationError("", "Ruleset does not contain any rules."));
            return errors;
        }

        Object ruleMap = ruleset.get("rules");
        if (ruleMap == null) {
            errors.add(new RulesetValidationError("", "Ruleset contains a null value for rules."));
        } else if (!(ruleMap instanceof Map)) {
            errors.add(new RulesetValidationError("", "Ruleset contains an invalid value for rules."));
            return errors;
        } else {
            errors.addAll(validateRules((Map<String, Object>) ruleMap));
        }

        // Validate extends
        if (ruleset.containsKey("extends")) {
            if (!(ruleset.get("extends") instanceof List) || !(ruleset.get("extends") instanceof String)) {
                errors.add(new RulesetValidationError("",
                        "'extends' field of a ruleset should be a list or a string."));
            } else if (ruleset.get("extends") instanceof List) {
                List<Object> extendsList = (List<Object>) ruleset.get("extends");
                for (int i = 0; i < extendsList.size(); i++) {
                    if (!(extendsList.get(i) instanceof String)) {
                        errors.add(new RulesetValidationError("",
                                "Invalid 'extends' object in index " + i));
                    }
                }
            }
        }

        // Validate Formats
        errors.addAll(validateFormats("", ruleset));



        return errors;
    }

    private static List<RulesetValidationError> validateRules(Map<String, Object> rules) {
        ArrayList<RulesetValidationError> errors = new ArrayList<>();

        for (Map.Entry<String, Object> entry : rules.entrySet()) {
            String key = entry.getKey();
            Object ruleObject = rules.get(key);
            if (!(ruleObject instanceof Map)) {
                errors.add(new RulesetValidationError(key, "Rule is not a valid object."));
                continue;
            }
            Map<String, Object> rule = (Map<String, Object>) ruleObject;

            // Validate given
            errors.addAll(validateGiven(key, rule));

            // Validate then
            errors.addAll(validateThen(key, rule));

            // Validate description
            if (rule.containsKey("description") && !(rule.get("description") instanceof String)) {
                errors.add(new RulesetValidationError(key, "'description' field of a rule should be a string"));
            }
            // Validate message
            if (rule.containsKey("message") && !(rule.get("message") instanceof String)) {
                errors.add(new RulesetValidationError(key, "'message' field of a rule should be a string"));
            }
            if (rule.get("message") instanceof String) {
                String message = (String) rule.get("message");
                if (rule.containsKey("message") && message.contains("{{")) {
                    String regex = "\\{\\{.*?}}";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(message);
                    while (matcher.find()) {
                        String match = matcher.group();
                        String placeholder = match.substring(2, match.length() - 2);
                        List<String> placeholders = Arrays.asList("error", "description", "path", "property", "value");
                        if (!placeholders.contains(placeholder)) {
                            errors.add(new RulesetValidationError(key, "Invalid placeholder: " + match));
                        }
                    }
                }
            }

            // Validate severity
            if (rule.containsKey("severity") && !(rule.get("severity") instanceof String)) {
                errors.add(new RulesetValidationError(key, "'severity' field of a rule should be a string"));
            } else {
                String severity = (String) rule.get("severity");
                List<String> severities = Arrays.asList("error", "warn", "info", "hint", "off");
                if (!severities.contains(severity)) {
                    errors.add(new RulesetValidationError(key, "Invalid severity: " + severity));
                }
            }

            // Validate formats
            errors.addAll(validateFormats(key, rule));

            // Validate recommended
            if (rule.containsKey("recommended") && !(rule.get("recommended") instanceof Boolean)) {
                errors.add(new RulesetValidationError(key, "'recommended' field of a rule should be a boolean"));
            }

            // Validate resolved
            if (rule.containsKey("resolved") && !(rule.get("resolved") instanceof Boolean)) {
                errors.add(new RulesetValidationError(key, "'resolved' field of a rule should be a boolean"));
            }
        }

        return errors;
    }

    private static List<RulesetValidationError> validateFormats(String ruleName, Map<String, Object> object) {
        ArrayList<RulesetValidationError> errors = new ArrayList<>();

        if (object.containsKey("formats") && !(object.get("formats") instanceof List)) {
            errors.add(new RulesetValidationError(ruleName, "'formats' field of a rule should be a list"));
        }
        if (object.containsKey("formats")) {
            List<Object> formatObjects = (List<Object>) object.get("formats");
            for (int i = 0; i < formatObjects.size(); i++) {
                if (!(formatObjects.get(i) instanceof String)) {
                    errors.add(new RulesetValidationError(ruleName, "Invalid format in index " + i));
                    continue;
                }
                Format format = Format.getFormat((String) formatObjects.get(i));
                if (format == null) {
                    errors.add(new RulesetValidationError(ruleName, "Unknown format: " + formatObjects.get(i)));
                }
            }
        }

        return errors;
    }

    private static List<RulesetValidationError> validateThen (String ruleName, Map<String, Object> rule) {
        ArrayList<RulesetValidationError> errors = new ArrayList<>();

        if (!rule.containsKey("then")) {
            errors.add(new RulesetValidationError(ruleName, "Rule does not contain a 'then' field."));
        } else if (!(rule.get("then") instanceof List) && !(rule.get("then") instanceof Map)) {
            errors.add(new RulesetValidationError(ruleName, "'then' field of a rule should be an object or a list"));
        } else {
            if (rule.get("then") instanceof List) {
                List<Object> thenList = (List<Object>) rule.get("then");
                for (int i = 0; i < thenList.size(); i++) {
                    if (thenList.get(i) instanceof Map) {
                        errors.addAll(validateThenObject(ruleName, (Map<String, Object>) thenList.get(i)));
                    } else {
                        errors.add(new RulesetValidationError(ruleName, "Invalid 'then' object in index " + i));
                    }
                }
            } else {
                if (!(rule.get("then") instanceof Map)) {
                    errors.add(new RulesetValidationError(ruleName, "Invalid 'then' object"));
                } else {
                    errors.addAll(validateThenObject(ruleName, (Map<String, Object>) rule.get("then")));
                }
            }
        }

        return errors;
    }

    private static List<RulesetValidationError> validateThenObject(String ruleName, Map<String, Object> then) {
        ArrayList<RulesetValidationError> errors = new ArrayList<>();

        // Field can be undefined. If it is defined, it should be a string
        if (then.containsKey("field") && !(then.get("field") instanceof String)) {
            errors.add(new RulesetValidationError(ruleName, "'field' field of a then object should be a string"));
        }

        if (!then.containsKey("function")) {
            errors.add(new RulesetValidationError(ruleName, "Then object does not contain a 'function' field."));
            return errors;
        } else if (!(then.get("function") instanceof String)) {
            errors.add(new RulesetValidationError(ruleName, "'function' field of a then object should be a string"));
            return errors;
        } else {
            String function = (String) then.get("function");
            if (!FunctionFactory.isFunction(function)) {
                errors.add(new RulesetValidationError(ruleName, "Unknown function: " + function));
            }
        }

        // Match each function with the function options
        String function = (String) then.get("function");
        Map<String, Object> functionOptions = null;
        if (then.containsKey("functionOptions")) {
            if (then.get("functionOptions") instanceof Map) {
                functionOptions = (Map<String, Object>) then.get("functionOptions");
            } else {
                errors.add(new RulesetValidationError(ruleName,
                        "'functionOptions' field of a then object should be an object"));
            }
        }
        LintFunction lintFunction = FunctionFactory.getFunction(function, then);
        List<String> functionErrors = lintFunction.validateFunctionOptions(functionOptions);
        for (String error : functionErrors) {
            errors.add(new RulesetValidationError(ruleName, error));
        }

        return errors;
    }

    private static List<RulesetValidationError> validateGiven (String ruleName, Map<String, Object> rule) {
        // TODO: Check for alias usage

        ArrayList<RulesetValidationError> errors = new ArrayList<>();

        if (!rule.containsKey("given")) {
            errors.add(new RulesetValidationError(ruleName, "Rule does not contain a 'given' field."));
        } else if (!(rule.get("given") instanceof List) && !(rule.get("given") instanceof String)) {
            errors.add(new RulesetValidationError(ruleName, "'given' field of a rule should be a string or a list"));
        } else {
            if (rule.get("given") instanceof List) {
                List<String> givenList = (List<String>) rule.get("given");
                for (String given : givenList) {
                    if (!validateJsonPath(given)) {
                        errors.add(new RulesetValidationError(ruleName, "Invalid JSON path: " + given));
                    }
                }
            } else {
                if (!validateJsonPath((String) rule.get("given"))) {
                    errors.add(new RulesetValidationError(ruleName, "Invalid JSON path: " + rule.get("given")));
                }
            }
        }

        return errors;
    }

    private static boolean validateJsonPath(String jsonPath) {
        try {
            JsonPath.compile(jsonPath);
            return true;
        } catch (InvalidPathException e) {
            return false;
        }
    }
}
