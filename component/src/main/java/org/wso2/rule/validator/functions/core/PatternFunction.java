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
package org.wso2.rule.validator.functions.core;

import org.wso2.rule.validator.Constants;
import org.wso2.rule.validator.document.LintTarget;
import org.wso2.rule.validator.functions.FunctionName;
import org.wso2.rule.validator.functions.LintFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Pattern function implementation
 */
@FunctionName("pattern")
public class PatternFunction extends LintFunction {

    public PatternFunction(Map<String, Object> options) {
        super(options);
    }

    private static class PatternAndFlags {
        public String pattern;
        public String flags;

        public PatternAndFlags(String pattern, String flags) {
            this.pattern = pattern;
            this.flags = flags;
        }
    }

    @Override
    public List<String> validateFunctionOptions() {
        ArrayList<String> errors = new ArrayList<>();

        if (options == null) {
            errors.add("Pattern function requires a regex pattern");
            return errors;
        }

        if (!options.containsKey(Constants.RULESET_PATTERN_MATCH) &&
                !options.containsKey(Constants.RULESET_PATTERN_NOT_MATCH)) {
            errors.add("Pattern function requires either match or notMatch options");
        }

        if (options.containsKey(Constants.RULESET_PATTERN_MATCH) &&
                !(options.get(Constants.RULESET_PATTERN_MATCH) instanceof String)) {
            errors.add("Pattern function match option must be a string.");
        }

        if (options.containsKey(Constants.RULESET_PATTERN_NOT_MATCH) &&
                !(options.get(Constants.RULESET_PATTERN_NOT_MATCH) instanceof String)) {
            errors.add("Pattern function notMatch option must be a string.");
        }

        if (options.containsKey(Constants.RULESET_PATTERN_MATCH) &&
                !isValidRegex((String) options.get(Constants.RULESET_PATTERN_MATCH))) {
            errors.add("Pattern function match option is not a valid regex pattern.");
        }

        if (options.containsKey(Constants.RULESET_PATTERN_NOT_MATCH) &&
                !isValidRegex((String) options.get(Constants.RULESET_PATTERN_NOT_MATCH))) {
            errors.add("Pattern function notMatch option is not a valid regex pattern.");
        }

        if (options.containsKey(Constants.RULESET_PATTERN_MATCH)) {
            PatternAndFlags patternAndFlags = extractPatternAndFlags(
                    (String) options.get(Constants.RULESET_PATTERN_MATCH));
            if (getFlagsFromFlagString(patternAndFlags.flags) == -1) {
                errors.add("Pattern function match option contains invalid flags.");
            }
        }

        if (options.containsKey(Constants.RULESET_PATTERN_NOT_MATCH)) {
            PatternAndFlags patternAndFlags = extractPatternAndFlags(
                    (String) options.get(Constants.RULESET_PATTERN_NOT_MATCH));
            if (getFlagsFromFlagString(patternAndFlags.flags) == -1) {
                errors.add("Pattern function notMatch option contains invalid flags.");
            }
        }

        return errors;
    }

    private boolean isValidRegex(String regex) {
        try {
            Pattern.compile(Pattern.quote(regex));
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    private PatternAndFlags extractPatternAndFlags(String regex) {
        String pattern = regex;
        String flags = "";
        int lastSlash = regex.lastIndexOf("/");
        if (lastSlash != -1) {
            pattern = regex.substring(1, lastSlash);
            flags = regex.substring(lastSlash + 1);
        }
        return new PatternAndFlags(pattern, flags);
    }

    private int getFlagsFromFlagString(String flags) {
        int flag = 0;

        if (flags.trim().isEmpty()) {
            return flag;
        }

        if (flags.contains("i")) {
            flag |= Pattern.CASE_INSENSITIVE;
        }
        if (flags.contains("u")) {
            flag |= Pattern.UNICODE_CASE;
        }
        if (flags.contains("m")) {
            flag |= Pattern.MULTILINE;
        }
        if (flags.contains("s")) {
            flag |= Pattern.DOTALL;
        }
        if (flags.contains("d")) {
            flag |= Pattern.UNIX_LINES;
        }

        if (!flags.matches("[iumsdgy]+")) {
            return -1;
        }

        return flag;
    }

    private boolean matches(String value, PatternAndFlags patternAndFlags) {

        int flags = getFlagsFromFlagString(patternAndFlags.flags);

        Pattern pattern = Pattern.compile(patternAndFlags.pattern, flags);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public boolean executeFunction(LintTarget target) {
        Object match = options.get(Constants.RULESET_PATTERN_MATCH);
        Object notMatch = options.get(Constants.RULESET_PATTERN_NOT_MATCH);

        if (target.value == null) {
            return false;
        }
        if (!(target.value instanceof String)) {
            return true;
        }

        boolean matchResult = false;
        boolean notMatchResult = false;

        try {
            if (match != null) {
                PatternAndFlags matchPatternAndFlags = extractPatternAndFlags((String) match);
                boolean result = matches((String) target.value, matchPatternAndFlags);
                if (notMatch == null) {
                    return result;
                }
                matchResult = result;
            }
            if (notMatch != null) {
                PatternAndFlags notMatchPatternAndFlags = extractPatternAndFlags((String) notMatch);
                boolean result = !matches((String) target.value, notMatchPatternAndFlags);
                if (match == null) {
                    return result;
                }
                notMatchResult = result;
            }
            return matchResult && notMatchResult;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
