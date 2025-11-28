package org.wso2.json.path.evaluator;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertyFeatureTest {

    @Test
    public void comparingPropertyFeature() {
        Path jsonDocPath = Paths.get("src/test/resources/data.json");
        try {
            String jsonDocStringContent = Files.readString(jsonDocPath);
            JSONPathEvaluator jsonPathEvaluatorInstance = new JSONPathEvaluator(jsonDocStringContent);
            String jsonPathExpression =
                    "$.store.bicycle[?(@property === 'color')]";
            List<String> spectralResults = List.of(
                    "$['store']['bicycle']['color']"
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
    public void comparingPropertyFeatureInMultipleBraces() {
        Path jsonDocPath = Paths.get("src/test/resources/data.json");
        try {
            String jsonDocStringContent = Files.readString(jsonDocPath);
            JSONPathEvaluator jsonPathEvaluatorInstance = new JSONPathEvaluator(jsonDocStringContent);
            String jsonPathExpression =
                    "$..book[?(@property === 0)][?(@property !== 'category')]";
            List<String> spectralResults = List.of(
                    "$['store']['book'][0]['author']",
                    "$['store']['book'][0]['title']",
                    "$['store']['book'][0]['price']"

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
