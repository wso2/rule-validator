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
package org.wso2.rule.validator.utils;

import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.wso2.rule.validator.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for ruleset operations.
 */
public class Util {
    public static String doubleBackslashesAfterMatch(String input) {
        Pattern pattern = Pattern.compile(Constants.RULESET_REGEX_EXTRACTION_REGEX);

        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            // Group 1 => leading spaces (if any)
            // Group 2 => "match" or "notMatch"
            // Group 3 => colon and optional spaces after it (e.g. ": " or ":")
            // Group 4 => everything else (the content to transform)

            String leadingSpaces = matcher.group(1);
            String keyword = matcher.group(2); // "match" or "notMatch"
            String colonAndSpaces = matcher.group(3);
            String remainder = matcher.group(4);

            // Replace every single '\' with '\\'
            String doubled = remainder.replace("\\", "\\\\");

            // Construct the new line
            String replacement = leadingSpaces + keyword + colonAndSpaces + doubled;
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public static Object loadYaml(String yamlString) {
        return (new Load(LoadSettings.builder().build())).loadFromString(yamlString);
    }
}
