/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.rule.validator.document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.wso2.rule.validator.functions.FunctionResult;
import org.wso2.rule.validator.ruleset.Format;
import org.wso2.rule.validator.ruleset.Rule;
import org.wso2.rule.validator.ruleset.RuleThen;
import org.wso2.rule.validator.ruleset.Ruleset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Document class to represent a target document for rules to be applied.
 */
public class Document {

    private String documentString;
    private Object document;
    Format format;

    public Document(String documentString) {
        LoadSettings settings = LoadSettings.builder().build();
        Load yamlLoader = new Load(settings);
        Object yamlData = yamlLoader.loadFromString(documentString);

        if (yamlData == null) {
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        this.documentString = gson.toJson(yamlData);

        this.document = JsonPath.parse(this.documentString).json();
        resolveReferences();

        // Read format
        Map<String, Object> documentMap = (Map<String, Object>) yamlData;
        if (documentMap.containsKey("openapi")) {
            String oasVersion = (String) documentMap.get("openapi");
            if (oasVersion.startsWith("3.1")) {
                this.format = Format.OAS3_1;
            } else if (oasVersion.startsWith("3.0")) {
                this.format = Format.OAS3_0;
            } else {
                this.format = Format.OAS3;
            }
        } else if (documentMap.containsKey("swagger")) {
            this.format = Format.OAS2;
        }
    }

    public ArrayList<FunctionResult> lint(Ruleset ruleset) {
        // TODO: Add parsing errors to the result set

        // TODO: Filter enabled and relevant rules

        ArrayList<FunctionResult> results = new ArrayList<>();

        for (Rule rule : ruleset.rules.values()) {
            for (String given : rule.given) {
                // TODO: Implement aliases
                if (given.startsWith("#")) {
                    continue;
                }
                try {
                    Configuration config = Configuration.builder().options(Option.AS_PATH_LIST).build();
                    List<String> paths = JsonPath.using(config).parse(this.document).read(given);
                    for (String path : paths) {
                        results.addAll(lintNode(path, rule));
                    }
                    // log("Json Path resolved: " + given);
                } catch (PathNotFoundException e) {
                    // log("Json Path not found: " + given);
                } catch (InvalidPathException e) {
                    // log("Unsupported Json Path: " + given);
                    // TODO: Implement json path plus features
                }
            }
        }

        return results;
    }

    private void resolveReferences() {
        // TODO: Resolve references
        /**
         * A document Inventory maintains a graph (non-circular) pointing to other documents via refs. When a ref is in
         * a document, it adds a Node in the graph pointing to the document, and if there are refs within that ref, that
         * node will point to another node with a ref.
         *
         * When traversing the json paths for linting with a ruleset, it will get the closest path until the ref, then
         * traverse the rest on the ref doc, and this is done recursively.
         */
    }

    private ArrayList<FunctionResult> lintNode(String path, Rule rule) {
        ArrayList<FunctionResult> results = new ArrayList<>();
        Object node;
        try {
            node = JsonPath.read(this.document, path);
        } catch (PathNotFoundException e) {
            return results;
        }
        for (RuleThen then : rule.then) {
            ArrayList<LintTarget> lintTargets = getLintTargets(node, then);

            for (LintTarget target : lintTargets) {
                boolean result = then.lintFunction.execute(target);
                String targetPath = target.getPathString();
                results.add(new FunctionResult(result, path + targetPath, rule.message, rule));
            }
        }
        return results;
    }

    private ArrayList<LintTarget> getLintTargets(Object node, RuleThen then) {
        ArrayList<LintTarget> lintTargets = new ArrayList<>();

        if ((node instanceof List || node instanceof Map) && (then.field != null && !then.field.isEmpty())) {
            if (then.field.equals("@key")) {
                if (node instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) node;
                    for (String key : map.keySet()) {
                        lintTargets.add(new LintTarget(new ArrayList<>(Arrays.asList(key)), key));
                    }
                } else if (node instanceof List) {
                    // TODO: Test
                    List<Object> list = (List<Object>) node;
                    for (int i = 0; i < list.size(); i++) {
                        lintTargets.add(new LintTarget(new ArrayList<>(Arrays.asList(String.valueOf(i))),
                                String.valueOf(i)));
                    }
                } else {
                    throw new RuntimeException("Node is not a Map or List but the field is @key");
                }
            } else if (then.field.startsWith("$")) {
                Configuration config = Configuration.builder().options(Option.AS_PATH_LIST).build();
                List<String> paths;
                try {
                    paths = JsonPath.using(config).parse(node).read(then.field);
                } catch (PathNotFoundException e) {
                    return lintTargets;
                }

                for (String path : paths) {
                    ArrayList<String> splitPath = splitJsonPath(path);
                    Object value;
                    try {
                        value = JsonPath.read(node, path);
                        lintTargets.add(new LintTarget(splitPath, value));
                    } catch (PathNotFoundException ignored) {

                    }
                }
            } else {
                ArrayList<String> path = toPath(then.field);
                Object value;
                try {
                    value = JsonPath.read(node, then.field);
                    lintTargets.add(new LintTarget(path, value));
                } catch (PathNotFoundException e) {
                    lintTargets.add(new LintTarget(path, null));
                }
            }
        } else {
            lintTargets.add(new LintTarget(new ArrayList<>(), node));
        }

        return lintTargets;
    }

    public static ArrayList<String> splitJsonPath(String jsonPath) {
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        boolean insideBrackets = false;

        for (char ch : jsonPath.toCharArray()) {
            if (ch == '[') {
                insideBrackets = true;
                continue; // Skip the opening bracket
            } else if (ch == ']') {
                insideBrackets = false;
                parts.add(currentPart.toString().replace("'", "").replace("\"", ""));
                currentPart.setLength(0); // Clear the current part
            } else if (insideBrackets) {
                currentPart.append(ch);
            }
        }

        return parts;
    }

    public static ArrayList<String> toPath(String path) {
        ArrayList<String> segments = new ArrayList<>();

        // Regex to match either dot-separated keys or bracket notation
        Pattern pattern = Pattern.compile(
                "\\[(?:'([^']*)'|\"([^\"]*)\")\\]|([^.\\[\\]]+)"
        );
        Matcher matcher = pattern.matcher(path);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Single-quoted key in brackets
                segments.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // Double-quoted key in brackets
                segments.add(matcher.group(2));
            } else if (matcher.group(3) != null) {
                // Unquoted index or key in brackets
                segments.add(matcher.group(3));
            } else if (matcher.group(4) != null) {
                // Dot-separated key
                segments.add(matcher.group(4));
            }
        }

        return segments;
    }
}
