package org.wso2.json.path.evaluator;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RootFeatureTest {

    @Test
    public void comparingPathFeature() {
        Path jsonDocPath = Paths.get("src/test/resources/data.json");
        try {
            String jsonDocStringContent = Files.readString(jsonDocPath);
            JSONPathEvaluator jsonPathEvaluatorInstance = new JSONPathEvaluator(jsonDocStringContent);
            String jsonPathExpression =
                    "$..book[?(@root.store.book[2].price !== 12.99)]";
            List<String> spectralResults = List.of(
                    "$['store']['book'][0]",
                    "$['store']['book'][1]",
                    "$['store']['book'][2]",
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
    public void comparingRootFeatureTesting() {
        Path jsonDocPath = Paths.get("src/test/resources/data.json");
        try {
            String jsonDocStringContent = Files.readString(jsonDocPath);
            JSONPathEvaluator jsonPathEvaluatorInstance = new JSONPathEvaluator(jsonDocStringContent);
            String jsonPathExpression =
                    "$..book[?(@.price === @root.store.book[2].price)]";
            List<String> spectralResults = List.of(
                    "$['store']['book'][2]"
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
