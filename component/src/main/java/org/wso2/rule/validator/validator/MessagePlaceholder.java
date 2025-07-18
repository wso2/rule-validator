package org.wso2.rule.validator.validator;

/**
 * Placeholder for the rule validator application.
 */
public class MessagePlaceholder {
    public static final String DESCRIPTION_PLACEHOLDER = "{{description}}";
    public static final String ERROR_PLACEHOLDER = "{{error}}";
    public static final String PROPERTY_PLACEHOLDER = "{{property}}";
    public static final String PATH_PLACEHOLDER = "{{path}}";
    public static final String VALUE_PLACEHOLDER = "{{value}}";

    private String description;
    private String error;
    private String property;
    private String path;
    private String value;

    public MessagePlaceholder(String description, String error, String property, String path, String value) {
        this.description = description;
        this.error = error;
        this.property = property;
        this.path = path;
        this.value = value;

    }

    public String getDescription() {
        return description;
    }

    public String getError() {
        return error;
    }

    public String getProperty() {
        return property;
    }

    public String getPath() {
        return path;
    }

    public String getValue() {
        return value;
    }

    public String replacePlaceholders(String template) {
        if (template == null || template.isEmpty()) {
            return "";
        }
        String result = template;
        if (this.getDescription() != null) {
            result = result.replace(DESCRIPTION_PLACEHOLDER, this.getDescription());
        }
        if (this.getError() != null) {
            result = result.replace(ERROR_PLACEHOLDER, this.getError());
        }
        if (this.getProperty() != null) {
            result = result.replace(PROPERTY_PLACEHOLDER, this.getProperty());
        }
        if (this.getPath() != null) {
            result = result.replace(PATH_PLACEHOLDER, this.getPath());
        }
        if (this.getValue() != null) {
            result = result.replace(VALUE_PLACEHOLDER, this.getValue());
        }
        return result;

    }

}
