/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.json.path.evaluator;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Constants
 */
public class Constants {
    public static final String EMPTY_STRING = "";
    public static final String COMMA = ",";
    public static final String SINGLE_QUOTE = "'";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String QUESTION_MARK = "?";
    public static final String OPEN_BRACKET = "[";
    public static final String CLOSE_BRACKET = "]";
    public static final String OPEN_PARENTHESES = "(";
    public static final String CLOSE_PARENTHESES = ")";
    public static final String CARET = "^";
    public static final String TILDE = "~";
    public static final String DOT = ".";
    public static final String DOUBLE_DOT = "..";
    public static final String DOUBLE_DOT_WILDCARD = "..*";
    public static final String PATH_KEY_PREFIX = "['";
    public static final String PATH_KEY_SUFFIX = "']";

    public static final String JSON_PATH_ROOT = "$";
    public static final String JSON_PATH_ROOT_DOT = "$.";
    public static final String VALUE_KEY = "value";

    public static final String REPLACE_ENDING_DOT_REGEX = "\\.\\.$";
    public static final String LEADING_DOLLAR_REGEX = "^\\$\\.?";

    public static final String MATCH_FUNCTION_REGEX = "\\.match\\((/(?:\\\\/|[^/])*/[a-zA-Z]*)\\)";
    public static final String MATCH_FUNCTION_PLACEHOLDER_REGEX =
            "((?:@[a-zA-Z0-9_\\.]+|@))\\.match\\(\\s*(PLACEHOLDER_[0-9]+)\\s*\\)";

    public static final String MULTI_KEY_FIELDS_REGEX = "\\[\\s*'([^']+)'\\s*(?:,\\s*'([^']+)')+\\s*\\]";
    public static final String REGEX_BODY = "^/(.*)/([a-zA-Z]*)$";

    public static final Pattern JSONPATH_COMPARISON_REGEX = Pattern.compile(
            ".*(?:\\\"|')?\\$+(?:\\.?[A-Za-z0-9_\\[\\]\\.']+)+(?:\\\"|')?\\s*(?:===|!==|==|!=|>=|<=|>|<)\\s*" +
            "(?:\\\"|')?\\$+(?:\\.?[A-Za-z0-9_\\[\\]\\.']+)+(?:\\\"|')?.*");

    public static final Pattern JSONPATH_MATCHER_REGEX = Pattern.compile(
            "(\\$+(?:\\.?[A-Za-z0-9_\\[\\]'\".]+)+)\\s*(===|!==|==|!=|>=|<=|>|<)\\s*" +
            "(\\$+(?:\\.?[A-Za-z0-9_\\[\\]'\".]+)+)");

    public static final String NODE_VALUE_JSONPATH_REGEX = "\\$(?:\\.\\.?[a-zA-Z0-9'\\\\_\\$]+|\\[[^\\]]+\\])+";
    public static final String COMPARISON_REGEX = "\\s*(===|!==|==|!=|>=|<=|>|<|\\+|\\-|\\*|\\/).*";
    public static final String STRING_FUNCTIONS_REGEX = "\"([^\"]+)\"\\.(\\w+)\\(([^)]*)\\)";
    public static final String SINGLE_QUOTE_REGEX = "^'|'$";

    public static final String ADVANCED_FEATURE_CURRENT = "@.";
    public static final String ADVANCED_FEATURE_LENGTH = "@.length";
    public static final String ADVANCED_FEATURE_PROPERTY = "@property";
    public static final String ADVANCED_FEATURE_PARENT_PROPERTY = "@parentProperty";
    public static final String ADVANCED_FEATURE_PARENT = "@parent";
    public static final String ADVANCED_FEATURE_PATH = "@path";
    public static final String ADVANCED_FEATURE_ROOT = "@root";
    public static final String ADVANCED_FEATURE_AT = "@";

    public static final String ADVANCED_FUNCTION_NUMBER = "@number()";
    public static final String ADVANCED_FUNCTION_STRING = "@string()";
    public static final String ADVANCED_FUNCTION_INTEGER = "@integer()";
    public static final String ADVANCED_FUNCTION_BOOLEAN = "@boolean()";
    public static final String ADVANCED_FUNCTION_ARRAY = "@array()";
    public static final String ADVANCED_FUNCTION_OBJECT = "@object()";
    public static final String ADVANCED_FUNCTION_NULL = "@null()";
    public static final String ADVANCED_FUNCTION_SCALAR = "@scalar()";
    public static final List<String> ADVANCED_FUNCTIONS = List.of(
            ADVANCED_FUNCTION_NUMBER,
            ADVANCED_FUNCTION_STRING,
            ADVANCED_FUNCTION_INTEGER,
            ADVANCED_FUNCTION_BOOLEAN,
            ADVANCED_FUNCTION_ARRAY,
            ADVANCED_FUNCTION_OBJECT,
            ADVANCED_FUNCTION_NULL,
            ADVANCED_FUNCTION_SCALAR
    );

    public static final String STRING_METHOD_CHAR_AT = "charAt()";
    public static final String STRING_METHOD_CODE_POINT_AT = "codePointAt()";
    public static final String STRING_METHOD_CONCAT = "concat()";
    public static final String STRING_METHOD_ENDS_WITH = "endsWith()";
    public static final String STRING_METHOD_INCLUDES = "includes()";
    public static final String STRING_METHOD_INDEX_OF = "indexOf()";
    public static final String STRING_METHOD_LAST_INDEX_OF = "lastIndexOf()";
    public static final String STRING_METHOD_STARTS_WITH = "startsWith()";
    public static final String STRING_METHOD_TO_LOWER_CASE = "toLowerCase()";
    public static final String STRING_METHOD_TO_UPPER_CASE = "toUpperCase()";

    public static final String STRING_METHOD_NAME_CHAR_AT = "charAt";
    public static final String STRING_METHOD_NAME_CODE_POINT_AT = "codePointAt";
    public static final String STRING_METHOD_NAME_CONCAT = "concat";
    public static final String STRING_METHOD_NAME_ENDS_WITH = "endsWith";
    public static final String STRING_METHOD_NAME_INCLUDES = "includes";
    public static final String STRING_METHOD_NAME_INDEX_OF = "indexOf";
    public static final String STRING_METHOD_NAME_LAST_INDEX_OF = "lastIndexOf";
    public static final String STRING_METHOD_NAME_STARTS_WITH = "startsWith";
    public static final String STRING_METHOD_NAME_TO_LOWER_CASE = "toLowerCase";
    public static final String STRING_METHOD_NAME_TO_UPPER_CASE = "toUpperCase";

    public static final String MATCH_FUNCTION = ".match";
    public static final String MATCH_FUNCTION_CALL_PREFIX = ".match(";
    public static final String PLACEHOLDER_PREFIX = "PLACEHOLDER_";
    public static final String CASE_INSENSITIVE_FLAG = "i";
    public static final String PREDICATE_REPLACEMENT_VALUE = "?";

    public static final String ERROR_MESSAGE_PREFIX_JSON_PATH = "Error in the given JSON Path";
    public static final String ERROR_MESSAGE_PREFIX_UNBALANCED_BRACKETS =
            "Brackets are not balanced in the given Expression: ";
    public static final String ERROR_MESSAGE_EXTRA_CLOSING_BRACKET = "Extra closing bracket at position ";
    public static final String ERROR_MESSAGE_UNBALANCED_BRACKETS = "Unbalanced brackets in expression ";

    public static final String LOG_PATH_NOT_FOUND = "Path Not Found";
    public static final String LOG_JEXL_EVALUATION_FAILED = "JEXL failed to evaluate";
    public static final String LOG_PATH_NOT_FOUND_FOR_EXPRESSION = "Path Not Found for expression: ";
    public static final String LOG_PATH_NOT_FOUND_WHILE_PROCESSING = "Path not found while processing: ";
    public static final String LOG_INTERMEDIATE_SEPARATOR = ", intermediate: ";
}
