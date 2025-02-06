package org.wso2.rule.validator.functions.core;

import org.junit.jupiter.api.Test;
import org.wso2.rule.validator.InvalidRulesetException;
import org.wso2.rule.validator.document.LintTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PatternFunctionTest {
    private boolean runPattern(String value, Map<String, Object> options) throws InvalidRulesetException {
        PatternFunction patternFunction = new PatternFunction(options);
        return patternFunction.execute(new LintTarget(new ArrayList<>(), value));
    }

    @Test
    void testMatchingPatternWithoutSlashes() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "[abc]+");
        try {
            assertTrue(runPattern("abc", options));
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    void testMatchingPatternWithSlashes() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "/[abc]+/");
        try {
            assertTrue(runPattern("abc", options));
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    void testMatchingPatternWithSlashesAndModifiers() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "/[abc]+/im");
        try {
            assertTrue(runPattern("aBc", options));
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    void testMatchingPatternWithGlobalModifier() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "/[abc]+/gi");
        try {
            assertTrue(runPattern("abc", options));
            assertTrue(runPattern("abc", options));
            assertTrue(runPattern("abc", options));
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    void testInvalidRegexFlags() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "/[abc]+/invalid");
        assertThrows(InvalidRulesetException.class, () -> runPattern("aBc", options));
    }

    @Test
    void testNonMatchingNotMatchPattern() {
        Map<String, Object> options = new HashMap<>();
        options.put("notMatch", "/[abc]+/i");
        try {
            assertTrue(runPattern("def", options));
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    void testMatchingBothMatchAndNotMatch() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "[def]+");
        options.put("notMatch", "[abc]+");
        try {
            assertTrue(runPattern("def", options));
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    void testValidFunctionOptions() {
        Map<String, Object> options1 = new HashMap<>();
        options1.put("match", "foo");
        assertDoesNotThrow(() -> runPattern("def", options1));

        Map<String, Object> options2 = new HashMap<>();
        options2.put("notMatch", "foo");
        assertDoesNotThrow(() -> runPattern("def", options2));

        Map<String, Object> options3 = new HashMap<>();
        options3.put("match", "foo");
        options3.put("notMatch", "bar");
        assertDoesNotThrow(() -> runPattern("def", options3));
    }

    @Test
    void testInvalidFunctionOptions() {
        Map<String, Object> options1 = null;
        assertThrows(InvalidRulesetException.class, () -> runPattern("abc", options1));

        Map<String, Object> options2 = new HashMap<>();
        assertThrows(InvalidRulesetException.class, () -> runPattern("abc", options2));

        Map<String, Object> options3 = new HashMap<>();
        options3.put("foo", true);
        assertThrows(InvalidRulesetException.class, () -> runPattern("abc", options3));

        Map<String, Object> options4 = new HashMap<>();
        options4.put("match", 2);
        assertThrows(InvalidRulesetException.class, () -> runPattern("abc", options4));

        Map<String, Object> options5 = new HashMap<>();
        options5.put("notMatch", null);
        assertThrows(InvalidRulesetException.class, () -> runPattern("abc", options5));

        Map<String, Object> options6 = new HashMap<>();
        options6.put("match", 4);
        options6.put("notMatch", 10);
        assertThrows(InvalidRulesetException.class, () -> runPattern("abc", options6));
    }
}
