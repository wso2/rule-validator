package org.wso2.json.path.evaluator;

import java.util.regex.Pattern;

public class Constants {
    public static final String REPLACE_ENDING_DOT_REGEX = "\\.\\.$";
    public static final String LEADING_DOLLAR_REGEX = "^\\$\\.?";
    public static final String MATCH_FUNCTION_REGEX = "((?:@[a-zA-Z0-9_\\.]*|[a-zA-Z0-9_\\.]+))\\.match\\(/(.*?)/([a-z]*)\\)";
    public static final Pattern JSONPATH_COMPARISON_REGEX = Pattern.compile(
            ".*(?:\\\"|')?\\$+(?:\\.?[A-Za-z0-9_\\[\\]\\.']+)+(?:\\\"|')?\\s*(?:===|!==|==|!=|>=|<=|>|<)\\s*" +
            "(?:\\\"|')?\\$+(?:\\.?[A-Za-z0-9_\\[\\]\\.']+)+(?:\\\"|')?.*");
    public static final Pattern JSONPATH_MATCHER_REGEX = Pattern.compile(
            "(\\$+(?:\\.?[A-Za-z0-9_\\[\\]'\".]+)+)\\s*(===|!==|==|!=|>=|<=|>|<)\\s*" +
            "(\\$+(?:\\.?[A-Za-z0-9_\\[\\]'\".]+)+)");
    public static final String NODE_VALUE_JSONPATH_REGEX = "\\$(?:\\.\\.?[a-zA-Z0-9_\\$]+|\\[[^\\]]+\\])+";
    public static final String COMPARISON_REGEX = "\\s*(===|!==|==|!=|>=|<=|>|<|\\+|\\-|\\*|\\/).*";
    public static final String STRING_FUNCTIONS_REGEX = "\"([^\"]+)\"\\.(\\w+)\\(([^)]*)\\)";
    public static final String SINGLE_QUOTE_REGEX = "^'|'$";

    public static final String ADVANCED_FEATURE_LENGTH = "@.length";
    public static final String ADVANCED_FEATURE_PROPERTY = "@property";
    public static final String ADVANCED_FEATURE_PARENT_PROPERTY = "@parentProperty";
    public static final String ADVANCED_FEATURE_PARENT = "@parent";
    public static final String ADVANCED_FEATURE_PATH = "@path";
    public static final String ADVANCED_FEATURE_ROOT = "@root";
    public static final String ADVANCED_FEATURE_AT = "@";
}
