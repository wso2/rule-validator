package org.wso2.json.path.evaluator;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiKeyFieldsTest {

    @Test
    public void validatingMultiKeyFieldsTest() {
        Path jsonDocPath = Paths.get("src/test/resources/data.json");
        try {
            String jsonDocStringContent = Files.readString(jsonDocPath);
            JSONPathEvaluator jsonPathEvaluatorInstance = new JSONPathEvaluator(jsonDocStringContent);
            String jsonPathExpression =
                    "$.store.book[(@.length-@.length)]['category','author']";
            List<String> spectralResults = List.of(
                    "$['store']['book'][0]['category']",
                    "$['store']['book'][0]['author']"
            );
            List<String> validatorResults = jsonPathEvaluatorInstance.jsonPathEvaluate(jsonPathExpression);
            assertEquals(
                    new HashSet<>(spectralResults),
                    new HashSet<>(validatorResults),
                    "Rule Validator JSONPath results must match Spectral results"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void validatingMultiKeyFields() {
        Path jsonDocPath = Paths.get("src/test/resources/data.json");
        try {
            String jsonDocStringContent = Files.readString(jsonDocPath);
            JSONPathEvaluator jsonPathEvaluatorInstance = new JSONPathEvaluator(jsonDocStringContent);
            String jsonPathExpression =
                    "$.store.book[0]['category','author']['name','publication']";
            List<String> spectralResults = List.of(
                    "$['store']['book'][0]['category']['name']",
                    "$['store']['book'][0]['category']['publication']",
                    "$['store']['book'][0]['author']['name']",
                    "$['store']['book'][0]['author']['publication']"
            );
            List<String> validatorResults = jsonPathEvaluatorInstance.jsonPathEvaluate(jsonPathExpression);
            assertEquals(
                    new HashSet<>(spectralResults),
                    new HashSet<>(validatorResults),
                    "Rule Validator JSONPath results must match Spectral results"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
