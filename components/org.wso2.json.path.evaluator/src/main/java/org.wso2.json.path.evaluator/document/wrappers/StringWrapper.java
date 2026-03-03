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
package org.wso2.json.path.evaluator.document.wrappers;

/**
 * Wraps string values so they can be tracked by identity in traversal maps.
 */
public class StringWrapper {

    public String value;

    /**
     * Creates a wrapper for a string value.
     *
     * @param value string value to wrap
     */
    public StringWrapper(String value) {
        this.value = value;
    }

    /**
     * Returns the wrapped string value.
     *
     * @return wrapped string value
     */
    @Override
    public String toString() {
        return value;
    }
}
