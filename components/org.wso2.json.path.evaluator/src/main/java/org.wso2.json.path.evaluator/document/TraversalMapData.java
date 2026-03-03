/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

import org.wso2.json.path.evaluator.Constants;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds parent-child and node-path mappings for a document tree.
 */
public class TraversalMapData {
    private final IdentityHashMap<Object, Object> parentChildMap = new IdentityHashMap<>();
    private final IdentityHashMap<Object, String> pathMap = new IdentityHashMap<>();

    /**
     * Builds traversal maps for the provided root document.
     *
     * @param rootDocument root document object
     */
    public TraversalMapData(Object rootDocument) {
        generateParentChildMap(rootDocument, null);
        generatePathMap(rootDocument, Constants.JSON_PATH_ROOT);
    }

    private void generateParentChildMap(Object current, Object parent) {
        if (parent != null) {
            parentChildMap.put(current, parent);
        }
        if (current instanceof Map) {
            for (Map.Entry<Object, Object> entry: ((Map<Object, Object>) current).entrySet()) {
                Object value = (entry.getValue());
                generateParentChildMap(value , current);
            }
        } else if (current instanceof List) {
            List<Object> list = (List<Object>) current;
            for (int i = 0; i < list.size(); i++) {
                Object item = (list.get(i));
                generateParentChildMap(item, current);
            }
        }
    }

    private void generatePathMap(Object node, String path) {
        if (node != null) {
            pathMap.put(node, path);
        }
        if (node instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) node;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                generatePathMap(entry.getValue(), path + Constants.PATH_KEY_PREFIX + entry.getKey() +
                        Constants.PATH_KEY_SUFFIX);
            }
        } else if (node instanceof List) {
            List<?> list = (List<?>) node;
            for (int i = 0; i < list.size(); i++) {
                generatePathMap(list.get(i), path + Constants.OPEN_BRACKET + i + Constants.CLOSE_BRACKET);
            }
        }
    }

    /**
     * Returns the stored JSONPath for a node.
     *
     * @param currentNode node to resolve
     * @return JSONPath for the node or {@code null} if unavailable
     */
    public String getPath(Object currentNode) {
        if (currentNode == null) {
            return null;
        }
        return pathMap.get(currentNode);
    }

    /**
     * Returns the stored parent for a node.
     *
     * @param currentNode node to resolve
     * @return parent node or {@code null} if unavailable
     */
    public Object getParent(Object currentNode) {
        if (currentNode == null) {
            return null;
        }
        return parentChildMap.get(currentNode);
    }
}
