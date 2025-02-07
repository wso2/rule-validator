package org.wso2.rule.validator.functions.core;

import org.junit.jupiter.api.Test;
import org.wso2.rule.validator.InvalidRulesetException;
import org.wso2.rule.validator.document.LintTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EnumerationFunctionTest {
    @Test
    public void givenValidInputShouldReturnTrue() {
        Map<String, Object> options = new HashMap<>();
        options.put("values", new String[]{"x", "y", "z"});

        EnumerationFunction function = new EnumerationFunction(options);
        LintTarget target = new LintTarget(new ArrayList<>(), "x");

        try {
            assertTrue(function.execute(target));
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    public void givenInvalidInputShouldReturnFalse() {
        Map<String, Object> options = new HashMap<>();
        options.put("values", new String[]{"y", "z"});

        EnumerationFunction function = new EnumerationFunction(options);
        LintTarget target = new LintTarget(new ArrayList<>(), "x");

        try {
            assertFalse(function.execute(target));
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    public void givenNonPrimitiveValueShouldReturnFalse() {
        Map<String, Object> options = new HashMap<>();
        options.put("values", new String[]{"test"});

        EnumerationFunction function = new EnumerationFunction(options);
        LintTarget target = new LintTarget(new ArrayList<>(), new Object());

        try {
            assertTrue(function.execute(target));
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    public void givenValidOptionsShouldNotThrow() {
        Map<String, Object> options = new HashMap<>();
        options.put("values", new String[]{"foo", "2"});

        EnumerationFunction function = new EnumerationFunction(options);
        LintTarget target = new LintTarget(new ArrayList<>(), "foo");

        assertDoesNotThrow(() -> function.execute(target));
    }

    @Test
    public void givenInvalidOptionsShouldThrowInvalidRulesetException() {
        List<Map<String, Object>> invalidOptionsList = new ArrayList<>(List.of(
                Map.of("values", new String[]{"foo", "2"}, "foo", true),
                Map.of("values", new Object[]{}),
                Map.of("values", 2)
        ));

        invalidOptionsList.add(null);

        for (Map<String, Object> options : invalidOptionsList) {
            EnumerationFunction function = new EnumerationFunction(options);
            LintTarget target = new LintTarget(new ArrayList<>(), "foo");
            assertThrows(InvalidRulesetException.class, () -> function.execute(target));
        }
    }
}
