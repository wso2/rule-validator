package org.wso2.rule.validator.functions.core;

import org.junit.jupiter.api.Test;
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

class TruthyFunctionTest {

    private final TruthyFunction truthy = new TruthyFunction(null);

    @Test
    void givenTruthyInputsShouldReturnNoErrorMessage() {
        List<Object> truthyInputs = List.of(true, 1, new ArrayList<>(List.of(1)),
                new HashMap<>(Map.of("key", "value")));

        for (Object input : truthyInputs) {
            LintTarget target = new LintTarget(new ArrayList<>(), input);
            try {
                boolean result = truthy.execute(target);
                assertTrue(result, "Expected truthy input: " + input + " to be truthy");
            } catch (InvalidRulesetException e) {
                fail("Exception thrown for truthy input: " + e.getMessage());
            }
        }
    }

    @Test
    void givenFalsyInputsShouldReturnErrorMessage() {
        List<Object> falsyInputs = new ArrayList<>();
        falsyInputs.add(false);
        falsyInputs.add(null);
        falsyInputs.add(0);
        falsyInputs.add("");

        for (Object input : falsyInputs) {
            LintTarget target = new LintTarget(new ArrayList<>(), input);
            try {
                boolean result = truthy.execute(target);
                assertFalse(result, "Expected falsy input: " + input + " to be falsy");
            } catch (InvalidRulesetException e) {
                fail("Exception thrown for falsy input: " + e.getMessage());
            }
        }
    }

    @Test
    void validationTestForInvalidOptions() {
        Map<String, Object> invalidOption = Map.of("unsupportedKey", true); // Unsupported key

        TruthyFunction function = new TruthyFunction(invalidOption);
        LintTarget target = new LintTarget(new ArrayList<>(), null);

        assertThrows(InvalidRulesetException.class, () -> function.execute(target),
                "Expected InvalidRulesetException for invalid option: " + invalidOption);
    }

}
