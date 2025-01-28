# WSO2 Rule Validator

---
WSO2 Rule Validator is a JSON/YAML linter that is a Java implementation of Stoplight Spectral. [https://github.com/stoplightio/spectral](https://github.com/stoplightio/spectral)

## Features
WSO2 Rule validator supports most features that Spectral itself does. With the exception of a few due to disadvantages in language and environment.

### 1. Rules
- Given path is a JSON Path. But currently WSO2 Rule Validator does not support [JSON Path Plus](https://github.com/JSONPath-Plus/JSONPath) features, even though Spectral does.
- All [core functions](https://docs.stoplight.io/docs/spectral/cb95cf0d26b83-core-functions) of Spectral are supported.
- Custom functions are <b>not</b> supported.
- Documents are always resolved before the validation happens.

### 2. Rulesets
- Supports both YAML and JSON rulesets
- Extends are currently not supported
- Parser options are <b>not</b> supported
- Overrides are currently not supported
- Only Async API and Open API are supported (all versions)
- Above formats are supported at all levels (Ruleset and Rule level)
- Aliases are supported.

## Usage

1. Build using `mvn clean install`.
2. Use the `.jar` file generated in `component/target`.
3. Use the following maven dependency
   ```xml
        <dependency>
            <groupId>org.wso2.carbon</groupId>
            <artifactId>rule.validator</artifactId>
            <version>${rule.validator.version}</version>
        </dependency>
    ```
4. Read the ruleset file into a string and call the `validateRuleset` method to validate the ruleset.
    ```java
        String document = new String(Files.readAllBytes(Paths.get("path/to/ruleset/ruleset.yaml")));
        String rulesetValidationResult = Validator.validateRuleset(ruleset);
    ```
5. Read the target document file and the ruleset into strings and call the `validateDocument` method to validate the document against the ruleset.
    ```java
        String document = new String(Files.readAllBytes(Paths.get("path/to/document/document.yaml")));
        String documentValidationResult = Validator.validateDocument(document, ruleset);
    ```