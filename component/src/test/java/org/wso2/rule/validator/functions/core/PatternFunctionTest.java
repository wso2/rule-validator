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

    @Test
    public void testMatchingPatternWithoutSlashes() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "[abc]+");

        PatternFunction patternFunction = new PatternFunction(options);
        try {
            assertTrue(patternFunction.execute(new LintTarget(new ArrayList<>(), "abc")).passed);
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    public void testMatchingPatternWithSlashes() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "/[abc]+/");

        PatternFunction patternFunction = new PatternFunction(options);
        try {
            assertTrue(patternFunction.execute(new LintTarget(new ArrayList<>(), "abc")).passed);
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    public void testMatchingPatternWithSlashesAndModifiers() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "/[abc]+/im");

        PatternFunction patternFunction = new PatternFunction(options);
        try {
            assertTrue(patternFunction.execute(new LintTarget(new ArrayList<>(), "aBc")).passed);
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    public void testMatchingPatternWithGlobalModifier() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "/[abc]+/gi");

        PatternFunction patternFunction = new PatternFunction(options);
        try {
            assertTrue(patternFunction.execute(new LintTarget(new ArrayList<>(), "abc")).passed);
            assertTrue(patternFunction.execute(new LintTarget(new ArrayList<>(), "abc")).passed);
            assertTrue(patternFunction.execute(new LintTarget(new ArrayList<>(), "abc")).passed);
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    public void testInvalidRegexFlags() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "/[abc]+/invalid");

        PatternFunction patternFunction = new PatternFunction(options);
        assertThrows(InvalidRulesetException.class,
                () -> patternFunction.execute(new LintTarget(new ArrayList<>(), "aBc")));
    }

    @Test
    public void testNonMatchingNotMatchPattern() {
        Map<String, Object> options = new HashMap<>();
        options.put("notMatch", "/[abc]+/i");

        PatternFunction patternFunction = new PatternFunction(options);
        try {
            assertTrue(patternFunction.execute(new LintTarget(new ArrayList<>(), "def")).passed);
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    public void testMatchingBothMatchAndNotMatch() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "[def]+");
        options.put("notMatch", "[abc]+");

        PatternFunction patternFunction = new PatternFunction(options);
        try {
            assertTrue(patternFunction.execute(new LintTarget(new ArrayList<>(), "def")).passed);
        } catch (InvalidRulesetException e) {
            fail("Execution should not throw an exception.");
        }
    }

    @Test
    public void testValidFunctionOptions() {
        Map<String, Object> options1 = new HashMap<>();
        options1.put("match", "foo");
        PatternFunction patternFunction1 = new PatternFunction(options1);
        assertDoesNotThrow(() -> patternFunction1.execute(new LintTarget(new ArrayList<>(), "def")));

        Map<String, Object> options2 = new HashMap<>();
        options2.put("notMatch", "foo");
        PatternFunction patternFunction2 = new PatternFunction(options2);
        assertDoesNotThrow(() -> patternFunction2.execute(new LintTarget(new ArrayList<>(), "def")));

        Map<String, Object> options3 = new HashMap<>();
        options3.put("match", "foo");
        options3.put("notMatch", "bar");
        PatternFunction patternFunction3 = new PatternFunction(options3);
        assertDoesNotThrow(() -> patternFunction3.execute(new LintTarget(new ArrayList<>(), "def")));
    }

    @Test
    public void testInvalidFunctionOptions() {
        Map<String, Object> options1 = null;
        PatternFunction patternFunction1 = new PatternFunction(options1);
        assertThrows(InvalidRulesetException.class,
                () -> patternFunction1.execute(new LintTarget(new ArrayList<>(), "abc")));

        Map<String, Object> options2 = new HashMap<>();
        PatternFunction patternFunction2 = new PatternFunction(options2);
        assertThrows(InvalidRulesetException.class,
                () -> patternFunction2.execute(new LintTarget(new ArrayList<>(), "abc")));

        Map<String, Object> options3 = new HashMap<>();
        options3.put("foo", true);
        PatternFunction patternFunction3 = new PatternFunction(options3);
        assertThrows(InvalidRulesetException.class,
                () -> patternFunction3.execute(new LintTarget(new ArrayList<>(), "abc")));

        Map<String, Object> options4 = new HashMap<>();
        options4.put("match", 2);
        PatternFunction patternFunction4 = new PatternFunction(options4);
        assertThrows(InvalidRulesetException.class,
                () -> patternFunction4.execute(new LintTarget(new ArrayList<>(), "abc")));

        Map<String, Object> options5 = new HashMap<>();
        options5.put("notMatch", null);
        PatternFunction patternFunction5 = new PatternFunction(options5);
        assertThrows(InvalidRulesetException.class,
                () -> patternFunction5.execute(new LintTarget(new ArrayList<>(), "abc")));

        Map<String, Object> options6 = new HashMap<>();
        options6.put("match", 4);
        options6.put("notMatch", 10);
        PatternFunction patternFunction6 = new PatternFunction(options6);
        assertThrows(InvalidRulesetException.class,
                () -> patternFunction6.execute(new LintTarget(new ArrayList<>(), "abc")));
    }
}
