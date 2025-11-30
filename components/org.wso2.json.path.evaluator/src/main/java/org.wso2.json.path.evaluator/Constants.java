/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

import java.util.regex.Pattern;

/**
 * Constants
 */
public class Constants {
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
