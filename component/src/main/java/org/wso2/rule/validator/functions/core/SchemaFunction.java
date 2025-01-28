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
package org.wso2.rule.validator.functions.core;

import com.google.gson.Gson;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.wso2.rule.validator.Constants;
import org.wso2.rule.validator.document.LintTarget;
import org.wso2.rule.validator.functions.FunctionName;
import org.wso2.rule.validator.functions.LintFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class implements the schema function.
 */
@FunctionName("schema")
public class SchemaFunction extends LintFunction {

    public SchemaFunction(Map<String, Object> options) {
        super(options);
    }

    @Override
    public List<String> validateFunctionOptions() {
        ArrayList<String> errors = new ArrayList<>();

        if (options == null) {
            errors.add("Schema function should at least contain the schema option.");
            return errors;
        }

        if (!options.containsKey(Constants.RULESET_SCHEMA_SCHEMA)) {
            errors.add("Schema function should contain the schema option.");
        }

        if (options.containsKey(Constants.RULESET_SCHEMA_SCHEMA)) {
            String schema = new Gson().toJson(options.get(Constants.RULESET_SCHEMA_SCHEMA));
            if (!isValidSchema(schema)) {
                errors.add("Schema function should contain a valid JSON schema.");
            }
        }

        if (options.containsKey(Constants.RULESET_SCHEMA_DIALECT)) {
            if (!(options.get(Constants.RULESET_SCHEMA_DIALECT) instanceof String)) {
                errors.add("Schema function should contain a string value for the dialect option.");
            } else {
                String dialect = (String) options.get(Constants.RULESET_SCHEMA_DIALECT);
                List<String> dialects = new ArrayList<>(Arrays.asList(
                        "auto", "draft4", "draft6", "draft7", "draft2019-09", "draft2020-12"));
                if (!dialects.contains(dialect)) {
                    errors.add("Schema function should contain a valid JSON schema dialect.");
                }
            }
        }

        if (options.containsKey(Constants.RULESET_SCHEMA_ALL_ERRORS) &&
                !(options.get(Constants.RULESET_SCHEMA_ALL_ERRORS) instanceof Boolean)) {
            errors.add("Schema function should contain a boolean value for the allErrors option.");
        }

        return errors;
    }

    private boolean isValidSchema(String jsonSchemaString) {
        try {
            JSONObject rawSchema = new JSONObject(new JSONTokener(jsonSchemaString));
            SchemaLoader.load(rawSchema);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean executeFunction(LintTarget target) {

        String targetString = new Gson().toJson(target.value);
        JSONObject targetObject = new JSONObject(targetString);

        String schema = new Gson().toJson(options.get(Constants.RULESET_SCHEMA_SCHEMA));
        JSONObject schemaObject = new JSONObject(schema);
        org.everit.json.schema.Schema everitSchema = SchemaLoader.load(schemaObject);

        try {
            everitSchema.validate(targetObject);
        } catch (org.everit.json.schema.ValidationException e) {
            return false;
        }
        return true;
    }

}
