package org.wso2.json.path.evaluator;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LengthFeatureTest {

    @Test
    public void comparingLengthFeature() {
        Path jsonDocPath = Paths.get("src/test/resources/data.json");
        try {
            String jsonDocStringContent = Files.readString(jsonDocPath);
            JSONPathEvaluator jsonPathEvaluatorInstance = new JSONPathEvaluator(jsonDocStringContent);
            String jsonPathExpression =
                    "$.store.book[(@.length -1)] ";
            List<String> spectralResults = List.of(
                    "$['store']['book'][3]"
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
    public void comparingLengthFeatureInMultipleBraces() {
        Path jsonDocPath = Paths.get("src/test/resources/data.json");
        try {
            String jsonDocStringContent = Files.readString(jsonDocPath);
            JSONPathEvaluator jsonPathEvaluatorInstance = new JSONPathEvaluator(jsonDocStringContent);
            String jsonPathExpression =
                    "$.store.book[(@.length -1)]['category','author']";
            List<String> spectralResults = List.of(
                    "$['store']['book'][3]['category']",
                    "$['store']['book'][3]['author']"

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
