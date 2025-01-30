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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wso2.rule.validator.Constants;
import org.wso2.rule.validator.InvalidRulesetException;
import org.wso2.rule.validator.document.LintTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class LengthFunctionTest {

    private List<Object> inputs; // Common input values to test
    private Map<String, Object> options; // Common options map

    @BeforeEach
    void setUp() {
        // Initialize common inputs
        inputs = List.of(
                "123",                  // String
                3,                      // Integer
                List.of(1, 2, 3),       // List
                Map.of("one", 1, "two", 2, "three", 3) // Map
        );

        // Default options (can be modified in individual tests)
        options = new HashMap<>();
    }

    private LengthFunction createFunction(Map<String, Object> customOptions) {
        return new LengthFunction(customOptions);
    }

    @Test
    void testExceedsMaxLength() {
        options.put(Constants.RULESET_LENGTH_MAX, 2); // Max length: 2

        for (Object input : inputs) {
            LintTarget target = new LintTarget(new ArrayList<>(), input);
            LengthFunction function = createFunction(options);

            try {
                boolean result = function.execute(target);
                assertFalse(result, "Expected input to exceed max length of 2");
            } catch (InvalidRulesetException e) {
                // Handle exception, optionally log
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Test
    void testFallsBelowMinLength() {
        options.put(Constants.RULESET_LENGTH_MIN, 4); // Min length: 4

        for (Object input : inputs) {
            LintTarget target = new LintTarget(new ArrayList<>(), input);
            LengthFunction function = createFunction(options);

            try {
                boolean result = function.execute(target);
                assertFalse(result, "Expected input to fall below min length of 4");
            } catch (InvalidRulesetException e) {
                // Handle exception, optionally log
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Test
    void testWithinMinAndMaxLength() {
        options.put(Constants.RULESET_LENGTH_MIN, 3); // Min length: 3
        options.put(Constants.RULESET_LENGTH_MAX, 3); // Max length: 3

        for (Object input : inputs) {
            LintTarget target = new LintTarget(new ArrayList<>(), input);
            LengthFunction function = createFunction(options);

            try {
                boolean result = function.execute(target);
                assertTrue(result, "Expected input to be within min and max length of 3");
            } catch (InvalidRulesetException e) {
                // Handle exception, optionally log
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Test
    void testValidOptions() {
        Map<String, Object>[] validOptions = new Map[]{
                Map.of(Constants.RULESET_LENGTH_MIN, 2),
                Map.of(Constants.RULESET_LENGTH_MAX, 4),
                Map.of(Constants.RULESET_LENGTH_MIN, 2, Constants.RULESET_LENGTH_MAX, 4)
        };

        for (Map<String, Object> validOption : validOptions) {
            LengthFunction function = createFunction(validOption);
            try {
                // Validate that no exception is thrown during execution
                boolean result = function.execute(new LintTarget(new ArrayList<>(), "123"));
                assertTrue(result, "Expected no exception for valid options.");
            } catch (InvalidRulesetException e) {
                // Handle exception, optionally log
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Test
    void testInvalidOptions() {
        Map<String, Object>[] invalidOptions = new Map[]{
                null,                       // Null options
                Map.of("foo", true),        // Unsupported key
                Map.of(Constants.RULESET_LENGTH_MIN, "2"), // Invalid type for min
                Map.of(Constants.RULESET_LENGTH_MAX, "2"), // Invalid type for max
                Map.of(Constants.RULESET_LENGTH_MIN, "4", Constants.RULESET_LENGTH_MAX, "2") // Invalid types for both
        };

        for (Map<String, Object> invalidOption : invalidOptions) {
            LengthFunction function = createFunction(invalidOption);
            // Expect an InvalidRulesetException to be thrown
            assertThrows(InvalidRulesetException.class, () -> function.execute(new LintTarget(new ArrayList<>(), "")),
                    "Expected InvalidRulesetException for invalid options.");
        }
    }
}
