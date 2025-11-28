package org.wso2.json.path.evaluator;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONPathPlusFeaturesCombinationTest {

    @Test
    public void validatingAdvancedFeaturesCombinations() {
        Path jsonDocPath = Paths.get("src/test/resources/data.json");
        try {
            String jsonDocStringContent = Files.readString(jsonDocPath);
            JSONPathEvaluator jsonPathEvaluatorInstance = new JSONPathEvaluator(jsonDocStringContent);
            String jsonPathExpression =
                    "$..book[?(@property && (@parent.bicycle.price && @parent.bicycle.price + 19.95))]";
            List<String> spectralResults = List.of(
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
    public void validatingDifferentFeatureCombinations() {
        Path jsonDocPath = Paths.get("src/test/resources/data.json");
        try {
            String jsonDocStringContent = Files.readString(jsonDocPath);
            JSONPathEvaluator jsonPathEvaluatorInstance = new JSONPathEvaluator(jsonDocStringContent);
            String jsonPathExpression =
                    "$.store.book[?(@property === 0)].category[?(@parentProperty !==0)]";
            List<String> spectralResults = List.of(
                    "$['store']['book'][0]['category']['name']",
                    "$['store']['book'][0]['category']['publication']"
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
