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
package org.wso2.json.path.evaluator.document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import org.wso2.json.path.evaluator.document.wrappers.BooleanWrapper;
import org.wso2.json.path.evaluator.document.wrappers.NumberWrapper;
import org.wso2.json.path.evaluator.document.wrappers.StringWrapper;

import java.util.List;
import java.util.Map;

/**
 * Process the document
 */
public class Document {
    private final Object document;
    private TraversalMapData traversalInstance;

    public Document(String documentString) {
        Object yamlData = new Load(LoadSettings.builder().build()).loadFromString(documentString);
        if (yamlData == null) {
            this.document = null;
            return;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        documentString = gson.toJson(yamlData);

        Object parsedDocument = JsonPath.parse(documentString).json();

        this.document = wrapPrimitives(parsedDocument);
        this.traversalInstance = new TraversalMapData(this.document);
    }

    public Object getRootDocument() {
        return this.document;
    }

    public TraversalMapData getTraversalInstanceDetails() {
        return this.traversalInstance;
    }

    private static Object wrapPrimitives(Object node) {
        if (node instanceof String) {
            return new StringWrapper((String) node);
        } else if (node instanceof Number) {
            return new NumberWrapper((Number) node);
        } else if (node instanceof Boolean) {
            return new BooleanWrapper((Boolean) node);
        } else if (node instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) node;
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                entry.setValue(wrapPrimitives(entry.getValue()));
            }
            return map;
        } else if (node instanceof List) {
            List<Object> list = (List<Object>) node;
            for (int i = 0; i < list.size(); i++) {
                list.set(i, wrapPrimitives(list.get(i)));
            }
            return list;
        } else {
            return node;
        }
    }
}
